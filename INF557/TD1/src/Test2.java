import java.util.Timer;
import java.util.TimerTask;


public class Test2 {
	public static void main(String[] args){
		final PhysicalLayer pl = new PhysicalLayer();
		
		pl.send(TypeMessage.election("-1", ">0"));
		
		/*Timer t1 = new Timer();
		t1.schedule(new TimerTask() {
			
			@Override
			public void run() {
				pl.send("A");
				
			}
		}, 0, 100);
		t1.schedule(new TimerTask() {
			
			@Override
			public void run() {
				pl.send("B");
				
			}
		}, 11000, 7000);*/
		
	}
}
