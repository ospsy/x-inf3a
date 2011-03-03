package rendu;



import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

public class SCO {

	/**positionne un objet dans l'espace relativement à un parent (l'attache)
	 * 
	 * @param obj l'objet à positionner 
	 * @param parent le parent auquel l'attacher
	 * @param position position de l'objet
	 * @return le TransformGroup associé à la translation
	 */
	protected static TransformGroup positionate(Node obj, Group parent, Vector3f position){
		
		
		TransformGroup tg = new TransformGroup();
		Transform3D trans = new Transform3D();
		trans.setTranslation(position);
		tg.setTransform(trans);
		tg.addChild(obj);
		parent.addChild(tg);
		
		return tg;
	}
	
	
	protected static TransformGroup positionate(Node obj, Group parent, Matrix3f rot){
		TransformGroup tg = new TransformGroup();
		Transform3D trans = new Transform3D();
		trans.setRotation(rot);
		tg.setTransform(trans);
		tg.addChild(obj);
		parent.addChild(tg);
		
		return tg;
	}
	
	protected static TransformGroup positionate(Node obj, Group parent, Matrix3f rot, Vector3f position){
		TransformGroup tg = new TransformGroup();
		Transform3D trans = new Transform3D();
		trans.setRotation(rot);
		trans.setTranslation(position);
		tg.setTransform(trans);
		tg.addChild(obj);
		parent.addChild(tg);
		
		return tg;
	}
	/**
	 * 
	 * @param angle (en degres)
	 * @return
	 */
	protected static Matrix3f rotationZ(double angle){
		double a = angle*Math.PI/180;
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);		
		Matrix3f res = new Matrix3f(cos, -sin, 0,sin,cos,0,0,0,1);
		return res;
	}
	/**
	 * 
	 * @param angle (en degres)
	 * @return
	 */
	protected static Matrix3f rotationY(double angle){
		double a = angle*Math.PI/180;
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);		
		Matrix3f res = new Matrix3f(cos, 0, -sin,0,1,0,sin,0,cos);
		return res;
	}
	/**
	 * 
	 * @param angle (en degres)
	 * @return
	 */
	protected static Matrix3f rotationX(double angle){
		double a = angle*Math.PI/180;
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);		
		Matrix3f res = new Matrix3f(1,0, 0,0,cos,-sin,0,sin,cos);
		return res;
	}
}
