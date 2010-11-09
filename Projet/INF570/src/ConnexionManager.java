import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * La classe ConnexionManager gère les relations entre les différentes Connexions,
 * comme l'envoi global, les déconnexions et les confirmations de connexions.
 * @see Connexion
 * @author Julien, Malik, Benoit X08
 *
 */
public class ConnexionManager extends Thread{
	private HashMap<Connexion, Connexion> connexions;
	private HashMap<Connexion, Connexion> preConnexions;
	private LinkedList<Message> toSend;

	

	public ConnexionManager() {
		super();
		this.connexions = new HashMap<Connexion, Connexion>();
		this.toSend = new LinkedList<Message>();
		this.preConnexions = new HashMap<Connexion, Connexion>();
	}
	
	/**
	 * Enlève une connexion de la liste des connexions gérées
	 * @param c la connexion à enlever
	 */
	public synchronized void remove(Connexion c){
		connexions.remove(c);
	}
	
	/**
	 * Ajoute une connexion à confirmer à la liste des preConnexions gérées
	 * @param preConnexion la connexion à confirmer
	 */
	public synchronized void addConnexion(Connexion preConnexion){
		preConnexions.put(preConnexion, preConnexion);
	}

	/**
	 * Envoit un message à toutes les connexions gérées sauf 
	 * @param m le message à envoyer
	 * @param exclude la connexion à exclure
	 */
	public synchronized void sendAll(Message m, Connexion exclude){
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
	private synchronized Message getFirstToSend(){
		if(toSend.size()>0)
			return toSend.removeFirst();
		else 
			return null;
	}
	
	/**
	 * Confirme la connexion dans la liste globale
	 * @param c la connexion à confirmer
	 */
	private synchronized void confirmConnexion(Connexion c){
		if(preConnexions.remove(c)!=null){
			connexions.put(c, c);
		}else{//si la connexion n'était pas dans la liste de preConnexion
			System.err.println("confirmConnexion : la connexion n'était pas à confirmer...");
		}
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}
	
	

}
