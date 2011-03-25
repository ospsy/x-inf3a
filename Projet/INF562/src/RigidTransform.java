import javax.vecmath.Vector3d;

import Jama.Matrix;
import Meanshift.Point_D;


public class RigidTransform {
	private double angleX;
	private double angleY;
	private double angleZ;
	private Matrix rot;
	private int degres;//nombre de points répondant à cette transformation
	
	public int getDegres() {
		return degres;
	}

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

	public RigidTransform(Point_D trans, Integer degres) {
		this.degres = degres;
		this.angleX = trans.getCartesian(3).doubleValue();
		this.angleY = trans.getCartesian(4).doubleValue();
		this.angleZ = trans.getCartesian(5).doubleValue();
		double cos;
		double sin;
		
		cos = Math.cos(angleX);
		sin = Math.sin(angleX);
		Matrix tX = new Matrix(3, 3);
		tX.set(0, 0, 1);
		tX.set(1, 1, cos);
		tX.set(1, 2, -sin);
		tX.set(2, 1, sin);
		tX.set(2, 2, cos);
		
		cos = Math.cos(angleY);
		sin = Math.sin(angleY);
		Matrix tY = new Matrix(3, 3);
		tY.set(0, 0, cos);
		tY.set(0, 2, -sin);
		tY.set(1, 1, 1);
		tY.set(2, 0, sin);
		tY.set(2, 2, cos);
		
		cos = Math.cos(angleZ);
		sin = Math.sin(angleZ);
		Matrix tZ = new Matrix(3, 3);
		tZ.set(0, 0, cos);
		tZ.set(0, 1, -sin);
		tZ.set(1, 0, sin);
		tZ.set(1, 1, cos);
		tZ.set(2, 2, 1);
		
		this.rot = tX.times(tY.times(tZ));
				
		
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

	public Matrix getRot() {
		return rot;
	}
	
}
