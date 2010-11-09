package message;

/**
 * @author Julio
 *
 */
public class MessageHeader {
	private byte[] MessageID;
	private byte PayloadDescriptor;
	private byte TTL;
	private byte Hops;
	private byte[] PayloadLength;
	
	public MessageHeader(){
		MessageID = new byte[16];
		PayloadLength = new byte[3];
	}

	/**
	 * convertit un byte en entier en respectant l'ordre Big endian
	 *
	 * @param b
	 * @return
	 */
	private int intOfByte(Byte b){
		return Byte.valueOf(b);
	}
	
	private int intOfByte(Byte[] b){
		int res = 0;
		for (int i = 0; i<b.length;i++){
			res = 255*res+Byte.valueOf(MessageID[i]);
		}
		return res;
	}
	
	public int getMessageID() {
		int res = 0;
		for (int i = 0; i<16;i++){
			res = 255*res+Byte.valueOf(MessageID[i]);
		}
		return res;
	}

	public void setMessageID(byte[] messageID) {
		MessageID = messageID;
	}

	public byte getPayloadDescriptor() {
		return PayloadDescriptor;
	}

	public void setPayloadDescriptor(byte payloadDescriptor) {
		PayloadDescriptor = payloadDescriptor;
	}

	public byte getTTL() {
		return TTL;
	}

	public void setTTL(byte tTL) {
		TTL = tTL;
	}

	public byte getHops() {
		return Hops;
	}

	public void setHops(byte hops) {
		Hops = hops;
	}

	public byte[] getPayloadLength() {
		return PayloadLength;
	}

	public void setPayloadLength(byte[] payloadLength) {
		PayloadLength = payloadLength;
	}
	
	
	
	
	
}
