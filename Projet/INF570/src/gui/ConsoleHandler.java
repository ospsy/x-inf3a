package gui;

import java.util.LinkedList;

import connexion.ConnexionManager;
import connexion.Neighbour;
import connexion.QueryResult;

public class ConsoleHandler implements OutputControler {

	@Override
	public void displayQueryResults() {
		LinkedList<QueryResult> tab = ConnexionManager.getQueryResults();
		System.out.println("---résultats---");
		for (QueryResult n : tab) {
			System.out.println(n.toString());
		}
		System.out.println("-------------");
		
	}

	@Override
	public void displayNeighbours() {
		System.out.println("---voisins---");
		for (Neighbour n : ConnexionManager.getNeighbours()) {
			System.out.println(n.toString());
		}
		System.out.println("-------------");
	}

	@Override
	public void printInConsole(String s) {
		System.out.println(s);
	}

	@Override
	public void majFiles() {
		
		
	}

}
