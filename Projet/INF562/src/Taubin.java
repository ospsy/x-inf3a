import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;

import Jama.Matrix;
import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;
import Jcg.viewer.MeshViewer;


public class Taubin extends CourbureEstimator {
	
	// Variables
	static final int tailleSignature = 512 ;
	static final double moyenneCourbure = 0.2 ; // Coefficient dans le Arctan
	HashMap<Vertex<Point_3>, TenseurCourbure> courbureMap;
	double[][] signature ;
	
	// Constructeur
	public Taubin (Polyhedron_3<Point_3> poly) {
		this.poly=poly;
		courbureMap= new HashMap<Vertex<Point_3>, TenseurCourbure>();
		weightMap= new HashMap<Vertex<Point_3>, Double>();
		signature = new double [tailleSignature][tailleSignature] ;
	}

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
		
		// w est la somme des surface avoisinantes fois deux ;
		weightMap.put(v, w/2) ;
		
		// R�cup�ration des projections des voisins sur le plan tangent
		Matrix Mvi = new Matrix(3,3) ;
		i = 0 ;
		for (Vertex<Point_3> vertex : voisins) {
			
			Vector_3 vij = (Vector_3) v.getPoint().minus(vertex.getPoint()) ;
			
			int j = (i-1 + nVoisins) % nVoisins ;
			double wij = surfaces[i]+surfaces[j] ;
			double kij = vij.innerProduct(normal).doubleValue()*2/(vij.squaredLength().doubleValue()) ;
			
			double[][] array = {{vij.getX().doubleValue()},{vij.getY().doubleValue()},{vij.getZ().doubleValue()}} ; 
			Matrix mVij = new Matrix (array) ;
			Matrix Tij = Matrix.identity(3, 3).minus(mNormal.times(mNormal.transpose())).times(mVij) ;
			Tij = Tij.times(1./Tij.norm2()) ;
			
			Mvi = Mvi.plus(Tij.times(Tij.transpose())).times(wij*kij/w) ;
			
			i++ ;
		}
		
		// on l'ajoute dans la hashmap des courbures
		courbureMap.put(v, new TenseurCourbure(v, Mvi, mNormal)) ;
	}
	
	public void computeSignature ()
	{
		
		// On calcule la moyenne des courbures
		int nVertex = 0 ;
		double courbureMoyenne = 0 ;
		for(TenseurCourbure k : courbureMap.values()){
			courbureMoyenne += k.getEigenvalue(1) + k.getEigenvalue(2) ;
			nVertex++ ;
		}
		courbureMoyenne /= 2*nVertex ;
		
		// On recense chaque courbure
		double sommeSignatures = 0 ; // Pour normaliser
		for(TenseurCourbure k : courbureMap.values()){
			double dx = (Math.atan(moyenneCourbure * k.getEigenvalue(1)/courbureMoyenne)/Math.PI + 0.5) * tailleSignature ;
			double dy = (Math.atan(moyenneCourbure * k.getEigenvalue(2)/courbureMoyenne)/Math.PI + 0.5) * tailleSignature ;
			int x = (int) dx ;
			int y = (int) dy ;
			double px = x - dx + 1 ; // Ponderation dans l'arrondi inferieur pour x
			double py = y - dy + 1 ; // Ponderation dans l'arrondi inferieur pour y
			if (x == 511) px = 1 ; // Effets de bord
			if (y == 511) py = 1 ;
			if (x >= 0 && y >= 0 && x < 512 && y < 512) // On ne sait jamais...
			{
				double w = weightMap.get(k.point) ;
				signature[y][x] = signature[x][y] = signature[x][y] + px*py*w ;
				if (y+1 < 512) signature[y+1][x] = signature[x][y+1] = signature[x][y+1] + px*(1-py)*w ;
				if (x+1 < 512) signature[y][x+1] = signature[x+1][y] = signature[x+1][y] + (1-px)*py*w ;
				if (x+1 < 512 && y+1 < 512) signature[y+1][x+1] = signature[x+1][y+1] = signature[x+1][y+1] + (1-px)*(1-py)*w ;
				sommeSignatures += w ;
			}
		}
		
		// On normalise
		for (int i=0 ; i<tailleSignature ; i++)
			for (int j=0 ; j<tailleSignature ; j++)
				{
					signature[i][j] /= sommeSignatures ;
				}
		
		
		
	}

	@Override
	void show() {
		LinkedList<Point_3> pts = new LinkedList<Point_3>();
		Color[] col = new Color[tailleSignature*tailleSignature];
		
		int k = 0 ;
		for (int i=0 ; i<tailleSignature ; i++)
			for (int j=0 ; j<tailleSignature ; j++)
			{
				pts.push(new Point_3((double) i, (double) j, signature[i][j]*10)) ;
				col[k] = new Color(1.0f, 1.0f, 1.0f) ;
				k++ ;
			}
		//pts.add(new Point_3(10,5,10)) ;

		new MeshViewer(pts, col);
	}

}
