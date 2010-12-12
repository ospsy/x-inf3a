package connexion;

import gui.Out;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import sharing.SharingManager;

import link.Link;;

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
	private long fileIndex;
	private String fileName;
	private double status;
	private long size;
	private TransferConnexionType type;
	/**
	 * Création de la connexion d'envoi de fichier
	 * @param socket la socket de la connexion
	 * @param fileIndex l'index du fichier à envoyer
	 */
	TransferConnexion(Socket socket, long fileIndex, File file){
		super(socket);
		type=TransferConnexionType.OUT;
		this.fileIndex=fileIndex;
		this.fileName=file.getName();
		this.size=file.length();
		status=0;
		final File f=file;
		try{
			final OutputStream out = s.getOutputStream();
			Thread t=new Thread(){
				public void run() {
					try {
						Link.sendFile(f, out);
						Out.println("Envoi terminé!");
					} catch (IOException e) {
						Out.println("Problème sur l'envoi...");
					}
					close();
				}
			};
			isConnected=true;
			ConnexionManager.addConnexion(this);
			t.start();
		}catch(Exception e){
			System.err.println("Problème à la création de la DownloadConnexion...closing.");
			e.printStackTrace();
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
	TransferConnexion(Socket socket, String fileName, long fileIndex, long size){
		super(socket);
		type=TransferConnexionType.IN;
		this.fileIndex=fileIndex;
		this.fileName=fileName;
		this.size=size;
		status=0;
		try{
			final InputStream in = s.getInputStream();
			final String name=fileName;
			Thread t=new Thread(){
				public void run() {
					try {
						Link.receiveFile(name, in);
						Out.println("Téléchargement terminé!");
						SharingManager.update();
					} catch (Exception e) {
						Out.println("Problème sur le téléchargement...");
					}
					close();
				}
			};
			isConnected=true;
			ConnexionManager.addConnexion(this);
			t.start();
		}catch(Exception e){
			System.err.println("Problème à la création de la DownloadConnexion...closing.");
			close();
		}
	}

	
	public TransferConnexionType getType(){
		return type;
	}
	public long getFileIndex() {
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
