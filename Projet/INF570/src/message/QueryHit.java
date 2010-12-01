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

	private Identifiant serventIdentifier;

	public int getPort() {
		return iport;
	}

	public String getIp() {
		return sip;
	}

	public Identifiant getServentIdentifier() {
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

	protected QueryHit(MessageHeader mh,short[] port, short[] ip, short[] speed, short[] resultSet, short[] serventId) {
		super();
		this.header = mh;
		this.port = port;
		this.iport = port[0]*256+port[1];
		this.ip = ip;
		this.sip = stringFromIp(ip);
		this.serventIdentifier = new Identifiant(serventId);
		this.speed = speed;
		this.dSpeed = longFromTab(speed);
		this.resultSet = resultSet;
		this.sresultSet = getResults(resultSet, resultSet.length);
	}

	protected static Result[] getResults(short[] resultSet, int length) {
		int offset = 0;
		Result[] res = new Result[length];
		int i = 0;
		for (i = 0; i < res.length; i++) {
			short[] temp = subTab(resultSet,8+offset, -1);
			short[] temp2 = subTab(resultSet,offset+9+temp.length, -1);
			res[i] = new Result(subTab(resultSet,0+offset, 3+offset), subTab(resultSet,4+offset, 7+offset), temp, temp2);
			offset = offset + temp.length + temp2.length + 8+2;
		}
		
		return res;
	}


	protected static short[] tabResultSet(Result[] resultSet) {
		
		int l = 0;
		for (int i = 0; i < resultSet.length; i++) {
			l += resultSet[i].length();
		}
		
		short[] res = new short[l];
		int offset = 0;
		for (int i = 0; i < resultSet.length; i++) {
			short[] temp = resultSet[i].toShortTab();
			for (int j = 0; j < temp.length; j++) {
				res[j+offset] = temp[j];
			}
			offset += temp.length;
		}
		
		return res;
	}
	
	
	/**
	 * constructeur en lecture d'un message QueryHit
	 * <p><i><b>exemple :</b> new QueryHit(Message.getRandomId(), 5, 6, 8080, "129.104.127.1", 1000, resultSet, Message.getRandomId());</i></br>
	 * <br><b>--header--</b>
	 * <br><i>id:</i> D6 55 0B F2 89 AE C2 64 A9 6D E7 5D 0B 99 7D BB 
	 * <br><i>type:</i> QUERY_HIT
	 * <br><i>TTL:</i> 5
	 * <br><i>Hops:</i> 6
	 * <br><i>payload length:</i> 52
	 * <br><b>--payload--</b>
	 * <br><i>number of hits:</i> 2
	 * <br><i>port:</i> 8080
	 * <br><i>ip:</i> 129.104.127.1
	 * <br><i>speed:</i> 1000kB/s
	 * <br><i>results:</i> {index:[11] size:[512kB] name:[toto.txt] optionalData:[64 28 41 06 0F E5 47 6D DB 16 9C 90 A9 27 82 02 ]} {index:[22] size:[3512kB] name:[panda.mp3] optionalData:[]} 
	 * <br><i>servent id:</i> AB DA 28 0E E4 B6 F0 0D AF 10 5D BE D2 74 9B 94
	 * <br><b>--end--</b></br>
	 * @see {@link Result}
	 * @param port
	 * @param ip
	 * @param speed
	 * @param Result_Set
	 */
	public QueryHit(Identifiant id,int ttl, int hops,int port, String ip, long speed, Result[] resultSet) {
		super();
		this.iport = port;
		this.sip = ip;
		this.sresultSet = resultSet;
		this.serventIdentifier = Message.getRandomId();
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
			
			res[i+23+1+2+4+4+resultSet.length] = serventIdentifier.getData()[i];
		}
		return res;
	}
	
	@Override
	public String toString() {
		return header+"\n--payload--\nnumber of hits: " +getNumberOfHits()+"\nport: "+getPort()+"\nip: "+getIp()+"\nspeed: "+getSpeed()+"kB/s\nresults: "+stringFromResultTab(getResultSet())+"\nservent id: "+getServentIdentifier()+"\n---end---\n";
	}

}