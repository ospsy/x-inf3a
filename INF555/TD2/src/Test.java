

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Image img = new Image("polytechnique.png");
		new ImageViewer(img);
		
		double[][] tab1={{1,0},{0,-1}};
		@SuppressWarnings("unused")
		Mask m1 = new Mask(tab1, 0,0);
		double[][] tab2={{0,1},{-1,0}};
		@SuppressWarnings("unused")
		Mask m2 = new Mask(tab2, 0,0);
		
		int sigma=2;
		double[][] tab3= new double[2*sigma+1][2*sigma+1];
		double tot=0;
		for(int i =-sigma;i<=sigma;i++){
			for(int j=-sigma;j<=sigma;j++){
				tab3[i+sigma][j+sigma]=Math.exp(-(double)(i*i+j*j)/(2*sigma*sigma));
				tot+=tab3[i+sigma][j+sigma];
			}
		}
		for(int i =-sigma;i<=sigma;i++){
			for(int j=-sigma;j<=sigma;j++){
				tab3[i+sigma][j+sigma]/=tot;
				System.out.print(tab3[i+sigma][j+sigma]+" ");
			}
			System.out.println();
		}
		Mask m3 = new Mask(tab3, sigma,sigma);
		
		
		HarrisStephen HS = new HarrisStephen(img);
		//HS.toConvole(m1);
		//HS.toConvole(m2);
		HS.process(m3);
		new ImageViewer(HS.toImage());
		
		//HS.toPPM().save("polytechnique2.ppm");
		
		
		

	}

}
