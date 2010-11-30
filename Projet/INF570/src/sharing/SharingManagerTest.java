package sharing;

import message.Result;

public class SharingManagerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test2();

	}

	public static void test1() {

		int DT = 1;
		int t = 0;
		int nMemo = 0, n;
		long sMemo = 0, s;

		while(true) {

			switch (t) {
			case 300:
				SharingManager.setSharedDirPath("D:/Malik/Polytechnique/info3a/P2P/TD1");
				break;
			case 1000:
				SharingManager.setSharedDirPath("D:/Malik/Polytechnique");
				break;
			case 6300:
				SharingManager.setSharedDirPath("D:/Malik/Polytechnique/info3a/P2P/TD2");
				break;


			default:
				break;
			}

			n = SharingManager.getNumberOfSharedFiles();
			s = SharingManager.getSharedFilesSize();
			if(n!=nMemo || s!=sMemo) {
				nMemo = n; sMemo = s;
				System.out.println(t + "ms : " + n + " files ; " + s + " octets.");
			}

			try {
				Thread.sleep(DT); t+=DT;
			} catch (InterruptedException e) {}
		}

	}
	
	public static void test2() {
		SharingManager.setSharedDirPath("D:/Malik/Polytechnique/info3a/P2P");
		
		String[] criteria = new String[2];
		criteria[0] = "tD";
		criteria[1] = "mesSA";
		displayResults(SharingManager.search(criteria));
		
	}
	public static void displayResults(Result[] results) {
		
		System.out.println("____________________________________________");
		for(int i=0; i<results.length; i++) {
			System.out.println(results[i]);
		}
		System.out.println("____________________________________________\n");

	}
}
