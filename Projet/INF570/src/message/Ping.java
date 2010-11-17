package message;

/**
 * Structure de données pour les messages Ping 
 * @author Julio
 *
 */
public class Ping extends Message {

	/**
	 * constructeur pour les pings
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