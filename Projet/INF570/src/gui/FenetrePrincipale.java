package gui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import kernel.Main;
import sharing.SharingManager;
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

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuBar Menu;
	private JPanel Console;
	private JTabbedPane Multitab;
	private JScrollPane jScrollPane1;
	private AbstractAction connect;
	private JButton jButton1;
	private JButton Connect;
	private AbstractAction pingClicked;
	private JButton Query;
	private JPanel Buttons;
	private Graphe graphe;
	private JScrollPane jScrollPane4;
	private JTree shared;
	private JPanel sharedFiles;
	private JLabel critereslabel;
	private JTextField criteria;
	private JScrollPane jScrollPane3;
	private JTable resultats;
	private JScrollPane jScrollPane2;
	private JLabel peerLabel;
	private JTable tabPeer;
	private JTabbedPane peerVisualisation;
	private JPanel jPanel2;
	private AbstractAction query;
	private JButton QUERY;
	private JButton PING;
	private JPanel ButtonsContainer;
	private AbstractAction saisieClavier;
	private JTextField Saisie;
	private JPanel jPanel1;
	private static JTextPane Sortie;
	private JPanel ConsoleReceiver;
	private JScrollPane Principal;
	private JMenuItem jMenuItem1;
	private AbstractAction ExitAction;
	private JMenuItem Settings;
	private JMenu Peers;
	private JMenu jMenu1;
	protected static FenetrePrincipale thi;


	
	
	public FenetrePrincipale() {
		super();
		thi = this;
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
			getSortie();
			this.setSize(809, 600);
			this.setResizable(false);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	private AbstractAction getExitAction() {
		if(ExitAction == null) {
			ExitAction = new AbstractAction("Exit", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

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
				FenetrePrincipale inst = new FenetrePrincipale();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
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
			ConsoleReceiver.setBackground(new java.awt.Color(224,224,224));
			ConsoleReceiver.add(getMultitab());
			ConsoleReceiver.add(getJPanel1());
			ConsoleReceiver.add(getJPanel2());
		}
		return ConsoleReceiver;
	}
	
	private JTextPane getSortie() {
		if(Sortie == null) {
			Sortie = new JTextPane();
			Sortie.setText("");
			Sortie.setBounds(37, -4, 528, 138);
			Sortie.setPreferredSize(new java.awt.Dimension(617, 152));
			Sortie.setEditable(false);
		}
		return Sortie;
	}
	
	private JScrollPane getJScrollPane1() {
		if(jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setBounds(12, 24, 623, 161);
			jScrollPane1.setViewportView(getSortie());
		}
		return jScrollPane1;
	}
	
	private JTabbedPane getMultitab() {
		if(Multitab == null) {
			Multitab = new JTabbedPane();
			Multitab.setBounds(11, 243, 765, 287);
			Multitab.setBorder(BorderFactory.createTitledBorder(""));
			Multitab.setBackground(new java.awt.Color(224,224,224));
			Multitab.setOpaque(true);
			Multitab.addTab("Console", null, getConsole(), null);
			Multitab.addTab("Files", null, getSharedFiles(), null);
		}
		return Multitab;
	}
	
	private JPanel getConsole() {
		if(Console == null) {
			Console = new JPanel();
			Console.setLayout(null);
			Console.setBounds(48, 302, 702, 240);
			Console.setPreferredSize(new java.awt.Dimension(740, 229));
			Console.setBackground(new java.awt.Color(224,224,224));
			Console.add(getJScrollPane1());
			Console.add(getSaisie());
			Console.add(getButtonsContainer());
			Console.add(getButtons());
		}
		return Console;
	}
	
	private JPanel getJPanel1() {
		if(jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(null);
			jPanel1.setBackground(new java.awt.Color(224,224,224));
			jPanel1.setBounds(490, 6, 286, 231);
			jPanel1.setBorder(BorderFactory.createTitledBorder(""));
			jPanel1.add(getPeerVisualisation());
			jPanel1.add(getPeerLabel());
		}
		return jPanel1;
	}
	
	private JTextField getSaisie() {
		if(Saisie == null) {
			Saisie = new JTextField();
			Saisie.setBounds(12, 197, 623, 27);
			Saisie.setAction(getSaisieClavier());
		}
		return Saisie;
	}
	
	private AbstractAction getSaisieClavier() {
		if(saisieClavier == null) {
			saisieClavier = new AbstractAction("saisieClavier", null) {
				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					
					Input.send(Saisie.getText());
					Saisie.setText("");
				}
			};
		}
		return saisieClavier;
	}

	protected static void display(String s){
		Sortie.setText(Sortie.getText()+'\n'+s);
	}

	private void close() {
		Main.close();
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
	
	private JPanel getButtonsContainer() {
		if(ButtonsContainer == null) {
			ButtonsContainer = new JPanel();
			BoxLayout ButtonsContainerLayout = new BoxLayout(ButtonsContainer, javax.swing.BoxLayout.Y_AXIS);
			ButtonsContainer.setLayout(ButtonsContainerLayout);
			ButtonsContainer.setBounds(641, 27, 87, 200);
			ButtonsContainer.setBackground(new java.awt.Color(224,224,224));
			ButtonsContainer.setLocation(new java.awt.Point(0, 0));
			ButtonsContainer.setVisible(false);
			ButtonsContainer.add(getPING());
			ButtonsContainer.add(getQUERY());
		}
		return ButtonsContainer;
	}
	
	private JButton getPING() {
		if(PING == null) {
			PING = new JButton();
			BoxLayout PINGLayout = new BoxLayout(PING, javax.swing.BoxLayout.Y_AXIS);
			PING.setLayout(PINGLayout);
			PING.setText("PING");
			PING.setPreferredSize(new java.awt.Dimension(72, 28));
		}
		return PING;
	}

	private JButton getQUERY() {
		if(QUERY == null) {
			QUERY = new JButton();
			BoxLayout QUERYLayout = new BoxLayout(QUERY, javax.swing.BoxLayout.Y_AXIS);
			QUERY.setLayout(QUERYLayout);
			QUERY.setText("QUERY");
		}
		return QUERY;
	}
	
	private JPanel getButtons() {
		if(Buttons == null) {
			Buttons = new JPanel();
			BorderLayout ButtonsLayout = new BorderLayout();
			Buttons.setLayout(ButtonsLayout);
			Buttons.setBounds(646, 24, 90, 199);
			Buttons.setBackground(new java.awt.Color(224,224,224));
			Buttons.add(getJButton1(), BorderLayout.NORTH);
			Buttons.add(getConnect(), BorderLayout.SOUTH);
		}
		return Buttons;
	}

	private JButton getQuery() {
		if(Query == null) {
			Query = new JButton();
			Query.setText("GO !");
			Query.setAction(getQueryx());
			Query.setBounds(407, 2, 63, 23);
		}
		return Query;
	}

	private AbstractAction getPingClicked() {
		if(pingClicked == null) {
			pingClicked = new AbstractAction("PING", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					ConnexionManager.ping();
				}
			};
		}
		return pingClicked;
	}
	
	private JButton getConnect() {
		if(Connect == null) {
			Connect = new JButton();
			Connect.setText("Connect");
			Connect.setPreferredSize(new java.awt.Dimension(90, 90));
			Connect.setAction(getConnectx());
		}
		return Connect;
	}
	
	private JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("PING");
			jButton1.setPreferredSize(new java.awt.Dimension(90, 90));
			jButton1.setAction(getPingClicked());
		}
		return jButton1;
	}
	
	private AbstractAction getConnectx() {
		if(connect == null) {
			connect = new AbstractAction("Connect", null) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent evt) {
					ConnexionForm form= new ConnexionForm();
					form.setVisible(true);
					
				}
			};
		}
		return connect;
	}
	
	private AbstractAction getQueryx() {
		if(query == null) {
			query = new AbstractAction("GO !", null) {
				/**
				* 
				*/
				private static final long serialVersionUID = 1L;
				
				public void actionPerformed(ActionEvent evt) {
					if (criteria.getText().length() == 0){
						JOptionPane.showMessageDialog(new JFrame(), "Veuiller entrer au moins un critère","Erreur",JOptionPane.ERROR_MESSAGE);
					}
					String[] criter = criteria.getText().split(" ");
					if (criter.length == 0){
						JOptionPane.showMessageDialog(new JFrame(), "Veuiller entrer au moins un critère","Erreur",JOptionPane.ERROR_MESSAGE);
					}
					else{
						ConnexionManager.query(criter);
					}
				}
			};
		}
		return query;
	}
	
	private JPanel getJPanel2() {
		if(jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(null);
			jPanel2.setBackground(new java.awt.Color(224,224,224));
			jPanel2.setBorder(BorderFactory.createTitledBorder(""));
			jPanel2.setBounds(11, 6, 473, 231);
			jPanel2.add(getJScrollPane3());
			jPanel2.add(getQuery());
			jPanel2.add(getCriteria());
			jPanel2.add(getCritereslabel());
		}
		return jPanel2;
	}
	
	private JTabbedPane getPeerVisualisation() {
		if(peerVisualisation == null) {
			peerVisualisation = new JTabbedPane();
			peerVisualisation.setBounds(3, 1, 280, 225);
			peerVisualisation.addTab("Graphe", null, getGraphe(), null);
			peerVisualisation.addTab("Tableau", null, getJScrollPane2(), null);
		}
		return peerVisualisation;
	}

	public JTable getTabPeer() {
		if(tabPeer == null) {
			
			DefaultTableModel model = 
				new DefaultTableModel(
						new String[][]{},
						new String[] { "ip","port", "dist","number of files", "total size (kB)" });
			tabPeer = new JTable();
			tabPeer.setModel(model);
			tabPeer.setDragEnabled(false);
			tabPeer.setRowSelectionAllowed(false);
		 	tabPeer.setAutoCreateRowSorter(true);
			tabPeer.setGridColor(new java.awt.Color(217,209,155));
			
		}
		return tabPeer;
	}
	
	
	
	
	private JLabel getPeerLabel() {
		if(peerLabel == null) {
			peerLabel = new JLabel();
			peerLabel.setText("Peers");
			peerLabel.setBounds(228, 5, 50, 19);
			peerLabel.setFont(new java.awt.Font("SansSerif",1,14));
			peerLabel.setAlignmentX(0.5f);
			peerLabel.setForeground(new java.awt.Color(0,0,128));
		}
		return peerLabel;
	}
	
	private JScrollPane getJScrollPane2() {
		if(jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setPreferredSize(new java.awt.Dimension(280, 195));
			jScrollPane2.setViewportView(getTabPeer());
			
		}
		return jScrollPane2;
	}
	
	public JTable getResultats() {
		if(resultats == null) {			
			TableModel resultatsModel = 
				new DefaultTableModel(
						new String[][] {  },
						new String[] { "nom", "taille" , "peer" });
			resultats = new JTable();
			resultats.setModel(resultatsModel);
			resultats.setDragEnabled(false);
			resultats.setRowSelectionAllowed(false);
			resultats.setAutoCreateRowSorter(true);
			resultats.setGridColor(new java.awt.Color(217,209,155));
		
		}
		return resultats;
	}
	
	private JScrollPane getJScrollPane3() {
		if(jScrollPane3 == null) {
			jScrollPane3 = new JScrollPane();
			jScrollPane3.setBounds(3, 24, 467, 202);
			jScrollPane3.setViewportView(getResultats());
		}
		return jScrollPane3;
	}
	
	private JTextField getCriteria() {
		if(criteria == null) {
			criteria = new JTextField();
			criteria.setBounds(153, 2, 254, 23);
			criteria.setFont(new java.awt.Font("SansSerif",0,10));
		}
		return criteria;
	}
	
	private JLabel getCritereslabel() {
		if(critereslabel == null) {
			critereslabel = new JLabel();
			critereslabel.setText("Nouvelle recherche");
			critereslabel.setBounds(9, 5, 144, 16);
			critereslabel.setFont(new java.awt.Font("SansSerif",1,14));
			critereslabel.setForeground(new java.awt.Color(0,0,128));
		}
		return critereslabel;
	}
	
	private JPanel getSharedFiles() {
		if(sharedFiles == null) {
			sharedFiles = new JPanel();
			BorderLayout sharedFilesLayout = new BorderLayout();
			sharedFiles.setLayout(sharedFilesLayout);
			sharedFiles.add(getJScrollPane4(), BorderLayout.CENTER);
		}
		return sharedFiles;
	}
	
	public JTree getShared() {
		if(shared == null) {
			shared = SharingManager.getJTree();
		}
		return shared;
	}
	
	private JScrollPane getJScrollPane4() {
		if(jScrollPane4 == null) {
			jScrollPane4 = new JScrollPane();
			jScrollPane4.setPreferredSize(new java.awt.Dimension(737, 229));
			jScrollPane4.setViewportView(getShared());
		}
		return jScrollPane4;
	}
	
	public Graphe getGraphe() {
		if(graphe == null) {
			graphe = new Graphe(280,195);
			
		}
		
		return graphe;
	}

	public static void setShared(JTree jTree) {
		thi.shared = jTree;
		
	}

}
