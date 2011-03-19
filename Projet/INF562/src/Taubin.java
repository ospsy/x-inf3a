import java.util.LinkedList;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Vertex;


public class Taubin extends CourbureEstimator {

	@Override
	public double compareTo(CourbureEstimator ce) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void computeCurvatureAtVertex(Vertex<Point_3> v) {
		
		// Vecteur normal
		Vector_3 normal = Utils.vecteurNormal(v) ;
		double[][] arrayNormal = {{normal.getX().doubleValue()},{normal.getY().doubleValue()},{normal.getZ().doubleValue()}} ;
		Matrix mNormal = new Matrix (arrayNormal) ;
		
		// Liste des voisins
		LinkedList<Vertex<Point_3>> voisins = new LinkedList<Vertex<Point_3>>() ; 
		Halfedge<Point_3> he = v.getHalfedge(), premier = he ;
		boolean debut = true ;
		
		while (debut || he != premier)
		{
			debut = false ;
		
			voisins.add(he.getNext().getVertex()) ;
			
			he = he.getNext().getNext().getOpposite() ; // En supposant qu'il s'agit d'un triangle
		}
		
		int nVoisins = voisins.size() ;
		
		// Calcul des surfaces
		double[] surfaces = new double[nVoisins] ;
		int i=0 ;
		Vertex<Point_3> premierVoisin = null ;
		Vertex<Point_3> precedent = voisins.get(0) ;
		double w = 0 ; // Somme des surfaces
		for (Vertex<Point_3> vertex : voisins) {
			// Cas du premier vertex
			if (premierVoisin == null)
			{
				premierVoisin = vertex ;
				precedent = premierVoisin ;
				continue ;
			}
			Vector_3 v1 = (Vector_3) vertex.getPoint().minus(v.getPoint()) ;
			Vector_3 v2 = (Vector_3) precedent.getPoint().minus(v.getPoint()) ;
			surfaces[i] = Math.sqrt(v1.crossProduct(v2).squaredLength().doubleValue()) / 2 ;
			w += surfaces[i]*2 ;
			i++ ;
			precedent = vertex ;
		}
		Vector_3 v1 = (Vector_3) premierVoisin.getPoint().minus(v.getPoint()) ;
		Vector_3 v2 = (Vector_3) precedent.getPoint().minus(v.getPoint()) ;
		surfaces[i] = Math.sqrt(v1.crossProduct(v2).squaredLength().doubleValue()) / 2 ;
		w += surfaces[i]*2 ;
		
		// Rï¿½cupï¿½ration des projections des voisins sur le plan tangent
		Matrix Mvi = new Matrix(3,3) ;
		i = 0 ;
		for (Vertex<Point_3> vertex : voisins) {
			
			Vector_3 vij = (Vector_3) v.getPoint().minus(vertex.getPoint()) ;
			
			int j = (i-1 + nVoisins) % nVoisins ;
			double wij = surfaces[i]+surfaces[j] ;
			double kij = vij.innerProduct(normal).doubleValue()*2/(vij.squaredLength().doubleValue()) ;
			
			Matrix mVij = new Matrix (3,1) ;
			mVij.set(0, 0, vij.getX().doubleValue()) ;
			mVij.set(1, 0, vij.getY().doubleValue()) ;
			mVij.set(2, 0, vij.getZ().doubleValue()) ;
			Matrix Tij = Matrix.identity(3, 3).minus(mNormal.times(mNormal.transpose())).times(mVij) ;
			Tij = Tij.times(1./Tij.norm2()) ;
			
			Mvi = Mvi.plus(Tij.times(Tij.transpose())).times(wij*kij/w) ;
			
			i++ ;
		}
		
		// Calcul de la matrice de Householder
		double[][] array = {{1},{0},{0}} ;
		Matrix e1 = new Matrix (array) ;
		Matrix nMoins = e1.minus(mNormal) ;
		Matrix nPlus = e1.plus(mNormal) ;
		Matrix Wvi ;
		if (nMoins.norm2() > nPlus.norm2()) Wvi = nMoins.times(1. / nMoins.norm2()) ;
		else Wvi = nPlus.times(1. / nPlus.norm2()) ;
		Matrix Qvi = Matrix.identity(3, 1).minus(Wvi.times(Wvi.transpose()).times(2)) ; 
		
		// Diagonalisation de la sous-matrice 2x2
		Matrix m = Qvi.transpose().times(Mvi.times(Qvi)).getMatrix(1, 2, 1, 2) ;
		double a = m.get(0,0) ;
		double b = m.get(0,1) ;
		double c = m.get(1,1) ;
		double delta = (a - b)*(a - b) + 4*c*c ;
		double vp1 = (a + b + Math.sqrt(delta))/2 ;
		double vp2 = (a + b - Math.sqrt(delta))/2 ;
		double y1 = - (a - vp1)/c ; // le vecteur est (x,y) avec x = 1. Il reste ˆ normaliser
		double x1 = 1. / Math.sqrt(1 + y1*y1) ;
		y1 = y1 / Math.sqrt(1 + y1*y1) ;
		// L'autre vecteur est -y, x
		double x2 = -y1 ;
		double y2 = x1 ;
		
		// Vecteurs propres de Mvi
		Matrix T1 = new Matrix(3,0) ;
		Matrix T2 = new Matrix(3,0) ;
		Matrix T1p = Qvi.getMatrix(0, 2, 1, 1) ;
		Matrix T2p = Qvi.getMatrix(0, 2, 2, 2) ;
		T1 = T1p.times(x1).plus(T2p.times(y1)) ;
		T2 = T1p.times(x2).plus(T2p.times(y2)) ;
	}

	@Override
	void show() {
		// TODO Auto-generated method stub
		
	}

}
