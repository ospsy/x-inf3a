package message;

public class Identifiant {
	private short[] tab;

	protected Identifiant(short[] tab) {
		this.tab = tab;
	}
	
	public int hashCode(){
		
		return this.toString().hashCode();
	}
	
	public boolean equals(Identifiant id){
		boolean b = true;
		for (int i = 0; i < tab.length; i++) {
			b = b && (id.getData()[i]==tab[i]);
		}
		return b;
	}
	
	public String toString(){
		
		return Message.stringOfTab(tab);
	}

	protected short[] getData() {
		return tab;
	}
	
}
