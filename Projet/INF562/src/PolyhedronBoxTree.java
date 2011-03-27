import java.util.ArrayList;
import java.util.Iterator;

import Jcg.geometry.Point_3;
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
	Double distanceMin ; // Utile pour le calcul de distance � un point : � chaque instant, on sait qu'elle est < distanceMin
	
	// Constructeurs
	private PolyhedronBoxTree(Polyhedron_3<Point_3> poly, ArrayList<Face<Point_3>> faces, int cutDim)
	{
		this.cutDim = cutDim ;
		this.poly = poly ;
		this.facets = faces ;
		this.nFacets = faces.size() ;
		boundingBox = new double[3][2] ; // coordonn�e, puis min et max
		//if (nFacets > 1) computeTree() ;
	}
	public PolyhedronBoxTree(Polyhedron_3<Point_3> poly)
	{
		distanceMin = new Double(1e15) ;
		boundingBox = new double[3][2] ; // coordonn�e, puis min et max
		cutDim = 0 ;
		this.poly = poly ;
		facets = poly.facets ;
		this.nFacets = facets.size() ;
		computeBoundingBox() ;
		if (nFacets > 1) computeTree() ;
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
		//double[][] boxLeft = new double[3][2] ; // 3 : coordonn�es, 2 : min et max
		double limitLeft = -1e7 ;
		double limitRight = 1e7 ;
		//double[][] boxRight = new double[3][2] ; // 3 : coordonn�es, 2 : min et max
		it = facets.iterator() ;
		boolean lastLeft = (Math.random() < 0.5) ; // Pour la r�partition des faces � cheval
		while (it.hasNext())
		{
			Face<Point_3> f = it.next() ;
			boolean gauche = false, droite = false ;
			// Limite haute et basse de la face selon cutDim
			double faceMin=1e7, faceMax=-1e7 ;
			
			// On regarde la position de la face (� gauche, � droite, � cheval)
			Halfedge<Point_3> he = f.getEdge(), premier = he ;
			do
			{
				double coord = he.getVertex().getPoint().getCartesian(cutDim).doubleValue() ;
				if (coord <= cutValue) gauche = true ;
				else droite = true ;
				if (coord < faceMin) faceMin = coord ;
				if (coord > faceMax) faceMax = coord ;
				he = he.getNext() ;
			}
			while (premier != he) ;
			
			// On ajoute � l'arbre de gauche ou de droite (si � cheval, une fois l'un une fois l'autre)
			if (gauche && (!droite || !lastLeft))
			{
				lastLeft = true ;
				facesLeft.add(f) ;
				if (faceMax > limitLeft) limitLeft = faceMax ;
			}
			else
			{
				lastLeft = false ;
				facesRight.add(f) ;
				if (faceMin < limitRight) limitRight = faceMin ;
			}
			//tour++ ;
		}
		
		// Construction des arbres
		//System.out.println("Sous-Tailles : " + facesLeft.size() + " : " + facesRight.size()) ;
		left = new PolyhedronBoxTree(poly, facesLeft, (cutDim+1)%3) ;
		right = new PolyhedronBoxTree(poly, facesRight, (cutDim+1)%3) ;
		//if (this.distanceMin == null) System.out.println("Fail !!!") ;
		left.distanceMin = this.distanceMin ;
		right.distanceMin = this.distanceMin ;
		if (left.nFacets > 1) left.computeTree() ;
		if (right.nFacets > 1) right.computeTree() ;
		
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
	
	// Distance d'un point � ce polyhedron (r�cursif)
	public double distance (Point_3 p)
	{
		// Cas terminal : zero ou une seule face
		if (nFacets == 0)
		{
			return 1e16 ;
		}
		else if (nFacets == 1)
		{
			Face<Point_3> f = facets.get(0) ;
			return Utils.distance(p, f) ;
		}
		//if (distanceMin == null) System.out.println("FAILLL") ;
		distanceMin = Math.min(distanceMin, p.distanceFrom(facets.get(0).getEdge().getVertex().getPoint()).doubleValue()) ;
		// A chaque instance, on sait que la distance � poly est <=
		
		// On regarde dans les sous-arbres
		double distanceToLeft = Math.max(p.getCartesian(cutDim).doubleValue() - left.boundingBox[cutDim][1], 0) ;
		double distanceToRight = Math.max(right.boundingBox[cutDim][0] - p.getCartesian(cutDim).doubleValue(), 0) ;
		//System.out.println("DistanceToLeft = " + distanceToLeft + " --- DistanceToRight = " + distanceToRight) ;
		
		double currentDistanceLeft = 1e18 ;
		double currentDistanceRight = 1e15 ;
		if (distanceToLeft <= distanceMin)
			currentDistanceLeft = left.distance(p) ;
		if (distanceToRight <= distanceMin)
			currentDistanceRight = right.distance(p) ;
		return Math.min(currentDistanceLeft, currentDistanceRight) ;
	}
}
