package connexion;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import link.Link;
import message.Message;

/**
 * Classe MessageSender héritant de Thread, qui va chercher les messages de la connexion pour les envoyer
 * @author Benoit
 *
 */
public class MessageSender extends Thread {
	private Connexion connexion;
	private PrintWriter pw;
	private boolean closing;

	/**
	 * Constructeur du MessageSender.
	 * @param connexion la connexion où aller chercher les messages à envoyer
	 * @param s la socket sur laquelle écrire
	 * @throws IOException si la création du PrintWriter d'écriture a échoué
	 */
	public MessageSender(Connexion connexion, Socket s) throws IOException {
		super();
		closing=false;
		this.connexion=connexion;
		pw=new PrintWriter(s.getOutputStream());
		this.start();
	}

	/**
	 * Ferme le Thread d'écriture
	 * TODO à vérifier le comportement
	 */
	public void close(){
		closing=true;
		pw.close();
		System.out.println("Fermeture MessageSender");
	}

	@Override
	public void run() {
		while(!closing){
			Message m;
			while((m=connexion.getMessageToSend())!=null){
				Link.sendMessage(pw,m);
				pw.flush();
			}
			try {
				this.wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
