import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import Jcg.geometry.Point_3;
import Jcg.polyhedron.Vertex;



// Cette classe implémente le tenseur de courbure en un point
public class TenseurCourbure {
	
	// Variables
	Vertex<Point_3> point ;
	Matrix kappa ;
	Matrix[] eigenvector ;
	double[] eigenvalue ; // Valeurs propres
	
	// Constructeur
	public TenseurCourbure (Vertex<Point_3> p, Matrix tenseur, Matrix normal)
	{
		point = p ;
		kappa = tenseur ;
		Matrix[] ev = {normal, null, null} ;
		eigenvector = ev ;
		eigenvalue = new double[3] ;
		this.computeEigenvectors() ;
		
	}
	
	// Méthodes d'accès
	public Matrix getTenseur() { return kappa ;}
	public double getEigenvalue(int i) { return eigenvalue[i] ;}
	public Matrix getEigenvector(int i) { return eigenvector[i] ;}
	
	// Calcul des vecteurs propres et valeurs propres
	public void computeEigenvectors()
	{
		// Matrice normale
		Matrix mNormal = eigenvector[0] ;
		
		// Calcul de la matrice de Householder
		double[][] array = {{1},{0},{0}} ;
		Matrix e1 = new Matrix (array) ;
		Matrix nMoins = e1.minus(mNormal) ;
		Matrix nPlus = e1.plus(mNormal) ;
		Matrix Wvi ;
		if (nMoins.norm2() > nPlus.norm2()) Wvi = nMoins.times(1. / nMoins.norm2()) ;
		else Wvi = nPlus.times(1. / nPlus.norm2()) ;
		Matrix Qvi = Matrix.identity(3, 3).minus(Wvi.times(Wvi.transpose()).times(2)) ; 
		
		// Diagonalisation de la sous-matrice 2x2
		Matrix m = Qvi.transpose().times(kappa.times(Qvi)).getMatrix(1, 2, 1, 2) ;
		double a = m.get(0,0) ;
		double b = m.get(0,1) ;
		double c = m.get(1,1) ;
		//System.out.println(kappa.get(0,0)) ;
		double delta = (a - c)*(a - c) + 4*b*b ;
		double vp1 = (a + c + Math.sqrt(delta))/2 ;
		double vp2 = (a + c - Math.sqrt(delta))/2 ;
		double y1 = - (a - vp1)/b ; // le vecteur est (x,y) avec x = 1. Il reste à normaliser
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
		
		// On s'arrange pour que l'orientation (T1, T2, mNormal) soit directe
		Matrix base = new Matrix(3,3) ;
		base.setMatrix(0, 2, 0, 0, mNormal) ;
		base.setMatrix(0, 2, 1, 1, T1) ;
		base.setMatrix(0, 2, 2, 2, T2) ;
		if (base.det() < 0)
		{
			Matrix temp = T1 ;
			T1 = T2 ;
			T2 = temp ;
			double t = vp1 ; 
			vp1 = vp2 ; 
			vp2 = t ;
		}
		
		
		//System.out.println(vp1 + " " + vp2) ;
		
		// Ajustement des valeurs
		eigenvector[1] = T1 ;
		eigenvector[2] = T2 ;
		double[] arrayVp = {0, vp1, vp2} ;
		//double[] arrayVp = {0, Math.random()*10, Math.random()*10} ;
		eigenvalue = arrayVp ;
	}
	
	// Distance à un tenseur de courbure (distance euclidienne via les vp, 
	// a ceci près qu'on peut les inverser)
	public double distanceTo (TenseurCourbure k)
	{
		double d1 = (k.eigenvalue[1] - this.eigenvalue[1])*(k.eigenvalue[1] - this.eigenvalue[1]) + (k.eigenvalue[2] - this.eigenvalue[2])*(k.eigenvalue[2] - this.eigenvalue[2]) ;
		double d2 = (k.eigenvalue[1] - this.eigenvalue[2])*(k.eigenvalue[1] - this.eigenvalue[2]) + (k.eigenvalue[2] - this.eigenvalue[1])*(k.eigenvalue[2] - this.eigenvalue[2]) ;
		return Math.min(Math.sqrt(d1), Math.sqrt(d2)) ;
	}
	
	// Verifie s'il faut inverser les correspondances des VP quand on calcule une transformation entre deux TenseurCourbure
	public boolean revertVp (TenseurCourbure k)
	{
		double d1 = (k.eigenvalue[1] - this.eigenvalue[1])*(k.eigenvalue[1] - this.eigenvalue[1]) + (k.eigenvalue[2] - this.eigenvalue[2])*(k.eigenvalue[2] - this.eigenvalue[2]) ;
		double d2 = (k.eigenvalue[1] - this.eigenvalue[2])*(k.eigenvalue[1] - this.eigenvalue[2]) + (k.eigenvalue[2] - this.eigenvalue[1])*(k.eigenvalue[2] - this.eigenvalue[2]) ;
		return d2 < d1 ;
	}
	
}
