package gui;

import message.Result;


public class Out {
	private static OutputControler oc;

	public static void init(OutputControler occ) {
		oc = occ;
	}
	
	public static void println(String s){
		oc.printInConsole(s);
	}
	
	public static void displayResult(Result[] tab) {
		oc.displayResult(tab);
	}
	
	public static void displayVoisin(){
		oc.displayVoisin();
	}
}
