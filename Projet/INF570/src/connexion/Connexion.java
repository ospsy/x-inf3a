package connexion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

import sharing.SharingManager;
import message.*;
/**
 * Chaque connexion contient une file de messages à envoyer, un thread chargé de l'envoi des messages
 * et un thread chargé de la lecture et du traitement des messages reçus
 * @author Malik
 *
 */
public class Connexion {
	private boolean isConnected;
	private Socket s;
	private LinkedList<Message> toSend;
	//private LinkedList<Message> received; // sans intérêt pour l'instant
	private MessageReader messageReader;
	private MessageSender messageSender;

	/**
	 * Crée une nouvelle connexion
	 * @param s la socket sur laquelle la connexion a lieu
	 * @param isServer TRUE si on a recu la connexion, FALSE si on l'a créée
	 */
	Connexion(Socket socket, final boolean isServer){
		s = socket;
		toSend = new LinkedList<Message>();
		//received = new LinkedList<Message>(); // sans intérêt pour l'instant
		isConnected=false;
		new Thread(){
			public void run() {
				BufferedReader br=null;
				PrintWriter pw=null;
				try {
					br=new BufferedReader(new InputStreamReader(s.getInputStream()));
					pw=new PrintWriter(s.getOutputStream());
					if(isServer){//cote serveur
						if(!br.readLine().equals("GNUTELLA CONNECT/0.4"))
							if(!br.readLine().equals("")){
								pw.print("GNUTELLA OK\n\n");
								init();
							}
					}else{//cote client
						pw.print("GNUTELLA CONNECT/0.4\n\n");
						if(!br.readLine().equals("GNUTELLA OK\n\n"))
							if(!br.readLine().equals("")){
								init();
							}
					}
				} catch (IOException e) {
					close();
				}
				if(br!=null)
					try {
						br.close();
					} catch (IOException e) {}
				if(pw!=null) pw.close();
			}
		};
	}

	/**
	 * Fonction appelée après la réception des messages de confirmation, qui
	 * permet de finaliser la connexion
	 */
	private void init(){
		try{
			messageReader = new MessageReader(this, s.getInputStream());
			messageSender = new MessageSender(this, s.getOutputStream());
			isConnected=true;
			ConnexionManager.confirmConnexion(this);
		}catch(Exception e){
			close();
		}
	}

	/**
	 * Ajoute m à la file d'envoi : cette fonction est donc non bloquante.
	 * @param m Le message à ajouter à la file d'envoi
	 */
	public synchronized void send(Message m) {
		toSend.addFirst(m);
	}

	/**
	 * Retire et renvoie le message le plus ancien de la file d'envoi de messages.
	 * @return Le message le plus ancien de la file d'envoi ou null si la file est vide.
	 */
	public synchronized Message getMessageToSend() {
		if(toSend.isEmpty()) return null;
		return toSend.removeLast();
	}

	/**
	 * Ferme la connexion : appel les fonctions de fermeture des thread de lecture et d'envoi de messages.
	 */
	public void close() {
		if(messageReader!=null)
			messageReader.close();
		if(messageSender!=null)
			messageSender.close();
		if(isConnected){
			ConnexionManager.remove(this);
		}else{
			ConnexionManager.removePreConnexion(this);
		}
	}

	/**
	 * Traitement d'un message.
	 * @param m Message à traiter
	 */
	public void processMsg(Message m) { 
		MessageHeader h=m.getHeader();
		switch(m.getHeader().getMessageType()){
		case PING:
			System.out.println("PING recu");
			//TODO vérifier le TTL du pong à envoyer
			send(new Pong(h.getMessageID(), h.getHops(), h.getTTL(), ConnexionManager.getPort(), ConnexionManager.getIP(), SharingManager.getNumberOfSharedFiles(), SharingManager.getSharedFilesSize()));
			if(h.getTTL()>0){
				m.decreaseTTL();
				ConnexionManager.sendAll(m, this);
			}
			break;
		case PONG:
			System.out.println("PONG recu");
			break;
		case QUERY:
			System.out.println("PING recu");
			//TODO envoyer un queryHIT
			//send(new Pong(h.getMessageID(), h.getHops(), h.getTTL(), ConnexionManager.getPort(), ConnexionManager.getIP(), SharingManager.getNumberOfSharedFiles(), SharingManager.getSharedFilesSize()));
			if(h.getTTL()>0){
				m.decreaseTTL();
				ConnexionManager.sendAll(m, this);
			}
			break;
		}
	}

}
