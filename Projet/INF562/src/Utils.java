
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import Jama.Matrix;
import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import Jcg.polyhedron.Face;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Polyhedron_3;
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

			he = he.getNext().getOpposite() ; // En supposant qu'il s'agit d'un triangle
		}

		// On normalise le vecteur normal
		//System.out.println("ESSAI : " + normal.crossProduct(v.getPoint().minus(new Point_3(0,0,0))).squaredLength().doubleValue()) ;
		//normal = (Vector_3) v.getPoint().minus(new Point_3(0,0,0)) ;
		normal = normal.divisionByScalar(Math.sqrt(normal.squaredLength().doubleValue())) ;

		return normal ;
	}

	// Conversion d'une matrice en vecteur
	public static Vector_3 Matrix2Vector(Matrix m)
	{
		if (m.getRowDimension() != 3 || m.getColumnDimension() != 1) return null ;

		else return new Vector_3 (m.get(0,0), m.get(1,0), m.get(2,0)) ;
	}

	// Recherche d'une rotation selon deux directions principales
	public static Matrix[] getTransformation(TenseurCourbure k1, TenseurCourbure k2)
	{
		Matrix base1 = new Matrix(3,3) ;
		Matrix base2 = new Matrix(3,3) ;


		if (!k1.revertVp(k2))//Math.abs(k1.getEigenvalue(1)/k1.getEigenvalue(2) - k2.getEigenvalue(1)/k2.getEigenvalue(2)) < Math.abs(k1.getEigenvalue(1)/k1.getEigenvalue(2) - k2.getEigenvalue(2)/k2.getEigenvalue(1)))
		{
			base1.setMatrix(0, 2, 0, 0, k1.getEigenvector(0).times(1./k1.getEigenvector(0).norm2())) ;
			base1.setMatrix(0, 2, 1, 1, k1.getEigenvector(1).times(1./k1.getEigenvector(1).norm2())) ;
			base1.setMatrix(0, 2, 2, 2, k1.getEigenvector(2).times(1./k1.getEigenvector(2).norm2())) ;

			base2.setMatrix(0, 2, 0, 0, k2.getEigenvector(0).times(1./k2.getEigenvector(0).norm2())) ;
			base2.setMatrix(0, 2, 1, 1, k2.getEigenvector(1).times(1./k2.getEigenvector(1).norm2())) ;
			base2.setMatrix(0, 2, 2, 2, k2.getEigenvector(2).times(1./k2.getEigenvector(2).norm2())) ;
		}
		else
		{
			base1.setMatrix(0, 2, 0, 0, k1.getEigenvector(0).times(1./k1.getEigenvector(0).norm2())) ;
			base1.setMatrix(0, 2, 1, 1, k1.getEigenvector(1).times(1./k1.getEigenvector(1).norm2())) ;
			base1.setMatrix(0, 2, 2, 2, k1.getEigenvector(2).times(1./k1.getEigenvector(2).norm2())) ;

			base2.setMatrix(0, 2, 0, 0, k2.getEigenvector(0).times(1./k2.getEigenvector(0).norm2())) ;
			base2.setMatrix(0, 2, 1, 1, k2.getEigenvector(2).times(1./k2.getEigenvector(2).norm2())) ;
			base2.setMatrix(0, 2, 2, 2, k2.getEigenvector(1).times(-1./k2.getEigenvector(1).norm2())) ;
		}

		// Construction de la transformation k1 -> k2
		//Matrix m = base2.transpose().times(base1) ;

		Matrix m[] = new Matrix[4];
		
		
		Matrix rotn =new Matrix(3,3);//rotation de Pi/2 autour de l'axe normal
		rotn.set(0, 0, 1);
		rotn.set(2, 1, 1);
		rotn.set(1, 2, -1);
		
		
		
		m[0] = base2.times(base1.transpose()) ;
		m[1] = rotn.times(m[0]) ;
		m[2] = rotn.times(m[1]) ;
		m[3] = rotn.times(m[2]) ;
		
//		m[1] = base2.times(rotn).times(base1.transpose());
//		m[2] = base2.times(rotn).times(rotn).times(base1.transpose());
//		m[3] = base2.times(rotn).times(rotn).times(rotn).times(base1.transpose());
		//System.out.println("NORME : " + m.times(m.transpose()).minus(Matrix.identity(3, 3)).norm2()) ;
		return m ;
	}

	// Retourne les angles (0:x, 1:y, 2:z)
	public static double[][] getRotation(TenseurCourbure k1, TenseurCourbure k2)
	{
		Matrix[] m = getTransformation(k1, k2) ;
		//int signe = 1 ;
		double[][] array  = new double[4][];
		for (int i = 0; i<4;i++){
			double b = Math.atan2(m[i].get(0, 2), Math.sqrt(m[i].get(0,0)*m[i].get(0,0) + m[i].get(0,1)*m[i].get(0,1))) ;
			double c = Math.atan2(-m[i].get(0,1)/Math.cos(b), m[i].get(0,0)/Math.cos(b)) ;
			double a = Math.atan2(-m[i].get(1,2)/Math.cos(b), m[i].get(2,2)/Math.cos(b)) ;
			Matrix mat= getRotation(a,b,c);
			if (mat.minus(m[i]).norm2()>0.01)
			{
				b = Math.atan2(m[i].get(0, 2), -Math.sqrt(m[i].get(0,0)*m[i].get(0,0) + m[i].get(0,1)*m[i].get(0,1))) ;
				c = Math.atan2(-m[i].get(0,1)/Math.cos(b), m[i].get(0,0)/Math.cos(b)) ;
				a = Math.atan2(-m[i].get(1,2)/Math.cos(b), m[i].get(2,2)/Math.cos(b)) ;
			}
			
			array[i] = new double[3];
			array[i][0] = a;
			array[i][1] = b;
			array[i][2] = c;
		}
		// System.out.println("Angles : " + getRotation(a,b,c).minus(m).norm2()) ;

	

		return array ;
	}

	// Retourne une matrice de transformation � partir de 3 angles
	public static Matrix getRotation (double a, double b, double c)
	{
		double[][] arraya = {{1,0,0}, {0, Math.cos(a), -Math.sin(a)}, {0, Math.sin(a), Math.cos(a)}} ;
		Matrix ra = new Matrix (arraya) ;
		double[][] arrayb = {{Math.cos(b),0,Math.sin(b)}, {0,1,0}, {-Math.sin(b), 0, Math.cos(b)}} ;
		Matrix rb = new Matrix (arrayb) ;
		double[][] arrayc = {{Math.cos(c),-Math.sin(c),0}, {Math.sin(c),Math.cos(c),0}, {0,0,1}} ;
		Matrix rc = new Matrix (arrayc) ;
		return ra.times(rb).times(rc) ;
	}

	public static Collection<Vertex<Point_3>> getNeighbors(Vertex<Point_3> v){
		Halfedge<Point_3> first = v.getHalfedge();
		LinkedList<Vertex<Point_3>> result = new LinkedList<Vertex<Point_3>>();
		Halfedge<Point_3> e = v.getHalfedge();
		while(true){
			result.add(e.next.vertex);
			e=e.next.opposite;
			if(e==first)
				break;
		}
		return result;
	}

	public static double distance(Point_3 p, Face<Point_3> f)
	{
		double current = 1e17 ;
		// Calcul de la distance aux points de la face
		Halfedge<Point_3> he = f.getEdge(), premier = he ;
		do
		{
			double d = he.getVertex().getPoint().distanceFrom(p).doubleValue() ;
			if (d < current) current = d ;
			he = he.getNext() ;
		}
		while (he != premier) ;

		// Calcul de la distance � la face
		Point_3 p1 = he.getVertex().getPoint() ;
		Point_3 p2 = he.getNext().getVertex().getPoint() ;
		Point_3 p3 = he.getNext().getNext().getVertex().getPoint() ;
		Vector_3 v1 = (Vector_3) p2.minus(p1) ;
		Vector_3 v2 = (Vector_3) p3.minus(p1) ;
		Vector_3 normal = v1.crossProduct(v2) ;
		normal.divisionByScalar(Math.sqrt(normal.squaredLength().doubleValue())) ;
		double d = Math.abs(normal.innerProduct(p.minus(p1)).doubleValue()) ;
		if (d<current) current = d ;
		return current ;
	}

	public static double distance(Point_3 p, Polyhedron_3<Point_3> poly)
	{
		double current = 1e15 ;
		Iterator<Face<Point_3>> it = poly.facets.iterator() ;
		while (it.hasNext())
		{
			Face<Point_3> f = it.next() ;
			current = Math.min (current, distance(p,f)) ;
		}
		return current ;
	}

}
