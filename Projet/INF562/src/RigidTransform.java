import javax.vecmath.Vector3d;

import Meanshift.Point_D;


public class RigidTransform {
	private double angleX;
	private double angleY;
	private double angleZ;
	
	private Vector3d translation;

	public double getAngleX() {
		return angleX;
	}

	public double getAngleY() {
		return angleY;
	}

	public double getAngleZ() {
		return angleZ;
	}

	public RigidTransform(Point_D trans) {
	
		this.angleX = trans.getCartesian(3).doubleValue();
		this.angleY = trans.getCartesian(4).doubleValue();
		this.angleZ = trans.getCartesian(5).doubleValue();
		this.translation = new Vector3d(Math.tan(trans.getCartesian(0).doubleValue()/2), Math.tan(trans.getCartesian(1).doubleValue()/2), Math.tan(trans.getCartesian(2).doubleValue()/2));
	}

	public Vector3d getTranslation() {
		return translation;
	}
	
	@Override
	public String toString() {
		return "translation :\nx: "+translation.x+"\ny: "+translation.y+"\nz: "+translation.z+"\n\nrotation :\nselon x: "+
		angleX+"\n selon y: "+angleY+"\nselon z:"+angleZ;
	}
	
}
