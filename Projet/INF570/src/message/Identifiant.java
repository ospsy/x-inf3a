package message;

public class Identifiant {
	private short[] tab;

	public Identifiant(short[] tab) {
		this.tab = tab;
	}

	public int hashCode(){

		return this.toString().hashCode();
	}

	public boolean equals(Object o){
		if(o instanceof Identifiant){
			Identifiant id = (Identifiant)o;
			boolean b = true;
			for (int i = 0; i < tab.length; i++) {
				b = b && (id.getData()[i]==tab[i]);
			}
			return b;
		}else{
			return false;
		}
	}

	public String toString(){

		return Message.stringOfTab(tab);
	}

	protected short[] getData() {
		return tab;
	}

}
