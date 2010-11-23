package message;

public class Push extends Message{

	private short[] port;
	private int iport;
	private short[] ip;
	private String sip;
	private Identifiant serventIdentifier;
	private short[] fileIndex;
	private long lfileIndex;

	public int getPort() {
		return iport;
	}

	public String getIp() {
		return sip;
	}

	public Identifiant getServentIdentifier() {
		return serventIdentifier;
	}

	public long getFileIndex() {
		return lfileIndex;
	}

	/**
	 * constructeur en lecture d'un message Push
	 * <p><i><b>exemple :</b> new Pong(Message.getRandomId(),5,2,8080,"129.104.227.1",2,520);</i></br>
	 * <br><b>--header--</b>
	 * <br><i>id:</i> 7B AE 24 62 39 2A 06 F0 14 C0 5E EE 3E 91 8A 82 
	 * <br><i>type:</i> PUSH
	 * <br><i>TTL:</i> 5
	 * <br><i>Hops:</i> 6
	 * <br><i>payload length:</i> 26
	 * <br><b>--payload--</b>
	 * <br><i>servent id:</i> CE 62 89 15 53 9C 59 72 C1 D5 16 48 43 C2 2E 81 
	 * <br><i>file index:</i> 2
	 * <br><i>ip:</i> 129.104.127.1
	 * <br><i>port:</i> 8080
	 * <br><b>--end--</b></br>
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
		this.serventIdentifier = new Identifiant(serventId);
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
	public Push(Identifiant id,int ttl, int hops,int port, String ip, long fileIndex,	Identifiant serventId) {
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
			res[i+23] = serventIdentifier.getData()[i];
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
		getServentIdentifier()+
		"\nfile index: "+getFileIndex()+
		"\nip: "+getIp()+"\nport: "+getPort()+"\n---end---\n";
	}
	
}

