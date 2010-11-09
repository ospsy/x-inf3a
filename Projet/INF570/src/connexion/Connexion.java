package connexion;

import java.net.Socket;
import java.util.LinkedList;
import message.*;

public class Connexion {
	
	private Socket s;
	private LinkedList<Message> toSend;
	private LinkedList<Message> received;
	
	public void init(){};
	public synchronized void send(Message m){};
	public void close(){};
	
}
