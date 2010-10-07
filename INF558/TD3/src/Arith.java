import java.io.*;

class Arith {
    static int MAXIMUM;
    static int MOITIE;
    static int QUART;
    static int taille;
    static int fini = 0;
    static int compteur = 0;
    static int buffer = 0;
    static int min, max;
    static int [] somme;
    static int [] proba = {
        1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1688, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1,
        10402, 377, 22, 1, 1, 1, 1, 662,
        2, 3, 1, 1, 1002, 552, 936, 2,
        1, 3, 1, 1, 1, 1, 1, 1,
        1, 3, 47, 123, 4, 2, 4, 131,
        1, 439, 81, 485, 157, 549, 25, 92,
        100, 317, 99, 1, 216, 189, 344, 296,
        120, 67, 403, 170, 199, 189, 85, 2,
        76, 113, 3, 1, 1, 1, 1, 1,
        1, 3493, 430, 1187, 1655, 6617, 452, 455,
        427, 3154, 250, 3, 2284, 1240, 3291, 2547,
        1102, 517, 2963, 3608, 3126, 3047, 813, 1,
        211, 150, 148, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 35, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 32, 1, 1, 1, 1,
        4, 1, 1, 1, 1, 1, 1, 1,
        5, 7, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1,
        213, 1, 39, 1, 1, 1, 1, 24,
        172, 682, 99, 1, 1, 1, 33, 5,
        1, 1, 1, 1, 29, 1, 1, 1,
        1, 16, 1, 19, 1, 1, 1, 1
    };

    static void init() {
	somme = new int[proba.length];
	int total = 0;
	// on multiplie par 4 pour garantir une amplitude > 0 pour
	// tous les intervalles
	for (int i = 0; i < proba.length; ++i) {
	    proba[i] *= 4;
	    somme[i] = total;
	    total += proba[i];
	}
	MAXIMUM = total;
	MOITIE = MAXIMUM / 2;
	QUART =  MAXIMUM / 4;
	min = 0;
	max = MAXIMUM;
	// la taille utile (en bits) pour representer MAXIMUM, donc
	// suffisante pour toute probabilite
	for (taille = 0; MAXIMUM > (1 << taille); ++taille);
    }

    // retourne 0, 1, 2 si l'intervalle a ete reduit, -1 sinon
    static int ajuster() {
	if (max <= MOITIE) {
	    min *= 2;
	    max *= 2;
	    return 0;
	}
	else if (min >= MOITIE) {
	    min = 2 * (min - MOITIE);
	    max = 2 * (max - MOITIE);
	    return 1;
	}
	else if ((min >= QUART) && ((max - QUART) <= MOITIE)) {
	    min = 2 * (min - QUART);
	    max = 2 * (max - QUART);
	    return 2;
	}
	else
	    return -1;
    }

    // lit les '0' et les '1' dans un flux, ignore les autres
    // lettres. Au dela de EOF, incremente le compteur fini (pour
    // decodage des derniers caracteres) et retourne 0.
    static int lire01(FileInputStream fichier) throws IOException {
	int c = fichier.read();
	if (c == -1) {
	    fini++;
	    return 0;
	}
	else if (c == '0')
	    return 0;
	else if (c == '1')
	    return 1;
	else
	    return lire01(fichier);
    }

    static int chercher(int x, int a, int b) {
	if (b - a <= 1)
	    return a;
	else {
	    int m = (a + b) / 2;
	    if (x < somme[m])
		return chercher(x, a, m);
	    else
		return chercher(x, m, b);
	}
    }

    static int chercher(int x) {
	return chercher(x, 0, 256);
    }

    static int lireLettre(FileInputStream fichier) throws IOException {
	// les "long" pour garder la precision tout en evitant les
	// problemes de depassement de capacite. A la fin on a un
	// "int"
	long delta = max - min;
	int c = chercher((int) ((((long) buffer - min) * MAXIMUM) / delta));
	// max mis a jour avant car on a besoin de min
	max = min + ((int) (((somme[c] + proba[c]) * delta) / MAXIMUM));
	if (max <= buffer) { // possible a cause des arrondis
	    ++c;
	    max = min + ((int) (((somme[c] + proba[c]) * delta) / MAXIMUM));
	}
	min = min + ((int) ((somme[c] * delta) / MAXIMUM));
	System.out.write(c);
	for (int l = ajuster(); l >= 0; l = ajuster()) {
	    int b = lire01(fichier);
	    int mask = (1 << taille) - 1;
	    if (l < 2) {
		buffer = ((buffer << 1) & mask) ^ b;
		compteur = 0;
	    }
	    else {
		buffer = ((buffer << 1) & mask) ^ (1 << (taille - 1)) ^ b;
		compteur++;
	    }
	}
	return c;
    }

    static void decoder(FileInputStream fichier) throws IOException {
	int c;
	// Initialisation de "buffer", qui contient TOUJOURS les
	// "taille" prochains bits du flux. Sa mise a jour est faite
	// dans lireLettre.
	for (int i = 0; i < taille; ++i)
	    buffer = buffer * 2 + lire01(fichier);

	do {
	    c = lireLettre(fichier);
	} while ((fini == 0) || (taille - fini >= compteur + 2));
    }
}

class Decoder {
    public static void main(String [] args) throws IOException {
	FileInputStream fichier = new FileInputStream(args[0]);

	Arith.init();
	Arith.decoder(fichier);
    }
}