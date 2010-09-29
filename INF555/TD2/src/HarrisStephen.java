/**
 * Classe permettant le calcul de HarrisStephen, on l'initialise avec une Image, qui est
 * copiée en matrice d'intensité. La fonction toImage() permet de récupérer une nouvelle image
 * pour l'afficher.
 * @author benoit
 *
 */
public class HarrisStephen {
	private static final double k = 0.05;//cste pour le calcul de HS
	private static final int threshold=100000;//seuil 
	private int[][] I;//matrice représentant l'intensité de l'image
	private int height;
	private int width;
	private Image sourceImage;
	
	/**
	 * Transforme la matrice d'intensité du calcul en Image
	 * @return une copie de l'image
	 */
	public Image toImage() {
		Image result = new Image(width, height);
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				int tmp = I[y][x];
				result.setPixel(x, y, tmp+(tmp << 8)+(tmp<<16)+0xFF000000);
			}
		}
		return result;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
	
	/**
	 * Constructeur principal, copie l'image
	 * @param image Image à traiter
	 */
	public HarrisStephen(Image image){
		height = image.getHeight();
		width = image.getWidth();
		sourceImage= image;
		I=new int[height][width];
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				I[y][x]=(int)(image.getBlue(x, y)*0.11+image.getGreen(x, y)*0.59+image.getRed(x, y)*0.3);
				assert(I[y][x]<256 && I[y][x]>=0);
			}
		}
	}
	
	/**
	 * Gradient horizontal en X
	 * @param i
	 * @param j
	 * @return la valeur du gradient
	 */
	public int Gx(int i,int j){
		return I[i][(j+1)%width]-I[i][j];
	}
	
	/**
	 * Gradient vertical en Y
	 * @param i
	 * @param j
	 * @return la valeur du gradient
	 */
	public int Gy(int i,int j){
		return I[(i+1)%height][j]-I[i][j];
	}
	
	/**
	 * Modifie la matrice d'intensité pour correspondre à la norme des gradients
	 */
	public void toGradient(){
		for(int i=0;i<height-1;i++){
			for(int j=0;j<width-1;j++){
				I[i][j]=(int)(Math.sqrt(Gx(i,j)*Gx(i,j)+Gy(i,j)*Gy(i,j)));
			}
		}
	}
	
	/*public PPM toPPM(){
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				img.b[i][j]=I[i][j];
				img.g[i][j]=I[i][j];
				img.r[i][j]=I[i][j];
			}
		}
		return img;
	}*/
	
	/**
	 * Convole la matrice avec un masque
	 * @param m masque à convoler
	 */
	public void toConvole(Mask m){
		I= convolution(I,m);
	}
	
	public static int[][] convolution(int[][] I, Mask mask){
		int height = I.length;
		int width = I[0].length;
		int[][] result = new int[I.length][I[0].length];
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				result[i][j]=mask.convole(I,i,j);
			}
		}
		return result;
	}
	
	/**
	 * Effectue le calcul de HS
	 * @param m masque de convolution
	 */
	public void process(Mask m){
		int[][] result = new int[I.length][I[0].length];
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				result[i][j]=HSconvolution(m, i, j);
			}
		}
		for(int i=1;i<height-1;i++){
			for(int j=1;j<width-1;j++){
				int tmp = result[i][j];
				if(tmp>result[i][j+1] && tmp>result[i][j-1] && tmp>result[i+1][j] && tmp>result[i-1][j])
					sourceImage.setPixel(j, i, 0xFFFF0000);
				if(tmp<result[i][j+1] && tmp<result[i][j-1] && tmp<result[i+1][j] && tmp<result[i-1][j])
					sourceImage.setPixel(j, i, 0xFF00FF00);
			}
		}
	}
	
	/**
	 * Produit de convolution en un point pour HS
	 * @param m masque de convolution
	 * @param y ordonnée du point
	 * @param x abscisse du point
	 * @return la valeur du produit de convolution au point considéré
	 */
	private int HSconvolution(Mask m,int y,int x){
		int a=0;
		int b=0;
		int c=0;
		int d=0;
		for(int i=0;i<m.height;i++){
			for(int j=0;j<m.width;j++){
				int i2= (y-m.i0+i+I.length)%I.length;
				int j2= (x-m.j0+j+I[0].length)%I[0].length;
				a+=m.t[i][j]*Gx(i2,j2)*Gx(i2,j2);
				b+=m.t[i][j]*Gx(i2,j2)*Gy(i2,j2);
				c+=m.t[i][j]*Gy(i2,j2)*Gx(i2,j2);
				d+=m.t[i][j]*Gy(i2,j2)*Gy(i2,j2);
			}
		}
		int tmp=(int)((a*d-b*c)-k*(a+c));
		if (Math.abs(tmp)>threshold) return tmp;
		else return 0;
	}
	
	/**
	 * @deprecated
	 * @author benoit
	 *
	 */
	class Matrice2_2 {
		int a,b,c,d;
		public Matrice2_2(){
			a=0;
			b=0;
			c=0;
			d=0;
		}
		public Matrice2_2(int a0, int b0, int c0, int d0){
			a=a0;
			b=b0;
			c=c0;
			d=d0;
		}
		public void plus(Matrice2_2 m){
			a+=m.a;
			b+=m.b;
			c+=m.c;
			d+=m.d;
		}
		public int det(){
			return (a*c - b*d);
		}
		public int trace(){
			return a+c;
		}
	}
}
