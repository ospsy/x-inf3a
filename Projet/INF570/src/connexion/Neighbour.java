package connexion;

import message.Message;
import message.Pong;
import message.TypeMessage;

public class Neighbour {
	long numberOfKilobytesShared;
	long numberOfSharedFiles;
	String IP;
	int port;
	int distance;
	
	Neighbour(Message m){
		if(m.getHeader().getMessageType()!=TypeMessage.PONG)
			throw new IllegalArgumentException("Tentative de créer un Neighbour avec un message qui n'est pas de type PONG");
		Pong p=(Pong)m;
		numberOfKilobytesShared=p.getNumberOfKilobytesShared();
		numberOfSharedFiles=p.getNumberOfSharedFiles();
		IP=p.getIp();
		port=p.getPort();
		distance=p.getHeader().getTTL()+1;
	}

	public int getDistance() {
		return distance;
	}

	public long getNumberOfKilobytesShared() {
		return numberOfKilobytesShared;
	}

	public long getNumberOfSharedFiles() {
		return numberOfSharedFiles;
	}

	public String getIP() {
		return IP;
	}

	public int getPort() {
		return port;
	}
}
