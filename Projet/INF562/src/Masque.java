import Jama.Matrix;


public class Masque {
	public static double gauss(double sig, double norme){
		return Math.exp(-norme*norme/(2*sig*sig));
	}
	
	public static Matrix MasqueGauss(double sig, int taille){
		Matrix res = new Matrix(taille, taille);
		double tot = 0;
		for (int i = 0;i<taille;i++){
			for(int j= 0;j<taille;j++){
				double tmp = gauss(sig,Math.sqrt(((taille/2-i)*(taille/2-i)+(taille/2-j)*(taille/2-j))));
				tot= tot + tmp;
				res.set(i,j,tmp);
				
			}
		}
		for (int i = 0;i<taille;i++){
			for(int j= 0;j<taille;j++){
				res.set(i,j,res.get(i,j)/tot);
			}
		}
		
		
		return res;
	}
}
