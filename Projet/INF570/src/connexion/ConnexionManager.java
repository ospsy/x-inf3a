package connexion;

import java.util.HashMap;
import java.util.LinkedList;
import message.*;

/**
 * La classe ConnexionManager gère les relations entre les différentes Connexions,
 * comme l'envoi global, les déconnexions et les confirmations de connexions.
 * @see Connexion
 * @author Julien, Malik, Benoit X08
 *
 */
public class ConnexionManager{
	static private HashMap<Connexion, Connexion> connexions;
	static private HashMap<Connexion, Connexion> preConnexions;
	static private LinkedList<Message> toSend;
	static private ServerThread thread;

	
	static public void init() {
		connexions = new HashMap<Connexion, Connexion>();
		toSend = new LinkedList<Message>();
		preConnexions = new HashMap<Connexion, Connexion>();
		thread = new ServerThread();
		thread.start();
	}
	
	//TODO
	static public void close(){
		thread.close();
	}
	
	/**
	 * Enlève une connexion de la liste des connexions gérées
	 * @param c la connexion à enlever
	 */
	static public synchronized void remove(Connexion c){
		connexions.remove(c);
	}
	
	/**
	 * Ajoute une connexion à confirmer à la liste des preConnexions gérées
	 * @param preConnexion la connexion à confirmer
	 */
	static public synchronized void addConnexion(Connexion preConnexion){
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
	 * Extrait le premier message de la file d'envoi de manière synchronisée
	 * @return le message à envoyer
	 */
	static private synchronized Message getFirstToSend(){
		if(toSend.size()>0)
			return toSend.removeFirst();
		else 
			return null;
	}
	
	/**
	 * Confirme la connexion dans la liste globale
	 * Si la preConnexion n'existe pas, affiche un message d'erreur
	 * @param c la connexion à confirmer
	 */
	static public synchronized void confirmConnexion(Connexion c){
		if(preConnexions.remove(c)!=null){
			connexions.put(c, c);
		}else{//si la connexion n'était pas dans la liste de preConnexion
			System.err.println("confirmConnexion : la connexion n'était pas à confirmer...");
		}
	}
	
	/**
	 * Retire une preConnexion, si par exemple le protocole de confirmation a échoué
	 * @param c la preConnexion a retirer
	 */
	static public synchronized void removePreConnexion(Connexion c){
		preConnexions.remove(c);
	}
}


//TODO
class ServerThread extends Thread{
	
	public ServerThread(){
		
	}
	
	
	public void close() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}
}
