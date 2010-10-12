import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


public class MessageREPLY extends Message {
	String pseudonyme, status, id;

	public MessageREPLY(String id, String pseudonyme, String status){
		type=Type.REPLY;
		this.pseudonyme=pseudonyme;
		this.id=id;
		this.status=status;
	}
	
	public MessageREPLY(BufferedReader br) throws IOException{
		type=Type.REPLY;
		id=br.readLine();
		pseudonyme=br.readLine();
		status=br.readLine();
		if(!br.readLine().equals("END REPLY"))
			throw new IllegalArgumentException("Ce n'est pas un message REPLY");
	}
	
	@Override
	public void write(PrintWriter pw) {
		pw.println("REPLY");
		pw.println(id);
		pw.println(pseudonyme);
		pw.println(status);
		pw.println("END REPLY");
	}
	
}
