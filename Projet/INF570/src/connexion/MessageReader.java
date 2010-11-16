package connexion;

import java.io.IOException;
import java.io.InputStream;

import link.Link;
import message.Message;

/**
 * Classe MessageReader héritant de Thread, qui lit en boucle les messages et les redirige vers la Connexion.
 * @author Benoit
 *
 */
public class MessageReader extends Thread {
	private Connexion connexion;
	private InputStream in;
	private boolean closing;
	
	/**
	 * Constructeur du MessageReader.
	 * Le constructeur lance le start() du Thread automatiquement.
	 * @param connexion la connexion vers qui rediriger les msgs
	 * @param in le flux d'entrée à écouter
	 * @throws IOException si la création du Reader d'écoute a échoué
	 */
	public MessageReader(Connexion connexion, InputStream in){
		super("MessageReader-"+connexion.getId());
		closing=false;
		this.connexion=connexion;
		this.in=in;
		this.start();
	}
	
	/**
	 * Ferme le Thread d'écoute
	 * TODO à vérifier le comportement
	 */
	public void close(){
		if(!closing){
			System.out.println("Closing MessageReader-"+connexion.getId());
			closing=true;
			try {
				in.close();
			} catch (IOException e) {
				System.err.println("MessageReader : erreur à close().");
				e.printStackTrace();
			}
			connexion.close();
		}
	}
	
	@Override
	public void run() {
		while(!closing){
			try{
				Message m=Link.readMessage(in);
				connexion.processMsg(m);
			}catch(Exception e){
				close();
			}
			
		}
	}

}
