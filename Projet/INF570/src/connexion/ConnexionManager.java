package connexion;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import message.*;

/**
 * La classe ConnexionManager gère les relations entre les différentes Connexions,
 * comme l'envoi global, les déconnexions et les confirmations de connexions. De plus,
 * il héberge le thread du serveur recevant les connexions
 * @see Connexion
 * @author Benoit
 *
 */
public class ConnexionManager{
	static private int port;
	static private HashMap<Connexion, Connexion> connexions;
	static private HashMap<Connexion, Connexion> preConnexions;
	static private ServerThread server;


	static public void init(int ServerPort) {
		port=ServerPort;
		connexions = new HashMap<Connexion, Connexion>();
		preConnexions = new HashMap<Connexion, Connexion>();
		boolean serverCreated=false;
		while(!serverCreated){
			try {
				System.out.println("Tentative de création de serveur sur le port "+port);
				server = new ServerThread(port);
				serverCreated=true;
				System.out.println("Serveur créé sur le port "+port);
			} catch (IOException e) {
				System.out.println("Impossible de créer le ServerSocket");
				port++;
			}
		}
	}

	static public int getPort(){
		return port;
	}

	static public String getIP(){
		return server.getIP();
	}

	//TODO
	static public void close(){
		server.close();
		for(Connexion c : connexions.keySet())
			c.close();
		for(Connexion c : preConnexions.keySet())
			c.close();
	}
	
	static public void connect(String ip, int port){
		try {
			Socket s = new Socket(ip,port);
			addPreConnexion(new Connexion(s, false));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Connexion impossible...");
		}
	}

	/**
	 * Enlève une connexion de la liste des connexions gérées
	 * @param c la connexion à enlever
	 */
	static public synchronized void remove(Connexion c){
		System.out.println("Connexion "+c.getId()+" retirée");
		connexions.remove(c);
	}

	/**
	 * Retire une preConnexion, si par exemple le protocole de confirmation a échoué
	 * @param c la preConnexion a retirer
	 */
	static public synchronized void removePreConnexion(Connexion c){
		System.out.println("Confirmation de la Preconnexion "+c.getId()+" échouée");
		preConnexions.remove(c);
	}

	/**
	 * Ajoute une connexion à confirmer à la liste des preConnexions gérées
	 * @param preConnexion la connexion à confirmer
	 */
	static public synchronized void addPreConnexion(Connexion preConnexion){
		System.out.println("Preconnexion "+preConnexion.getId()+" réussie");
		preConnexions.put(preConnexion, preConnexion);
	}

	/**
	 * Envoit un message à toutes les connexions gérées sauf une
	 * @param m le message à envoyer
	 * @param exclude la connexion à exclure
	 */
	static public synchronized void sendAll(Message m, Connexion exclude){
		for(Connexion c : connexions.keySet()){
			if(c!=exclude){
				c.send(m);
			}
		}
	}

	/**
	 * Confirme la connexion dans la liste globale
	 * Si la preConnexion n'existe pas, affiche un message d'erreur
	 * @param c la connexion à confirmer
	 */
	static public synchronized void confirmPreConnexion(Connexion c){
		if(preConnexions.remove(c)!=null){
			System.out.println("Preconnexion "+c.getId()+" confirmée");
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
