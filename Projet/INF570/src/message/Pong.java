package message;



public class Pong extends Message {
	private short[] port;
	private int iport;
	private short[] ip;
	private String sip;
	private short[] numberOfSharedFiles;
	private long lnumberOfSharedFiles;
	private short[] sharedFilesSize;
	private long lsharedFilesSize;


	/**
	 * constructeur en lecture d'un message Pong
	 * @param port
	 * @param ip
	 * @param numberOfSharedFiles
	 * @param sharedFilesSize
	 */
	protected Pong(MessageHeader mh,short[] port, short[] ip, short[] numberOfSharedFiles,	short[] sharedFilesSize) {
		super();
		this.header = mh;
		this.port = port;
		this.iport = port[0]*256+port[1];
		this.ip = ip;
		this.sip = stringFromIp(ip);
		this.numberOfSharedFiles = numberOfSharedFiles;
		this.lnumberOfSharedFiles = longFromTab(numberOfSharedFiles);
		this.sharedFilesSize = sharedFilesSize;
		this.lsharedFilesSize = longFromTab(sharedFilesSize);
	}

	/**
	 * constructeur pour l'écriture d'un message Pong
	 * @param port
	 * @param ip
	 * @param numberOfSharedFiles
	 * @param sharedFilesSize
	 */
	public Pong(short[] id,int ttl, int hops,int port, String ip, long numberOfSharedFiles,	long sharedFilesSize) {
		super();
		this.header = new MessageHeader(id,TypeMessage.PONG,ttl,hops,14);
		this.iport = port;
		this.sip = ip;
		this.lnumberOfSharedFiles = numberOfSharedFiles;
		this.lsharedFilesSize = sharedFilesSize;

		short[] bport = new short[2];
		bport[1]=(short) (port%256);
		bport[0]= (short) ((port-bport[1])/256);
		this.port = bport;
		this.ip = ipFromString(ip);
		this.numberOfSharedFiles = tabFromLong(numberOfSharedFiles);
		this.sharedFilesSize = tabFromLong(sharedFilesSize);
	}

	@Override
	public short[] toShortTab() {
		short[] res = new short[37];
		for (int i = 0; i<23;i++){
			res[i] = header.getHeader()[i];
		}
		for (int i = 0; i<2;i++){
			res[i+23] = port[i];
		}
		for (int i = 0; i<4;i++){
			res[i+25] = ip[i];
		}
		for (int i = 0; i<4;i++){
			res[i+29] = numberOfSharedFiles[i];
		}
		for (int i = 0; i<4;i++){
			res[i+33] = sharedFilesSize[i];
		}
		return res;

	}

	public int getPort(){
		return iport;
	}

	public String getIp(){
		return sip;
	}

	public long getNumberOfSharedFiles(){
		return lnumberOfSharedFiles;
	}

	public long getNumberOfKilobytesShared(){
		return lsharedFilesSize;
	}
}
