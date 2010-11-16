package connexion;

import java.io.IOException;
import java.io.OutputStream;

import link.Link;
import message.Message;

/**
 * Classe MessageSender héritant de Thread, qui va chercher les messages de la connexion pour les envoyer
 * @author Benoit
 *
 */
public class MessageSender extends Thread {
	private Connexion connexion;
	private OutputStream out;
	private boolean closing;

	/**
	 * Constructeur du MessageSender.
	 * @param connexion la connexion où aller chercher les messages à envoyer
	 * @param out le flux sur lequel écrire
	 * @throws IOException si la création du PrintWriter d'écriture a échoué
	 */
	public MessageSender(Connexion connexion, OutputStream out) throws IOException {
		super();
		closing=false;
		this.connexion=connexion;
		this.out=out;
		this.start();
	}

	/**
	 * Ferme le Thread d'écriture
	 * TODO à vérifier le comportement
	 */
	public void close(){
		closing=true;
		try {
			out.close();
		} catch (IOException e) {
			System.err.println("MessageSender : erreur à close().");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(!closing){
			Message m;
			while((m=connexion.getMessageToSend())!=null){
				Link.sendMessage(out,m);
				try {
					out.flush();
				} catch (IOException e) {}
			}
			try {
				this.wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
