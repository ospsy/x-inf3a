package kernel;

import gui.GraphicServer;

public class Main {
	public static void main(String[] args) {
		
		GraphicServer.start();
		GraphicServer.launchSculpture();
		
	}

	public static void close() {
		// TODO Auto-generated method stub
		System.exit(0);
	}
}
