import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


public class SNACASD {
  public String myName;
  private PhysicalLayer pl;
  private int retries;
  private int maxtime;
  private HashMap<String, User> users;

  public void send(String m){
	  pl.send(m);
  }
  
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
    users=new HashMap<String, User>();
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
		  boolean myMessage=true;//permet d'éviter de traiter son propre message
		  while((s=pl.receive())!=null){
			  if((TypeMessage.isDISCOVER(s) && TypeMessage.getDest(s).equals(tentativeName))){
				  if(myMessage) myMessage=false;
				  else{
					  failed=true;
					  break;
				  }
			  }
			  if((TypeMessage.isSERVICES(s) && TypeMessage.getSource(s).equals(tentativeName)))
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
	  if(TypeMessage.isDISCOVER(m) && TypeMessage.getDest(m).equals(myName))
		  pl.send(TypeMessage.services(myName, "ALL"));
  }
  
  public void handleService(String m){
	  if(TypeMessage.isSERVICES(m)){
		  String tmp=TypeMessage.getSource(m);
		  if(users.containsKey(tmp)){
			  users.get(tmp).update();
		  }else{
			  users.put(tmp, new User(tmp));
		  }
	  }
  }
  
  /**
   * Etablit la connection (generation du nom et création du thread des SERVICE)
   * @return true si OK, false sinon
   */
  public boolean connect(){
	  if(getUniqueName().equals(""))
		  return false;
	  else{
		  Timer t1 = new Timer();
			t1.schedule(new TimerTask() {
				
				@Override
				public void run() {
					pl.send(TypeMessage.services(myName, "ALL"));
					
				}
			}, 0, 4000);
			t1.schedule(new TimerTask() {
				
				@Override
				public void run() {
					System.out.println("Copains:");
					LinkedList<String> removables=new LinkedList<String>();
					for(User u : users.values()){
						if(!u.check(pl, myName)){
							removables.add(u.userName);
						}
					}
					for(String s :removables)
						users.remove(s);
					for(User u : users.values()){
						System.out.println(u.userName);
					}
				}
			}, 0, 1000);
			
			return true;
	  }
  }
}