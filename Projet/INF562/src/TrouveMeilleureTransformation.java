import Jama.Matrix;
import Jcg.geometry.Point_3;
import Jcg.polyhedron.LoadMesh;
import Jcg.polyhedron.MeshRepresentation;
import Jcg.polyhedron.Polyhedron_3;


public class TrouveMeilleureTransformation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test1();
	}

	private static void test1() {
		String fichierOFF1 = "torus.off";
		MeshRepresentation mesh1 = new MeshRepresentation();
		mesh1.readOffFile(fichierOFF1);
		LoadMesh<Point_3> load3D = new LoadMesh<Point_3>();
		Polyhedron_3<Point_3> poly1 = load3D.createPolyhedron(mesh1.points,
				mesh1.faceDegrees, mesh1.faces, mesh1.sizeHalfedges);
		System.out.println("Fichier " + fichierOFF1 + " chargé!");

		String fichierOFF2 = "torus2.off";
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


		 
		
	}

}
