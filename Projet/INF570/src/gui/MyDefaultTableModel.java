package gui;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import connexion.QueryResult;

public class MyDefaultTableModel extends DefaultTableModel implements
		TableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isCellEditable(int row, int column) {
		
		return getValueAt(row, column).getClass().equals(QueryResult.class);
	}
}
