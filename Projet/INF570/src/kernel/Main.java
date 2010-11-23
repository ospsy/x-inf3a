package kernel;
import gui.FenetrePrincipale;
import gui.Input;

import java.io.IOException;

import config.Settings;
import connexion.ConnexionManager;


public class Main {
	
	public static void close(){
		ConnexionManager.close();
		System.exit(0);
	}
	
	public static boolean loadSettings(){
		return Settings.load();
	}
	

	public static void main(String[] args){
		loadSettings();
		ConnexionManager.init(7777);
		FenetrePrincipale.launch();
		while(true){
			String s=Input.readString();
			if(s==null){
				ConnexionManager.close();
				break;
			}
			if(s.equals("EXIT")){
				close();
				break;
			}
			if(s.equals("PING"))
				ConnexionManager.ping();
			if(s.equals("QUERY"))
				ConnexionManager.query();
			String[] ss=s.split(" ");
			if(ss.length>=3){
				if(ss[0].equals("CONNECT"))
					ConnexionManager.connect(ss[1], Integer.parseInt(ss[2]));
			}
		}
		
	}

}
