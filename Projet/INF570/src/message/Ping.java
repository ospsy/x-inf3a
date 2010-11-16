package message;

/**
 * Structure de donnée pour les messages Ping 
 * @author Julio
 *
 */
public class Ping extends Message {

	public Ping(short[] id,int ttl,int hops){
		short[] vide = new short[4];
		vide[0] = vide[1] = vide[2] = vide[3] = 0;
		header = new MessageHeader(id, TypeMessage.PING, ttl, hops, vide);
	}

	@Override
	public short[] toShortTab() {
		return header.getHeader();
	}
	
}