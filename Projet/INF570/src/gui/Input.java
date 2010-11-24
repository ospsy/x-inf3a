package gui;
import java.util.concurrent.LinkedBlockingQueue;

public class Input {

	private static LinkedBlockingQueue<String> buffer = new LinkedBlockingQueue<String>();
	
	public static String readString() {
		try {
			return buffer.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void send(String text) {
		try {
			FenetrePrincipale.display(text);
			buffer.put(text);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
