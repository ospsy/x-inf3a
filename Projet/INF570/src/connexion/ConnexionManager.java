package connexion;

import gui.Out;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import config.Settings;
import message.*;

/**
 * La classe ConnexionManager gère les relations entre les différentes Connexions,
 * comme l'envoi global, les déconnexions et les confirmations de connexions. De plus,
 * il héberge le thread du serveur recevant les connexions. Il faut l'initialiser avec <code>init(ServerPort)</code>
 * @see NeighbourConnexion
 * @author Benoit
 *
 */
public class ConnexionManager{
	static private boolean closing;
	static private int port;
	static private HashMap<NeighbourConnexion, NeighbourConnexion> connexions;
	static private HashMap<PreConnexion, PreConnexion> preConnexions;
	static private HashMap<TransferConnexion, TransferConnexion> transferConnexions;
	static private ServerThread server;
	static private Thread sweepingThread;
	static private HashMap<Identifiant, NeighbourConnexion> forwarding;
	static private HashMap<Identifiant, Long> lastTimeId;
	static private LinkedList<Neighbour> neighbours;
	static private LinkedList<QueryResult> queryResults;
	static private Identifiant lastPing;

	/**
	 * Initialise le ConnexionManager, il faut IMPERATIVEMENT appeler cette fonction avant de faire
	 * quoi que ce soit. Cette fonction crée aussi le serverSocket d'écoute avec le premier port libre
	 * trouvé à partir de <code>ServerPort</code>
	 * @param ServerPort le port sur lequel il faut essayer d'écouter.
	 */
	static public void init(int ServerPort) {
		closing=false;
		port=ServerPort;
		connexions = new HashMap<NeighbourConnexion, NeighbourConnexion>();
		transferConnexions = new HashMap<TransferConnexion, TransferConnexion>();
		preConnexions = new HashMap<PreConnexion, PreConnexion>();
		forwarding = new HashMap<Identifiant, NeighbourConnexion>();
		lastTimeId = new HashMap<Identifiant, Long>();
		neighbours = new LinkedList<Neighbour>();
		boolean serverCreated=false;
		while(!serverCreated){
			try {
				System.out.println("Tentative de création de serveur sur le port "+port);
				server = new ServerThread(port);
				serverCreated=true;
				Out.println("Serveur créé "+server.getIP()+":"+port);
			} catch (IOException e) {
				System.out.println("Impossible de créer le ServerSocket");
				port++;
			}
		}
		//Thread du sweep des forwarding
		sweepingThread = new Thread("forwarding-sweep"){
			public void run() {
				while(!closing){
					synchronized (ConnexionManager.class) {
						for (Iterator<Identifiant> it = lastTimeId.keySet().iterator();it.hasNext();) {
							Identifiant id=it.next();
							if(System.currentTimeMillis()-lastTimeId.get(id)>20000){
								it.remove();
								forwarding.remove(id);
							}
						}
					}
					try {
						Thread.sleep(5000);
					} catch (Exception e) {
					}
				}
				System.out.println("Thread de forwarding-sweep closed.");
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
	 * Renvoit la liste décrivant les voisins découverts par le dernier PING
	 * @return la liste des voisins
	 */
	@SuppressWarnings("unchecked")
	static synchronized public LinkedList<Neighbour> getNeighbours(){
		return (LinkedList<Neighbour>) neighbours.clone();
	}

	/**
	 * Renvoit la liste décrivant les résultats de recherches découverts par le dernier QUERY
	 * @return la liste des voisins
	 */
	@SuppressWarnings("unchecked")
	static synchronized public LinkedList<QueryResult> getQueryResults(){
		return (LinkedList<QueryResult>) queryResults.clone();
	}
	
	static synchronized public LinkedList<Transfer> getTransfers(){
		LinkedList<Transfer> tmp= new LinkedList<Transfer>();
		for (TransferConnexion transferConnexion : transferConnexions.keySet()) {
			tmp.add(new Transfer(transferConnexion));
		}
		return tmp;
	}

	/**
	 * Ferme toutes les connexions et le server d'écoute
	 */
	static synchronized public void close(){
		closing=true;
		sweepingThread.interrupt();
		server.close();
		LinkedList<NeighbourConnexion> tmp = new LinkedList<NeighbourConnexion>(connexions.keySet());
		for(NeighbourConnexion c : tmp)
			c.close();
		LinkedList<PreConnexion> tmp2 = new LinkedList<PreConnexion>(preConnexions.keySet());
		for(PreConnexion pc : tmp2)
			pc.close();
	}

	/**
	 * Crée une connexion vers le pair spécifié
	 * @param ip l'adresse IP du pair
	 * @param port le port du pair
	 */
	static public void connect(String ip, int port){
		try {
			Socket s = new Socket(ip,port);
			new PreConnexion(s, false);
		} catch (Exception e) {
			Out.println("Connexion impossible...");
		}
	}
	
	/**
	 * Télécharge le fichier décrit
	 * @param r le QueryResult décrivant le fichier
	 */
	static public void download(QueryResult r){
		try {
			Socket s = new Socket(r.getIP(),r.getPort());
			new PreConnexion(s, r.getName(), r.getIndex());
		} catch (Exception e) {
			Out.println("Téléchargement impossible...");
			//e.printStackTrace();
		}
	}
	
	/*static public void download(){
		download(queryResults.getFirst());
	}*/

	/**
	 * Envoit un PING à tout le monde
	 */
	static synchronized public void ping(){
		neighbours = new LinkedList<Neighbour>();
		Out.displayVoisin();
		Message m=new Ping(Settings.getMaxTTL(), 0);
		lastPing=m.getHeader().getMessageID();
		sendAll(m, null);
	}

	/**
	 * Envoit un QUERY à tout le monde
	 */
	public synchronized static void query(String[] criteria) {
		queryResults=new LinkedList<QueryResult>();
		Message m=new Query(Settings.getMaxTTL(), 0,criteria,0);
		sendAll(m, null);
	}

	static protected synchronized void addConnexion(TransferConnexion c){
		Out.println("DownloadConnexion "+c.getId()+" ajoutée");
		transferConnexions.put(c, c);
	}
	static protected synchronized void removeConnexion(TransferConnexion c){
		Out.println("DownloadConnexion "+c.getId()+" retirée");
		transferConnexions.remove(c);
	}
	
	static protected synchronized void addConnexion(NeighbourConnexion c){
		Out.println("Connexion "+c.getId()+" ajoutée");
		connexions.put(c, c);
	}
	static protected synchronized void removeConnexion(NeighbourConnexion c){
		Out.println("Connexion "+c.getId()+" retirée");
		connexions.remove(c);
	}


	static protected synchronized void addPreConnexion(PreConnexion preConnexion){
		System.out.println("Preconnexion "+preConnexion.getId()+" réussie");
		preConnexions.put(preConnexion, preConnexion);
	}
	static protected synchronized void removePreConnexion(PreConnexion c, boolean success){
		if(!success)
			Out.println("Confirmation de la Preconnexion "+c.getId()+" échouée");
		preConnexions.remove(c);
	}

	

	/**
	 * Ajoute une connexion à confirmer à la liste des preConnexions gérées
	 * @param preConnexion la connexion à confirmer
	 */
	

	/**
	 * Envoit un message à toutes les connexions gérées sauf une
	 * @param m le message à envoyer
	 * @param exclude la connexion à exclure
	 */
	static protected synchronized void sendAll(Message m, NeighbourConnexion exclude){
		if(!forwarding.containsKey(m.getHeader().getMessageID())){//si on a pas encore inondé
			forwarding.put(m.getHeader().getMessageID(), exclude);
			lastTimeId.put(m.getHeader().getMessageID(), new Long(System.currentTimeMillis()));
			for(NeighbourConnexion c : connexions.keySet()){
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
	static protected void forward(Message m){
		synchronized (forwarding) {
			Identifiant id=m.getHeader().getMessageID();
			if(!forwarding.containsKey(id)){
				System.err.println("Impossible de forwarder...");
				return;
			}
			NeighbourConnexion c=forwarding.get(id);
			if(c!=null && m.getHeader().getTTL()>0){//je transfère
				m.decreaseTTL();
				c.send(m);
			}else{//il est pour moi
				processMsg(m);
			}
		}
	}

	/**
	 * Cette fonction traite les messages qui nous sont destinés
	 * @param m le message à traiter
	 */
	static synchronized private void processMsg(Message m){
		//System.out.println(m);
		switch(m.getHeader().getMessageType()){
		case PONG:
			System.out.println("PONG recu");
			if(m.getHeader().getMessageID().equals(lastPing)){
				neighbours.add(new Neighbour(m));
				Out.displayVoisin();
			}
			break;
		case QUERY_HIT:
			System.out.println("QUERY_HIT recu");
			QueryHit qh = (QueryHit)m;
			Result[] results = qh.getResultSet();
			System.out.println(queryResults.size()+"résultats "+results.length+" à ajouter");
			for(int i=0;i<results.length;i++){
				queryResults.add(new QueryResult(qh.getIp(), qh.getPort(), qh.getSpeed(), results[i], qh.getServentIdentifier()));
			}
			System.out.println(queryResults.size()+"résultats");
			Out.displayQueryResults();
			break;
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
	private String IP;

	/**
	 * Création du serveur et de son thread d'écoute
	 * @param port le port sur lequel écouter
	 * @throws IOException si la création du serveur a échoué
	 */
	public ServerThread(int port) throws IOException{
		super();
		closing=false;
		server = new ServerSocket(port);
		findIP();
		this.start();
	}

	private void findIP() {
		InetAddress ip=null;
		try{
			Enumeration<NetworkInterface> netInterfaces=NetworkInterface.getNetworkInterfaces();
			while(netInterfaces.hasMoreElements()){
				NetworkInterface ni=(NetworkInterface)netInterfaces.nextElement();
				Enumeration<InetAddress> ad=ni.getInetAddresses();
				while(ad.hasMoreElements()){
					ip=(InetAddress) ad.nextElement();
					if(ip instanceof Inet4Address) break;
				}
				System.out.println(ip);
				if(ip==null || !(ip instanceof Inet4Address)) continue;
				if(!ip.isLoopbackAddress()){
					System.out.println("Interface "+ni.getName()+" seems to be InternetInterface. I'll take it...");
					break;
				}else{
					ip=null;
				}
			}	
		} catch (Exception e) {
			System.err.println("ERROR");
		}
		if(ip!=null){
			IP=ip.getHostAddress();
		}
		else{
			IP="0.0.0.0";
			System.err.println("Impossible de trouver l'IP.");
		}
	}

	public String getIP(){
		return IP;
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
			try {
				s=server.accept();
				new PreConnexion(s,true);
			} catch (IOException e) {
				System.out.println("Arrêt de l'attente de nouvelles connexions");
			}
		}
	}
}
