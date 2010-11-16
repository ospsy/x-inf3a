package link;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import message.Message;
import message.MessageHeader;

/**
 * Classe contenant les fonctions statiques de lecture et d'écriture des messages.
 * @see {@link #readMessage}, {@link #sendMessage}
 * @author Malik
 *
 */
public class Link {

	/**
	 * Lit sur le flux d'entrée {@code in} le bon nombre d'octet
	 * pour renvoyer le {@link Message} correspondant.
	 * @param in Le flux d'entrée sur lequel on lit.
	 * @return Le MessageHeader lu.
	 * @throws IOException
	 */
	public static Message readMessage(InputStream in) throws IOException{
		MessageHeader msgH = readMessageHeader(in);
		int l = msgH.getPayloadLength();
		
		int n=0;
		byte[] bTab = new byte[l];
		while(n!=l) {
			n += in.read(bTab, n, l - n);
		}
		short[] sTab = toShortTable(bTab);
		
		Message msg = Message.parseMessage(msgH, sTab);
		
		return msg;
	}
	
	/**
	 * Lit sur le flux d'entrée {@code in} le bon nombre d'octet
	 * pour renvoyer le {@link Message} correspondant.
	 * @param out Le flux de sortie vers lequel on envoie le message.
	 * @return Le MessageHeader lu.
	 * @throws IOException
	 */
	public static void sendMessage(OutputStream out, Message m) throws IOException{
		out.write(toByteTable(m.toShortTab()));
	}
	
	/**
	 * Lit sur le flux d'entrée in les {@link MessageHeader#STANDARD_SIZE} premiers octets
	 * et renvoie le {@link MessageHeader} correspondant.
	 * @param in Le flux d'entrée sur lequel on lit.
	 * @return Le MessageHeader lu.
	 * @throws IOException
	 */
	private static MessageHeader readMessageHeader(InputStream in) throws IOException {
		
		int n=0;
		byte[] bTab = new byte[MessageHeader.STANDARD_SIZE];
		while(n!=MessageHeader.STANDARD_SIZE) {
			n += in.read(bTab, n, MessageHeader.STANDARD_SIZE - n);
		}
		short[] sTab = toShortTable(bTab);
		MessageHeader msgH = new MessageHeader(sTab);
		
		return msgH;
	}
	
	/**
	 * Convertit un tableau de {@code short} en tableau de {@code byte}
	 * @param sTab Tableau de {@code short} à convertir
	 * @return tableau de {@code byte}
	 */
	private static byte[] toByteTable(short[] sTab) {
		int l = sTab.length;
		byte[] bTab = new byte[l];
		for(int i=0; i<l; i++)
			bTab[i] = (byte) sTab[i];
		
		return bTab;
	}
	
	/**
	 * Convertit un tableau de {@code byte} en tableau de {@code short}
	 * @param bTab Tableau de {@code byte} à convertir
	 * @return tableau de {@code short}
	 */
	private static short[] toShortTable(byte[] bTab) {
		int l = bTab.length;
		short[] sTab = new short[l];
		for(int i=0; i<l; i++)
			sTab[i] = (short) (bTab[i]&0xFF);
		return sTab;
	}
}
