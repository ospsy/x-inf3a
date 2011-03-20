import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;
import Jcg.viewer.MeshViewer;


public class GaussianCourbureEstimator extends CourbureEstimator {
	HashMap<Vertex<Point_3>, Double> courbureMap;
	public double[] signature;
	public double average;
	static final int signatureSize=128;
	static final double signatureAverage=-0.5;
	
	public GaussianCourbureEstimator(Polyhedron_3<Point_3> poly) {
		this.poly=poly;
		courbureMap= new HashMap<Vertex<Point_3>, Double>();
		weightMap= new HashMap<Vertex<Point_3>, Double>();
		signature= new double[signatureSize];
	}
	
	@Override
	public double compareTo(CourbureEstimator ce) {
		if(!(ce instanceof GaussianCourbureEstimator))
			System.err.println("Pas le bon type");
		double[] s=((GaussianCourbureEstimator)ce).signature;
		double err=0;
		for(int i=0;i<signatureSize;i++){
			double tmp=signature[i]-s[i];
			err+=(tmp)*(tmp);
		}
		return Math.sqrt(err);
	}
	
	public void computeSignature(){
		//moyenne et poids total
		double total=0;
		for (java.util.Map.Entry<Vertex<Point_3>, Double> e : weightMap.entrySet()) {
			average+=e.getValue()*courbureMap.get(e.getKey());
			total+=e.getValue();
		}
		average/=total;
		for (java.util.Map.Entry<Vertex<Point_3>, Double> e : courbureMap.entrySet()) {
			double indicef=(Math.atan(e.getValue()*signatureAverage/average)*signatureSize/Math.PI+signatureSize/2);
			int indice=(int)indicef;
			double weight=weightMap.get(e.getKey())/total;
			signature[indice]+=(indicef-indice+1)*weight;
			signature[indice+1]+=(indicef-indice)*weight;
		}
	}

	@Override
	protected void computeCurvatureAtVertex(Vertex<Point_3> v) {
		Halfedge<Point_3> he = v.getHalfedge(), premier = he ;
		
		double totalSum=0;
		double totalAngle=0;
		while (true)
		{
			Point_3 p1 = (Point_3) he.getVertex().getPoint()  ;
			he = he.next ;
			Point_3 p2 = (Point_3) he.getVertex().getPoint()  ;
			he = he.next ;
			Point_3 p3 = (Point_3) he.getVertex().getPoint()  ;
			Vector_3 v1 = (Vector_3) p2.minus(p1) ;
			Vector_3 v2 = (Vector_3) p3.minus(p1) ;
			totalSum+=Math.sqrt( v1.crossProduct(v2).squaredLength().doubleValue())/2;
			totalAngle+=Math.acos(v1.innerProduct(v2).doubleValue());
			

			he=he.prev.opposite; // En supposant qu'il s'agit d'un triangle
			if(he==premier) //tour terminé
				break;
		}
		weightMap.put(v, totalSum);
		courbureMap.put(v, 3*(2*Math.PI-totalAngle)/totalSum) ;
	}
	
	void show(){
		LinkedList<Point_3> pts = new LinkedList<Point_3>();
		Color[] col = new Color[courbureMap.size()];
		int i=0;
		for (java.util.Map.Entry<Vertex<Point_3>, Double> e : courbureMap.entrySet()) {
			pts.add(e.getKey().getPoint());
			if(e.getValue()>0){
				float tmp = (float) (Math.atan(e.getValue()*signatureAverage/average)*2/Math.PI);
				col[i]=new Color(1, 1-tmp, 1-tmp);
			}else{
				float tmp = (float) (-Math.atan(e.getValue()*signatureAverage/average)*2/Math.PI);
				col[i]=new Color(1-tmp, 1, 1-tmp);
			}
			i++;
		}
		new MeshViewer(pts,col);
	}
	

	
	void print(String fileName){
		FileWriter fw;
		try {
			fw = new FileWriter(fileName);
			for(int i=0;i<signatureSize;i++){
				fw.write(signature[i]+"\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
