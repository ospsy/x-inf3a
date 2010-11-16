package message;


public abstract class Message {
	MessageHeader header;
	
	/**
	 * renvoie la version aplatie d'un message
	 * @return	le short[] qu'il faut envoyer à travers la socket
	 */
	public abstract short[] toShortTab();
	
	public MessageHeader getHeader(){
		return header;
	}
	
	static String stringFromIp(short[]s){
		if (s.length != 4){
			System.err.println("ip incorrecte, conversion byte[] => String impossible");
			return null;
		}
		return (s[0]+"."+s[1]+"."+s[2]+"."+s[3]);
	}
	/**
	 * parse des long pour coder des short[] à 4 emplacement donc < 256^5-1
	 * @param n
	 * @return un short[] qui code n
	 */
	static short[] tabFromLong(long n){
		short[] res = new short[4];
		long temp = n;
		
		res[3] = (short) (temp%256);
		temp = (temp - res[3])/256;
		res[2] = (short) (temp%256);
		temp = (temp - res[2])/256;
		res[1] = (short) (temp%256);
		temp = (temp - res[1])/256;
		res[0] = (short) (temp%256);
		
		return res;
	}
	
	/**
	 * convertit un tableau de short en long
	 * @param tab
	 * @return la valeur sous forme de long du tableau tab
	 */
	static long longFromTab(short[] tab){
		long res = 0;
	
		for (int i = 0;i<tab.length;i++){
			res = (res*256 + tab[i]);
		}
		
		return res;
	}

	
	static short[] ipFromString(String ip) {
		String s[] = ip.split(".");
		short[] sip = new short[4];
		for (int i = 0;i<4;i++){
			sip[i] = (short) Integer.parseInt(s[i]);
		}
		return sip;
	}
	
	
	/**
	 * à appeler lors de la lecture d'un message après avois analysé le header
	 * 
	 * @param h
	 * @param payload
	 * @return	un message de type cohérent avec le header (null sinon)
	 */
	public static Message parseMessage(MessageHeader h,short[] payload){
		switch (h.getMessageType()) {
		case PING:
			if (payload!=null) {
				System.err.println("payload et header incohérents");
				return null;
			}
			return new Ping(h.getMessageID(), h.getTTL(), h.getHops());
		case PONG:
			if (payload.length!=14) {
				System.err.println("payload et header incohérents");
				return null;
			}
			return new Pong(h,subTab(payload, 0, 1),subTab(payload, 2, 5),subTab(payload, 6, 9),subTab(payload, 10, 13));
		case PUSH:
			return null;
		case QUERY:
			return null;
		case QUERY_HIT:
			return null;
		default:
			break;
		}
		return null;
	}
	
	private static short[] subTab(short[] t,int i, int j){
		short[] tab = new short[j-i+1];
		for(int k = i;k<j+1;k++){
			tab[k-i] = t[k];
		}
		return tab;
	}
}

/**
 * Structure de donnée pour les messages Ping 
 * @author Julio
 *
 */
class Ping extends Message {

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

class Pong extends Message {
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
	public Pong(MessageHeader mh,short[] port, short[] ip, short[] numberOfSharedFiles,	short[] sharedFilesSize) {
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
		this.header = new MessageHeader(id,TypeMessage.PONG,ttl,hops,tabFromLong(14));
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

class Push extends Message{
	
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

	public long getLfileIndex() {
		return lfileIndex;
	}

	/**
	 * constructeur en lecture d'un message Push
	 * @param port
	 * @param ip
	 * @param file index
	 * @param Servent Identifier
	 */
	public Push(MessageHeader mh,short[] port, short[] ip, short[] serventId,	short[] fileIndex) {
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
		this.header = new MessageHeader(id,TypeMessage.PUSH,ttl,hops,tabFromLong(26));
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
		// TODO Auto-generated method stub
		return null;
	}
	
}

