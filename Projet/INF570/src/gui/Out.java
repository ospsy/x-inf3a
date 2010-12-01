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
	
	public static void displayQueryResults() {
		oc.displayQueryResults();
	}
	
	public static void displayVoisin(){
		oc.displayNeighbours();
	}
	
	public static void majFiles(){
		oc.majFiles();
	}
}
