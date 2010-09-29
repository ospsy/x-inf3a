import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		if(args.length==0){
			System.out.println("Et le nom du fichier alors???");
			System.exit(-1);
		}
		String fileName = args[0];
		try{
			socket = new Socket("localhost",8000);
			out = new PrintWriter(socket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch (Exception e) {
			System.out.println("Problème avec la socket.");
			System.out.println(e.getMessage());
		}
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		String input;
		File f = new File(fileName);
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		out.println(f.getName());
		while((input = fileReader.readLine()) != null){
			out.println(input);
			System.out.println("Ecriture de : "+input);
		}

	}

}
