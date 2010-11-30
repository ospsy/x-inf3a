package gui;

import connexion.ConnexionManager;
import connexion.Neighbour;
import message.Result;

public class ConsoleHandler implements OutputControler {

	@Override
	public void displayResult(Result[] tab) {
		System.out.println("---résultats---");
		for (Result n : tab) {
			System.out.println(n.toString());
		}
		System.out.println("-------------");
	}

	@Override
	public void displayVoisin() {
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

}
