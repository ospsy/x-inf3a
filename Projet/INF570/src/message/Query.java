package message;

import java.util.ArrayList;


public class Query extends Message {
	private short[] minSpeed;
	private int dminSpeed;
	private short[] criteria;
	private String[] scriteria;


	/**
	 * constructeur en lecture d'un message Query
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
	public Query(short[] id,int ttl, int hops, String[] searchCriteria,	int minSpeed) {
		super();

		this.scriteria = searchCriteria;
		this.criteria = shortFromCriteria(scriteria);

		this.dminSpeed = minSpeed;
		this.minSpeed = tabFromLong(minSpeed);

		this.header = new MessageHeader(id,TypeMessage.QUERY,ttl,hops,tabFromLong(2+this.criteria.length));

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

	private static String[] getCriteria(short[]tab,int maxLength){

		if((tab.length>maxLength) || (tab[tab.length-1]!=0)){
			System.err.println("format de payload incorrect");
			return null;
		}
		ArrayList<String> al = new ArrayList<String>();
		String s = "";
		for (int i = 0; i < tab.length; i++) {
			
			
			if((tab[i]==32)&&(!s.equals(""))){
				al.add(s);
				s = "";
			}
		}
		
		
		return null;
	}

	private static short[] shortFromCriteria(String[] scriteria2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public double getMinimumSpeed(){		
		return dminSpeed;
	}

	public String[] getCriteria(){
		return scriteria;
	}
}
