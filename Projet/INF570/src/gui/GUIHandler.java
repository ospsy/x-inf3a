package gui;

import java.util.LinkedList;

import javax.swing.table.DefaultTableModel;

import sharing.SharingManager;



import connexion.ConnexionManager;
import connexion.Neighbour;
import connexion.QueryResult;

public class GUIHandler implements OutputControler{

	public void printInConsole(String s) {
		FenetrePrincipale.display(s);
	}

	@Override
	public void displayQueryResults() {

		LinkedList<QueryResult> list = ConnexionManager.getQueryResults();
		DefaultTableModel model  = (DefaultTableModel) FenetrePrincipale.thi.getResultats().getModel();
		
		while (model.getRowCount()>0) {//vide l'affichage
			model.removeRow(0);
		}
		
		for(QueryResult b : list ){
			model.addRow(new Object[]{b.getName(),b.getSize(),b.getIP()});;
		}
	}

	@Override
	public synchronized void displayNeighbours() {
		LinkedList<Neighbour> list = ConnexionManager.getNeighbours();
		DefaultTableModel model  = (DefaultTableModel) FenetrePrincipale.thi.getTabPeer().getModel();
		
		while (model.getRowCount()>0) {//vide l'affichage
			model.removeRow(0);
		}
		
		for(Neighbour b : list ){
			model.addRow(new Object[]{b.getIP(),b.getPort(),b.getDistance(),b.getNumberOfSharedFiles(),b.getNumberOfKilobytesShared()});;
		}
	
		FenetrePrincipale.thi.getGraphe().setNeighbour(list);
		
	}

	@Override
	public void majFiles() {
		FenetrePrincipale.thi.setShared(SharingManager.getJTree());
		
	}
	
	

}
