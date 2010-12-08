package connexion;

import java.io.IOException;
import java.net.Socket;
import sharing.SharingManager;
import config.Settings;

enum DownloadConnexionType{
	IN,OUT
}

/**
 * Chaque connexion contient une file de messages à envoyer, un thread chargé de l'envoi des messages
 * et un thread chargé de la lecture et du traitement des messages reçus
 * @author Malik, Benoit
 *
 */
public class DownloadConnexion {
	private boolean isConnected;
	private boolean closing;
	private DownloadConnexionType type;
	private Socket s;

	private String id;

	/**
	 * Création de la connexion d'envoi de fichier
	 * @param socket la socket de la connexion
	 * @param fileIndex l'index du fichier à envoyer
	 */
	DownloadConnexion(Socket socket, int fileIndex){
		s=socket;
		type=DownloadConnexionType.OUT;
		id=s.getInetAddress().getHostAddress()+":"+s.getPort();
		try{
			//TODO création de l'objet d'envoi
			isConnected=true;
			ConnexionManager.addConnexion(this);
		}catch(Exception e){
			System.err.println("Problème à la création de la DownloadConnexion...closing.");
			close();
		}
	}
	
	/**
	 * Création de la connexion de réception du fichier
	 * @param socket la socket de la connexion
	 * @param fileName le nom du fichier à recevoir
	 * @param size la taille du fichier
	 */
	DownloadConnexion(Socket socket, String fileName, int size){
		s=socket;
		type=DownloadConnexionType.IN;
		id=s.getInetAddress().getHostAddress()+":"+s.getPort();
		try{
			//TODO création de l'objet de réception
			isConnected=true;
			ConnexionManager.addConnexion(this);
		}catch(Exception e){
			System.err.println("Problème à la création de la DownloadConnexion...closing.");
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
	
	public DownloadConnexionType getType(){
		return type;
	}


	/**
	 * Ferme la connexion : appel les fonctions de fermeture des thread de lecture et d'envoi de messages.
	 */
	public synchronized void close() {
		if(!closing){
			closing=true;
			System.out.println("Closing DownloadConnexion-"+id);
			/*if(messageReader!=null)
				messageReader.close();
			if(messageSender!=null)
				messageSender.close();*/
			try {
				s.close();
			} catch (IOException e) {	}
			if(isConnected)
				ConnexionManager.removeConnexion(this);
		}
	}

}
