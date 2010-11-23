import java.io.IOException;

import message.Ping;

import config.Settings;
import connexion.ConnexionManager;


public class Main {
	
	public static boolean loadSettings(){
		return Settings.load();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		loadSettings();
		ConnexionManager.init(7777);
		
		while(true){
			String s=Keyboard.readString();
			if(s==null){
				ConnexionManager.close();
				break;
			}
			if(s.equals("EXIT")){
				ConnexionManager.close();
				break;
			}
			if(s.equals("PING"))
				ConnexionManager.ping();
			String[] ss=s.split(" ");
			if(ss.length>=3){
				if(ss[0].equals("CONNECT"))
					ConnexionManager.connect(ss[1], Integer.parseInt(ss[2]));
			}
		}
		
	}

}
