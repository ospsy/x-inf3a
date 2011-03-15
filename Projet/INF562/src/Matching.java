import java.io.IOException;

import Jcg.geometry.Point_3;
import Jcg.polyhedron.LoadMesh;
import Jcg.polyhedron.MeshRepresentation;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;
import Jcg.viewer.MeshViewer;


public class Matching {


	public static void main (String[] args) throws IOException {
		String fichierOFF="chair.off";
		if (args.length >= 1) {
			fichierOFF=args[0];
			return;
		}
		// read input
		MeshRepresentation mesh = new MeshRepresentation();
		mesh.readOffFile(fichierOFF);
    	LoadMesh<Point_3> load3D=new LoadMesh<Point_3>();
    	Polyhedron_3<Point_3> poly=
    		load3D.createPolyhedron(mesh.points,mesh.faceDegrees,mesh.faces,mesh.sizeHalfedges);
		System.out.println("Fichier "+fichierOFF+" chargé!");
		
		//Calcul de la courbure
		CourbureEstimator estimator = new GaussianCourbureEstimator(poly);
		estimator.computeCurvature();
		
		//Affichage du mesh texturé par la courbure
		estimator.show();
	}

}
