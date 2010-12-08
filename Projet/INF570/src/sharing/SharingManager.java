package sharing;

import gui.Out;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

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
	private static HashMap<Integer, File> fileHashMap = new HashMap<Integer, File>();
	private static JTree jTree = new JTree();
	
	private static Updater updater = new Updater();
	
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
	public synchronized static void setFileHashMap(HashMap<Integer,File> fhm) {
		fileHashMap = fhm;
	}
	public synchronized static File getFileFromId(int id) {
		return fileHashMap.get(id);
	}
	public synchronized static JTree getJTree() {
		return jTree;
	}
	public synchronized static void setJTree(JTree tree) {
		jTree = tree;
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
		update();
	}
	
	/**
	 * Met à jour l'arbre des fichiers paragés.
	 */
	public static void update() {
		updater.run();
	}
	
	/**
	 * Renvoie le chemin du répertoire partagé.
	 * @return La chaîne de caractère correspondant au chemin du répertoire partagé.
	 */
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

}

/**
 * Gere le parcours récursif à l'intérieur du dossier de partage pour mettre à jour
 * les champs {@link SharingManager#numberOfSharedFiles} et {@link SharingManager#sharedFilesSize} de {@link SharingManager}
 * 
 * @author Malik
 *
 */
class Updater implements Runnable {

	private int numberOfSharedFiles;
	private long sharedFilesSize;
	private HashMap<Integer, File> fileHashMap;
	
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
		fileHashMap = new HashMap<Integer, File>();
		
		DefaultMutableTreeNode fileTree = new DefaultMutableTreeNode(sharedDir.getName());
		
		parcours(sharedDir, fileTree); // modifie numberOfSharedFiles et sharedFilesSize
		
		
		SharingManager.setNumberOfSharedFiles(numberOfSharedFiles);
		SharingManager.setSharedFilesSize(sharedFilesSize);
		SharingManager.setFileHashMap(fileHashMap);
		JTree jTree = new JTree(fileTree);
		SharingManager.setJTree(jTree);
		Out.majFiles();
		
	}
	
	public void parcours(File dir, DefaultMutableTreeNode dirNode) {
		File[] fileList = dir.listFiles();
		File f;
		for(int i=0; i<fileList.length; i++) {
			f = fileList[i];
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileWrapper(f));
			
			dirNode.add(childNode);
			if(f.isDirectory()) parcours(f,childNode);
			else if(f.isFile()) {
				fileHashMap.put(numberOfSharedFiles, f);
				numberOfSharedFiles++;
				sharedFilesSize+=f.length();
			}
		}
	}
	
}

/**
 * Classe utilisée uniquement pour donner une méthode toString particulière aux fichiers.
 * @author Malik
 *
 */
class FileWrapper {

	File file;
	
	FileWrapper(File file) {
		this.file = file;
	}
	
	public String toString() {
		if(file.isFile()) return file.getName() + " [" + file.length() + " octets]";
		else return file.getName();
	}
}
