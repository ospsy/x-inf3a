import Jcg.geometry.Point_3;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;


public abstract class CourbureEstimator {
	protected Polyhedron_3<Point_3> poly;

	public abstract double compareTo(CourbureEstimator ce);

	public void computeCurvature(){
		for(Vertex<Point_3> v : poly.vertices){
			computeCurvatureAtVertex(v);
		}
	}

	protected abstract void computeCurvatureAtVertex(Vertex<Point_3> v);
	
	abstract void show();
}
