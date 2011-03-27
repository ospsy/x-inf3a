import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import Jcg.geometry.Point_3;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;


public abstract class CourbureEstimator {
	protected Polyhedron_3<Point_3> poly;
	protected HashMap<Vertex<Point_3>, Double> weightMap;
	final protected double rayonIntegral = 0.2;

	public abstract double compareTo(CourbureEstimator ce);

	public void computeCurvature(){
		for(Vertex<Point_3> v : poly.vertices){
			computeCurvatureAtVertex(v);
		}
	//	computeIntegralCurvature();
	}
	
	protected abstract void computeIntegralCurvature();

	protected Collection<Vertex<Point_3>> integralNeighbors(Vertex<Point_3> v){
		//djikstra
		HashMap<Vertex<Point_3>,Double> neighbors = new HashMap<Vertex<Point_3>,Double>();
		Collection<Vertex<Point_3>> candidates = new LinkedList<Vertex<Point_3>>();
		neighbors.put(v, 0.);
		candidates.addAll(Utils.getNeighbors(v));
		while(true){
			//recherche du nouveau voisin
			double min=Double.MAX_VALUE;
			Vertex<Point_3> newNeighbor=null;
			for (Vertex<Point_3> c : candidates) {
				for (Vertex<Point_3> cn : Utils.getNeighbors(c)) {
					if(neighbors.containsKey(cn)){
						double tmp=neighbors.get(cn)+cn.getPoint().distanceFrom(c.getPoint()).doubleValue();
						if(min>tmp){
							min=tmp;
							newNeighbor=c;
						}
					}
				}
			}
			//attention on prends que les proches
			if(min>rayonIntegral) break;
			//Ajout du nouveau voisin
			candidates.remove(newNeighbor);
			neighbors.put(newNeighbor, min);
			Collection<Vertex<Point_3>> tmp=Utils.getNeighbors(newNeighbor);
			for (Iterator<Vertex<Point_3>> it = tmp.iterator(); it.hasNext();) {
				Vertex<Point_3> vertex = (Vertex<Point_3>) it.next();
				if(neighbors.containsKey(vertex))
					it.remove();
			}
			candidates.addAll(tmp);
		}
		System.out.println(neighbors.size());
		return neighbors.keySet();
	}
	
	public abstract void computeSignature();

	protected abstract void computeCurvatureAtVertex(Vertex<Point_3> v);
	
	
	abstract void show();
}
