package gui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import kernel.Main;

public class MainFrame {
	private static JFrame fenetrePrincipale;
	
	protected static void init(){
		fenetrePrincipale = new JFrame();
		
		WindowListener listener = new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {			}
			@Override
			public void windowIconified(WindowEvent arg0) {			}
			@Override
			public void windowDeiconified(WindowEvent arg0) {			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {			}
			@Override
			public void windowClosing(WindowEvent arg0) {
				Main.close();
			}
			@Override
			public void windowClosed(WindowEvent arg0) {			}
			@Override
			public void windowActivated(WindowEvent arg0) {			}
			
		};
		
		
		fenetrePrincipale.setTitle("Bernard Minet");
		fenetrePrincipale.addWindowListener(listener);
		fenetrePrincipale.setSize(Gui_Config.WIDTH,Gui_Config.EIGHT);
		fenetrePrincipale.setResizable(false);
		fenetrePrincipale.setLocationRelativeTo(null);
		fenetrePrincipale.setVisible(true);
	}
	
	
	protected static JFrame getFenetrePrincipale() {
		return fenetrePrincipale;
	}


	protected static void changeScreen(Screen p){
		fenetrePrincipale.getContentPane().removeAll();
		fenetrePrincipale.getContentPane().add(p,BorderLayout.CENTER);
		fenetrePrincipale.pack();
		fenetrePrincipale.setSize(Gui_Config.WIDTH,Gui_Config.EIGHT);
	}
}


