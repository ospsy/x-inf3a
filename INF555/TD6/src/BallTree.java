import java.util.LinkedList;


public class BallTree {
	LinkedList<BallPoint> points=null;
	int N;
	BallPoint ptFeuille1,ptFeuille2;
	BallTree fils1, fils2;
	int r1,r2;
	boolean isFeuille;
	
	BallTree(LinkedList<BallPoint> points){
		N=points.size();
		
		if(N>25){
			isFeuille=false;
			generateArbre(points);
		}else{
			this.points=points;
			isFeuille=true;
		}
		
	}

	private void generateArbre(LinkedList<BallPoint> points2) {
		int n1=(int)(Math.random()*N);
		int n2=(int)(Math.random()*(N-1));;
		ptFeuille1=points.get(n1);
		ptFeuille2=points.get(n2);
		r1=0;r2=0;
		LinkedList<BallPoint> l1=new LinkedList<BallPoint>();
		LinkedList<BallPoint> l2=new LinkedList<BallPoint>();
		for(BallPoint pt:points){
			int d1=ptFeuille1.dist(pt);
			int d2=ptFeuille2.dist(pt);
			if(d1<d2){
				l1.add(pt);
				if(d1>r1)
					r1=d1;
			}else{
				l2.add(pt);
				if(d2>r2)
					r2=d2;
			}
		}
		fils1=new BallTree(l1);
		fils2=new BallTree(l2);
	}
	
	BallPoint findNearest(BallPoint pt){
		if(isFeuille){
			int d=Integer.MAX_VALUE;
			BallPoint result=null;
			for(BallPoint pt2:points){
				int tmp=pt.dist(pt2);
				if(tmp<d){
					d=tmp;
					result=pt2;
				}
			}
			return result;
		}else{
			//TODO
		}
	}
	
}

interface BallPoint{
	int dist(BallPoint pt);
}

