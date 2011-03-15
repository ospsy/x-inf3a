import java.util.HashMap;

import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import Jcg.polyhedron.Face;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;


public class GaussianCourbureEstimator extends CourbureEstimator {
	HashMap<Vertex<Point_3>, Double> courbureMap;
	
	public GaussianCourbureEstimator(Polyhedron_3<Point_3> mesh) {
		this.mesh=mesh;
		courbureMap= new HashMap<Vertex<Point_3>, Double>();
	}
	
	@Override
	public double compareTo(CourbureEstimator ce) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void computeCurvatureAtVertex(Vertex<Point_3> v) {
		Halfedge<Point_3> he = v.getHalfedge(), premier = he ;
		
		double totalSum=0;
		double totalAngle=0;
		while (true)
		{
			Point_3 p1 = (Point_3) he.getVertex().getPoint()  ;
			he = he.next ;
			Point_3 p2 = (Point_3) he.getVertex().getPoint()  ;
			he = he.next ;
			Point_3 p3 = (Point_3) he.getVertex().getPoint()  ;
			Vector_3 v1 = (Vector_3) p2.minus(p1) ;
			Vector_3 v2 = (Vector_3) p3.minus(p1) ;
			totalSum+=Math.sqrt( v1.crossProduct(v2).squaredLength().doubleValue())/2;
			totalAngle+=Math.acos(v1.innerProduct(v2).doubleValue());
			

			he=he.prev.opposite; // En supposant qu'il s'agit d'un triangle
			if(he==premier) //tour terminé
				break;
		}
		courbureMap.put(v, 3*(2*Math.PI-totalAngle)/totalSum) ;
	}

}
