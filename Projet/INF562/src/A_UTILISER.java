
public class A_UTILISER {

	//pour calculer la meilleure transformation entre deux formes
	public static void matching(){
		
		
		CourbureEstimator.courbure_integrale_mode= false;
		TrouveMeilleureTransformation.test1("torus5.off","torus5_2.off");
		
	}
	
	
	
	public static void main(String[] args) {
		matching();

	}

}
