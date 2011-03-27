import java.util.ArrayList;
import java.util.Iterator;

import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import Jcg.polyhedron.Face;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Polyhedron_3;


public class PolyhedronBoxTree {
	
	Polyhedron_3<Point_3> poly ;
	ArrayList<Face<Point_3>> facets ;
	int nFacets ;
	PolyhedronBoxTree left ;
	PolyhedronBoxTree right ;
	// limites des arbres (normalement, limitLeft >= limitRight)
	/*double limitLeft ;
	double limitRight ;*/
	double[][] boundingBox ;
	int cutDim ;
	double cutValue ;
	
	// Constructeurs
	public PolyhedronBoxTree(Polyhedron_3<Point_3> poly, ArrayList<Face<Point_3>> faces, int cutDim)
	{
		this.cutDim = cutDim ;
		this.poly = poly ;
		this.facets = faces ;
		this.nFacets = faces.size() ;
		boundingBox = new double[3][2] ; // coordonnée, puis min et max
		if (nFacets > 1) computeTree() ;
	}
	public PolyhedronBoxTree(Polyhedron_3<Point_3> poly)
	{
		this(poly, poly.facets, 0) ;
		computeBoundingBox() ;
	}
	
	// Bounding Box
	private void computeBoundingBox()
	{
		// Initialisation
		for (int i=0 ; i<3 ; i++)
		{
			boundingBox[i][0] = 1e7 ;
			boundingBox[i][1] = -1e7 ;
		}
		
		// Parcours
		Iterator<Face<Point_3>> it = facets.iterator() ;
		while (it.hasNext())
		{
			Face<Point_3> f = it.next() ;
			Halfedge<Point_3> he = f.getEdge(), premier = he ;
			do
			{
				Point_3 p = he.getVertex().getPoint() ;
				for (int i=0 ; i<3 ; i++)
				{
					double coord = p.getCartesian(i).doubleValue() ; 
					if (coord < boundingBox[i][0]) boundingBox[i][0] = coord ;
					if (coord > boundingBox[i][1]) boundingBox[i][1] = coord ;
				}
				he = he.getNext() ;
			}
			while (premier != he) ;
		}
	}
	
	// Construction de l'arbre
	public void computeTree ()
	{
		// On recherche la valeur de coupure
		int n = 0 ;
		double cutValue = 0 ;
		Iterator<Face<Point_3>> it = facets.iterator() ;
		while (it.hasNext())
		{
			Face<Point_3> f = it.next() ;
			Halfedge<Point_3> he = f.getEdge(), premier = he ;
			boolean debut = true ;
			while (debut || premier != he)
			{
				debut = false ;
				cutValue += he.getVertex().getPoint().getCartesian(cutDim).doubleValue() ;
				n++ ;
				he = he.getNext() ;
			}
		}
		cutValue = cutValue/n ;
		this.cutValue = cutValue ;
		
		// Separation
		ArrayList<Face<Point_3>> facesLeft = new ArrayList<Face<Point_3>>() ; 
		ArrayList<Face<Point_3>> facesRight = new ArrayList<Face<Point_3>>() ; 
		//double[][] boxLeft = new double[3][2] ; // 3 : coordonnées, 2 : min et max
		double limitLeft = -1e7 ;
		double limitRight = 1e7 ;
		//double[][] boxRight = new double[3][2] ; // 3 : coordonnées, 2 : min et max
		it = facets.iterator() ;
		int tour = 0 ; // Pour la répartition des faces à cheval
		while (it.hasNext())
		{
			Face<Point_3> f = it.next() ;
			boolean gauche = false, droite = false ;
			// Limite haute et basse de la face selon cutDim
			double faceMin=1e7, faceMax=-1e7 ;
			
			// On regarde la position de la face (à gauche, à droite, à cheval)
			Halfedge<Point_3> he = f.getEdge(), premier = he ;
			do
			{
				double coord = he.getVertex().getPoint().getCartesian(cutDim).doubleValue() ;
				if (coord <= cutValue) gauche = true ;
				if (coord < faceMin) faceMin = coord ;
				if (coord > faceMax) faceMax = coord ;
				else droite = true ;
				he = he.getNext() ;
			}
			while (premier != he) ;
			
			// On ajoute à l'arbre de gauche ou de droite (si à cheval, une fois l'un une fois l'autre)
			if (gauche && (!droite || (++tour % 2)==0))
			{
				facesLeft.add(f) ;
				if (faceMax > limitLeft) limitLeft = faceMax ;
			}
			else
			{
				facesRight.add(f) ;
				if (faceMin < limitRight) limitRight = faceMin ;
			}
		}
		
		// Construction des arbres
		left = new PolyhedronBoxTree(poly, facesLeft, (cutDim+1)%3) ;
		right = new PolyhedronBoxTree(poly, facesRight, (cutDim+1)%3) ;
		
		// Bounding Boxes
		for (int i=0 ; i<3 ; i++)
		{
			left.boundingBox[i][0] = boundingBox[i][0] ;
			right.boundingBox[i][1] = boundingBox[i][0] ;
			
			if (i == cutDim)
			{
				left.boundingBox[i][1] = limitLeft ;
				right.boundingBox[i][0] = limitRight ;
				
			}
			else
			{
				left.boundingBox[i][0] = boundingBox[i][0] ;
				right.boundingBox[i][0] = boundingBox[i][0] ;
			}
			
		}
	}
	
	// Distance d'un point à ce polyhedron (récursif)
	public double distance (Point_3 p)
	{
		// Cas terminal : une seule face
		if (nFacets == 1)
		{
			double current = 1e18 ;
			Face<Point_3> f = facets.get(0) ;
			
			// Calcul de la distance aux points de la face
			Halfedge<Point_3> he = f.getEdge(), premier = he ;
			do
			{
				double d = he.getVertex().getPoint().distanceFrom(p).doubleValue() ;
				if (d < current) current = d ;
				he = he.getNext() ;
			}
			while (he != premier) ;
			
			// Calcul de la distance à la face
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
		
		double distanceMin = p.distanceFrom(facets.get(0).getEdge().getVertex().getPoint()).doubleValue() ;
		// A chaque instance, on sait que la distance à poly est <=
		
		// On regarde dans les sous-arbres
		double distanceToLeft = Math.abs(p.getCartesian(cutDim).doubleValue() - left.boundingBox[cutDim][1]) ;
		double distanceToRight = Math.abs(p.getCartesian(cutDim).doubleValue() - right.boundingBox[cutDim][0]) ;
		
		double currentDistanceLeft = 1e18 ;
		double currentDistanceRight = 1e18 ;
		if (distanceToLeft <= distanceMin)
			currentDistanceLeft = left.distance(p) ;
		if (distanceToRight <= distanceMin)
			currentDistanceRight = left.distance(p) ;
		return Math.min(distanceToLeft, distanceToRight) ;
	}
}
