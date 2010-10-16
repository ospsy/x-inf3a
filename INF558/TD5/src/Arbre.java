
public class Arbre {
	int indice;		//indice du mot représenté par ce noeud
	Arbre[] fils;	//tableau de 256 fils
	
	Arbre(int i){
		indice=i;
		fils=new Arbre[256];
	}
}
