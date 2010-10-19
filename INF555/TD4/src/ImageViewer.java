import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class ImageViewer extends JFrame {
    private ImageComponent ic;
	
    public ImageViewer(Image img) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ic = new ImageComponent(img);
        add(ic);
        pack();
        setVisible(true);
    }
}

@SuppressWarnings("serial")
class ImageComponent extends JComponent {

    private Image img;

    public ImageComponent(Image img) {
        this.img = img;
        setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
    }

    public void paint(Graphics g) {
        g.drawImage(img.toImage(), 0, 0, this);
    }
    
    
}