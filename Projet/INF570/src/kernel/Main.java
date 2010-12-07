package kernel;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		test();
		loadSettings();
		FenetrePrincipale.launch();
		
		
		Out.init(new GUIHandler());
		ConnexionManager.init(7777);
		
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

	private static void test() {
		Pattern p = Pattern.compile("^GET /get/[0-9]+/[a-zA-Z_0-9.-]+/ HTTP/1.0\r\n");
		String s = "GET /get/2/a-b/ HTTP/1.0\r\n";
		String ss = s.substring(9);System.out.println(ss);
		StringTokenizer st = new StringTokenizer(ss,"/");
		System.out.println(st.nextToken());
		System.out.println(st.nextToken());
		Matcher m = p.matcher(s);
		System.out.println(m.matches());
	}

}
