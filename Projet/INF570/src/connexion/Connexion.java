package connexion;

import java.net.Socket;


public abstract class Connexion {
	protected boolean isConnected;
	protected boolean closing;
	protected Socket s;
	protected String IP;
	protected int port;
	protected String id;

	
	
	Connexion(Socket socket){
		s=socket;
		IP=s.getInetAddress().getHostAddress();
		port=s.getPort();
		id=IP+":"+port;
	}
	

	public String getId(){
		return id;
	}
	public String getIP() {
		return IP;
	}
	public int getPort() {
		return port;
	}

	public abstract void close();

}
