package gui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

import connexion.ConnexionManager;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class FenetrePrincipale extends javax.swing.JFrame {
	private JMenuBar Menu;
	private JMenuItem jMenuItem1;
	private AbstractAction ExitAction;
	private JMenuItem Settings;
	private JMenu Peers;
	private JMenu jMenu1;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				FenetrePrincipale inst = new FenetrePrincipale();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public FenetrePrincipale() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("Newtella");
			{
				Menu = new JMenuBar();
				setJMenuBar(Menu);
				{
					jMenu1 = new JMenu();
					Menu.add(jMenu1);
					jMenu1.setText("Application");
					{
						Settings = new JMenuItem();
						jMenu1.add(Settings);
						jMenu1.add(getJMenuItem1());
						Settings.setText("Settings");
					}
				}
				{
					Peers = new JMenu();
					Menu.add(Peers);
					Peers.setText("Peers");
				}
			}
			pack();
			this.setSize(800, 600);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	private AbstractAction getExitAction() {
		if(ExitAction == null) {
			ExitAction = new AbstractAction("Exit", null) {
				public void actionPerformed(ActionEvent evt) {
					ConnexionManager.close();
					System.exit(0);
				}
			};
		}
		return ExitAction;
	}
	
	private JMenuItem getJMenuItem1() {
		if(jMenuItem1 == null) {
			jMenuItem1 = new JMenuItem();
			jMenuItem1.setAction(getExitAction());
		}
		return jMenuItem1;
	}

	public static void launch() {
		main(null);
		
	}

}
