import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Coder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Source s = new Source_fr();
		Dico d = new Dico(s);
		
		if(args.length!=0){
			BufferedReader br=null;
			try {
				br = new BufferedReader(new FileReader(args[0]));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.err.println("Impossible de charger le fichier.");;
			}
			String texte="";
			String lineRead;
			try {
				while((lineRead=br.readLine())!=null){
					texte+=lineRead;
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Impossible de lire le fichier.");
			}
			
			System.out.println(d.coder(texte));
		}else System.out.println("Pas d'argument.");
	}

}
