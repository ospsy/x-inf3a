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
		test1();
	}

	private static void test1() {
		String fichierOFF1 = "torus4.off";
		MeshRepresentation mesh1 = new MeshRepresentation();
		mesh1.readOffFile(fichierOFF1);
		LoadMesh<Point_3> load3D = new LoadMesh<Point_3>();
		Polyhedron_3<Point_3> poly1 = load3D.createPolyhedron(mesh1.points,
				mesh1.faceDegrees, mesh1.faces, mesh1.sizeHalfedges);
		System.out.println("Fichier " + fichierOFF1 + " chargé!");

		String fichierOFF2 = "torus5.off";
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
		tE.evaluate();
		RigidTransform[] candidats = tE.getGlobalTransform();
		int maxinf = 0;
		int max  = 0;
		int indice = 0;
		for (int i = 0; i < candidats.length; i++) {
			if(max<candidats[i].getDegres()){
				maxinf = max;
				max = candidats[i].getDegres();
				indice = i;
			}
			else if(maxinf<candidats[i].getDegres()){
				maxinf = candidats[i].getDegres();
			}
		}
		
		System.out.println("max :"+max);
		System.out.println("maxinf :"+maxinf);
		System.out.println("max/maxinf :"+max*1./maxinf);
		System.out.println("indice : "+indice);
		
		
		RigidTransform best = candidats[indice];
		
		show(best, poly1,poly2);
		
		
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