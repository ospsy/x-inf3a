// (c) Frank NIELSEN, INF555-TD3-2010
import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import Jama.*;


public class ICPApplet extends Applet {
	private static final long serialVersionUID = -517368936032866861L;
	static final int width=800, height=600;
	Collection<Matrix> P,Q,R;
	static final double epsilon=1;
	static final double xMax=8;
	static final double xMin=-xMax;
	static final double yMax=8;
	static final double yMin=-yMax;


	public Matrix mean(Collection<Matrix> c){
		Matrix result = new Matrix(2,1);
		for(Matrix pt : c){
			result.plusEquals(pt);
		}
		result.timesEquals(1/(double)c.size());
		return result;
	}
	
	public Matrix scatter(Collection<Matrix> P, Collection<Matrix> Q){
		assert(P.size()==Q.size());
		Matrix result = new Matrix(2, 2);
		Iterator<Matrix> iP = P.iterator();
		Iterator<Matrix> iQ = Q.iterator();
		while(iP.hasNext()){
			Matrix ptP=iP.next();
			Matrix ptQ=iQ.next();
			result.plusEquals(ptP.times(ptQ.transpose()));
		}
		return result;
	}
	
	public Matrix ICP(Collection<Matrix> P, Collection<Matrix> Q){
		return null;
	}
	
	public void generationAleatoire(Matrix R, Matrix t, Collection<Matrix> P, Collection<Matrix> Q,int n){
		assert(P!=null && Q!=null);
		t=new Matrix(new double[][]{{(Math.random()-0.5)*6},{(Math.random()-0.5)*6}});
		double theta=Math.random()*2*Math.PI;
		R=new Matrix(new double[][]{{Math.cos(theta),-Math.sin(theta)},{Math.sin(theta),Math.cos(theta)}});
		for(int i=0;i<n;i++){
			Matrix pt = new Matrix(new double[][]{{(Math.random()-0.5)*12},{(Math.random()-0.5)*12}});
			P.add(pt);
			Matrix pt2=R.times(pt).plusEquals(t);
			pt2.plusEquals(new Matrix(new double[][]{{(Math.random()-0.5)*2*epsilon},{(Math.random()-0.5)*2*epsilon}}));
			Q.add(pt2);
		}
	}

	public void init() {
		P=new LinkedList<Matrix>();
		Q=new LinkedList<Matrix>();
		P.add(new Matrix(new double[][] {{2}, {2}}));
		P.add(new Matrix(new double[][] {{6}, {3}}));
		P.add(new Matrix(new double[][] {{5}, {1}}));
		Q.add(new Matrix(new double[][] {{-0.8}, {-1}}));
		Q.add(new Matrix(new double[][] {{-3.7}, {-1.9}}));
		Q.add(new Matrix(new double[][] {{-1.5}, {-2.1}}));
		resize(width, height);
		

		Matrix t = mean(P).minus(mean(Q));
		t.print(4, 1);
		R=new LinkedList<Matrix>();
		for(Matrix ptQ : Q){
			R.add(ptQ.plus(t));
		}
		
		Matrix m= scatter(P, R);
		SingularValueDecomposition svd=m.svd();
		Matrix R=svd.getU().times(svd.getV().transpose());
		
		
	}
	
	
	public void drawCollection(Collection<Matrix> c, Graphics g){
		if(c==null) return;
		int widthCircle=5;
		for(Matrix pt : c){
			int x=(int)((pt.get(0,0)-xMin)/(xMax-xMin)*width);
			int y=(int)((pt.get(1,0)-yMin)/(yMax-yMin)*height);
			g.drawOval(x-widthCircle, y-widthCircle, widthCircle, widthCircle);
		}
	}

	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
		drawCollection(P, g);
		g.setColor(Color.RED);
		drawCollection(Q, g);
		g.setColor(Color.GREEN);
		drawCollection(R, g);
	}
}