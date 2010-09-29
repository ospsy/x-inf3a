public class Feuille extends ArbreHuffman{
    /**
       La feuille contient comme information l'indice de la lettre dans la source.
     */
    char lettre;
    public Feuille(char c, double p){
	proba = p;
	lettre = c;
    }
    public void imprime(){
	System.out.print("["+this.lettre+";"+this.proba+"]");
    }
    
}