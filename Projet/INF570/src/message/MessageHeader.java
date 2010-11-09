package message;

/**
 * @author Julio
 *attention les bytes en java sont signés
 */
public class MessageHeader {
	private short[] header;
	
	public MessageHeader(int id,TypeMessage type,int ttl, int hops, int payloadLength){
		header = new short[23];
		
	}
	


	/**
	 * extrait l'entier des octets compris entre les indices i et j dans le tableau en respectant l'ordre Big endian
	 * @param bornes de l'entier dans le header
	 * @return l'entier correspondant
	 */
	private int intOfByte(int i, int j){
		int res = 0;
		for (int k = i ; k<j+1;k++){
			res = 256*res+(header[k]);
		}
		return res;
	}
	
	/**
	 * 
	 * @return L'identifiant du message
	 */
	public int getMessageID() {return intOfByte(0,15);}
	/**
	 * 
	 * @return Le type du message
	 */
	public TypeMessage getMessageType() {
		switch (header[16]) {
		case 0:
			return TypeMessage.PING;
		case 1:
			return TypeMessage.PONG;
		case 64:
			return TypeMessage.PUSH;
 		case 128:
 			return TypeMessage.QUERY;
 		case 129:
 			return TypeMessage.QUERY_HIT;
		default:
			break;
		}
		return null;

	}
	/**
	 * 
	 * @return Le TTL
	 */
	public int getTTL() {
		return header[17];
	}
	/**
	 * 
	 * @return Le Hops
	 */
	public int getHops() {
		return header[18];
	}
	/**
	 * 
	 * @return La taille du message
	 */
	public int getPayloadLength() {
		return intOfByte(19, 22);
	}
	
}
