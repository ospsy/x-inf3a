public class Noeud extends ArbreHuffman{
    ArbreHuffman gauche,droite;
    public Noeud(ArbreHuffman a1, ArbreHuffman a2){
	proba = a1.proba+a2.proba;
	gauche = a1;
	droite = a2;
    }
    public void imprime(){
	    System.out.print("(");
	    gauche.imprime();
	    droite.imprime();
	    System.out.print(")");
    }
}