
public class Election {
	SNACASD sd;
	private int myNumber;
	
	public Election(SNACASD sd){
		this.sd=sd;
		myNumber=Integer.parseInt(sd.myName);
	}
	

	public void handleElectionMessages(String m){
		if(TypeMessage.isELECTION(m)){
			String sender=TypeMessage.getSource(m);
			if(Integer.parseInt(sender)<myNumber){
				sd.send(TypeMessage.ok(sd.myName, sender));
			}
			sd.send(TypeMessage.election(sd.myName, ">"+myNumber));
			//TODO lancer un timer d'attente des messages
		}
	}
}
