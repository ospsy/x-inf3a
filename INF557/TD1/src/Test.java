
public class Test {
	public static void main(String[] args){
		PhysicalLayer pl = new PhysicalLayer();
		
		while(true){
			String s;
			if((s=pl.receive())!=null){
				System.out.println(s);
			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.out.println("Je peux pas dormir...");
				}
			}
		}
	}
}
