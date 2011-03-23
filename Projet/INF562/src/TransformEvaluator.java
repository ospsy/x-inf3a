import Jcg.geometry.Point_3;
import Jcg.polyhedron.Vertex;
import Meanshift.MeanShiftClustering;
import Meanshift.PointCloud;
import Meanshift.Point_D;


public class TransformEvaluator {
	public static double clusteringRadius = 5 ; //en degrés
	private Taubin a;
	private Taubin b;
	private int nombre_de_points;
	private PointCloud transformSpace;
	private RigidTransform[] globalTransform;
	
	public TransformEvaluator(Taubin a, Taubin b) {
		this.a = a;
		this.b = b;
		nombre_de_points = (int)Math.sqrt(0.5*a.courbureMap.size()+0.5*b.courbureMap.size());
		
	}
	
	
	@SuppressWarnings("unchecked")
	private void computeTransformMap(){
		Vertex<Point_3>[] U = (Vertex<Point_3>[]) a.courbureMap.keySet().toArray();
		Vertex<Point_3>[] V = (Vertex<Point_3>[]) b.courbureMap.keySet().toArray();
		for (int i = 0; i < nombre_de_points; i++) {
			Vertex<Point_3> u = U[(int) (Math.random()*U.length)];
			for (int j = 0; j < nombre_de_points; j++) {
				Vertex<Point_3> v = V[(int) (Math.random()*V.length)];
				vote(u,v);
			}
		}
	}
	
/**
 * ajoute au nuage des transformations 6D
 * celle qui envoie u sur v
 * @param u point du premier maillage
 * @param v point du second maillage
 */
	private void vote(Vertex<Point_3> u, Vertex<Point_3> v){
		double[] trans = new double[3];
		double[] rot= Utils.getRotation(a.courbureMap.get(u), b.courbureMap.get(v));
		
		//le facteur *2 c'est pour avoir un résultat dans [-Pi,Pi] et donc être homogène avec les angles)
		trans[0] = Math.atan(v.getPoint().x-u.getPoint().x)*2;
		trans[1] = Math.atan(v.getPoint().y-u.getPoint().y)*2;
		trans[2] = Math.atan(v.getPoint().z-u.getPoint().z)*2;
		
		double[] coords = new double[trans.length + rot.length];
		System.arraycopy(trans, 0, coords, 0, trans.length);
		System.arraycopy(rot, 0, coords, trans.length, rot.length);

		transformSpace =new PointCloud(new Point_D(coords), transformSpace, false);
	
	}
	
	private void clusterDetection(){
		double radius = Math.PI/180*clusteringRadius;
		MeanShiftClustering msc = new MeanShiftClustering(transformSpace, radius);
		Point_D[] clust = msc.detectClusters();
		int k = 0;
		for (int i = 0; i < clust.length; i++) {
			if (clust[i]==null)break;
			k++;
		}
		
	    globalTransform = new RigidTransform[k];
		for (int i = 0; i < k; i++) {
			globalTransform[i] = new RigidTransform(clust[i]);
		}
		
	}
	
	public RigidTransform[] getGlobalTransform() {
		return globalTransform;
	}


	public void evaluate(){
		computeTransformMap();
		clusterDetection();
	}
	
}
