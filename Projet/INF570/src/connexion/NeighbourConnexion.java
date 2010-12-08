package connexion;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import message.Message;
import message.MessageHeader;
import message.Pong;
import message.Query;
import message.QueryHit;
import message.Result;
import sharing.SharingManager;
import config.Settings;
/**
 * Chaque connexion contient une file de messages à envoyer, un thread chargé de l'envoi des messages
 * et un thread chargé de la lecture et du traitement des messages reçus
 * @author Malik, Benoit
 *
 */
public class NeighbourConnexion {
	private boolean isConnected;
	private boolean closing;
	private Socket s;
	private LinkedList<Message> toSend;
	private MessageReader messageReader;
	private MessageSender messageSender;

	private String id;

	/**
	 * Crée une nouvelle connexion
	 * @param s la socket sur laquelle la connexion a lieu
	 * @param isServer TRUE si on a recu la connexion, FALSE si on l'a créée (c'est nous qui nous sommes connecté)
	 */
	NeighbourConnexion(Socket socket){
		s=socket;
		id=s.getInetAddress().getHostAddress()+":"+s.getPort();
		toSend = new LinkedList<Message>();
		try{
			messageSender = new MessageSender(this, s.getOutputStream());
			messageReader = new MessageReader(this, s.getInputStream());
			ConnexionManager.addConnexion(this);
			isConnected=true;
		}catch(Exception e){
			System.err.println("Problème à la création de la NeighbourConnexion...closing.");
			close();
		}
	}

	/**
	 * Un entier généré aléatoirement pour repérer les connexions (rien n'assure qu'il soit unique)
	 * @return l'identifiant de la connexion
	 */
	public String getId(){
		return id;
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
	public synchronized void close() {
		if(!closing){
			closing=true;
			System.out.println("Closing connexion-"+id);
			if(messageReader!=null)
				messageReader.close();
			if(messageSender!=null)
				messageSender.close();
			try {
				s.close();
			} catch (IOException e) {	}
			if(isConnected)
				ConnexionManager.removeConnexion(this);
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
			send(new Pong(h.getMessageID(), Settings.getMaxTTL(), 0, ConnexionManager.getPort(), ConnexionManager.getIP(), SharingManager.getNumberOfSharedFiles(), SharingManager.getSharedFilesSize()));
			if(h.getTTL()>0){
				m.decreaseTTL();
				ConnexionManager.sendAll(m, this);
			}
			break;
		case PONG:
		case QUERY_HIT:
			ConnexionManager.forward(m);
			break;
		case QUERY:
			System.out.println("QUERY recu");
			Query q = (Query)m;
			Result[] results=SharingManager.search(q.getCriteria());
			if(results.length>0 && q.getMinimumSpeed()<=Settings.getSpeed())
				send(new QueryHit(h.getMessageID(), Settings.getMaxTTL(), 0, ConnexionManager.getPort(), ConnexionManager.getIP(), Settings.getSpeed() ,results));
			if(h.getTTL()>0){//inondation
				m.decreaseTTL();
				ConnexionManager.sendAll(m, this);
			}
			break;
		}
	}

}
