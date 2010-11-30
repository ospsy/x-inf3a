package gui;


public class Out {
	private static OutputControler oc;

	public static void init(OutputControler occ) {
		oc = occ;
	}
	
	public static void println(String s){
	oc.printInConsole(s);
	}
}
