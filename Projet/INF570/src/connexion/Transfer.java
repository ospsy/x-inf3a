package connexion;

/**
 * Cette classe n'est qu'une petite classe pour indexer les voisins détectés avec les PONG.
 * Les champs accessibles sont :
 * numberOfKilobytesShared
 * numberOfSharedFiles
 * IP
 * port
 * distance;
 * @author Benoit
 *
 */
public class Transfer {
	private String IP;
	private int port;
	private int fileIndex;
	private String fileName;
	private double status;
	private int size;
	private TransferConnexionType type;
	
	
	Transfer(TransferConnexion c){
		IP=c.getIP();
		port=c.getPort();
		fileIndex=c.getFileIndex();
		fileName=c.getFileName();
		fileIndex=c.getFileIndex();
		status=c.getStatus();
		size=c.getSize();
		type=c.getType();
	}
	
	public String getIP() {
		return IP;
	}
	public int getPort() {
		return port;
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
	public int getSize() {
		return size;
	}

	public TransferConnexionType getType() {
		return type;
	}



	@Override
	public String toString() {
		String s=IP;
		s+=":"+port;
		s+=", dist="+type;
		s+=" | "+fileName+" "+fileIndex+", "+size+"o "+status+"%";
		return s;
	}
}
