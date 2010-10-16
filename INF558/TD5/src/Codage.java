import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Codage {
	static Arbre racine=new Arbre(-1);
	static int nbBits=0;
	static int indiceMax=0;
	static int nbIndicesRestants=0;

	static void init() {
		racine.indice=-1;
		Arbre[] fils = new Arbre[256];
		for(int i=0;i<256;i++){
			fils[i] = new Arbre(i);
		}
		racine.fils=fils;
		nbBits=8;
		indiceMax=255;
		nbIndicesRestants=0;
	}

	static void codage(FileInputStream fichier) {
		int c=0;
		Arbre encours=racine;

		while(true){
			try {
				c= fichier.read();
			} catch (IOException e) {
				System.err.println("Erreur de lecture fichier");
				System.exit(0);
			}
			if(c==-1){//fin du texte
				Mot.ecrireEntier(encours.indice,nbBits);
				break;
			}

			if(encours.fils[c]!=null){
				encours=encours.fils[c];
			}else{
				Mot.ecrireEntier(encours.indice,nbBits);
				indiceMax++;
				if(nbIndicesRestants==0){
					nbBits++;
					nbIndicesRestants=indiceMax;
				}
				nbIndicesRestants--;
				encours.fils[c]=new Arbre(indiceMax);
				encours=racine.fils[c];
			}

		}
	}
}

class Decodage{
	static void decodage(FileInputStream in){
		Mot.initialise();
		int n=0;
		int last=-1;
		while(true){
			try {
				n= Mot.lireEntier(in, Mot.logtaille);
				System.out.println(Mot.logtaille);
			} catch (IOException e) {
				System.err.println("Erreur de lecture fichier");
				System.exit(0);
			}
			if(n==-1){//fin du texte
				break;
			}
			if(n<Mot.taille){
				if(last!=-1){
					Mot.ajouteMot(Mot.dico[last]+Mot.dico[n].charAt(0));
				}
			}else{//si je ne connais pas le mot c'est que c'était le précédent
				Mot.ajouteMot(Mot.dico[last]+Mot.dico[last].charAt(0));
			}
			last=n;
			System.out.print(Mot.dico[n]);
		}
	}
}

class Coder{
	public static void main(String[] args) throws FileNotFoundException{
		FileInputStream fichier = new FileInputStream(args[0]);
		Codage.init();
		Codage.codage(fichier);
	}
}

class Decoder{
	public static void main(String[] args) throws FileNotFoundException{
		FileInputStream fichier = new FileInputStream(args[0]);
		Decodage.decodage(fichier);
	}
}
