import java.util.Collection;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;


public class Dico {
	ArbreHuffman arbre;
	
	class Char{
		char lettre;
		Char(char c){
			lettre=c;
		}
		public boolean equals(Object o){
			return ((Char)o).lettre==lettre;
		}
		public int hashCode(){
			return (int)lettre;
		}
	}
	HashMap<Char, String> dico;
	
	public Dico(Source s){
		ArbreHuffman[] tabArbres = new ArbreHuffman[s.taille()];
		for(int i=0;i<s.taille();i++){
			tabArbres[i]=new Feuille(s.lettre[i], s.proba[i]);
		}
		int length=s.taille();
		while(length>2){
			length--;
			TriArbreHuffman.quickSort(tabArbres);
			ArbreHuffman[] tabArbres2 = new ArbreHuffman[length];
			for(int i =0;i<length-1;i++){
				tabArbres2[i]=tabArbres[i+2];
			}
			tabArbres2[length-1]=new Noeud(tabArbres[0],tabArbres[1]);
			tabArbres=tabArbres2;
		}
		if(length==2)
			arbre=new Noeud(tabArbres[0],tabArbres[1]);
		if(length==1)
			arbre=tabArbres[0];
		if(length==0)
			throw new IllegalArgumentException();
		
		//création du dico
		dico = new HashMap<Char, String>();
		creerDico(arbre, "");
	}
	
	private void creerDico(ArbreHuffman a,String s) {
		if(a instanceof Feuille){
			dico.put(new Char(((Feuille) a).lettre), s);
		}else{
			assert(a instanceof Noeud);
			creerDico(((Noeud) a).gauche,s+"0");
			creerDico(((Noeud) a).droite,s+"1");
		}
	}

	public void imprime(){
		System.out.println("%\tDico");
		for(Char c : dico.keySet()){
			System.out.println(c.lettre+"\t"+dico.get(c));
		}
	}
	
	public String coder(String s){
		char[] cc = s.toCharArray();
		String result="";
		for(int i =0;i<cc.length;i++){
			result+=dico.get(new Char(cc[i]));
		}
		return result;
	}

	public String decoder(char[] cs) {
		ArbreHuffman temp =arbre;
		String result="";
		for(int i=0;i<cs.length;i++){
			assert((cs[i]=='0' || cs[i]=='1') && temp instanceof Noeud);
			if(cs[i]=='0') temp=((Noeud)temp).gauche;
			else temp=((Noeud)temp).droite;
			if (temp instanceof Feuille){
				result+=((Feuille)temp).lettre;
				temp=arbre;
			}	
		}
		return result;
	}
}
