import java.awt.Color;
import java.util.LinkedList;

import javax.vecmath.Matrix3f;

import Jama.Matrix;
import Jcg.geometry.Point_3;
import Jcg.polyhedron.LoadMesh;
import Jcg.polyhedron.MeshRepresentation;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;
import Jcg.viewer.MeshViewer;


public class TrouveMeilleureTransformation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test1("torus","torus5");
	}

	static void test1(String fichier1,String fichier2) {
		String fichierOFF1 = fichier1;
		MeshRepresentation mesh1 = new MeshRepresentation();
		mesh1.readOffFile(fichierOFF1);
		LoadMesh<Point_3> load3D = new LoadMesh<Point_3>();
		Polyhedron_3<Point_3> poly1 = load3D.createPolyhedron(mesh1.points,
				mesh1.faceDegrees, mesh1.faces, mesh1.sizeHalfedges);
		System.out.println("Fichier " + fichierOFF1 + " chargé!");

		String fichierOFF2 = fichier2;
		MeshRepresentation mesh2 = new MeshRepresentation();
		mesh2.readOffFile(fichierOFF2);
		LoadMesh<Point_3> load3D2 = new LoadMesh<Point_3>();
		Polyhedron_3<Point_3> poly2 = load3D2.createPolyhedron(mesh2.points,
				mesh2.faceDegrees, mesh2.faces, mesh2.sizeHalfedges);
		System.out.println("Fichier " + fichierOFF2 + " chargé!");

		// Calcul de la courbure
		Taubin estimator1 = new Taubin(poly1);
		estimator1.computeCurvature();
		estimator1.computeSignature();
		Taubin estimator2 = new Taubin(poly2);
		estimator2.computeCurvature();
		estimator2.computeSignature();

		TransformEvaluator tE = new TransformEvaluator(estimator1, estimator2,80);
		System.out.println("calcul des clusters");
		tE.evaluate();
		System.out.println("effectué : calcul des clusters");
		RigidTransform[] candidats = tE.getGlobalTransform();
		int maxinf = 0;
		int max  = 0;
		int indice = 0;
		int indice2 = 0;
		for (int i = 0; i < candidats.length; i++) {
			if(max<candidats[i].getDegres()){
				maxinf = max;
				max = candidats[i].getDegres();
				indice2 = indice;
				indice = i;
			}
			else if(maxinf<candidats[i].getDegres()){
				maxinf = candidats[i].getDegres();
				indice2 = i;
			}
		}
		
		System.out.println("max :"+max);
		System.out.println("maxinf :"+maxinf);
		System.out.println("max/maxinf :"+max*1./maxinf);
		System.out.println("indice : "+indice);
		
		

		show(candidats[indice2], poly1,poly2);
		
		show(candidats[indice], poly1,poly2);
		

		
		
	}
	
	private static void test2() {
		String fichierOFF = "tanglecube_fin.off";
		MeshRepresentation mesh1 = new MeshRepresentation();
		mesh1.readOffFile(fichierOFF);
		LoadMesh<Point_3> load3D = new LoadMesh<Point_3>();
		Polyhedron_3<Point_3> poly = load3D.createPolyhedron(mesh1.points,
				mesh1.faceDegrees, mesh1.faces, mesh1.sizeHalfedges);
		System.out.println("Fichier " + fichierOFF + " chargé!");

		PolyhedronBoxTree pbt = new PolyhedronBoxTree(poly) ;
		Point_3 p = new Point_3(0,0,0) ;
	
		double d1 = pbt.distance(p) ;
		
		System.out.println("Distance avec PolyhedronTreeBox : " + pbt.distance(p)) ;
		double d2 = Utils.distance(p, poly) ;
		
		System.out.println("Distance avec calcul lin�aire : " + Utils.distance(p, poly)) ;
	}
	
	static void show(RigidTransform best, Polyhedron_3<Point_3> poly1, Polyhedron_3<Point_3> poly2){
		LinkedList<Point_3> pts = new LinkedList<Point_3>();
		Color[] col = new Color[poly1.vertices.size()+poly2.vertices.size()];
		int i=0;
		for (Vertex<Point_3> e : poly1.vertices) {
			pts.add(e.getPoint());
			col[i]=new Color(1, 1, 0.5f);
			i++;
		}
		
		
		
		for (Vertex<Point_3> e : poly2.vertices) {
			Matrix m = new Matrix(3, 1);
			m.set(0, 0, e.getPoint().x);
			m.set(1, 0, e.getPoint().y);
			m.set(2, 0, e.getPoint().z);
			
			Matrix res = best.getRot().times(m);
	
		
			Number x = new Double(res.get(0, 0)+best.getTranslation().x) ;
			Number y = new Double(res.get(1, 0)+best.getTranslation().y) ;
			Number z = new Double(res.get(2, 0)+best.getTranslation().z) ;
			
			pts.add(new Point_3(x, y, z));
			col[i]=new Color(1, 0.5f,1 );
			i++;
		}
		new MeshViewer(pts,col);
	}

}
