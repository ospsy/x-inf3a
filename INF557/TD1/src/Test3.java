
public class Test3 {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		PhysicalLayer pl = new PhysicalLayer();
	    String tentativeName = "SuperPanda"; // replace as per your chosing
	    SNACASD sd = new SNACASD(pl, 5, 3);
	    if (!sd.verifyUniqueness(tentativeName)) {
	      System.out.println(tentativeName + " is already in use, exiting");
	      System.exit(0);
	    }
	    while (true) {
	      String m = pl.receive();
	      if (m != null) {
				sd.handleDiscover(m);
	      }
	      // sleep for a bit
	      Thread.sleep(500);
	    }
	}

}
