package rendu;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.vecmath.Vector3f;

public class CommandeCamera extends Behavior{

	private TransformGroup TG;
	private Transform3D rot=new Transform3D();
	private Transform3D rotation=new Transform3D();
	private Transform3D position=new Transform3D();
	private Transform3D orientation=new Transform3D();
	private Vector3f translation=new Vector3f();
	private float act_r;
	private float pos_r;
	private float pos_theta;
	private float pos_phi;
	private float or_x = 0;
	private float or_z = 0;


	private WakeupOnAWTEvent keyEvent=new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
	private float sentibiliteRot = 0.05f;
	private float sentibiliteTra = 0.06f;
	private float supTeta = (float) Math.PI;

	public CommandeCamera(TransformGroup TG, float r0){
		this.TG=TG;
		pos_r = r0;
		act_r = pos_r;
		defaultView();
		TG.setTransform(position);
	}


	public void defaultView(){
		pos_r = act_r;
		pos_theta = (float) (Math.PI/3);
		pos_phi = (float) (Math.PI/4);
		setPos();
		orienter();
	}
	@Override
	public void initialize()
	{
		this.wakeupOn(keyEvent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processStimulus(Enumeration criteria){
		AWTEvent events[]=keyEvent.getAWTEvent();
		rot.setIdentity();
		TG.getTransform(rotation);

		if (((KeyEvent)events[0]).getKeyCode()==KeyEvent.VK_NUMPAD1)
		{
			translation.set(-sentibiliteTra,0f,0f);
			rot.setTranslation(translation);
		}
		else if (((KeyEvent)events[0]).getKeyCode()==KeyEvent.VK_NUMPAD3)
		{
			translation.set(+sentibiliteTra,0f,0f);
			rot.setTranslation(translation);
		}
		else if (((KeyEvent)events[0]).getKeyCode()==KeyEvent.VK_NUMPAD2)
		{
			pos_theta =(float) Math.min( pos_theta + sentibiliteRot,supTeta );
		}
		else if (((KeyEvent)events[0]).getKeyCode()==KeyEvent.VK_NUMPAD5)
		{
			pos_r = pos_r - sentibiliteTra;
		}
		else if (((KeyEvent)events[0]).getKeyCode()==KeyEvent.VK_NUMPAD4)
			pos_phi = pos_phi - sentibiliteRot;

		else if (((KeyEvent)events[0]).getKeyCode()==KeyEvent.VK_NUMPAD6)			
			pos_phi = pos_phi + sentibiliteRot;
		else if (((KeyEvent)events[0]).getKeyCode()==KeyEvent.VK_NUMPAD0)			
			pos_r = pos_r + sentibiliteTra;
		else if (((KeyEvent)events[0]).getKeyCode()==KeyEvent.VK_NUMPAD8)			
			pos_theta = Math.max(pos_theta - sentibiliteRot,0);
		else if (((KeyEvent)events[0]).getKeyCode()==KeyEvent.VK_O){			
			defaultView();
		}







		setPos();
		orienter();
		this.wakeupOn(keyEvent);
	}

	public void orienter(){
		orientation.setIdentity();
		rot.setIdentity();
		or_x =  pos_theta;
		or_z = (float) (pos_phi+Math.PI/2 );
		orientation.rotZ(or_z);

		rot.rotX(or_x);
		orientation.mul(rot);
		position.mul(orientation);
		TG.setTransform(position);
	}
	public void setPos(){
		position.setIdentity();
		translation.set( (float) (pos_r*Math.sin(pos_theta)*Math.cos(pos_phi)), (float) (pos_r*Math.sin(pos_theta)*Math.sin(pos_phi)), (float) (pos_r*Math.cos(pos_theta)));
		position.setTranslation(translation);
		TG.setTransform(position);
	}
	
	public void finalize(){
		try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
