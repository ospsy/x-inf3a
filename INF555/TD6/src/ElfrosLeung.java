
public class ElfrosLeung {
	
	static void copy(PPM in, int i1, int j1, PPM out, int i2, int j2){
		out.r[i2][j2]=in.r[i1][j1];
		out.g[i2][j2]=in.g[i1][j1];
		out.b[i2][j2]=in.b[i1][j1];
	}
	
	static int diff(PPM in, int i1, int j1, PPM out, int i2, int j2){
		int result=0;
		int tmp;
		tmp=(out.r[i2][j2]-in.r[i1][j1]);
		result+=tmp*tmp;
		tmp=(out.g[i2][j2]-in.g[i1][j1]);
		result+=tmp*tmp;
		tmp=(out.b[i2][j2]-in.b[i1][j1]);
		result+=tmp*tmp;
		return result;
	}
	
	static int SSD(PPM in, int i1, int j1, PPM out, int i2, int j2,int s){
		int sRight=Math.min(s, out.width-1-j2);
		int result=0;
		for(int i=-s;i<0;i++){
			for(int j=-s;j<=sRight;j++){
				result+=diff(in,i1+i,j1+j,out,i2+i,j2+j);
			}
		}
		for(int j=-s;j<=0;j++){
			result+=diff(in,i1,j1+j,out,i2,j2+j);
		}
		return result;
	}
	
	static PPM synthesis(PPM in,int s){
		PPM out = new PPM(in.width*2,in.height*2);
		//initialisation de la première ligne aléatoirement
		for(int i=0;i<s;i++){
			for(int j=0;j<out.width;j++){
				copy(in,(int)(Math.random()*in.height),(int)(Math.random()*in.width),out,i,j);
			}
		}
		for(int i=0;i<out.height;i++){
			for(int j=0;j<s;j++){
				copy(in,(int)(Math.random()*in.height),(int)(Math.random()*in.width),out,i,j);
			}
		}
		//remplissage
		for(int i2=s;i2<out.height;i2++){
			System.out.println(i2);
			for(int j2=s;j2<out.width;j2++){
				int min=Integer.MAX_VALUE;
				int minI=0,minJ=0;
				for(int i1=s;i1<in.height;i1++){
					for(int j1=s;j1<in.width-s;j1++){
						int tmp= SSD(in, i1, j1, out, i2, j2, s);
						if(tmp<min){
							minI=i1;
							minJ=j1;
							min=tmp;
						}
					}
				}
				copy(in, minI, minJ, out, i2, j2);
			}
		}
		
		
		return out;
	}
	
	public static void main(String[] args){
		PPM in = new PPM();
		in.read("grass.ppm");
		PPM out=synthesis(in,6);
		out.save("grass6.ppm");
	}
}
