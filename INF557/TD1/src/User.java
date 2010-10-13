
public class User {
	final long defaultPeriod=1500;
	long timeOfLastMsg;
	long period;
	boolean isDying;
	String userName;
	
	User(String name){
		timeOfLastMsg=System.currentTimeMillis();
		period=defaultPeriod;
		isDying=false;
		userName=name;
	}
	
	synchronized void update(){
		isDying=false;
		long tmp = System.currentTimeMillis()-timeOfLastMsg;
		if(tmp>3*period || tmp<period/3)
			period=Math.max(2*tmp,defaultPeriod);
		timeOfLastMsg=System.currentTimeMillis();
	}
	
	synchronized boolean check(PhysicalLayer pl,String myName){
		if(isDying) return false;
		if((System.currentTimeMillis()-timeOfLastMsg)>3*period){
			isDying=true;
			pl.send(TypeMessage.discover(myName, userName));
		}
		return true;
	}
}
