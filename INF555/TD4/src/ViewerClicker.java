import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class ViewerClicker extends ImageViewer implements MouseListener{
	ImageComponent c;
	private static final long serialVersionUID = 1L;
	int n=0;
	Point[] points;

	public ViewerClicker(Image img) {
		super(img);
		this.addMouseListener(this);
		points=new Point[4];
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		System.out.println(arg0.getXOnScreen()+" "+arg0.getYOnScreen());
		if(n<4){
			points[n]=new Point(arg0.getXOnScreen(), arg0.getYOnScreen());
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

	public Point[] getPoints() {
		while(n<4){
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				System.exit(0);
			}
		}
		this.dispose();
		return points;
	}


}
