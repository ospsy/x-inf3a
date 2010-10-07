
public class User {
	long timeOfLastMsg;
	long period;
	boolean isDying;
	String userName;
	
	User(String name){
		timeOfLastMsg=System.currentTimeMillis();
		period=20000;
		isDying=false;
		userName=name;
	}
	
	void update(){
		isDying=false;
		long tmp = System.currentTimeMillis()-timeOfLastMsg;
		if(tmp>3*period || tmp<period/3)
			period=2*tmp;
		timeOfLastMsg=System.currentTimeMillis();
	}
	
	boolean check(PhysicalLayer pl,String myName){
		if(isDying) return false;
		if((System.currentTimeMillis()-timeOfLastMsg)>period){
			isDying=true;
			pl.send(TypeMessage.discover(myName, userName));
		}
		return true;
	}
}
