import java.util.Timer;
import java.util.TimerTask;


public class Election {
	SNACASD sd;
	private int myNumber;
	private boolean enCours;
	private Timer t;
	
	public Election(SNACASD sd){
		this.sd=sd;
		myNumber=Integer.parseInt(sd.myName);
		enCours=false;
		t = new Timer("Election");
	}
	
	private void startAttente(){
		enCours=true;
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				//m'autoproclamer
				sd.send(TypeMessage.leader(sd.myName, "ALL"));
			}
		}, 200);
	}
	
	private void cancelAttente(){
		enCours=false;
		t.cancel();//TODO verifier
	}
	
	
	public void handleElectionMessages(String m){
		if(TypeMessage.isELECTION(m)){
			String sender=TypeMessage.getSource(m);
			if(Integer.parseInt(sender)<myNumber){
				sd.send(TypeMessage.ok(sd.myName, sender));
				if(!enCours){
					startAttente();
					sd.send(TypeMessage.election(sd.myName, ">"+myNumber));
				}
			}
		}else if(TypeMessage.isLEADER(m)){
			System.out.println("Le leader est trouvé : "+TypeMessage.getSource(m));
			cancelAttente();
		}else if(TypeMessage.isOK(m)){
			if(Integer.parseInt(TypeMessage.getSource(m))>myNumber)
				cancelAttente();
		}
	}
}
