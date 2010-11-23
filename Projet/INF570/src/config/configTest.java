package config;

public class configTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Settings s= new Settings();	
		s.load();
		s.save();
		System.out.println(s.getSharePath());
		System.out.println(s.getVersion());

	}

}
