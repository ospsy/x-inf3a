package message;

import java.util.ArrayList;


public class Query extends Message {
	private short[] minSpeed;
	private int dminSpeed;
	private short[] criteria;
	private String[] scriteria;


	/**
	 * constructeur en lecture d'un message Query
	 * <p><i><b>exemple :</b> new Query(5, 6, "banane jaune   singe".split(" "), 1000);</i></br>
	 * <br><b>--header--</b>
	 * <br><i>id:</i> 52 4A A4 1E 83 B4 D5 F9 9B 33 91 B8 65 6F 3A 4B 
	 * <br><i>type:</i> QUERY
	 * <br><i>TTL:</i> 5
	 * <br><i>Hops:</i> 6
	 * <br><i>payload length:</i> 23
	 * <br><b>--payload--</b>
	 * <br><i>minimum speed:</i> 1000kB/s
	 * <br><i>criteria:</i> banane jaune singe 
	 * <br><b>--end--</b></br>
	 * @param minSpeed
	 * @param searchCriteria
	 */
	protected Query(MessageHeader mh, short[] minSpeed,	short[] criteria) {
		super();
		this.header = mh;
		this.minSpeed = minSpeed;
		this.dminSpeed = minSpeed[0]*256+minSpeed[1];
		this.criteria = criteria;
		this.scriteria = getCriteria(criteria, mh.getPayloadLength()-2);
	}

	/**
	 * constructeur pour l'écriture d'un message Query
	 * @param minSpeed
	 * @param searchCriteria
	 */
	public Query(int ttl, int hops, String[] searchCriteria,	int minSpeed) {
		super();

		this.scriteria = searchCriteria;
		this.criteria = shortFromCriteria(scriteria);

		this.dminSpeed = minSpeed;
		this.minSpeed = tabFromLong(minSpeed);

		this.header = new MessageHeader(getRandomId(),TypeMessage.QUERY,ttl,hops,2+this.criteria.length);

	}


	@Override
	public short[] toShortTab() {
		short[] res = new short[37];
		for (int i = 0; i<23;i++){
			res[i] = header.getHeader()[i];
		}
		for (int i = 0; i<2;i++){
			res[i+23] = minSpeed[i];
		}
		for (int i = 0; i<criteria.length;i++){
			res[i+25] = criteria[i];
		}
		return res;
	}

	protected static String[] getCriteria(short[]tab,int maxLength){

		if((tab.length>maxLength) || (tab[tab.length-1]!=0)){
			System.err.println("format de payload incorrect");
			return null;
		}
		ArrayList<String> al = new ArrayList<String>();
		String s = "";
		for (int i = 0; i < tab.length; i++) {
			if((tab[i]==0)||(tab[i]==32)&&(!s.equals(""))){
				al.add(s);
				s = "";
			}
			else{
				if((char)tab[i]!=' ')
				s = s + (char)tab[i];
			}
		}
		String[] res = new String[al.size()];
		System.out.println(al.size());
		for (int i = 0; i < al.size(); i++) {
			res[i] = al.get(i);
		}

		return res;
	}

	protected static short[] shortFromCriteria(String[] scriteria) {

		ArrayList<Short> al = new ArrayList<Short>();
		for (int i  = 0; i < scriteria.length;i++){
			for (int j = 0; j < scriteria[i].length(); j++) {
				al.add((short)scriteria[i].charAt(j));
			}

			if (i != (scriteria.length-1))al.add((short)' ');
		}
		al.add((short)0);
		short[] res = new short[al.size()];

		for (int i = 0; i < al.size(); i++) {
			res[i] = al.get(i);
		}

		return res;
	}

	public int getMinimumSpeed(){		
		return dminSpeed;
	}

	public String[] getCriteria(){
		return scriteria;
	}
	
	@Override
	public String toString() {
		return header+"\n--payload--\nminimum speed: "+getMinimumSpeed()+"kB/s\ncriteria: "+stringFromStringTab(getCriteria(criteria, criteria.length))+"\n---end---\n";
	}
}
