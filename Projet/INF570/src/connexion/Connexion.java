package connexion;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import message.*;
/**
 * Chaque connexion contient une file de messages à envoyer, un thread chargé de l'envoi des messages
 * et un thread chargé de la lecture et du traitement des messages reçus
 * @author Malik
 *
 */
public class Connexion {
	
	private Socket s;
	private LinkedList<Message> toSend;
	private LinkedList<Message> received; // sans intérêt pour l'instant
	private MessageReader messageReader;
	private MessageSender messageSender;
	
	Connexion(Socket s) throws IOException {
		this.s = s;
		toSend = new LinkedList<Message>();
		received = new LinkedList<Message>(); // sans intérêt pour l'instant
		messageReader = new MessageReader(this, s);
		messageSender = new MessageSender(this, s);
		messageReader.start();
		messageSender.start();
	}
	public void init(){};
	
	/**
	 * Ajoute m à la file d'envoi : cette fonction est donc non bloquante.
	 * @param m Le message à ajouter à la file d'envoi
	 */
	public synchronized void send(Message m) {
		toSend.addFirst(m);
	};
	/**
	 * Retire et renvoie le message le plus ancien de la file d'envoi de messages.
	 * @return Le message le plus ancien de la file d'envoi
	 */
	public synchronized Message getMessageToSend() {
		return toSend.removeLast();
	}
	/**
	 * Ferme la connexion : appel les fonctions de fermeture des thread de lecture et d'envoi de messages.
	 */
	public void close() {
		messageReader.close();
		messageSender.close();
	};
	/**
	 * Traitement d'un message.
	 * @param m Message à traiter
	 */
	public void processMsg(Message m) { 
		
	}
	
}
