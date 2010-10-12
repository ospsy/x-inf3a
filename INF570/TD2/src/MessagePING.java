import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


public class MessagePING extends Message {
	String pseudonyme, IP, id;
	int TTL, port;

	public MessagePING(int TTL, String pseudonyme, String IP, int port){
		type=Type.PING;
		this.pseudonyme=pseudonyme;
		this.IP=IP;
		this.TTL=TTL;
		this.port=port;
		this.id=pseudonyme+":"+IP+":"+(int)(Math.random()*655536);
	}
	
	public MessagePING(BufferedReader br) throws IOException{
		type=Type.PING;
		TTL=Integer.parseInt(br.readLine());
		id=br.readLine();
		pseudonyme=br.readLine();
		IP=br.readLine();
		port=Integer.parseInt(br.readLine());
		if(!br.readLine().equals("END PING"))
			throw new IllegalArgumentException("Ce n'est pas un message PING");
	}
	
	@Override
	public void write(PrintWriter pw) {
		pw.println("PING");
		pw.println(TTL);
		pw.println(id);
		pw.println(pseudonyme);
		pw.println(IP);
		pw.println(port);
		pw.println("END PING");
	}
	
	public String toString(){
		return type.toString()+'\n'+id+'\n'+pseudonyme+'\n'+IP+'\n'+port;
	}
	
}
