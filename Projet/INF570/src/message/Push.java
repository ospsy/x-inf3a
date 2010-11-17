package message;

public class Push extends Message{

	private short[] port;
	private int iport;
	private short[] ip;
	private String sip;
	private short[] serventIdentifier;
	private short[] fileIndex;
	private long lfileIndex;

	public int getPort() {
		return iport;
	}

	public String getIp() {
		return sip;
	}

	public short[] getServentIdentifier() {
		return serventIdentifier;
	}

	public long getFileIndex() {
		return lfileIndex;
	}

	/**
	 * constructeur en lecture d'un message Push
	 * @param port
	 * @param ip
	 * @param file index
	 * @param Servent Identifier
	 */
	protected Push(MessageHeader mh,short[] port, short[] ip, short[] serventId,	short[] fileIndex) {
		super();
		this.header = mh;
		this.port = port;
		this.iport = port[0]*256+port[1];
		this.ip = ip;
		this.sip = stringFromIp(ip);
		this.serventIdentifier = serventId;
		this.fileIndex = fileIndex;
		this.lfileIndex = longFromTab(fileIndex);
	}

	/**
	 * constructeur pour l'écriture d'un message Push
	 * @param port
	 * @param ip
	 * @param file index
	 * @param Servent Identifier
	 */
	public Push(short[] id,int ttl, int hops,int port, String ip, long fileIndex,	short[] serventId) {
		super();
		this.header = new MessageHeader(id,TypeMessage.PUSH,ttl,hops,26);
		this.iport = port;
		this.sip = ip;
		this.lfileIndex = fileIndex;


		short[] bport = new short[2];
		bport[1]=(short) (port%256);
		bport[0]= (short) ((port-bport[1])/256);
		this.port = bport;
		this.ip = ipFromString(ip);
		this.fileIndex = tabFromLong(fileIndex);
		this.serventIdentifier = serventId ;
	}



	@Override
	public short[] toShortTab() {
		short[] res = new short[49];
		for (int i = 0; i<23;i++){
			res[i] = header.getHeader()[i];
		}

		for (int i = 0; i<16;i++){
			res[i+23] = serventIdentifier[i];
		}
		for (int i = 0; i<4;i++){
			res[i+23+16] = fileIndex[i];
		}
		for (int i = 0; i<4;i++){
			res[i+23+20] = ip[i];
		}
		for (int i = 0; i<2;i++){
			res[i+23+24] = port[i];
		}
		return res;
	}

	
	public String toString() {
		return header+"\n--payload--\nservent id: "+
		Message.stringOfTab(getServentIdentifier())+
		"\nfile index: "+getFileIndex()+
		"\nip: "+getIp()+"\nport: "+getPort()+"\n---end---\n";
	}
	
}

