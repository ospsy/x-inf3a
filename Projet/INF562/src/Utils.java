import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import Jcg.polyhedron.Face;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Vertex;


public class Utils {
	
	/* Calcul du vecteur normal � une face. Sa norme est le double de l'aire de la face */
	public static Vector_3 vecteurNormal (Face<Point_3> f)
	{
		Halfedge<Point_3> he = f.getEdge() ;
		Point_3 p1 = (Point_3) he.getVertex().getPoint()  ;
		he = he.next ;
		Point_3 p2 = (Point_3) he.getVertex().getPoint()  ;
		he = he.next ;
		Point_3 p3 = (Point_3) he.getVertex().getPoint()  ;
		
		Vector_3 v1 = (Vector_3) p2.minus(p1) ;
		Vector_3 v2 = (Vector_3) p3.minus(p1) ;
		Vector_3 normal = v1.crossProduct(v2) ;
		//normal.divisionByScalar(Math.sqrt(normal.squaredLength().doubleValue())) ;
		
		return normal ;
	}

	// Calcul du vecteur normal � un point
	public static Vector_3 vecteurNormal (Vertex<Point_3> v)
	{
		Halfedge<Point_3> he = v.getHalfedge(), premier = he ;
		boolean debut = true ;
		Vector_3 normal = new Vector_3(0,0,0) ;
		
		while (debut || he != premier)
		{
			debut = false ;
			Face<Point_3> f = he.getFace() ;
			
			Vector_3 n = vecteurNormal (f) ;
			normal = normal.sum(n) ;
			
			he = he.next.next.opposite ; // En supposant qu'il s'agit d'un triangle
		}
		
		// On normalise le vecteur normal
		normal = normal.divisionByScalar(Math.sqrt(normal.squaredLength().doubleValue())) ;
		return normal ;
	}
}
