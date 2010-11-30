package kernel;
import message.Message;
import message.Result;
import gui.FenetrePrincipale;
import gui.GUIHandler;
import gui.Input;
import gui.Out;
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
		FenetrePrincipale.launch();
		
		
		Out.init(new GUIHandler());
		ConnexionManager.init(7777);
		
		//test pour l'affichage des résultats
		Result[] resultSet = new Result[2];
		Result r1 = new Result(11, 512, "toto.txt");
		Result r2 = new Result(22, 3512, "panda.mp3");
		resultSet[0] = r1;
		resultSet[1] = r2;
		Out.displayResult(resultSet);
		//-------------------------------------
		
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
			if(s.equals("QUERY")){
				//ConnexionManager.query();
			}
			String[] ss=s.split(" ");
			if(ss.length>=3){
				if(ss[0].equals("CONNECT"))
					ConnexionManager.connect(ss[1], Integer.parseInt(ss[2]));
			}
		}
		
	}

}
