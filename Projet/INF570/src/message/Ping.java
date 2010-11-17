package message;

/**
 * Structure de données pour les messages Ping 
 * @author Julio
 *
 */
public class Ping extends Message {

	/**
	 * constructeur pour les pings
	 * <p><i><b>exemple :</b> new Ping(5, 6);</i></br>
	 * <br><b>--header--</b>
	 * <br><i>id:</i> 0E 23 E9 84 F9 40 67 0D 09 53 FB 46 13 AC 14 C1 
	 * <br><i>type:</i> PING
	 * <br><i>TTL:</i> 5
	 * <br><i>Hops:</i> 6
	 * <br><i>payload length:</i> 0
	 * <br><b>--end--</b></br>
	 * @param ttl
	 * @param hops
	 */
	public Ping(int ttl,int hops){
		header = new MessageHeader(getRandomId(), TypeMessage.PING, ttl, hops, 0);
	}

	@Override
	public short[] toShortTab() {
		return header.getHeader();
	}
	
	@Override
	public String toString() {
		return header+"\n--end--\n";
	}
	
}