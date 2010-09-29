
public class Mask {
	public double[][] t;
	public int i0,j0;
	public int height, width;
	
	public Mask(double[][] tab,int i0,int j0){
		this.t=tab;
		this.i0=i0;
		this.j0=j0;
		height = t.length;
		width = t[0].length;
	}

	public int convole(int[][] I, int y, int x) {
		int result=0;
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				result+=t[i][j]*I[(y-i0+i)%I.length][(x-j0+j)%I[0].length];
			}
		}
		
		return Math.abs(result);
	}
	
}
