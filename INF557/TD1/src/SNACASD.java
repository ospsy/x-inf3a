
public class SNACASD {
  String myName;
  private PhysicalLayer pl;
  private int retries;
  private int maxtime;

  public SNACASD(PhysicalLayer pl, int retries, int maxtime) {
    // pl is the physical layer instance, via which
    //       outgoing messages are sent
    // retries is the number of DISCOVER messages sent
    //            in a duplicate detection cycle
    // maxtime is the maximum number of miliseconds
    //            between successive DISCOVER messages
    this.pl = pl;
    this.retries = retries;
    this.maxtime = maxtime;
  }

  public boolean verifyUniqueness(String tentativeName){
    // verify if the name tentativeName is unique on the network
    //
    // note that this requires reading incoming messages
    // from pl, for DISCOVER or SERVICE messages
    // return true if tentativeName is unique, false otherwise
	  
	  try {
		Thread.sleep((int)(Math.random()*maxtime));
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	  boolean failed=false;
	  for(int i=0;i<retries;i++){
		  pl.send(TypeMessage.discover("NEW",tentativeName) );
		  try {
			Thread.sleep((int)(Math.random()*maxtime));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		  String s;
		  boolean myMessage=true;
		  while((s=pl.receive())!=null){
			  if((TypeMessage.isDISCOVER(s) && TypeMessage.getDest(s)==tentativeName)){
				  if(myMessage) myMessage=false;
				  else{
					  failed=true;
					  break;
				  }
			  }
			  if((TypeMessage.isSERVICES(s) && TypeMessage.getSource(s)==tentativeName))
				  {failed=true;
				  break;
				  }
		  }
		  if(failed) break;
	  }
	  if(!failed){//On a réussi à obtenir ce nom!!!
		  myName=tentativeName;
		  System.out.println("Nom obtenu : "+myName);
	  }
	  return !failed;
  }

  public String getUniqueName() {
    // generate a name which is unique on the network
	  myName="";
	  for(int i = 0;i<20;i++){
			if(verifyUniqueness(Integer.toString(i)))
				  break;
	  }
	  return myName;
  }

  public void handleDiscover(String m) {
    // handles a DISCOVER message m, if appropriate
    // (i.e. is destined for this node or for all nodes),
    // and replies with a SERVICE message
	  if(TypeMessage.isDISCOVER(m) && TypeMessage.getDest(m)==myName)
		  pl.send(TypeMessage.services(myName, "ALL"));
  }
}