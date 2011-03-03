package gui;



public class GraphicServer {
	private static Screen sculpture;
	private static Screen loading;
	
	public static void start(){
		
		MainFrame.init();
	
	}

	private static Screen getSculpture() {
		if(sculpture == null){
			sculpture = new SculptureScene();
		}
		return sculpture;

	}
	
	public static void launchLaunding(){
		MainFrame.changeScreen(getLoading());
	}
	

	public static Screen getLoading() {
		if(loading == null){
			loading = new Loading();

		}
		return loading;
	}

	public static void launchSculpture(){
		launchLaunding();
		MainFrame.changeScreen(getSculpture());
		
	}
}
