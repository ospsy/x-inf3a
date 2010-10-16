import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

class Mot {
	static String [] dico;
	static int tailleTableau;
	static int taille = 0;
	static int logtaille = 0;
	final static int tailleParDefaut = 1000;
	final static int cardinalAlphabet = 256;

	static void ajouteMot(String m) {
		if (taille >= tailleTableau) {
			tailleTableau *= 2;
			String [] dicoBis = new String[tailleTableau];
			for (int i = 0; i < taille; ++i)
				dicoBis[i] = dico[i];
			dico = dicoBis;
		}
		dico[taille] = m;
		taille++;
		if (taille > (1 << logtaille))
			logtaille++;
	}

	static void initialise() {
		tailleTableau = tailleParDefaut;
		dico = new String[tailleTableau];
		for (int i = 0; i < cardinalAlphabet; ++i){
			ajouteMot("" + ((char) i));
		}
	}

	// ecrit un entier n sur logtaille bits
	static void ecrireEntier (int n, int logtaille ) {
		for (int i =0; i < logtaille ; ++i){
			System.out.print( n%2);
			n=n/2;
		}
	}

	// les n bits du fichier et construit l'entier correspondant
	static int lireEntier(FileInputStream fichier, int n) 
	throws IOException 
	{
		int l = 0, c;
		for (int i = 0; i < n; ++i) {
			do {
				c = fichier.read();
				if (c == -1)
					return c;
				c -= '0';		
			} while ((c != 0) && (c != 1));
			if (c == 1)
				l += (1 << i);
		}
		return l;
	}
}