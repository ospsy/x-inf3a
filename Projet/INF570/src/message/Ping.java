package message;

/**
 * Structure de donnée pour les messages Ping 
 * @author Julio
 *
 */
public class Ping extends Message {

	public Ping(short[] id,int ttl,int hops){
		header = new MessageHeader(id, TypeMessage.PING, ttl, hops, 0);
	}

	@Override
	public short[] toShortTab() {
		return header.getHeader();
	}
	
}