import java.util.HashMap;
import javax.vecmath.*;



public class kMeans {

	public static void main(String[] args){
		Image in = new Image("polytechnique.png");
		int h=in.getHeight();
		int w=in.getWidth();
		Point3d[] couleurs = new Point3d[h*w];
		HashMap<Point3d,Integer> associedColor = new HashMap<Point3d, Integer>();
		final int k=64;
		Point3d[] colorMap = new Point3d[k];

		//initialisation des points3D
		for(int j=0;j<h;j++){
			for(int i=0;i<w;i++){
				Point3d pt=new Point3d(in.getRed(i, j)/255., in.getGreen(i, j)/255., in.getBlue(i, j)/255.);
				couleurs[i*h+j]=pt;
				associedColor.put(pt, new Integer(-1));
			}
		}

		//initialisation des k couleurs
		for(int i=0;i<k;i++)
			colorMap[i]= (Point3d) couleurs[(int)(w*h*Math.random())].clone();

		int N=0;
		double lastDistance=Double.MAX_VALUE;
		while(true){
			N++;
			if(N==60)break;
			double distanceTotale=0;
			for(int n=0;n<w*h;n++){
				int iMin=-1;
				double min=Double.MAX_VALUE;
				for(int i=0;i<k;i++){
					double tmp=couleurs[n].distanceSquared(colorMap[i]);
					if(min>tmp){
						min=tmp;
						iMin=i;
					}
				}
				distanceTotale+=min;
				if(!associedColor.get(couleurs[n]).equals(new Integer(iMin))){
					associedColor.put(couleurs[n], new Integer(iMin));
				}
			}
			System.out.println("N="+N+" "+distanceTotale);
			if(distanceTotale>=lastDistance) break;
			lastDistance=distanceTotale;

			//on fait la moyenne pour les nouvelles graines
			int[] numbers= new int[k];
			for(int i=0;i<k;i++){
				numbers[i]=0;
				colorMap[i]= new Point3d(0,0,0);
			}
			for(Point3d pt : associedColor.keySet()){
				int i=associedColor.get(pt);
				numbers[i]++;
				Point3d seed=colorMap[i];
				seed.x+=pt.x;
				seed.y+=pt.y;
				seed.z+=pt.z;
			}
			int totalNumbers=0;
			for(int i=0;i<k;i++){
				totalNumbers+=numbers[i];
				Point3d seed=colorMap[i];
				seed.x/=numbers[i];
				seed.y/=numbers[i];
				seed.z/=numbers[i];
			}
			assert(totalNumbers==w*h);
		}

		//coloriage
		for(int j=0;j<h;j++){
			for(int i=0;i<w;i++){
				Point3d pt = colorMap[associedColor.get(couleurs[i*h+j])];
				in.setRed(i, j,(int) (pt.x*255));
				in.setGreen(i, j,(int) (pt.y*255));
				in.setBlue(i, j,(int) (pt.z*255));
			}
		}

		PointsDrawer.drawSet(colorMap);
		new ImageViewer(in);
	}

}
