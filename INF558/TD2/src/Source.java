abstract class Source{
    char[] lettre;
    double[] proba;
    int taille() { return lettre.length;}
    
    double entropy() {
    	double result=0;
    	for(int i=0;i<lettre.length;i++){
    		result+=proba[i]*Math.log(proba[i])/Math.log(2);
    	}
    	return -result;
    }
}