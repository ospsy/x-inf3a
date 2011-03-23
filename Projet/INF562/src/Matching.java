import java.awt.Color;
import java.io.IOException;

import javax.media.j3d.Appearance;
import javax.media.j3d.TransformGroup;

import Jama.Matrix;
import Jcg.geometry.Point_3;
import Jcg.polyhedron.LoadMesh;
import Jcg.polyhedron.MeshRepresentation;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;
import Jcg.viewer.MeshViewer;

public class Matching {

	public static void main (String[] args) throws IOException {
		
		test2() ;
	}

	public static void test1() {
		String fichierOFF1="torus.off";
		MeshRepresentation mesh1 = new MeshRepresentation();
		mesh1.readOffFile(fichierOFF1);
    	LoadMesh<Point_3> load3D=new LoadMesh<Point_3>();
    	Polyhedron_3<Point_3> poly1=
    		load3D.createPolyhedron(mesh1.points,mesh1.faceDegrees,mesh1.faces,mesh1.sizeHalfedges);
		System.out.println("Fichier "+fichierOFF1+" chargé!");

		String fichierOFF2="chair.off";
		MeshRepresentation mesh2 = new MeshRepresentation();
		mesh2.readOffFile(fichierOFF2);
		LoadMesh<Point_3> load3D2=new LoadMesh<Point_3>();
    	Polyhedron_3<Point_3> poly2=
    		load3D2.createPolyhedron(mesh2.points,mesh2.faceDegrees,mesh2.faces,mesh2.sizeHalfedges);
		System.out.println("Fichier "+fichierOFF2+" chargé!");

		String fichierOFF3="chair.off";
		MeshRepresentation mesh3 = new MeshRepresentation();
		mesh3.readOffFile(fichierOFF3);
		LoadMesh<Point_3> load3D3=new LoadMesh<Point_3>();
    	Polyhedron_3<Point_3> poly3=
    		load3D3.createPolyhedron(mesh3.points,mesh3.faceDegrees,mesh3.faces,mesh3.sizeHalfedges);
		System.out.println("Fichier "+fichierOFF3+" chargé!");

		//Calcul de la courbure
		GaussianCourbureEstimator estimator1 = new GaussianCourbureEstimator(poly1);
		estimator1.computeCurvature();
		estimator1.computeSignature();
		GaussianCourbureEstimator estimator2 = new GaussianCourbureEstimator(poly2);
		estimator2.computeCurvature();
		estimator2.computeSignature();
		GaussianCourbureEstimator estimator3 = new GaussianCourbureEstimator(poly3);
		estimator3.computeCurvature();
		estimator3.computeSignature();
		System.out.println(fichierOFF1+" - "+fichierOFF2+" : " + estimator1.compareTo(estimator2));
		System.out.println(fichierOFF1+" - "+fichierOFF3+" : "+ estimator1.compareTo(estimator3));

		estimator1.show();
		estimator2.show();
		estimator3.show();
		
		estimator1.print(fichierOFF1+".dat");
		estimator2.print(fichierOFF2+".dat");
		estimator3.print(fichierOFF3+".dat");
	}

	public static void test2() {
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

		String fichierOFF3 = "skull.off";
		MeshRepresentation mesh3 = new MeshRepresentation();
		mesh2.readOffFile(fichierOFF3);
		LoadMesh<Point_3> load3D3 = new LoadMesh<Point_3>();
		Polyhedron_3<Point_3> poly3 = load3D2.createPolyhedron(mesh2.points,
				mesh2.faceDegrees, mesh2.faces, mesh2.sizeHalfedges);
		System.out.println("Fichier " + fichierOFF3 + " chargé!");

		// Calcul de la courbure
		Taubin estimator1 = new Taubin(poly1);
		estimator1.computeCurvature();
		estimator1.computeSignature();
		Taubin estimator2 = new Taubin(poly2);
		estimator2.computeCurvature();
		estimator2.computeSignature();
		Taubin estimator3 = new Taubin(poly3);
		estimator3.computeCurvature();
		estimator3.computeSignature();
		System.out.println(fichierOFF1+" - "+fichierOFF2+" : "
				+ estimator1.compareTo(estimator2));
		 System.out.println(fichierOFF1+" - "+fichierOFF3+" : " +
		 estimator1.compareTo(estimator3));

		/*estimator1.show();
		estimator2.show();
		estimator3.show();*/
		
		/*new MeshViewer(poly1) ;
		new MeshViewer(poly2) ;*/
		//new MeshViewer(poly3) ;
		 
		TenseurCourbure k1 = estimator1.courbureMap.get(poly1.vertices.get((int) (poly1.vertices.size()*Math.random() ))) ;
		TenseurCourbure k2 = estimator2.courbureMap.get(poly2.vertices.get((int) (poly2.vertices.size()*Math.random() ))) ;
		Matrix m = Utils.getTransformation(k1, k2) ;
		Matrix c1 = m.times(k1.getEigenvector(1)) ;
		Matrix c2 = m.times(k1.getEigenvector(2)) ;
		c1 = c1.times(1/c1.norm2()) ;
		c2 = c2.times(1/c2.norm2()) ;
		double v1 = k2.kappa.times(c1).norm2() ;
		double v2 = k2.kappa.times(c2).norm2() ;
		
		System.out.println() ;
	}

}
