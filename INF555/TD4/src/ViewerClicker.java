import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;


public class ViewerClicker extends JFrame{
	private ImageComponentClicker ic;

	public ViewerClicker(Image img) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ic = new ImageComponentClicker(img);
		add(ic);
		pack();
		setVisible(true);
		addMouseListener(ic);
		addMouseMotionListener(ic);
	}


	public Point[] getPoints() {
		while(ic.getN()<4){
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				System.exit(0);
			}
		}
		this.dispose();
		return ic.getPoints();
	}
}

class ImageComponentClicker extends ImageComponent implements MouseListener, MouseMotionListener{

	private Point[] points;
	private int n;
	private Point mousePt;

	public ImageComponentClicker(Image img) {
		super(img);
		points=new Point[4];
		n=0;
		setMousePt(new Point(0,0));
	}

	public Point[] getPoints() {
		return points;
	}

	public int getN() {
		return n;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		System.out.println(arg0.getX()+" "+arg0.getY());
		System.out.println(this.getX()+" "+this.getY());
		System.out.println(this.getAlignmentX()+" "+this.getAlignmentY());
		if(n<4){
			points[n]=new Point(arg0.getX()-this.getX(), arg0.getY()-this.getY());
			n++;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int r=2;
		g.setColor(Color.BLACK);
		for(int i=0;i<n;i++){
			g.drawOval(points[i].x-r, points[i].y-r, 2*r, 2*r);
			if(i>0)
				g.drawLine(points[i].x, points[i].y, points[i-1].x, points[i-1].y);
		}
		g.setColor(Color.RED);
		Point pt = getMousePt();
		g.drawOval(pt.x-r, pt.y-r, 2*r, 2*r);
		if(n>0)
			g.drawLine(points[n-1].x, points[n-1].y, pt.x, pt.y);
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setMousePt(e.getPoint());
		//repaint();
	}

	public synchronized Point getMousePt() {
		return mousePt;
	}

	public synchronized void setMousePt(Point mousePt) {
		this.mousePt = mousePt;
	}
}