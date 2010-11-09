package connexion;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import message.Message;

/**
 * Classe MessageReader héritant de Thread, qui lit en boucle les messages et les redirige vers la Connexion
 * @author Benoit
 *
 */
public class MessageReader extends Thread {
	private Connexion connexion;
	private Socket s;
	private BufferedReader br;
	private boolean closing;
	
	/**
	 * Constructeur du MessageReader.
	 * @param connexion la connexion vers qui rediriger les msgs
	 * @param s la socket sur laquelle écouter
	 * @throws IOException si la création du Reader d'écoute a échoué
	 */
	public MessageReader(Connexion connexion, Socket s) throws IOException {
		closing=false;
		this.connexion=connexion;
		this.s=s;
		br=new BufferedReader(new InputStreamReader(s.getInputStream()));
	}
	
	/**
	 * Ferme le Thread d'écoute
	 * TODO à vérifier le comportement
	 */
	public void close(){
		closing=true;
		try {
			br.close();
		} catch (IOException e) {
			System.err.println("MessageReader : erreur à close().");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(!closing){
			//TODO lire un msg
			Message m;
			connexion.processMsg(m);
		}
	}

}
