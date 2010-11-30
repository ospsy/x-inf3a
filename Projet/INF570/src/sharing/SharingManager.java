package sharing;

import java.io.File;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTree;

import message.Result;

/**
 * Gère le dossier de partage.
 * On peut définir le chemin du dossier de partage par la méthode statique {@link #setSharedDirPath}
 * @author Malik
 *
 */
public class SharingManager {
	
	private static String sharedDirPath;
	
	private static int numberOfSharedFiles = 0;
	private static long sharedFilesSize = 0;
	private static final long UPDATE_PERIOD = 60000;
	
	private static Timer updateTimer = new Timer();
	private static Updater updater = new Updater();
	
	private static boolean timerLaunched = false;
	
	public synchronized static int getNumberOfSharedFiles() {
		return numberOfSharedFiles;
	}
	public synchronized static void setNumberOfSharedFiles(int n) {
		numberOfSharedFiles = n;
	}
	
	public synchronized static long getSharedFilesSize() {
		return sharedFilesSize;
	}
	public synchronized static void setSharedFilesSize(long s) {
		sharedFilesSize = s;
	}
	
	/**
	 * Définit le chemin d'accès du dossier de partage.
	 * Si le chemin ne correspond pas à un dossier existant, la fonction ne fait rien,
	 * elle envoie seulement un message d'erreur sur System.err
	 * @param path Le chemin du nouveau dossier de partage.
	 */
	public synchronized static void setSharedDirPath(String path) {
		File sharedDir = new File(path);
		if(!sharedDir.exists()) {
			System.err.println("Erreur : Le chemin spécifié pour le dossier de partage n'existe pas");
			return;
		}
		if(!sharedDir.isDirectory()) {
			System.err.println("Erreur : Le chemin spécifié pour le dossier de partage ne correspond pas à un répertoire");
			return;
		}
		sharedDirPath = path;
		updater.stop();
		if(timerLaunched) updater.cancel();
		else timerLaunched = true;
		updater = new Updater();
		updateTimer.schedule(updater, 0, UPDATE_PERIOD);
	}
	
	public synchronized static String getSharedDirPath() {
		return sharedDirPath;
	}
	
	/**
	 * renvoie le tableau de {@link Result} correspondant aux critères de recherche spécifiés en argument.
	 * @param criteria : critères de recherche
	 * @return
	 */
	public synchronized static Result[] search(String[] criteria) {
		LinkedList<Result> results = new LinkedList<Result>();
		
		File sharedDir = new File(SharingManager.getSharedDirPath());
		if(!sharedDir.exists()) {
			System.err.println("Echec de la mise à jour : Le chemin spécifié pour le dossier de partage n'existe pas");
			return null;
		}
		if(!sharedDir.isDirectory()) {
			System.err.println("Echec de la mise à jour : Le chemin spécifié pour le dossier de partage ne correspond pas à un répertoire");
			return null;
		}
		
		String[] loweredCriteria = new String[criteria.length];
		
		for(int i=0; i<criteria.length; i++) {
			loweredCriteria[i] = criteria[i].toLowerCase();
		}
		parcoursRecherche(sharedDir, loweredCriteria, results, new Integer(0));
		
		int resultsNb = results.size();
		Result[] resultsArray = new Result[resultsNb];
		int compteur = 0;
		for(Result res : results) {
			resultsArray[compteur++] = res;
		}
		
		return resultsArray;
	}
	
	private static void parcoursRecherche(File dir, String[] criteria, 
			LinkedList<Result> results, Integer currentIndex) {
		File[] fileList = dir.listFiles();
		File f;
		for(int i=0; i<fileList.length; i++) {
			f = fileList[i];
			if(f.isDirectory()) parcoursRecherche(f, criteria, results, currentIndex);
			else if(f.isFile()) {
				boolean matchCriteria = false;
				String fileName = f.getName().toLowerCase();
				for(int j=0; j<criteria.length; j++) {
					if(fileName.matches(".*" + criteria[j] + ".*")) {
						matchCriteria = true;
						break;
					}
				}
				if(matchCriteria) {
					results.addLast(new Result(currentIndex, f.length(), f.getName()));
				}
				currentIndex++;
			}
		}
	}
	public static JTree getJTree() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

/**
 * Gere le parcours récursif à l'intérieur du dossier de partage pour mettre à jour
 * les champs {@link SharingManager#numberOfSharedFiles} et {@link SharingManager#sharedFilesSize} de {@link SharingManager}
 * 
 * @author Malik
 *
 */
class Updater extends TimerTask {

	private int numberOfSharedFiles;
	private long sharedFilesSize;
	private boolean stop = false;
	
	@Override
	public void run() {
		File sharedDir = new File(SharingManager.getSharedDirPath());
		if(!sharedDir.exists()) {
			System.err.println("Echec de la mise à jour : Le chemin spécifié pour le dossier de partage n'existe pas");
			return;
		}
		if(!sharedDir.isDirectory()) {
			System.err.println("Echec de la mise à jour : Le chemin spécifié pour le dossier de partage ne correspond pas à un répertoire");
			return;
		}
		
		numberOfSharedFiles = 0;
		sharedFilesSize = 0;
		
		parcours(sharedDir); // modifie numberOfSharedFiles et sharedFilesSize
		if(stop) return;
		
		SharingManager.setNumberOfSharedFiles(numberOfSharedFiles);
		SharingManager.setSharedFilesSize(sharedFilesSize);
		
	}
	
	public void parcours(File dir) {
		File[] fileList = dir.listFiles();
		File f;
		for(int i=0; i<fileList.length; i++) {
			if(stop) return;
			f = fileList[i];
			if(f.isDirectory()) parcours(f);
			else if(f.isFile()) {
				numberOfSharedFiles++;
				sharedFilesSize+=f.length();
			}
		}
	}
	
	public synchronized void stop() { stop=true; }
}
