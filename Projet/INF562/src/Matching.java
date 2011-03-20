import java.awt.Color;
import java.io.IOException;

import javax.media.j3d.Appearance;
import javax.media.j3d.TransformGroup;

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

		String fichierOFF3="chair.off";
		MeshRepresentation mesh3 = new MeshRepresentation();
		mesh2.readOffFile(fichierOFF3);
		LoadMesh<Point_3> load3D3=new LoadMesh<Point_3>();
    	Polyhedron_3<Point_3> poly3=
    		load3D2.createPolyhedron(mesh2.points,mesh2.faceDegrees,mesh2.faces,mesh2.sizeHalfedges);
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
		System.out.println("tanglecube - tanglecube_fin : " + estimator1.compareTo(estimator2));
		System.out.println("tanglecube - chair : " + estimator1.compareTo(estimator3));

		estimator1.show();
		estimator2.show();
		estimator3.show();
	}
	
	public static void test2() {
		String fichierOFF1="bague.off";
		MeshRepresentation mesh1 = new MeshRepresentation();
		mesh1.readOffFile(fichierOFF1);
    	LoadMesh<Point_3> load3D=new LoadMesh<Point_3>();
    	Polyhedron_3<Point_3> poly1=
    		load3D.createPolyhedron(mesh1.points,mesh1.faceDegrees,mesh1.faces,mesh1.sizeHalfedges);
		System.out.println("Fichier "+fichierOFF1+" chargé!");
		
		String fichierOFF2="torus.off";
		MeshRepresentation mesh2 = new MeshRepresentation();
		mesh2.readOffFile(fichierOFF2);
		LoadMesh<Point_3> load3D2=new LoadMesh<Point_3>();
    	Polyhedron_3<Point_3> poly2=
    		load3D2.createPolyhedron(mesh2.points,mesh2.faceDegrees,mesh2.faces,mesh2.sizeHalfedges);
		System.out.println("Fichier "+fichierOFF2+" chargé!");

		String fichierOFF3="sphere.off";
		MeshRepresentation mesh3 = new MeshRepresentation();
		mesh2.readOffFile(fichierOFF3);
		LoadMesh<Point_3> load3D3=new LoadMesh<Point_3>();
    	Polyhedron_3<Point_3> poly3=
    		load3D2.createPolyhedron(mesh2.points,mesh2.faceDegrees,mesh2.faces,mesh2.sizeHalfedges);
		System.out.println("Fichier "+fichierOFF3+" chargé!");

		//Calcul de la courbure
		Taubin estimator1 = new Taubin(poly1);
		estimator1.computeCurvature();
		estimator1.computeSignature();
		Taubin estimator2 = new Taubin(poly2);
		estimator2.computeCurvature();
		estimator2.computeSignature();
		Taubin estimator3 = new Taubin(poly3);
		estimator3.computeCurvature();
		estimator3.computeSignature();
		System.out.println("tanglecube - tanglecube_fin : " + estimator1.compareTo(estimator2));
		System.out.println("tanglecube - chair : " + estimator1.compareTo(estimator3));

		estimator1.show();
		estimator2.show();
		estimator3.show();
		
		new MeshViewer(poly1) ;
		new MeshViewer(poly2) ;
		new MeshViewer(poly3) ;
	}

}
