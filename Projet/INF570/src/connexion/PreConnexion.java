package connexion;

import gui.Out;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sharing.SharingManager;


public class PreConnexion {
	private boolean closing;
	private Socket s;
	private String id;
	private boolean isConnected;
	private BufferedReader br;
	private PrintWriter pw;

	public PreConnexion(Socket socket, String fileName, long fileIndex) {
		init(socket,fileName,fileIndex,false);
	}

	PreConnexion(Socket socket, final boolean isServer){
		init(socket,"",0,isServer);
	}

	private void init(Socket socket, String fileName, final long fileIndex,final boolean isServer){
		s = socket;
		closing=false;
		isConnected=false;
		id=s.getInetAddress().getHostAddress()+":"+s.getPort();
		br=null;
		pw=null;
		ConnexionManager.addPreConnexion(this);
		final String name=new String(fileName);
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
							Pattern p = Pattern.compile("^GET /get/[0-9]+/[a-zA-Z_0-9.-]+/ HTTP/1.0");
							Matcher m = p.matcher(str);
							if(m.matches()){
								if(br.readLine().equals("User-Agent: Gnutella/0.4")){
									if(br.readLine().equals("Range: bytes=0-")){
										if(br.readLine().equals("Connection: Keep-Alive")){
											if(br.readLine().equals("")){
												StringTokenizer tokenizer = new StringTokenizer(str.substring(9),"/");
												int indexAsked = Integer.parseInt(tokenizer.nextToken());
												String nameAsked = tokenizer.nextToken();
												File fileAsked = SharingManager.getFileFromId(indexAsked);
												if(fileAsked.getName().equals(nameAsked)){
													Out.println("Réception d'une requête pour le fichier "+nameAsked);
													pw.print("HTTP/1.0 200 OK\r\n" +
															"Server: Gnutella/0.4\r\n" +
															"Content-Type: application/binary\r\n" +
															"Content-Length: "+fileAsked.length()+"\r\n" +
													"\r\n");
													pw.flush();
													new TransferConnexion(s, indexAsked, fileAsked);
													isConnected=true;
												}
											}
										}
									}
								}
							}
						}
					}else{//cote client
						if(name.equals("")){//connexion classique
							pw.print("GNUTELLA CONNECT/0.4\n\n");
							pw.flush();
							if(br.readLine().equals("GNUTELLA OK"))
								if(br.readLine().equals("")){
									new NeighbourConnexion(s);
									isConnected=true;
								}
						}else{//connexion de téléchargement
							String msg="GET /get/"+fileIndex+"/"+name+"/ HTTP/1.0\r\n" +
							"User-Agent: Gnutella/0.4\r\n" +
							"Range: bytes=0-\r\n" +
							"Connection: Keep-Alive\r\n" +
					"\r\n";
							pw.print(msg);
							pw.flush();
							if(br.readLine().equals("HTTP/1.0 200 OK")){
								if(br.readLine().equals("Server: Gnutella/0.4")){
									if(br.readLine().equals("Content-Type: application/binary")){
										String str=br.readLine();
										Pattern p = Pattern.compile("^Content-Length: [0-9]+");
										Matcher m = p.matcher(str);
										if(m.matches()){
											if(br.readLine().equals("")){
												int size=Integer.parseInt(str.substring(16));
												Out.println("Requête pour le fichier "+name+" acceptée!");
												new TransferConnexion(s, name, fileIndex, size);
												isConnected=true;
											}
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
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
