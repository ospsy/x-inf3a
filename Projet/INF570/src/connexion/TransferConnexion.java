package connexion;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import sharing.SharingManager;
import config.Settings;

enum TransferConnexionType{
	IN,OUT
}

/**
 * Chaque connexion contient une file de messages à envoyer, un thread chargé de l'envoi des messages
 * et un thread chargé de la lecture et du traitement des messages reçus
 * @author Malik, Benoit
 *
 */
public class TransferConnexion extends Connexion{
	private int fileIndex;
	private String fileName;
	private double status;
	private long size;
	private TransferConnexionType type;
	/**
	 * Création de la connexion d'envoi de fichier
	 * @param socket la socket de la connexion
	 * @param fileIndex l'index du fichier à envoyer
	 */
	TransferConnexion(Socket socket, int fileIndex, File file){
		super(socket);
		type=TransferConnexionType.OUT;
		this.fileIndex=fileIndex;
		this.fileName=file.getName();
		this.size=file.length();
		status=0;
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
	 * @param fileIndex index du fichier téléchargé (chez l'uploader)
	 * @param size la taille du fichier
	 */
	TransferConnexion(Socket socket, String fileName, int fileIndex, int size){
		super(socket);
		type=TransferConnexionType.IN;
		this.fileIndex=fileIndex;
		this.fileName=fileName;
		this.size=size;
		status=0;
		try{
			//TODO création de l'objet de réception
			isConnected=true;
			ConnexionManager.addConnexion(this);
		}catch(Exception e){
			System.err.println("Problème à la création de la DownloadConnexion...closing.");
			close();
		}
	}

	
	public TransferConnexionType getType(){
		return type;
	}
	public int getFileIndex() {
		return fileIndex;
	}
	public String getFileName() {
		return fileName;
	}
	public double getStatus() {
		return status;
	}
	public long getSize() {
		return size;
	}

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
