import java.io.IOException;

import Jcg.geometry.Point_3;
import Jcg.polyhedron.LoadMesh;
import Jcg.polyhedron.MeshRepresentation;
import Jcg.polyhedron.Polyhedron_3;


public class Matching {


	public static void main (String[] args) throws IOException {
		String fichierOFF="chair.off";
		if (args.length >= 1) {
			fichierOFF=args[0];
		}
		// read input
		String fichierOFF1="tanglecube.off";
		MeshRepresentation mesh1 = new MeshRepresentation();
		mesh1.readOffFile(fichierOFF1);
    	LoadMesh<Point_3> load3D=new LoadMesh<Point_3>();
    	Polyhedron_3<Point_3> poly1=
    		load3D.createPolyhedron(mesh1.points,mesh1.faceDegrees,mesh1.faces,mesh1.sizeHalfedges);
		System.out.println("Fichier "+fichierOFF1+" chargé!");
		
		String fichierOFF2="tanglecube_fin.off";
		MeshRepresentation mesh2 = new MeshRepresentation();
		mesh2.readOffFile(fichierOFF2);
		LoadMesh<Point_3> load3D2=new LoadMesh<Point_3>();
    	Polyhedron_3<Point_3> poly2=
    		load3D2.createPolyhedron(mesh2.points,mesh2.faceDegrees,mesh2.faces,mesh2.sizeHalfedges);
		System.out.println("Fichier "+fichierOFF2+" chargé!");
		
		//Calcul de la courbure
		CourbureEstimator estimator1 = new GaussianCourbureEstimator(poly1);
		estimator1.computeCurvature();
		estimator1.computeSignature();
		CourbureEstimator estimator2 = new GaussianCourbureEstimator(poly2);
		estimator2.computeCurvature();
		estimator2.computeSignature();
		System.out.println(estimator1.compareTo(estimator2));

		estimator1.show();
		estimator2.show();
	}

}
