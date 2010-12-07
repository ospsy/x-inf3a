package link;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sharing.SharingManager;

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
		int readBytesNb;
		byte[] bTab = new byte[l];
		while(n!=l) {
			readBytesNb = in.read(bTab, n, l - n);
			if(readBytesNb==-1) throw new IOException();
			n += readBytesNb;
		}
		short[] sTab = toShortTable(bTab);
		
		Message msg = Message.parseMessage(msgH, sTab);
		
		return msg;
	}
	
	/**
	 * Envoie vers le flux de sortie {@code out} le  message {@code m}.
	 * @param out Le flux de sortie vers lequel on envoie le message.
	 * @param m Le message à envoyer.
	 * @throws IOException
	 */
	public static void sendMessage(OutputStream out, Message m) throws IOException{
		out.write(toByteTable(m.toShortTab()));
		out.flush();
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
		int readBytesNb;
		byte[] bTab = new byte[MessageHeader.STANDARD_SIZE];
		while(n!=MessageHeader.STANDARD_SIZE) {
			readBytesNb = in.read(bTab, n, MessageHeader.STANDARD_SIZE - n);
			if(readBytesNb==-1) throw new IOException();
			n += readBytesNb;
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
	
	public static void sendFile(File file, OutputStream out) throws IOException {
		FileReader fr = new FileReader(file);
		int c;
		while((c = fr.read())!=-1)
			out.write(c);
		out.close();
	}
	
	public static void receiveFile(String fileName, InputStream in) throws IOException {
		String sharedDirectoryPath = SharingManager.getSharedDirPath();
		
		File file = new File(sharedDirectoryPath + File.pathSeparator + fileName);

		if(file.exists()) {
			String[] splitArr = fileName.split("\\.");

			String fileNameRoot = splitArr[0];
			for(int i=1; i<splitArr.length-1; i++)
				fileNameRoot += "." + splitArr[i];
			String fileNameExtension = "";
			if(splitArr.length>1) fileNameExtension = "." + splitArr[splitArr.length-1];

			int j=2;
			while(file.exists()) {
				file = new File(sharedDirectoryPath + File.pathSeparator +
						fileNameRoot + "_" + (j++) + fileNameExtension);
			}

		}
		
		file.createNewFile();
		
		FileWriter fw = new FileWriter(file);
		int c;
		while((c = in.read())!=-1) {
			fw.write(c);
		}
		fw.close();
	}
}
