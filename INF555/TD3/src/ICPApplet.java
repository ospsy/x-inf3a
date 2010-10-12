// (c) Frank NIELSEN, INF555-TD3-2010
import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import Jama.*;


public class ICPApplet extends Applet {
	private static final long serialVersionUID = -517368936032866861L;
	static final int width=800, height=600;
	Collection<Matrix> P,Q,Re;
	static final double epsilon=0.5;
	static final double xMax=8;
	static final double xMin=-xMax;
	static final double yMax=8;
	static final double yMin=-yMax;


	public Matrix mean(Collection<Matrix> c){
		Matrix result = new Matrix(3,1);
		for(Matrix pt : c){
			result.plusEquals(pt);
		}
		result.timesEquals(1/(double)c.size());
		return result;
	}
	
	/**
	 * Renvoit la matrice scatter 2*2
	 * @param P,Q collections des matrices des points en coordonnées projectives
	 * @return matrice 2*2 scatter
	 */
	public Matrix scatter(Collection<Matrix> P, Collection<Matrix> Q){
		assert(P.size()==Q.size());
		Matrix result = new Matrix(3, 3);
		Iterator<Matrix> iP = P.iterator();
		Iterator<Matrix> iQ = Q.iterator();
		while(iP.hasNext()){
			Matrix ptP=iP.next();
			Matrix ptQ=iQ.next();
			result.plusEquals(ptQ.times(ptP.transpose()));
		}
		return result.getMatrix(0, 1, 0, 1);
	}
	
	/**
	 * Retrouve la matrice de transformation
	 * @param P,Q les ensembles de points
	 * @return la matrice de transformation
	 */
	public Matrix ICP(Collection<Matrix> P, Collection<Matrix> Q){
		//calcul des moyennes
		Matrix mQ = mean(Q);
		mQ.set(2, 0, 0);
		Matrix mP = mean(P);
		mP.set(2, 0, 0);
		
		LinkedList<Matrix> P2 = new LinkedList<Matrix>();
		for(Matrix ptP : P){
			P2.add(ptP.minus(mP));
		}
		LinkedList<Matrix> Q2 = new LinkedList<Matrix>();
		for(Matrix ptQ : Q){
			Q2.add(ptQ.minus(mQ));
		}
		
		//on crée la matrice 2*2 rotation
		Matrix m= scatter(P2, Q2);
		SingularValueDecomposition svd=m.svd();
		Matrix R=svd.getU().times(svd.getV().transpose());
		
		Matrix result=new Matrix(3,3);
		result.setMatrix(0, 1, 0, 1, R);
		result.set(2,2, 1);
		
		//matrice 3*1 translation
		Matrix t=mQ.minus(result.times(mP));
		t.set(2, 0, 1);
		result.setMatrix(0, 2, 2, 2, t);
		
		return result;
	}
	
	/**
	 * Genère les deux ensembles de points
	 * @param P,Q les ensembles de points à remplir
	 * @param n nombre de points
	 * @return la matrice théorique de transformation
	 */
	public Matrix generationAleatoire(Collection<Matrix> P, Collection<Matrix> Q,int n){
		assert(P!=null && Q!=null);
		Matrix t=new Matrix(new double[][]{{(Math.random()-0.5)*6},{(Math.random()-0.5)*6},{1}});
		double theta=Math.random()*2*Math.PI;
		Matrix R=new Matrix(new double[][]{{Math.cos(theta),-Math.sin(theta)},{Math.sin(theta),Math.cos(theta)}});
		Matrix result = new Matrix(3,3);
		result.setMatrix(0, 1, 0, 1, R);
		result.setMatrix(0, 2, 2, 2, t);
		for(int i=0;i<n;i++){
			Matrix pt = new Matrix(new double[][]{{(Math.random()-0.5)*12},{(Math.random()-0.5)*12},{1}});
			P.add(pt);
			Matrix pt2=result.times(pt);
			pt2.plusEquals(new Matrix(new double[][]{{(Math.random()-0.5)*2*epsilon},{(Math.random()-0.5)*2*epsilon},{0}}));
			Q.add(pt2);
		}
		return result;
	}

	public void init() {
		//liste des points à aparier
		P=new LinkedList<Matrix>();
		Q=new LinkedList<Matrix>();
		/*P.add(new Matrix(new double[][] {{2}, {2},{1}}));
		P.add(new Matrix(new double[][] {{6}, {3},{1}}));
		P.add(new Matrix(new double[][] {{5}, {1},{1}}));
		Q.add(new Matrix(new double[][] {{-0.8}, {-1},{1}}));
		Q.add(new Matrix(new double[][] {{-3.7}, {-1.9},{1}}));
		Q.add(new Matrix(new double[][] {{-1.5}, {-2.1},{1}}));*/
		resize(width, height);
		
		Matrix theorique = generationAleatoire(P, Q, 15);
		theorique.print(4, 3);
		
		Matrix M=ICP(P, Q);
		M.print(4, 3);
		
		Re=new LinkedList<Matrix>();
		for(Matrix ptP : P){
			Re.add(M.times(ptP));
		}
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
		drawCollection(Re, g);
	}
}