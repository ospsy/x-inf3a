package message;


public class QueryHit extends Message{

	private short numberOfHits;
	private int inumberOfHits;
	private short[] port;
	private int iport;
	private short[] ip;
	private String sip;

	private short[] speed;
	private long dSpeed;
	private short[] resultSet;
	private Result[] sresultSet;

	private short[] serventIdentifier;

	public int getPort() {
		return iport;
	}

	public String getIp() {
		return sip;
	}

	public short[] getServentIdentifier() {
		return serventIdentifier;
	}

	public int getNumberOfHits() {
		return inumberOfHits;
	}

	public long getSpeed() {
		return dSpeed;
	}

	public Result[] getResultSet() {
		return sresultSet;
	}

	/**
	 * constructeur en lecture d'un message Push
	 * @param port
	 * @param ip
	 * @param speed
	 * @param Servent_Identifier
	 * @param Result_Set
	 */
	protected QueryHit(MessageHeader mh,short[] port, short[] ip, short[] speed, short[] resultSet, short[] serventId) {
		super();
		this.header = mh;
		this.port = port;
		this.iport = port[0]*256+port[1];
		this.ip = ip;
		this.sip = stringFromIp(ip);
		this.serventIdentifier = serventId;
		this.speed = speed;
		this.dSpeed = longFromTab(speed);
		this.resultSet = resultSet;
		this.sresultSet = getResults(resultSet, resultSet.length);
	}

	private Result[] getResults(short[] resultSet, int length) {
		// TODO Auto-generated method stub
		return null;
	}


	private short[] tabResultSet(Result[] resultSet) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * constructeur pour l'écriture d'un message Push
	 * @param port
	 * @param ip
	 * @param speed
	 * @param Result_Set
	 * @param Servent_Identifier
	 */
	public QueryHit(short[] id,int ttl, int hops,int port, String ip, long speed, Result[] resultSet, short[] serventId) {
		super();
		this.iport = port;
		this.sip = ip;
		this.sresultSet = resultSet;
		this.serventIdentifier = serventId;
		this.inumberOfHits = resultSet.length;
		this.dSpeed = speed;

		this.header = new MessageHeader(id,TypeMessage.QUERY_HIT,ttl,hops,50+resultSet.length);

		this.resultSet = tabResultSet(resultSet);
		short[] bport = new short[2];
		bport[1]=(short) (port%256);
		bport[0]= (short) ((port-bport[1])/256);
		this.speed = tabFromLong(speed);
		this.port = bport;
		this.ip = ipFromString(ip);
		this.numberOfHits = (short) resultSet.length;

	}




	@Override
	public short[] toShortTab() {
		short[] res = new short[23+16+resultSet.length];
		for (int i = 0; i<23;i++){
			res[i] = header.getHeader()[i];
		}
		res[23] = numberOfHits;
		for (int i = 0; i<2;i++){
			res[i+23+1] = port[i];
		}
		for (int i = 0; i<4;i++){
			res[i+23+1+2] = ip[i];
		}
		for (int i = 0; i<4;i++){
			res[i+23+1+2+4] = speed[i];
		}
		for (int i = 0; i<resultSet.length;i++){
			res[i+23+1+2+4+4] = resultSet[i];
		}

		for (int i = 0; i<16;i++){
			res[i+23+1+2+4+4+resultSet.length] = serventIdentifier[i];
		}
		return res;
	}

}