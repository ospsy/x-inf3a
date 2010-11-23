package gui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import principal.Entree;

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
public class FenetrePrincipale extends javax.swing.JFrame implements WindowListener {
	private JMenuBar Menu;
	private JPanel Console;
	private JTabbedPane Multitab;
	private JScrollPane jScrollPane1;
	private AbstractAction saisieClavier;
	private JTextField Saisie;
	private JPanel jPanel1;
	private JTextPane Sortie;
	private JPanel ConsoleReceiver;
	private JScrollPane Principal;
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
			this.setTitle("Newtella");
			this.addWindowListener(this);
			getContentPane().add(getPrincipal(), BorderLayout.CENTER);
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
			this.setSize(809, 600);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	private AbstractAction getExitAction() {
		if(ExitAction == null) {
			ExitAction = new AbstractAction("Exit", null) {
				public void actionPerformed(ActionEvent evt) {
					close();
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
	
	private JScrollPane getPrincipal() {
		if(Principal == null) {
			Principal = new JScrollPane();
			Principal.setViewportView(getConsoleReceiver());
		}
		return Principal;
	}
	
	private JPanel getConsoleReceiver() {
		if(ConsoleReceiver == null) {
			ConsoleReceiver = new JPanel();
			ConsoleReceiver.setPreferredSize(new java.awt.Dimension(784, 311));
			ConsoleReceiver.setLayout(null);
			ConsoleReceiver.add(getMultitab());
		}
		return ConsoleReceiver;
	}
	
	private JTextPane getSortie() {
		if(Sortie == null) {
			Sortie = new JTextPane();
			Sortie.setText("");
			Sortie.setBounds(37, -4, 528, 138);
			Sortie.setEditable(false);
		}
		return Sortie;
	}
	
	private JScrollPane getJScrollPane1() {
		if(jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setBounds(29, 24, 530, 140);
			jScrollPane1.setViewportView(getSortie());
		}
		return jScrollPane1;
	}
	
	private JTabbedPane getMultitab() {
		if(Multitab == null) {
			Multitab = new JTabbedPane();
			Multitab.setBounds(58, 242, 648, 257);
			Multitab.addTab("Console", null, getConsole(), null);
			Multitab.addTab("Peers", null, getJPanel1(), null);
		}
		return Multitab;
	}
	
	private JPanel getConsole() {
		if(Console == null) {
			Console = new JPanel();
			Console.setLayout(null);
			Console.add(getJScrollPane1());
			Console.add(getSaisie());
		}
		return Console;
	}
	
	private JPanel getJPanel1() {
		if(jPanel1 == null) {
			jPanel1 = new JPanel();
		}
		return jPanel1;
	}
	
	private JTextField getSaisie() {
		if(Saisie == null) {
			Saisie = new JTextField();
			Saisie.setBounds(29, 184, 530, 26);
			Saisie.setAction(getSaisieClavier());
		}
		return Saisie;
	}
	
	private AbstractAction getSaisieClavier() {
		if(saisieClavier == null) {
			saisieClavier = new AbstractAction("saisieClavier", null) {
				
				public void actionPerformed(ActionEvent evt) {
					
					Input.send(Saisie.getText());
					Saisie.setText("");
				}
			};
		}
		return saisieClavier;
	}



	private void close() {
		Entree.close();
	}
	
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {
		this.close();
	}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
	

}
