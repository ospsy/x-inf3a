package connexion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;


public class PreConnexion {
	private boolean closing;
	private Socket s;
	private String id;
	private boolean isConnected;
	private BufferedReader br;
	private PrintWriter pw;
	
	public PreConnexion(Socket socket, String fileName, int fileIndex) {
		init(socket,fileName,fileIndex,false);
	}

	PreConnexion(Socket socket, final boolean isServer){
		init(socket,"",0,isServer);
	}
	
	private void init(Socket socket, String fileName, int fileIndex,final boolean isServer){
		s = socket;
		closing=false;
		isConnected=false;
		id=s.getInetAddress().getHostAddress()+":"+s.getPort();
		br=null;
		pw=null;
		ConnexionManager.addPreConnexion(this);
		new Thread("preConnecting-"+id){
			public void run() {
				try {
					br=new BufferedReader(new InputStreamReader(s.getInputStream()));
					pw=new PrintWriter(s.getOutputStream());
					if(isServer){//cote serveur
						String str=br.readLine();
						if(str.equals("GNUTELLA CONNECT/0.4")){
							if(br.readLine().equals("")){
								pw.print("GNUTELLA OK\n\n");
								pw.flush();
								new NeighbourConnexion(s);
								isConnected=true;
							}
						}else{
							Pattern p = Pattern.compile("^GET /get/[0-9]+/[a-zA-Z_0-9.-]+/ HTTP/1.0\r\n");


						}
					}else{//cote client
						pw.print("GNUTELLA CONNECT/0.4\n\n");
						pw.flush();
						if(br.readLine().equals("GNUTELLA OK"))
							if(br.readLine().equals("")){
								new NeighbourConnexion(s);
								isConnected=true;
							}
					}
				} catch (IOException e) {
				}
				close();
			}
		}.start();
	}

	public void close() {
		if(!closing){
			closing=true;
			if(!isConnected){
				if(pw!=null) pw.close();
				if(br!=null)
					try {
						br.close();
					} catch (IOException e) {}
			}
			ConnexionManager.removePreConnexion(this,isConnected);
		}
	}

	public String getId() {
		return id;
	}
}
