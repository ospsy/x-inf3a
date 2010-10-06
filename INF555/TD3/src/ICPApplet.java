// (c) Frank NIELSEN, INF555-TD3-2010
import java.applet.Applet;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.Collection;
import java.util.LinkedList;
import Jama.*;


public class ICPApplet extends Applet {
	private static final long serialVersionUID = -517368936032866861L;
	static Image img, imgo;
	static int raster []; int rastero [];
	static PixelGrabber pg;
	static int width, height;
	static double sigmas=4.0;
	static double sigmai=10.25;
	static int k=2;
	Collection<Point2D.Double> P;
	Collection<Point2D.Double> Q;

	public Matrix ICP(Collection<Point2D.Double> P, Collection<Point2D.Double> Q){
		
	}

	public void init() {
		Collection<Point2D.Double> P=new LinkedList<Point2D.Double>();
		Collection<Point2D.Double> Q=new LinkedList<Point2D.Double>();
		P.add(new Point2D.Double(2, 2));
		P.add(new Point2D.Double(6, 3));
		P.add(new Point2D.Double(5, 1));
		Q.add(new Point2D.Double(-0.8, -1));
		Q.add(new Point2D.Double(-3.7, -1.9));
		Q.add(new Point2D.Double(-1.5, -2.1));
		
	}

	public void paint(Graphics g) {
		
	}
}