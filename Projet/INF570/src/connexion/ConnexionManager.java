package connexion;

import gui.Out;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

import config.Settings;
import message.*;

/**
 * La classe ConnexionManager gère les relations entre les différentes Connexions,
 * comme l'envoi global, les déconnexions et les confirmations de connexions. De plus,
 * il héberge le thread du serveur recevant les connexions. Il faut l'initialiser avec <code>init(ServerPort)</code>
 * @see Connexion
 * @author Benoit
 *
 */
public class ConnexionManager{
	static private boolean closing;
	static private int port;
	static private HashMap<Connexion, Connexion> connexions;
	static private HashMap<Connexion, Connexion> preConnexions;
	static private ServerThread server;
	static private Thread sweepingThread;
	static private HashMap<Identifiant, Connexion> forwarding;
	static private HashMap<Identifiant, Long> lastTimeId;
	static private LinkedList<Neighbour> neighbours;

	/**
	 * Initialise le ConnexionManager, il faut IMPERATIVEMENT appeler cette fonction avant de faire
	 * quoi que ce soit. Cette fonction crée aussi le serverSocket d'écoute avec le premier port libre
	 * trouvé à partir de <code>ServerPort</code>
	 * @param ServerPort le port sur lequel il faut essayer d'écouter.
	 */
	static public void init(int ServerPort) {
		closing=false;
		port=ServerPort;
		connexions = new HashMap<Connexion, Connexion>();
		preConnexions = new HashMap<Connexion, Connexion>();
		forwarding = new HashMap<Identifiant, Connexion>();
		lastTimeId = new HashMap<Identifiant, Long>();
		boolean serverCreated=false;
		while(!serverCreated){
			try {
				System.out.println("Tentative de création de serveur sur le port "+port);
				server = new ServerThread(port);
				serverCreated=true;
				Out.println("Serveur créé sur le port "+port);
			} catch (IOException e) {
				System.out.println("Impossible de créer le ServerSocket");
				port++;
			}
		}
		//Thread du sweep des forwarding
		sweepingThread = new Thread("forwarding-sweep"){
			public void run() {
				while(!closing){
					synchronized (this) {
						for (Identifiant id : lastTimeId.keySet()) {
							if(System.currentTimeMillis()-lastTimeId.get(id)>20000){
								lastTimeId.remove(id);
								forwarding.remove(id);
							}
						}
					}
					try {
						Thread.sleep(2000);
					} catch (Exception e) {
					}
				}
			};
		};
		sweepingThread.start();
	}

	/**
	 * Renvoit le port d'écoute actuel du serveur
	 * @return le numéro de port
	 */
	static public int getPort(){
		return port;
	}

	/**
	 * Renvoit l'adresse IP d'écoute actuelle du serveur
	 * @return l'adresse IP en format String
	 */
	static public String getIP(){
		return server.getIP();
	}
	
	/**
	 * Renvoit la liste décrivant les voisins découvert par le dernier PING
	 * @return la liste des voisins
	 */
	@SuppressWarnings("unchecked")
	static synchronized public LinkedList<Neighbour> getNeighbours(){
		return (LinkedList<Neighbour>) neighbours.clone();
	}

	/**
	 * Ferme toutes les connexions et le server d'écoute
	 */
	static public void close(){
		closing=true;
		sweepingThread.interrupt();
		server.close();
		for(Connexion c : connexions.keySet())
			c.close();
		for(Connexion c : preConnexions.keySet())
			c.close();
	}

	/**
	 * Crée une connexion vers le pair spécifié
	 * @param ip l'adresse IP du pair
	 * @param port le port du pair
	 */
	static public void connect(String ip, int port){
		try {
			Socket s = new Socket(ip,port);
			addPreConnexion(new Connexion(s, false));
		} catch (Exception e) {
			e.printStackTrace();
			Out.println("Connexion impossible...");
		}
	}
	
	/**
	 * Envoit un PING à tout le monde
	 */
	static synchronized public void ping(){
		neighbours = new LinkedList<Neighbour>();
		Message m=new Ping(Settings.getMaxTTL(), 0);
		sendAll(m, null);
	}

	/**
	 * Envoit un QUERY à tout le monde
	 */
	static public void query(){
		Message m=new Query(Settings.getMaxTTL(), 0,new String[] {"s"},0);
		sendAll(m, null);
	}
	public static void query(String[] criteria) {
		Message m=new Query(Settings.getMaxTTL(), 0,criteria,0);
		sendAll(m, null);
	}
	
	/**
	 * Enlève une connexion de la liste des connexions gérées
	 * @param c la connexion à enlever
	 */
	static protected synchronized void remove(Connexion c){
		Out.println("Connexion "+c.getId()+" retirée");
		connexions.remove(c);
	}

	/**
	 * Retire une preConnexion, si par exemple le protocole de confirmation a échoué
	 * @param c la preConnexion a retirer
	 */
	static protected synchronized void removePreConnexion(Connexion c){
		Out.println("Confirmation de la Preconnexion "+c.getId()+" échouée");
		preConnexions.remove(c);
	}

	/**
	 * Ajoute une connexion à confirmer à la liste des preConnexions gérées
	 * @param preConnexion la connexion à confirmer
	 */
	static protected synchronized void addPreConnexion(Connexion preConnexion){
		System.out.println("Preconnexion "+preConnexion.getId()+" réussie");
		preConnexions.put(preConnexion, preConnexion);
	}

	/**
	 * Envoit un message à toutes les connexions gérées sauf une
	 * @param m le message à envoyer
	 * @param exclude la connexion à exclure
	 */
	static protected synchronized void sendAll(Message m, Connexion exclude){
		if(!forwarding.containsKey(m.getHeader().getMessageID())){//si on a pas encore inondé
			forwarding.put(m.getHeader().getMessageID(), exclude);
			lastTimeId.put(m.getHeader().getMessageID(), new Long(System.currentTimeMillis()));
			for(Connexion c : connexions.keySet()){
				if(c!=exclude){
					c.send(m);
				}
			}
		}
	}
	
	/**
	 * Demande de faire remonter un message de réponse (i.e. dont l'identifiant est déjà passé
	 * @param m le message à faire remonter
	 */
	static protected synchronized void forward(Message m){
		Identifiant id=m.getHeader().getMessageID();
		if(!forwarding.containsKey(id)){
			System.err.println("Impossible de forwarder...");
			return;
		}
		Connexion c=forwarding.get(id);
		if(c!=null && m.getHeader().getTTL()>0){//je transfère
			m.decreaseTTL();
			c.send(m);
		}else{//il est pour moi
			processMsg(m);
		}
	}
	
	/**
	 * Cette fonction traite les messages qui nous sont destinés
	 * @param m le message à traiter
	 */
	static synchronized private void processMsg(Message m){
		System.out.println(m);
		switch(m.getHeader().getMessageType()){
		case PONG:
			neighbours.add(new Neighbour(m));
			break;
		case QUERY_HIT:
			break;
		}
	}

	/**
	 * Confirme la connexion dans la liste globale
	 * Si la preConnexion n'existe pas, affiche un message d'erreur
	 * @param c la connexion à confirmer
	 */
	static protected synchronized void confirmPreConnexion(Connexion c){
		if(preConnexions.remove(c)!=null){
			System.out.println("Preconnexion "+c.getId()+" confirmée");
			Out.println("Connexion "+c.getId()+" ajoutée");
			connexions.put(c, c);
		}else{//si la connexion n'était pas dans la liste de preConnexion
			System.err.println("confirmConnexion : la connexion n'était pas à confirmer...");
		}
	}


}

/**
 * Classe qui permet de recevoir des nouvelles connexions sur un ServerSocket.
 * Elle lance son start() toute seule.
 * @author Benoit
 */
class ServerThread extends Thread{
	private ServerSocket server;
	private boolean closing;

	/**
	 * Création du serveur et de son thread d'écoute
	 * @param port le port sur lequel écouter
	 * @throws IOException si la création du serveur a échoué
	 */
	public ServerThread(int port) throws IOException{
		super();
		closing=false;
		server = new ServerSocket(port);
		this.start();
	}

	public String getIP(){
		return server.getInetAddress().getHostAddress();
	}

	public void close() {
		closing=true;
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Fermeture du server");
	}


	@Override
	public void run() {
		while(!closing){
			Socket s=null;
			Connexion c=null;
			try {
				s=server.accept();
				c=new Connexion(s,true);
			} catch (IOException e) {
				System.out.println("Arrêt de l'attente de nouvelles connexions");
			}
			if(c!=null){
				System.out.println("Nouvelle preConnexion :"+s.getInetAddress().getCanonicalHostName());
				ConnexionManager.addPreConnexion(c);
			}
		}
	}
}
