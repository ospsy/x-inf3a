import java.io.IOException;

import Jcg.geometry.Point_3;
import Jcg.polyhedron.LoadMesh;
import Jcg.polyhedron.MeshRepresentation;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;
import Jcg.viewer.MeshViewer;


public class Matching {
	
	public static void test (String filename) throws IOException {
		// read input
		MeshRepresentation mesh = new MeshRepresentation();
		mesh.readOffFile(filename);
    	LoadMesh<Point_3> load3D=new LoadMesh<Point_3>();
    	Polyhedron_3<Point_3> poly=
    		load3D.createPolyhedron(mesh.points,mesh.faceDegrees,mesh.faces,mesh.sizeHalfedges);

		// slightly perturb data points to avoid degenerate cases
    	for (Vertex<Point_3> v : poly.vertices) {
    		Point_3 p = v.getPoint();
    		v.setPoint(new Point_3(p.getX().doubleValue()+1e-6*Math.random(),
				p.getY().doubleValue()+1e-6*Math.random(),
				p.getZ().doubleValue()+1e-6*Math.random()));
    	}

		// compute and store surface normals at vertices
//    	System.out.print("Computing normals...");
//    	HashMap<Point_3, Vector_3> normals = new HashMap<Point_3, Vector_3> ();
//    	for (Vertex<Point_3> v : poly.vertices)
//    		normals.put(v.getPoint(), computeNormal(poly, v));
//    	System.out.println(" done.");
    	
    	// build Delaunay triangulation	
    	/*System.out.print("Building Delaunay triangulation...");
    	Delaunay_3 del = new Delaunay_3();
    	//del.
    	int compteur = 0;
    	for (Vertex v : poly.vertices) {
			del.insert((Point_3)v.getPoint());
			if (compteur++ == 100) {
				System.out.print(".");
				compteur = 0;
			}
    	}
    	System.out.println("  done.");*/
    	
    	LoadMesh<Point_3> l=new LoadMesh<Point_3>();    	
    	Polyhedron_3<Point_3> polyhedron=l.createPolyhedron(mesh.points,mesh.faceDegrees,mesh.faces,mesh.sizeHalfedges);
    	
    	new MeshViewer(polyhedron);
    	polyhedron.isValid(true);

    	
    	//MeshViewer m = new MeshViewer(mesh, true) ;
    	
	}

	public static void main (String[] args) throws IOException {
		/*if (args.length != 1) {
			System.out.println("Args: <OFF filename>");
			return;
		}*/
		test("chair.off");
	}

}
