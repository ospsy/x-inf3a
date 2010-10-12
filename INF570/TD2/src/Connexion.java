import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;


public class Connexion {
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pw;
	private Queue<Capsule> list;
	private boolean status;
	private String IP;
	
	public boolean getStatus(){
		return status;
	}
	
	public String getIP(){
		return socket.getLocalAddress().getHostAddress();
	}
	
	public Connexion(Socket sock,Queue<Capsule> l) throws IOException{
		status=true;
		socket=sock;
		list=l;
		IP=socket.getInetAddress().getHostAddress();
		pw= new PrintWriter(socket.getOutputStream(),true);
		br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
		startListeningThread();
	}
	
	public Connexion(String IP, int port, Queue<Capsule> l) throws IOException{
		status=true;
		socket=new Socket(IP,port);
		this.IP=IP;
		if(!socket.isConnected())
			throw new IOException();
		this.list=l;
		pw= new PrintWriter(socket.getOutputStream(),true);
		br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
		startListeningThread();
	}
	
	private void startListeningThread(){
		final Connexion me=this;
		new Thread() {
			@Override
			public void run() {
				while(status){
					try {
						Message m = readOneMessage();
						list.add(new Capsule(m,me));
						System.out.println("-------------------\n" +
								"Réception d'un message ->"+IP+" : "+m.getType());
						System.out.println(m);
					} catch (IOException e) {
						System.out.println("Détection de déconnexion..."+IP);
						status=false;
					}
				}
			}
		}.start();
	}
	
	public synchronized void send(Message m){
		System.out.println("-------------------\n" +
				"Envoi d'un message ->"+IP+" : "+m.getType());
		System.out.println(m.toString());
		m.write(pw);
	}
	
	private Message readOneMessage() throws IOException{
		while(true){
			String s = br.readLine();
			if(s==null) throw new IOException();
			if(s.equals("PING")) return new MessagePING(br);
			if(s.equals("PONG")) return new MessagePONG(br);
			if(s.equals("QUERY")) return new MessageQUERY(br);
			if(s.equals("REPLY")) return new MessageREPLY(br);
			System.out.println("ERREUR ReadOneMessage()");
		}
	}
}
