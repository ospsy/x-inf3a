import link.PhysicalLayer;


public class Listen {
	public static void main(String[] args){
		PhysicalLayer pl = new PhysicalLayer();
		
		while(true){
			String s=pl.receive();
			if(s!=null) System.out.println(s);
		}
	}
}
