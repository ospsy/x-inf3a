

public class A_UTILISER {

	//pour calculer la meilleure transformation entre deux formes
	public static void matching(){
		CourbureEstimator.courbure_integrale_mode= false;//apparemment la courbure intégrale bug sur taubin 
		TrouveMeilleureTransformation.test1("torus5.off","torus5_2.off");//ces deux fichiers sont la même figure mais avec une rotation entre les deux
	}
	
	//évalue quantitativement la correspondance entre les maillages et affiche les courbures
	public static void evaluerSignature(){
		CourbureEstimator.courbure_integrale_mode= true;
		Matching.mode = Matching.ModeCourbure.TAUBIN;//TAUBIN ou GAUSS
		Matching.test("torus5.off","torus.off","skull.off");
	}
	
	
	public static void main(String[] args) {
		matching();
	//	evaluerSignature();

	}

}
