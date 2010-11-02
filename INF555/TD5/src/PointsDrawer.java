// INF555  Frank Nielsen
// Simple demo program for using JOGL
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import javax.vecmath.Point3d;

import com.sun.opengl.util.*;

class PointsDrawer implements GLEventListener, MouseListener, MouseMotionListener
{
   private GLU glu = new GLU();
   static float rotx=0.0f, roty=0.0f;
   static float mouseConst=0.5f;
   static int lastx, lasty;
   static private Point3d[] pts;

    public static void drawSet(Point3d[] points)
    {
	Frame frame = new Frame("INF555 | Point3d Drawer");
	GLCanvas canvas = new GLCanvas();
	canvas.addGLEventListener(new PointsDrawer());
	frame.add(canvas);
	frame.setSize(800,600);
	pts=points;

	final Animator animator = new Animator(canvas);
	frame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		new Thread(new Runnable() {
		    public void run() {
			animator.stop();
			System.exit(0);
		    }
		}).start();
	    }
	});
	
	frame.setVisible(true);
	animator.start();
    }

    public void init(GLAutoDrawable drawable)
    {
	   drawable.addMouseListener(this);		
	   drawable.addMouseMotionListener(this);	
	   
	   GL gl = drawable.getGL();
	   
	    gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        gl.glShadeModel(GL.GL_FLAT);
        
     
	}


    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
	   GL gl = drawable.getGL();

 	   
 	    gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, 1.0f, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        	
    }

    public void display(GLAutoDrawable drawable)
    {
		GL gl = drawable.getGL();
		int error = gl.glGetError();
		if (error != GL.GL_NO_ERROR)
			System.out.println("OpenGL Error" );
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(-0.5f, -0.5f, -4.0f);
        gl.glPushMatrix();
        gl.glRotatef(rotx,0.0f,1.0f,0.0f);
        gl.glRotatef(roty,1.0f,0.0f,0.0f);
        
        //tracé des axes
        gl.glColor3f(1, 0, 0);
        gl.glBegin(GL.GL_QUADS);           	
        gl.glVertex3f(0, 0, 0);	
        gl.glVertex3f(0, 0.02f, 0);	
        gl.glVertex3f(1f, 0.02f, 0);
        gl.glVertex3f(1f, 0, 0);
        gl.glEnd();	
        gl.glColor3f(0, 1, 0);
        gl.glBegin(GL.GL_QUADS);           	
        gl.glVertex3f(0, 0, 0);	
        gl.glVertex3f(0, 1f, 0);	
        gl.glVertex3f(0.02f, 1f, 0);
        gl.glVertex3f(0.02f, 0, 0);
        gl.glEnd();	
        gl.glColor3f(0, 0, 1);
        gl.glBegin(GL.GL_QUADS);           	
        gl.glVertex3f(0, 0, 0);	
        gl.glVertex3f(0, 0, 1f);	
        gl.glVertex3f(0.02f, 0, 1f);
        gl.glVertex3f(0.02f, 0, 0);
        gl.glEnd();	
        //Tracé des cubes
        for(int i=0;i<pts.length;i++){
        	Point3d pt=pts[i];
        	gl.glColor3d(pt.x, pt.y, pt.z);
        	drawCube(gl,pt.x,pt.y,pt.z,2/(double)pts.length);
        }
        gl.glPopMatrix();
        gl.glFlush();
        //System.out.println(rotx);
	}
    
    private void drawCube(GL gl, double x, double y, double z, double size){
    	double size2=size/2;
    	gl.glBegin(GL.GL_QUADS);           	
        gl.glVertex3d(x-size2, y+size2, z+size2);	
        gl.glVertex3d(x+size2, y+size2, z+size2);	
        gl.glVertex3d(x+size2, y-size2, z+size2);
        gl.glVertex3d(x-size2, y-size2, z+size2);
        gl.glEnd();	
        gl.glBegin(GL.GL_QUADS);  
        gl.glVertex3d(x-size2, y+size2, z-size2);	
        gl.glVertex3d(x+size2, y+size2, z-size2);	
        gl.glVertex3d(x+size2, y-size2, z-size2);
        gl.glVertex3d(x-size2, y-size2, z-size2);
        gl.glEnd();
        gl.glBegin(GL.GL_QUADS);           	
        gl.glVertex3d(x-size2, y+size2, z+size2);	
        gl.glVertex3d(x+size2, y+size2, z+size2);	
        gl.glVertex3d(x+size2, y+size2, z-size2);
        gl.glVertex3d(x-size2, y+size2, z-size2);
        gl.glEnd();
        gl.glBegin(GL.GL_QUADS);           	
        gl.glVertex3d(x-size2, y-size2, z+size2);	
        gl.glVertex3d(x+size2, y-size2, z+size2);	
        gl.glVertex3d(x+size2, y-size2, z-size2);
        gl.glVertex3d(x-size2, y-size2, z-size2);
        gl.glEnd();
        gl.glBegin(GL.GL_QUADS);           	
        gl.glVertex3d(x+size2, y-size2, z+size2);	
        gl.glVertex3d(x+size2, y+size2, z+size2);	
        gl.glVertex3d(x+size2, y+size2, z-size2);
        gl.glVertex3d(x+size2, y-size2, z-size2);
        gl.glEnd();
        gl.glBegin(GL.GL_QUADS);           	
        gl.glVertex3d(x-size2, y-size2, z+size2);	
        gl.glVertex3d(x-size2, y+size2, z+size2);	
        gl.glVertex3d(x-size2, y+size2, z-size2);
        gl.glVertex3d(x-size2, y-size2, z-size2);
        gl.glEnd();
    }

    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
    	lastx=e.getX();
    	lasty=e.getY();
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    public void mouseDragged(MouseEvent e)
    {
    	rotx+=(e.getX()-lastx)*mouseConst;
    	roty+=(e.getY()-lasty)*mouseConst;
    	lastx=e.getX();
    	lasty=e.getY();
    }

    public void mouseMoved(MouseEvent e)
    {
    }
}