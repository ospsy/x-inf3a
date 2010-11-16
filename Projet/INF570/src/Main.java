import java.io.IOException;

import message.Ping;

import connexion.ConnexionManager;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		ConnexionManager.init(7777);
		
		while(true){
			String s=Keyboard.readString();
			if(s.equals("EXIT")){
				ConnexionManager.close();
				break;
			}
			if(s.equals("PING"))
				ConnexionManager.sendAll(new Ping(4,0), null);
			String[] ss=s.split(" ");
			if(ss.length>=3){
				if(ss[0].equals("CONNECT"))
					ConnexionManager.connect(ss[1], Integer.parseInt(ss[2]));
			}
		}
		
	}

}
