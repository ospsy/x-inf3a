import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
	final static int TTLMAX=4;
	static int myPort=9999;
	static String userName="";
	static String status="...";
	static Collection<Connexion> connexions;
	static BlockingQueue<Capsule> msgQueue;
	//TODO changer la structure pour supprimer les id trop anciens
	static final HashMap<String, Connexion> msgTable = new HashMap<String, Connexion>();
	static final HashMap<String, Connexion> myPingTable = new HashMap<String, Connexion>();
	static final HashMap<String, Connexion> myQueryTable = new HashMap<String, Connexion>();
	
	
	public synchronized static void modifyStatus(String s){
		status=s;
	}
	
	public static boolean addFriend(Socket s){
		try {
			Connexion c = new Connexion(s,msgQueue);
			addConnexion(c);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	public static boolean addFriend(String IP, int port){
		try {
			Connexion c = new Connexion(IP,port,msgQueue);
			addConnexion(c);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	public static void addConnexion(Connexion c){
		connexions.add(c);
		System.out.println("Nouvelle connection ajoutée!");
		MessagePING ping = new MessagePING(TTLMAX, userName, c.getIP(), myPort);
		myPingTable.put(ping.id, c);
		c.send(ping);
	}
	
	public static void sendToEveryone(Message m){
		for(Connexion c : connexions)
			c.send(m);
	}

	public static void main(String[] args){
		//initialisation des variables
		String friendIP="";
		int friendPort=0;
		connexions=new LinkedList<Connexion>();
		msgQueue = new LinkedBlockingQueue<Capsule>();
		System.out.println("Port initial : "+myPort);
		
		//Récupération des arguments
		switch (args.length) {
		case 0:
			System.out.println("Pas de nom d'utilisateur.... Aborting");
			System.exit(0);
			break;
		case 2:
			System.out.println("J'ai besoin d'un numéro de port...");
			System.exit(0);
			break;
		case 3:
			friendIP=args[1];
			friendPort=Integer.parseInt(args[2]);
		case 1:
			userName=args[0];
			break;
		default:
			System.out.println("Pourquoi tant d'arguments???");
			System.exit(0);
			break;
		}
		
		//Thread de traitement des messages
		new Thread("Processing") {
			public void run() {
				while(true){
					while(!msgQueue.isEmpty()){
						Capsule cap = msgQueue.remove();
						switch (cap.m.getType()) {
						case PING:
							MessagePING msgPING = (MessagePING)(cap.m);
							//vérification (l'a-t-on déjà recu)
							if(msgTable.containsKey(msgPING.id)){
								System.out.println("PING dropped");
								break;
							}
							//envoi du PONG
							MessagePONG pong = new MessagePONG(msgPING.id, userName, cap.c.getIP(), myPort);
							cap.c.send(pong);
							//envoi des PING, si nécessaire
							if(msgPING.TTL>0){
								msgPING.TTL=msgPING.TTL-1;
								for(Connexion c : connexions){
									if(c!=cap.c){//on ne renvoit pas à l'expéditeur
										c.send(msgPING);
										msgTable.put(msgPING.id, cap.c);
									}
								}
							}
							break;
							
						case QUERY:
							MessageQUERY msgQUERY = (MessageQUERY)(cap.m);
							//vérification (l'a-t-on déjà recu)
							if(msgTable.containsKey(msgQUERY.id)){
								System.out.println("QUERY dropped");
								break;
							}
							//s'il est pour moi
							if(msgQUERY.pseudonyme.equals(userName)){
								cap.c.send(new MessageREPLY(msgQUERY.id, msgQUERY.pseudonyme, status));
								break;
							}
							//envoi des QUERY, si nécessaire
							if(msgQUERY.TTL>0){
								msgQUERY.TTL=msgQUERY.TTL-1;
								msgTable.put(msgQUERY.id, cap.c);
								for(Connexion c : connexions){
									if(c!=cap.c){//on ne renvoit pas à l'expéditeur
										c.send(msgQUERY);
									}
								}
							}
							break;
						case PONG:
							MessagePONG msgPONG = (MessagePONG)(cap.m);
							//est-il pour moi?
							if(myPingTable.containsKey(msgPONG.id)){
								System.out.println("---\nVoisin découvert : \n"+
										"\t"+msgPONG.pseudonyme+
										"\t"+msgPONG.IP+
										"\t"+msgPONG.port);
								break;
							}
							//vérification, est-ce un message connu?
							if(!msgTable.containsKey(msgPONG.id)){
								System.out.println("PONG inconnu reçu");
								break;
							}
							//on fait suivre le message
							msgTable.get(msgPONG.id).send(msgPONG);
							break;
						case REPLY:
							MessageREPLY msgREPLY = (MessageREPLY)(cap.m);
							//est-il pour moi?
							if(myQueryTable.containsKey(msgREPLY.id)){
								System.out.println("---\nStatus obtenu : \n"+
										"\t"+msgREPLY.pseudonyme+
										"\t"+msgREPLY.status);
								break;
							}
							//vérification, est-ce un message connu?
							if(!msgTable.containsKey(msgREPLY.id)){
								System.out.println("REPLY inconnu reçu");
								break;
							}
							//on fait suivre le message
							msgTable.get(msgREPLY.id).send(msgREPLY);
							break;
						default:
							break;
						}
					}
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
		
			
		//Thread de suppresion des déconnectés
		new Thread("Déconnexions") {
			@Override
			public void run() {
				while(true){
					LinkedList<Connexion> tmp = new LinkedList<Connexion>();
					for(Connexion c : connexions){
						if(!c.getStatus())
							tmp.add(c);
					}
					for(Connexion c : tmp)
						connexions.remove(c);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		//Thread d'ajouts de voisins
		new Thread("Connexions"){
			public void run(){
				ServerSocket server=null;
				boolean serverEstablished=false;
				while(!serverEstablished)
					try {
						server = new ServerSocket(myPort);
						serverEstablished=true;
						System.out.println("Serveur créé sur "+myPort);
					} catch (Exception e1) {
						System.err.println("Impossible de créer le serveur sur "+myPort);
						myPort++;
					}
				while(true){
					Socket s;
					try {
						s = server.accept();
						System.out.println("Tentative de connection reçue!");
						addFriend(s);
					} catch (IOException e) {
						System.err.println("CONNEXIONS: impossible d'accepter la connexion");
					}
					
				}
			}
		}.start();
		
		//Si j'ai donné un ami au début, je me connecte dessus
		if(!friendIP.equals("")){
			addFriend(friendIP, friendPort);
		}
		
		//Boucle principale, écriture clavier
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			try {
				String s= br.readLine();
				if(s.equals("STATUS")){
					System.out.println("Quel est votre status?");
					String reply = br.readLine();
					modifyStatus(reply);
				}else if(s.equals("QUERY")){
					System.out.println("Quel est l'utilisateur que vous voulez interroger?");
					String user = br.readLine();
					sendQuery(user);
				}else if(s.equals("EXIT"))
					break;
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Erreur de lecture entrée système...");
			}
		}
		System.out.println("Au revoir");
		
	}

	public static void sendQuery(String user) {
		MessageQUERY m = new MessageQUERY(4, user);
		myQueryTable.put(m.id, null);
		sendToEveryone(m);
	}
}
