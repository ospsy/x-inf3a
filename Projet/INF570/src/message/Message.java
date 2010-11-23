package message;

import java.util.ArrayList;
import java.util.Random;


public abstract class Message {
	protected MessageHeader header;

	/**
	 * renvoie la version aplatie d'un message
	 * @return	le short[] qu'il faut envoyer à travers la socket
	 */
	public abstract short[] toShortTab();
	
	public abstract String toString();

	public MessageHeader getHeader(){
		return header;
	}

	/**
	 * parse une ip à partir d'un tableau de short
	 * @param s
	 * @return l'ip sous forme de string
	 */
	protected static String stringFromIp(short[]s){
		if (s.length != 4){
			System.err.println("ip incorrecte, conversion byte[] => String impossible");
			return null;
		}
		return (s[0]+"."+s[1]+"."+s[2]+"."+s[3]);
	}
	
	public void decreaseTTL(){
		header.decreaseTTL();
	}
	
	/**
	 * transforme une ip sous forme de string en tableau de short
	 * @param ip
	 * @return	le tableau de short associé
	 */
	protected static short[] ipFromString(String ip) {
		String s[] = ip.split("\\p{Punct}");
		short[] sip = new short[4];
		for (int i = 0;i<4;i++){
			sip[i] = (short) Integer.parseInt(s[i]);
		}
		return sip;
	}


	/**
	 * parse des long pour coder des short[] à l'emplacement donc < 256^5-1
	 * @param n
	 * @return un short[] qui code n
	 */
	protected static short[] tabFromLong(long n){
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
	protected static long longFromTab(short[] tab){
		long res = 0;

		for (int i = 0;i<tab.length;i++){
			res = (res*256 + tab[i]);
		}

		return res;
	}

	protected static Identifiant getRandomId(){
		Random ran = new Random();
		short[] res = new short[16];
		for (int i = 0; i < res.length; i++) {
			res[i] = (short) ran.nextInt(256);
		}
		return new Identifiant(res);
	}
	
	protected static short[] getRandomShortTab(){
		Random ran = new Random();
		short[] res = new short[16];
		for (int i = 0; i < res.length; i++) {
			res[i] = (short) ran.nextInt(256);
		}
		return res;
	}
	
	/**
	 * à appeler lors de la lecture d'un message après avoir analysé le header
	 * 
	 * @param h
	 * @param payload
	 * @return	un message de type cohérent avec le header (null sinon)
	 */
	public static Message parseMessage(MessageHeader h,short[] payload){
		switch (h.getMessageType()) {
		case PING:
			if (payload.length!=0) {
				System.err.println("payload et header incohérents");
				return null;
			}
			return new Ping(h.getTTL(), h.getHops());
		case PONG:
			if (payload.length!=14) {
				System.err.println("payload et header incohérents");
				return null;
			}
			return new Pong(h,subTab(payload, 0, 1),subTab(payload, 2, 5),subTab(payload, 6, 9),subTab(payload, 10, 13));
		case PUSH:
			if (payload.length!=26) {
				System.err.println("payload et header incohérents");
				return null;
			}
			return new Push(h,subTab(payload, 24, 25),subTab(payload, 20, 23),subTab(payload, 0, 15),subTab(payload, 16, 19));
		case QUERY:
			if (payload.length<=3) {
				System.err.println("pas de critères de recherche");
				return null;
			}
			return new Query(h,subTab(payload, 0, 1),subTab(payload, 2, payload.length-1));
		case QUERY_HIT:
			if (payload.length<=11+16) {
				System.err.println("payload trop court");
				return null;
			}
			return new QueryHit(h,subTab(payload, 1, 2),subTab(payload, 3, 6),subTab(payload, 7, 10),subTab(payload, 11, payload.length-18), subTab(payload, payload.length-17, payload.length-1));
		default:
			break;
		}
		return null;
	}

	protected static short[] subTab(short[] t,int i, int j){
		if (j == -1){
			ArrayList<Short> al = new ArrayList<Short>();
			int k = i;
			
			while (t[k]!=0){
				al.add(t[k]);
				k++;
			}
			
			short[] tab = new short[k-i];
			
			for (int k2 = 0; k2 < al.size(); k2++) {
				tab[k2] = al.get(k2);
			}
			
			return tab;
		}
		else{
			short[] tab = new short[j-i+1];
			for(int k = i;k<j+1;k++){
				tab[k-i] = t[k];
			}
			return tab;
		}
	}

	protected static String stringFromTab(short[] tab) {
		String res = "";
		for (int i = 0; i < tab.length; i++) {
			res = res + (char)tab[i];
		}
		return res;
	}
	
	protected static short[] tabFromString(String s) {
		short[] res = new short[s.length()];
		for (int i = 0; i < s.length(); i++) {
			res[i] = (short) s.charAt(i);
		}
		return res;
	}
	
	protected static String stringFromStringTab(String[] s){
		String res = "";
		for (int i = 0; i < s.length; i++) {
			res = res +s[i]+" ";
		}
		return res;
	}
	
	protected static String stringFromResultTab(Result[] s){
		String res = "";
		for (int i = 0; i < s.length; i++) {
			res = res +s[i]+" ";
		}
		return res;
	}
	
	protected static String stringOfTab(short[] tab){
		String s= "";
		for (int i = 0; i < tab.length; i++) {
			s = s + hexa(tab[i])+" ";
		}
		return s;
	}
	
	private static String hexa(short i){
		int q = i/16;
		String s = hexFromInt(q);
		s = s+hexFromInt(i%16);
		return s;
	}

	private static String hexFromInt(int i){
		switch (i) {
		case 10:
			return "A";
		case 11:
			return "B";
		case 12:
			return "C";
		case 13:
			return "D";
		case 14:
			return "E";
		case 15:
			return "F";
			
		default:
			return ""+i;
		}
	}
}