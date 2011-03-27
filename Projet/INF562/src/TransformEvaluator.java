import java.util.Iterator;
import java.util.LinkedList;

import Jama.Matrix;
import Jcg.geometry.Point_3;
import Jcg.polyhedron.Vertex;
import Meanshift.MeanShiftClustering;
import Meanshift.PointCloud;
import Meanshift.Point_D;


public class TransformEvaluator {
	public static double clusteringRadius = 50 ; //en degrés
	private Taubin a;
	private Taubin b;
	private int nombre_de_points;
	private PointCloud transformSpace;
	private RigidTransform[] globalTransform;
	enum ModeAlgo {paires_de_points_aleatoires_et_clustering_sur_espace_des_transformations,transformation_paire_unique_minimisant_la_distance_entre_les_maillages};
	public static ModeAlgo mode = ModeAlgo.paires_de_points_aleatoires_et_clustering_sur_espace_des_transformations;
//	public static ModeAlgo mode = ModeAlgo.transformation_paire_unique_minimisant_la_distance_entre_les_maillages;
	
	public TransformEvaluator(Taubin a, Taubin b, double clustRad) {
		this.a = a;
		this.b = b;
		clusteringRadius = clustRad;
		nombre_de_points = (int)Math.sqrt(0.5*a.courbureMap.size()+0.5*b.courbureMap.size())*2;
		System.out.println("--------------"+nombre_de_points*nombre_de_points+" votants------------");
	}
	
	

	private void computeTransformMap(){
		Object[] U =  a.courbureMap.keySet().toArray();
		Object[] V =  b.courbureMap.keySet().toArray();
		for (int i = 0; i < nombre_de_points; i++) {
			Vertex<Point_3> u = (Vertex<Point_3>) U[(int) (Math.random()*U.length)];
			for (int j = 0; j < nombre_de_points; j++) {
				
				Vertex<Point_3> v = (Vertex<Point_3>) V[(int) (Math.random()*V.length)];
				vote(a.courbureMap.get(u),b.courbureMap.get(v));
			}
		}
	}
	
	private void computeTransformMap2(){
		
		// Cr�ation des ensembles de points
		LinkedList<TenseurCourbure> pointsA = new LinkedList<TenseurCourbure>() ;
		LinkedList<TenseurCourbure> pointsB = new LinkedList<TenseurCourbure>() ;
		Object[] U = a.courbureMap.values().toArray() ;
		Object[] V = b.courbureMap.values().toArray();
		while (pointsA.size() < nombre_de_points) {
			TenseurCourbure k = (TenseurCourbure) U[(int) (Math.random()*U.length)] ; 
			if (!pointsA.contains(k)) pointsA.add(k) ;
		}
		while (pointsB.size() < nombre_de_points) {
			TenseurCourbure k = (TenseurCourbure) V[(int) (Math.random()*V.length)] ;
			if (!pointsB.contains(k)) pointsB.add(k) ;
		}
		
		// On a maintenant nos deux ensembles de points
		findBestTransform(pointsA, pointsB) ;
	}
	
	private void findBestTransform(LinkedList<TenseurCourbure> pointsA, LinkedList<TenseurCourbure> pointsB)
	{
		// On suppose la m�me �chelle
		Iterator<TenseurCourbure> itA = pointsA.iterator() ;
		while (itA.hasNext())
		{
			TenseurCourbure k1 = itA.next() ;
			double distanceMin = 1e14 ;
			// On va rechercher le tenseur de courbure dans B qui convient le mieux
			Iterator<TenseurCourbure> itB = pointsA.iterator() ;
			TenseurCourbure k2 = pointsB.get(0) ;
			while (itB.hasNext())
			{
				TenseurCourbure k = itB.next() ;
				double distance = k.distanceTo(k1) ;
				if (distance < distanceMin)
				{
					k2 = k ;
					distanceMin = distance ;
				}
			}
			// On a maintenant le point "qui convient le mieux" � k1
			if (k2.point == null) System.out.println("FIST FIST FIST FIST FIST FIST FIST FIST ") ;
			vote(k1, k2) ;
		}
	}
	
/**
 * ajoute au nuage des transformations 6D
 * celle qui envoie u sur v
 * @param u point du premier maillage
 * @param v point du second maillage
 */
	private void vote(TenseurCourbure k1, TenseurCourbure k2){
		Vertex<Point_3> u = k1.point ;
		Vertex<Point_3> v = k2.point ;
		double[] trans = new double[3];
		double[] rot= Utils.getRotation(k1, k2);
		Matrix mrot = Utils.getTransformation(k1, k2);
		
		Vertex<Point_3> w = new Vertex<Point_3>();
		Matrix ww = new Matrix(3, 1);
		ww.set(0, 0, u.getPoint().x);
		ww.set(1, 0, u.getPoint().y);
		ww.set(2, 0, u.getPoint().z);
		ww = mrot.times(ww);
		w.setPoint(new Point_3(ww.get(0, 0),ww.get(1, 0),ww.get(2, 0)));
		//la translation doit être calculée apres rotation seulement
		
		// le facteur *2 c'est pxur avoir un résultat dans [-Pi,Pi] et donc être homogène avec les angles)
		trans[0] = Math.atan(w.getPoint().x-u.getPoint().x)*2;
		trans[1] = Math.atan(w.getPoint().y-u.getPoint().y)*2;
		trans[2] = Math.atan(w.getPoint().z-u.getPoint().z)*2;
		
		double[] coords = new double[trans.length + rot.length];
		System.arraycopy(trans, 0, coords, 0, trans.length);
		System.arraycopy(rot, 0, coords, trans.length, rot.length);

		transformSpace =new PointCloud(new Point_D(coords), transformSpace, false);
	
	}
	
	
	
	
	private void clusterDetection(){
		double radius = Math.PI/180*clusteringRadius;
		MeanShiftClustering msc = new MeanShiftClustering(transformSpace, radius);
		LinkedList<Integer> degres = new LinkedList<Integer>();
		Point_D[] clust = msc.detectClusters(degres);
		int k = 0;
		for (int i = 0; i < clust.length; i++) {
			if (clust[i]==null)break;
			k++;
		}
		
	    globalTransform = new RigidTransform[k];
		for (int i = 0; i < k; i++) {
			globalTransform[i] = new RigidTransform(clust[i],degres.get(i));
		}
		
	}
	
	public RigidTransform[] getGlobalTransform() {
		return globalTransform;
	}


	public void evaluate(){
		switch (mode) {
		case paires_de_points_aleatoires_et_clustering_sur_espace_des_transformations:
			computeTransformMap();
			break;
			
		case transformation_paire_unique_minimisant_la_distance_entre_les_maillages:
			computeTransformMap2();
			break;

		default:
			break;
		}
		
		clusterDetection();
	}
	
}
