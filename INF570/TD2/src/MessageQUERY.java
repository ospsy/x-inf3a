import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


public class MessageQUERY extends Message {
	String pseudonyme, id;
	int TTL;

	public MessageQUERY(int TTL, String pseudonyme){
		type=Type.QUERY;
		this.pseudonyme=pseudonyme;
		this.TTL=TTL;
		this.id=pseudonyme+":"+(int)(Math.random()*655536);
	}
	
	public MessageQUERY(BufferedReader br) throws IOException{
		type=Type.QUERY;
		TTL=Integer.parseInt(br.readLine());
		id=br.readLine();
		pseudonyme=br.readLine();
		if(!br.readLine().equals("END QUERY"))
			throw new IllegalArgumentException("Ce n'est pas un message QUERY");
	}
	
	@Override
	public void write(PrintWriter pw) {
		pw.println("QUERY");
		pw.println(TTL);
		pw.println(id);
		pw.println(pseudonyme);
		pw.println("END QUERY");
	}
	
}
