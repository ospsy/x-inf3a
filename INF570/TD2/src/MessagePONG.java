import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


public class MessagePONG extends Message {
	String pseudonyme, IP, id;
	int port;

	public MessagePONG(String id, String pseudonyme, String IP, int port){
		type=Type.PONG;
		this.pseudonyme=pseudonyme;
		this.IP=IP;
		this.port=port;
		this.id=id;
	}
	
	public MessagePONG(BufferedReader br) throws IOException{
		type=Type.PONG;
		id=br.readLine();
		pseudonyme=br.readLine();
		IP=br.readLine();
		port=Integer.parseInt(br.readLine());
		if(!br.readLine().equals("END PONG"))
			throw new IllegalArgumentException("Ce n'est pas un message PONG");
	}
	
	@Override
	public void write(PrintWriter pw) {
		pw.println("PONG");
		pw.println(id);
		pw.println(pseudonyme);
		pw.println(IP);
		pw.println(port);
		pw.println("END PONG");
	}
	
	public String toString(){
		return type.toString()+'\n'+id+'\n'+pseudonyme+'\n'+IP+'\n'+port;
	}
	
}
