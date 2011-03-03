package rendu;

import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;

public class Sculpture extends scene3d{

	/**
	 * 
	 */
	public static final double initAngleX = -3*Math.PI/4;
	public static final double initAngleY = -5*Math.PI/12;
	private static final long serialVersionUID = 1L;
	private MouseXmove mouseX;
	private MouseYmove mouseY;


	@Override
	public void createScene() {

		etLuxPerpetua();	    
		dessinerRepere();
		testSphere();
	}

	public void testSphere(){
		float r = 0.3f;
		TransparencyAttributes ta = new TransparencyAttributes(1,0.6f);
		Appearance ap = new Appearance();
		ap.setTransparencyAttributes(ta);
		ColoringAttributes ca = new ColoringAttributes(new Color3f(0,0.5f,0.5f), ColoringAttributes.FASTEST);
		ap.setColoringAttributes(ca);
		Sphere s = new Sphere(r,1,40);
		s.setAppearance(ap);
		SCO.positionate(s, BGprincipal, new Vector3f());
	}


	private void etLuxPerpetua(){
		Color3f lightColor =  new Color3f(0.5f, 0.5f, 0.5f);
		Vector3f[] lightDirs = new Vector3f[6];
		DirectionalLight[] lights = new DirectionalLight[6];
		float d = 1f;
		lightDirs[0]  = new Vector3f(-d, -d, d);
		lightDirs[1] = new Vector3f(d,d,-d);
		lightDirs[2]  = new Vector3f(d,-d,-d);
		lightDirs[3] = new Vector3f(-d,d,d);
		lightDirs[4]  = new Vector3f(d,-d,d);
		lightDirs[5] = new Vector3f(-d,d,-d);
		for (int i = 0; i < lights.length; i++) {
			lights[i] = new DirectionalLight(lightColor, lightDirs[i]);
			lights[i].setInfluencingBounds(bounds);
			BGprincipal.addChild(lights[i]);
		}
	}

	private void dessinerRepere() {

		float a = 0.003f;
		float b = 0.5f;

		Sphere o = new Sphere(a,1,10);
		Sphere x = new Sphere(a,1,10);
		Sphere y = new Sphere(a,1,10);
		Sphere z = new Sphere(a,1,10);

		Cylinder ox = new Cylinder(a, b);
		Cylinder oy = new Cylinder(a, b);
		Cylinder oz = new Cylinder(a, b);

		Cone cx = new Cone(3*a,b/15);
		Cone cy = new Cone(3*a,b/15);
		Cone cz = new Cone(3*a,b/15);

		Appearance ax = new Appearance();
		Appearance ay = new Appearance();
		Appearance az = new Appearance();
		Appearance ao = new Appearance();
		ax.setColoringAttributes(new ColoringAttributes(new Color3f(1f, 0, 0), 1));
		ay.setColoringAttributes(new ColoringAttributes(new Color3f(0, 1f, 0), 1));	
		az.setColoringAttributes(new ColoringAttributes(new Color3f(0, 0, 1f), 1));	  
		ao.setColoringAttributes(new ColoringAttributes(new Color3f(1f, 1f, 1f), 1));

		x.setAppearance(ax);
		y.setAppearance(ay);
		z.setAppearance(az);

		cx.setAppearance(ax);
		cy.setAppearance(ay);
		cz.setAppearance(az);

		ox.setAppearance(ax);
		oy.setAppearance(ay);
		oz.setAppearance(az);
		o.setAppearance(ao);

		SCO.positionate(x, BGprincipal, new Vector3f(b,0,0));
		SCO.positionate(y, BGprincipal, new Vector3f(0,b,0));
		SCO.positionate(z, BGprincipal, new Vector3f(0,0,b));
		SCO.positionate(o, BGprincipal, new Vector3f(0,0,0));

		SCO.positionate(ox, BGprincipal, SCO.rotationZ(90), 
				new Vector3f(b/2,0,0));
		SCO.positionate(oy, BGprincipal, SCO.rotationY(0), 
				new Vector3f(0,b/2,0));
		SCO.positionate(oz, BGprincipal, SCO.rotationX(90), 
				new Vector3f(0,0,b/2));

		SCO.positionate(cx, BGprincipal, SCO.rotationZ(-90), 
				new Vector3f(b,0,0));
		SCO.positionate(cy, BGprincipal, SCO.rotationX(0), 
				new Vector3f(0,b,0));
		SCO.positionate(cz, BGprincipal, SCO.rotationX(90), 
				new Vector3f(0,0,b));

	}

	@Override
	public void setLinkCameraMouse(TransformGroup view) {
//		//initialise la vue
//		Transform3D initView = new Transform3D();
//		initView.rotZ(initAngleX);
//		subTG.setTransform(initView);//controlé par mouvement en x de la souris
//
//		Transform3D initView0 = new Transform3D();
//		initView0.rotX(initAngleY);
//		overTG.setTransform(initView0);//controlé par mouvement en y de la souris
//
//
//		mouseX = new MouseXmove();
//		mouseX.setTransformGroup(subTG);
//		mouseY = new MouseYmove();
//		mouseY.setTransformGroup(overTG);
//		clicListener clic = new clicListener(mouseX,mouseY);
//
//		mouseX.setSchedulingBounds(bounds);
//		mouseY.setSchedulingBounds(bounds);
//		clic.setSchedulingBounds(bounds);
//
//		subTG.addChild(mouseX);
//		overTG.addChild(mouseY);
//		subTG.addChild(clic);
		BranchGroup camera = new BranchGroup();
		CommandeCamera cmd = new CommandeCamera(view, 2f);
		cmd.setSchedulingBounds(bounds);
		camera.addChild(cmd);
		BGprincipal.addChild(camera);
		

	}


	public double getTeta(){
		return mouseY.getAngle();
	}

	public double getPhi(){
		return mouseX.getAngle();
	}

}





class MouseXmove extends MouseRotate {

	protected double zero;
	protected double x_vir ;
	protected double last_x ;
	protected boolean saute ;



	public MouseXmove(){
		super();
		saute = false;
		last_x = 0;
		this.setFactor(clicListener.sensibilite);//sensibilité
		zero = Sculpture.initAngleX/getXFactor();
		x_vir = zero;
	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		saute = true ;

	}


	public void transformChanged( Transform3D transform ) {
		if (saute){
			saute = false;
			last_x = this.x;
		}  
		x_vir = x_vir + (this.x-last_x);
		transformX.setIdentity();
		transformX.rotZ(x_vir*getXFactor());
		transform = transformX;
		transformGroup.setTransform(transform);
		last_x = this.x;
	}

	/**
	 * revoie l'angle de la camera selon le phi des coordonnees sphériques (repéré par rapport à l'angle xOy)
	 * @return
	 */
	public double getAngle(){
		return (-x_vir*getXFactor()-Math.PI/2)%(2*Math.PI);
	}

}


class MouseYmove extends MouseRotate {
	protected double borneInf;
	protected double borneSup;
	protected double zero;
	protected double y_vir;
	protected double last_y;
	protected boolean saute = false;

	public MouseYmove(){
		super();
		saute = false;
		this.setFactor(clicListener.sensibilite);//sensibilité
		zero =  (Sculpture.initAngleY/getXFactor());
		last_y = 0;
		borneSup = 0;
		borneInf = -Math.PI;
		y_vir = zero;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		saute = true ;
	}


	public void transformChanged( Transform3D transform ) {
		if (saute){
			saute = false;
			last_y = this.y;
		}
		y_vir = y_vir + (this.y-last_y);
		y_vir =  Math.max(Math.min((y_vir), borneSup/getYFactor() ),borneInf/getYFactor());
		transformY.setIdentity();
		transformY.rotX(y_vir*getYFactor());
		transform = transformY;
		transformGroup.setTransform(transform);
		last_y = this.y;
	}

	/**
	 * revoie l'angle de la camera selon le teta (colatitude) des coordonnees sphériques
	 * @return
	 */
	public double getAngle(){
		return (-y_vir)*getYFactor();

	}

}

class clicListener extends Behavior {

	private WakeupOnAWTEvent mouseEvent= new WakeupOnAWTEvent(MouseEvent.MOUSE_EVENT_MASK);

	private MouseXmove behaviorX;
	private MouseYmove behaviorY;
	protected static double sensibilite = 0.01;
	protected static boolean leftButton = false;
	protected static boolean rightButton = false;
	protected static boolean middleButton = false;

	public clicListener(MouseXmove behaviorX,
			MouseYmove behaviorY) {
		this.behaviorX=behaviorX;
		this.behaviorY=behaviorY;
	}

	@Override
	public void initialize() {

		wakeupOn(mouseEvent);


	}

	@SuppressWarnings("unchecked")
	@Override
	public void processStimulus(Enumeration arg0) {

		MouseEvent me = (MouseEvent) ((WakeupOnAWTEvent) arg0.nextElement()).getAWTEvent()[0];
		switch (me.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			switch (me.getButton()) {
			case 1:
				clicListener.leftButton = true;
				behaviorX.mousePressed(me);
				behaviorY.mousePressed(me);
				break;
			case 2:
				clicListener.middleButton = true;
				break;
			case 3:
				clicListener.rightButton = true;
				break;
			default:
				break;
			}

			break;
		case MouseEvent.MOUSE_RELEASED:
			switch (me.getButton()) {
			case 1:
				clicListener.leftButton = false;
				break;
			case 2:
				clicListener.middleButton = false;
				break;
			case 3:
				clicListener.rightButton = false;
				break;
			default:
				break;
			}
			break;	
		default:
			break;
		}
		wakeupOn(mouseEvent );
	}

}