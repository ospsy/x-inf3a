import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocket server=null;
		try {
		    server = new ServerSocket(8000);
		} catch (IOException e) {
		    System.out.println("Could not listen on port: 8000");
		    System.out.println(e.getMessage());
		    System.exit(-1);
		}
		
		Socket clientSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		while(true){
			try {
			    clientSocket = server.accept();
			    out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String inputLine, outputLine;
				String directoryName;
				if((directoryName = in.readLine()) != null){
					File d = new File(directoryName);
					if(d.isDirectory()){
						out.println("Inspection de "+directoryName+" :");
						File[] listFile=d.listFiles();
						for(File f : listFile){
							out.println(f.getName());
						}
						out.println("Fin de l'inspection de "+directoryName);
					}else{
						out.println("Ce n'est pas un répertoire.");
					}
				}
				/* Version réception fichier
				 * 
				String fileName;
				if((fileName = in.readLine()) != null){
					File f = new File(fileName);
					out = new PrintWriter(f);
					System.out.println("Début d'écriture dans "+fileName);
					while((inputLine = in.readLine()) != null){
						out.println(inputLine);
						System.out.println("Ecriture dans "+fileName+" : "+inputLine);
					}
					System.out.println("Fin d'écriture dans "+fileName);		
				}*/
				out.close();
				in.close();	
				
			} catch (IOException e) {
			    System.out.println("Accept failed: 8000");
			    System.exit(-1);
			}
		}
		
	}

}
