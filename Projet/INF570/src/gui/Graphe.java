package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JPanel;
import connexion.Neighbour;

public class Graphe extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int w;
	private int h;
	private LinkedList<Neighbour> nodes;
	private LinkedList<Color> colors;
	private Font font = new Font(Font.SANS_SERIF,Font.PLAIN,10);
	
	
	public Graphe(int w, int h) {
		super();
		this.w = w;
		this.h = h;
		this.nodes = new LinkedList<Neighbour>();
		this.colors = new LinkedList<Color>();
		
		
		
		for (int i = 0; i < 3; i++) {
			colors.add(new Color(230,230,230));
			colors.add(new Color(204,207,242));
			colors.add(new Color(170,177,250));
		}
		
	}
	
	public void setNeighbour(LinkedList<Neighbour> n) {
		nodes = n;
		this.paintImmediately(this.getBounds());
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		HashMap<Integer, LinkedList<String>> set = new HashMap<Integer, LinkedList<String>>();
		for (Neighbour nei : nodes) {
			if(!set.containsKey(nei.getDistance()))set.put(nei.getDistance(), new LinkedList<String>());
			set.get(nei.getDistance()).add(nei.getIP());
		}
		int maxR = (int) (w*0.4);
		int disc = set.keySet().size();
		int k = disc;
		LinkedList<Integer> indices = new LinkedList<Integer>();
		for(int i : set.keySet()){
			indices.addFirst(i);
		}
		for(int i : indices){
			int r = (int) (k*maxR*1./disc);
			drawDisk(r,colors.get(i%3), g);
			k--;
		}
		k = disc;
		for(int i : indices){
			int r = (int) (k*maxR*1./disc);
			drawOnDisk(r, set.get(i), g);
			k--;
		}
		
		
		
	}
	
	
	private void drawDisk(int rayon, Color c,Graphics g){
		g.setColor(c);
		g.fillOval((int) (w*1./2-rayon),(int) ( h*1./2-rayon*1./2), 2*rayon, rayon);
		
	}
	
	private void drawOnDisk(int rayon, LinkedList<String> labels,Graphics g){
		g.setColor(new Color(0,0,155));
		int n = labels.size();
		int k = 0;
		double offset = Math.random()*2*3.1415;
		for (String string : labels) {
			int x = (int) (w*1./2+rayon*Math.cos(k*2*Math.PI/n+offset));
			int y = (int) (h*1./2+rayon*Math.sin(k*2*Math.PI/n+offset)/2);
			g.setColor(Color.RED);
			g.fillOval((int) (x-4),(int) (y-4),8, 8);
			g.setFont(font);
			g.setColor(Color.BLUE);
			g.drawString(string, x-5*string.length()/2, y-5);
			k++;
		}
		
		
		
	}
	
	
}
