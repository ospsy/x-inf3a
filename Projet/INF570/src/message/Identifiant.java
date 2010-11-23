package message;

public class Identifiant {
	private short[] tab;

	protected Identifiant(short[] tab) {
		super();
		this.tab = tab;
	}
	
	public int hashCode(){
		
		return 0;
	}
	
	public boolean equals(Identifiant id){
		
		return false;
	}
	
	public String toString(){
		
		return Message.stringOfTab(tab);
	}

	protected short[] getData() {
		return tab;
	}
	
}
