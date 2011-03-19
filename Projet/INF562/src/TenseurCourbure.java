import Jama.Matrix;
import Jcg.geometry.Point_3;
import Jcg.polyhedron.Vertex;



// Cette classe implémente le tenseur de courbure en un point
public class TenseurCourbure {
	
	// Variables
	Vertex<Point_3> point ;
	Matrix kappa ;
	double[] eigenvalue ; // Valeurs propres
	
	// Constructeur
	public TenseurCourbure (Vertex<Point_3> p, Matrix normal, Matrix T1, Matrix T2, double vp1, double vp2)
	{
		point = p ;
		kappa = new Matrix (3,3) ;
		kappa.setMatrix(0, 2, 0, 0, normal) ;
		kappa.setMatrix(0, 2, 1, 1, T1) ;
		kappa.setMatrix(0, 2, 2, 2, T2) ;
		double[] array = {0, vp1, vp2} ;
		eigenvalue = array ;
	}
	
	// Méthodes d'accès
	public Matrix getTenseur() { return kappa ;}
	public double getEigenvalue(int i) { return eigenvalue[i] ;}
	public Matrix getEigenvector(int i) { return kappa.getMatrix(0, 2, i, i) ;}
	
}
