package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import connexion.ConnexionManager;
import connexion.QueryResult;



public class Downloader extends  AbstractCellEditor implements TableCellEditor,ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton button;
	private static boolean saveDialog = false;
	private static JFileChooser filechooser= new JFileChooser();
	private QueryResult current;




	public Downloader(){
		button = new JButton();
		button.setText("download");
		button.addActionListener(this);



	}
	@Override
	public Component getTableCellEditorComponent(JTable tab, Object value,
			boolean arg2, int arg3, int arg4) {
		current = (QueryResult) value;
		return button;
	}

	@Override
	public Object getCellEditorValue() {

		return true;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (saveDialog){
			int inputReturnValue = filechooser.showSaveDialog(new JDialog());
			if(inputReturnValue == 0){
				File file = filechooser.getSelectedFile();
				file.canRead();
			}
			

		}
		
		ConnexionManager.download(current);
		
	}



}


class DownloaderRenderer extends JButton implements TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public DownloaderRenderer(){
		this.setOpaque(true);
	}


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
				 if (isSelected) {
				      setForeground(table.getSelectionForeground());
				      setBackground(table.getSelectionBackground());
				    } else{
				      setForeground(table.getForeground());
				      setBackground(UIManager.getColor("Button.background"));
				    }
		setText("download");
		return this;
	}

}


