// (c) Frank NIELSEN, INF555-TD3-2010
import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;

public class BilateralFiltering extends Applet {
	static Image img, imgo;
	static int raster []; int rastero [];
	static PixelGrabber pg;
	static int width, height;
	static double sigmas=2.0;
	static double sigmai=10.25;
	static int k=2;

	static double G(double x, double s){
		return Math.exp(-(double)(x*x)/(2*s*s));
	}

	static int [] BilateralFiltering(int [] raster, double sigmas, double sigmai, int k)
	{
		int [] result=null, rasteri;
		int grey=0, greyc, ngrey,alpha, index, indexc;	
		int i,j, ii, jj,l;
		int bound=(int)(3.0*sigmas);
		double m,p, wg, wr,w;


		for(l=0;l<k;l++)
		{
			System.out.println("Pass #"+l);
			result=new int[width*height];

			for(i=0;i<height;i++){
				System.out.println(i);
				for(j=0;j<width;j++)
				{
					m=p=0.0;
					index=j+i*width;
					alpha=-1;

					for(ii=-bound;ii<bound;ii++)
						for(jj=-bound;jj<bound;jj++) {
							if((j+jj>=0) && (i+ii>=0) && (j+jj<width) && (i+ii<height))//verification des bornes
							{
								indexc=j+jj+(i+ii)*width; 
								grey = (raster[index] & 0xFF) ; 
								greyc= (raster[indexc] & 0xFF);  

								// Remplir ces deux lignes...
								//wg=...; 
								// wr=...;
								wg=G(Math.sqrt(ii*ii+jj*jj),sigmas);
								wr=G(greyc-grey,sigmai);

								w=wg*wr; 
								m += w*greyc;
								p += w; // for normalization
							}
							ngrey=(int)(m/p);
							if (index <width*height) result [ index ] = ( (alpha << 24) | (ngrey << 16) | (ngrey << 8) | ngrey);	
						}
				}
			}

			raster=result;
		}

		return result;
	}

	public void init() {
		String nameimage=getParameter("img");

		Image image = getImage(getDocumentBase() , nameimage);
		img=image;
		pg = new PixelGrabber(image , 0 , 0 , -1 , -1 , true);
		try { pg.grabPixels();} 	catch (InterruptedException e) { }
		height=pg.getHeight(); width=pg.getWidth();
		System.out.println("Image filename:"+nameimage+" "+width+" "+height);
		raster = (int[])pg.getPixels();

		rastero=BilateralFiltering(raster, sigmas, sigmai, k);

		ImageProducer ip = new MemoryImageSource(width , height ,rastero , 0 , width);
		this.resize(width, height);
		imgo = createImage(ip);
		
	}

	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, this);
		g.drawImage(imgo , width , 0 , this);
	}
}