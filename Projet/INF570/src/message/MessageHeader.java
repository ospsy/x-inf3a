package message;

/**
 * @author Julio
 *
 */
public class MessageHeader {
	private short[] header;
	public static final int STANDARD_SIZE = 23;
	
	protected MessageHeader(short[] id,TypeMessage type,int ttl, int hops, int payloadLength){
		header = new short[23];
		setMessageID(id);
		setTypeMessage(type);
		setTTL(ttl);
		setHops(hops);
		setPayloadLength(Message.tabFromLong(payloadLength));
	}
	
	/**
	 * ce constructeur està utiliser pour la lecture d'un message
	 * @param s
	 */
	public MessageHeader(short [] s){
			if (s.length != 23) {
				System.err.println("Format de header non valide");
				header = null;
			}
			header = s;
	}


	public short[] getHeader(){
		return header;
	}
	
	/**
	 * 
	 * @return L'identifiant du message
	 */
	public String getMessageID() {
		char[] s = new char[16];
		for (int i = 0; i <16;i++){
			s[i]=(char) header[i];
		}
		return s.toString();
	}
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
		int res = 0;
		for (int i = 0;i<4;i++){
			res = (res*256 + header[i+19]);
		}
		return res;
		
	}

	private void setMessageID(short[] s){
		for (int i = 0; i <16;i++){
			header[i]=s[i];
		}
	}
	private void setTypeMessage(TypeMessage t){
		switch (t) {
		case PING:
			header[16] = 0;
			break;
		case PONG:
			header[16] = 1;
			break;
		case PUSH:
			header[16] = 64;
			break;
		case QUERY:
			header[16] = 128;
			break;
		case QUERY_HIT:
			header[16] = 129;
			break;

		default:
			break;
		}
	}
	private void setTTL(int i) {
		header[17] = (short) i;
	}
	private void setHops(int i) {
		header[18] = (short) i;
	}
	private void setPayloadLength(short[] s) {
		for (int i = 0; i <4;i++){
			header[i+19]=s[i];
		}
	}

	protected void decreaseTTL() {
		if(getTTL()==0){
			System.err.println("Le ttl est déjà nul, vous ne pouvez pas le d�cr�menter");
			return;
		}
		setTTL(getTTL()-1);
		setHops(getHops()+1);
		
	}
	

	public String toString(){
		return "--header--\nid: "+getMessageID()+"\ntype: "+getMessageType()+"\nTTL: "+getTTL()+"\nHops: "+getHops()+"\npayload length: "+getPayloadLength();
	}

}
