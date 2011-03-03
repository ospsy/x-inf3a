package gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;

public class Loading extends Screen {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel l;

	public Loading() {
		l = new JLabel();
		l.setText("Loading...");
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, l);
	}
}
