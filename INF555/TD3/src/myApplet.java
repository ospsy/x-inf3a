import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;

import Jama.Matrix;


public class myApplet extends Applet {
	
	final static int width =800;
	final static int height =600;
	final static double scale=6;
	final static double Xmin=-scale;
	final static double Xmax=scale;
	final static double Ymin=-scale;
	final static double Ymax=scale;
	double a1,b1,c1,a2,b2,c2;
	double x,y,z;
	Matrix l1,l2;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public myApplet(){
		super();
	}
	
	public void init(){
		super.init();
		a1=Math.random();
		b1=Math.random();
		c1=Math.random();
		a2=Math.random();
		b2=Math.random();
		c2=Math.random();
		l1 = new Matrix(new double[] {a1,b1,c1},1);
		l2 = new Matrix(new double[] {a2,b2,c2},1);
		x=(b1*c2-b2*c1);
		y=(c1*a2-c2*a1);
		z=a1*b2-a2*b1;
	}
	
	public void paint(Graphics g){
		g.drawLine(tX(Xmin), tY(-(c1+a1*Xmin)/b1), width, tY(-(c1+a1*Xmax)/b1));
		g.drawLine(tX(Xmin), tY(-(c2+a2*Xmin)/b2), width, tY(-(c2+a2*Xmax)/b2));
		g.drawOval(tX(x/z)-10, tY(y/z)-10, 20, 20);
		
		g.setColor(Color.BLUE);
		g.drawLine(tX(Xmin), tY(-(z+x*Xmin)/y), width, tY(-(z+x*Xmax)/y));
		g.drawOval(tX(a1/c1)-10, tY(b1/c1)-10, 20, 20);
		g.drawOval(tX(a2/c2)-10, tY(b2/c2)-10, 20, 20);
	}
	
	private int tX(double x){
		return (int)((x-Xmin)/(Xmax-Xmin)*width);
	}
	
	private int tY(double y){
		return (int)((y-Ymin)/(Ymax-Ymin)*height);
	}
	
	public static void main(String[] args){
		myApplet applet = new myApplet();
		Frame appletFrame= new Frame("Some applet");
		appletFrame.setLayout(new GridLayout(1,0));
		
		appletFrame.setSize(width, height);
		appletFrame.setVisible(true);
		
		appletFrame.add(applet);
		applet.init();
		applet.start();
		applet.repaint();
	}
	
}
