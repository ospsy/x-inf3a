import java.awt.Point;

import Jama.*;


public class Homographie {
	public static Matrix createHomographie4Points(Point[] tab1, Point[] tab2){
		if(tab1.length!=4 || tab2.length!=4)
			throw new IllegalArgumentException();
		double[][] tab = new double[][] {{tab2[0].x,tab2[0].y,
									tab2[1].x,tab2[1].y,
									tab2[2].x,tab2[2].y,
									tab2[3].x,tab2[3].y}};
		Matrix b = new Matrix(tab);
		b=b.transpose();
		Matrix A = new Matrix(8,8);
		for(int i=0;i<4;i++){
			A.set(2*i, 0, tab1[i].x);
			A.set(2*i, 1, tab1[i].y);
			A.set(2*i, 2, 1);
			A.set(2*i, 6, -tab1[i].x*tab2[i].x);
			A.set(2*i, 7, -tab1[i].y*tab2[i].x);
			A.set(2*i+1, 3, tab1[i].x);
			A.set(2*i+1, 4, tab1[i].y);
			A.set(2*i+1, 5, 1);
			A.set(2*i+1, 6, -tab1[i].x*tab2[i].y);
			A.set(2*i+1, 7, -tab1[i].y*tab2[i].y);
		}
		
		Matrix h= A.inverse().times(b);
		Matrix H=new Matrix(3,3);
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				if(j!=2 || i!=2){
					H.set(i, j, h.get(3*i+j, 0));
				}
			}
		}
		H.set(2, 2, 1);
		return H;
	}
	
	public static Matrix createHomographie(Point[] tab1, Point[] tab2){
		if(tab1.length!= tab2.length)
			throw new IllegalArgumentException();
		int l=tab1.length;
		Matrix b = new Matrix(2*tab1.length,1);
		for(int i=0;i<l;i++){
			b.set(2*i,0,tab2[i].x);
			b.set(2*i+1,0,tab2[i].y);
		}
		Matrix A = new Matrix(8,8);
		for(int i=0;i<l;i++){
			A.set(2*i, 0, tab1[i].x);
			A.set(2*i, 1, tab1[i].y);
			A.set(2*i, 2, 1);
			A.set(2*i, 6, -tab1[i].x*tab2[i].x);
			A.set(2*i, 7, -tab1[i].y*tab2[i].x);
			A.set(2*i+1, 3, tab1[i].x);
			A.set(2*i+1, 4, tab1[i].y);
			A.set(2*i+1, 5, 1);
			A.set(2*i+1, 6, -tab1[i].x*tab2[i].y);
			A.set(2*i+1, 7, -tab1[i].y*tab2[i].y);
		}
		
		Matrix h= A.transpose().times(A).inverse().times(A.transpose()).times(b);
		Matrix H=new Matrix(3,3);
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				if(j!=2 || i!=2){
					H.set(i, j, h.get(3*i+j, 0));
				}
			}
		}
		H.set(2, 2, 1);
		return H;
	}
	
	public static void main(String[] args){
		/*homographie*/
		/*	
		Point[] l=new Point[4], r=new Point[4];
		l[0]=new Point(831,281); 
		l[1]=new Point(948,423);
		l[2]=new Point(788, 505); 
		l[3]=new Point(874, 641); 

		r[0]=new Point(811,173);
		r[1]=new Point(985,251);
		r[2]=new Point(763,371);
		r[3]=new Point(904,443);
		
		Image out = img1;
		Image img1 = new Image("bookcovers1.png");
		Image img2 = new Image("bookcovers2.png");
		Matrix H = createHomographie4Points(r,l);
		//backward
		//Image img1 = new Image("bookcovers2.png");
		//Image img2 = new Image("bookcovers1.png");
		//Matrix H = createHomographie4Points(l,r);
		
		H.print(0, 4);
		
		for(int y=0;y<out.getHeight();y++){
			for(int x=0;x<out.getWidth();x++){
				Matrix pt = H.times(new Matrix(new double[][] {{x},{y},{1}}));
				int xx=(int) (pt.get(0,0)/pt.get(2, 0));
				int yy=(int) (pt.get(1,0)/pt.get(2, 0));
				if(xx>0 && xx<out.getWidth() && yy>0 && yy<out.getHeight()){
					out.setGreen(xx, yy, (img1.getGreen(xx, yy)+img2.getGreen(x, y))/2);
					out.setBlue(xx, yy, (img1.getBlue(xx, yy)+img2.getBlue(x, y))/2);
					out.setRed(xx, yy, (img1.getRed(xx, yy)+img2.getRed(x, y))/2);
				}
			}
		}*/
		
		
		/*unperpective*/
		Image img = new Image("newcourt.png");
		
		Point[] l=new Point[4], r=new Point[4];
		l[0]=new Point(455,806); r[0]=new Point(0,0);
		l[1]=new Point(779,537);r[1]=new Point(img.getWidth(),0);
		l[2]=new Point(803,202); r[2]=new Point(img.getWidth(),img.getHeight());
		l[3]=new Point(440,332); r[3]=new Point(0,img.getHeight());
		
		Image out = new Image(img.getWidth(), img.getHeight());
		
		Matrix H = createHomographie4Points(r,l);
		H.print(0, 4);
		
		for(int y=0;y<out.getHeight();y++){
			for(int x=0;x<out.getWidth();x++){
				Matrix pt = H.times(new Matrix(new double[][] {{x},{y},{1}}));
				double xxf=(pt.get(0,0)/pt.get(2, 0));
				double yyf=(pt.get(1,0)/pt.get(2, 0));
				int xx = (int) xxf;
				int yy = (int) yyf;
				if(xx>0 && xx<out.getWidth() && yy>0 && yy<out.getHeight()){
					out.setGreen(x, y, img.getGreen(xx, yy));
					out.setBlue(x, y, img.getBlue(xx, yy));
					out.setRed(x, y, img.getRed(xx, yy));
				}
			}
		}
		
		
		new ImageViewer(out);
		
	}
}
