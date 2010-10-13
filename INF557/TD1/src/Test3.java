import java.util.Timer;
import java.util.TimerTask;


public class Test3 {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		PhysicalLayer pl = new PhysicalLayer();
	   SNACASD sd = new SNACASD(pl, 5, 3);
	    if(!sd.connect()){
	    	System.out.println("Impossible de trouver un nom, aborting...");
	    	System.exit(0);
	    }
	    Election elec = new Election(sd);
	    
	    while (true) {
	      
	      while (true) {
	    	  String m = pl.receive();
	    	  if(m==null) break;
	    	  System.out.println(m);
				sd.handleDiscover(m);
				sd.handleService(m);
				elec.handleElectionMessages(m);
	      }
	      // sleep for a bit
	      Thread.sleep(20);
	    }
	}

}
