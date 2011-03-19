import java.util.HashMap;
import java.util.LinkedList;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Vertex;


public class Taubin extends CourbureEstimator {
	
	// Variables
	HashMap<Vertex<Point_3>, TenseurCourbure> courbureMap;
	

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
		
		// R�cup�ration des projections des voisins sur le plan tangent
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
		
		// on l'ajoute dans la hashmap des courbures
		courbureMap.put(v, new TenseurCourbure(v, Mvi, mNormal)) ;
	}

	@Override
	void show() {
		// TODO Auto-generated method stub
		
	}

}
