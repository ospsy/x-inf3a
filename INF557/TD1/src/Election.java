import java.util.Timer;
import java.util.TimerTask;


public class Election {
	SNACASD sd;
	private int myNumber;
	private boolean attenteEnCours;
	private boolean electionEnCours;
	private Timer t;
	
	public Election(SNACASD sd){
		this.sd=sd;
		myNumber=Integer.parseInt(sd.myName);
		attenteEnCours=false;
		electionEnCours=false;
		t = new Timer("Election");
	}
	
	synchronized private void startAttente(){
		attenteEnCours=true;
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				//m'autoproclamer
				sd.send(TypeMessage.leader(sd.myName, "ALL"));
			}
		}, 2000);
	}
	
	synchronized private void cancelAttente(){
		if(attenteEnCours){
			attenteEnCours=false;
			t.cancel();//TODO verifier si le timer est détruit
			t = new Timer("Election");
		}
	}
	
	
	public void handleElectionMessages(String m){
		if(TypeMessage.isELECTION(m)){
			String sender=TypeMessage.getSource(m);
			if(Integer.parseInt(sender)<myNumber){
				sd.send(TypeMessage.ok(sd.myName, sender));
				if(!attenteEnCours && ! electionEnCours){
					startAttente();
					sd.send(TypeMessage.election(sd.myName, ">"+myNumber));
				}
			}
			if(!electionEnCours){
				System.out.println("Début d'élection...");
				electionEnCours=true;
			}
		}else if(TypeMessage.isLEADER(m)){
			System.out.println("Le leader est trouvé : "+TypeMessage.getSource(m));
			if(!electionEnCours) System.err.println("Il n'y avait pas d'élections en cours");
			else {
				electionEnCours=false;
				System.out.println("Fin de l'élection.");
			}
			cancelAttente();
		}else if(TypeMessage.isOK(m)){
			if(Integer.parseInt(TypeMessage.getSource(m))>myNumber)
				cancelAttente();
		}
	}
}
