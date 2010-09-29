import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

//
// INF555. 2010. Frank Nielsen
//
class TestUnionFind
{
	
	
	public static void main(String[] args)
	{
		String fileName="anumeroter.ppm";
		int marge=0;
		if (args.length>0){//nom du fichier à charger
			fileName=args[0];
			if(args.length>1)//mode avec marge, c'est-à-dire seuillage
				marge=Integer.parseInt(args[1]);
		}
		//Chargement de l'image
		PPM img=new PPM(fileName);
		//Création de la structure de la couleur de fond
		UnionFind UF = new UnionFind(img.width*img.height);
		Color back = new Color(255,255,255);
		
		if(marge==0){//mode sans marge, "classique"
			//Union des éléments
			for(int x=0;x<img.width;x++){
				for(int y=0;y<img.height;y++){
					Color c1 = new Color(img.r[y][x],img.g[y][x],img.b[y][x]);
					if(y<img.height-1){
						Color c2 = new Color(img.r[y+1][x],img.g[y+1][x],img.b[y+1][x]);
						if (c1.NearlySameColor(c2,marge)){
							UF.Union(x+img.width*y, x+img.width*(y+1));
						}
					}
					if(x<img.width-1){
						Color c2 = new Color(img.r[y][x+1],img.g[y][x+1],img.b[y][x+1]);
						if (c1.NearlySameColor(c2,marge)){
							UF.Union(x+img.width*y, x+1+img.width*y);
						}
					}
				}
			}
			//filling avec une couleur aléatoire
			HashMap<Integer,Color> rootsColor = new HashMap<Integer,Color>();
			for(int i=0;i<img.width*img.height;i++){
				Integer rootNumber = new Integer(UF.Find(i));
				if (!rootsColor.containsKey(rootNumber)){
					int x=i%img.width;
					int y=i/img.width;
					rootsColor.put(rootNumber,new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)));
					System.out.println("Racine trouvee "+UF.Find(i)+" : x="+x+" y="+y);
					AreaFloodFilling.Flood(img, x, y, back, rootsColor.get(rootNumber));
				}
			}
			System.out.println("Nombre de racines : "+rootsColor.size());
		}else
		{//mode seuillage
			//union avec comme critère la différence entre les deux racines
			for(int y=0;y<img.height;y++){
				for(int x=0;x<img.width;x++){
					int i1=UF.Find(x+y*img.width);
					int y1=i1/img.width, x1=i1%img.width;
					Color c1 = new Color(img.r[y1][x1],img.g[y1][x1],img.b[y1][x1]);
					if(y<img.height-1){
						int i2=UF.Find(x+(y+1)*img.width);
						int y2=i2/img.width, x2=i2%img.width;
						Color c2 = new Color(img.r[y2][x2],img.g[y2][x2],img.b[y2][x2]);
						if (c1.NearlySameColor(c2,marge)){
							UF.Union(x+img.width*y, x+img.width*(y+1));
						}
					}
					if(x<img.width-1){
						int i2=UF.Find(x+1+y*img.width);
						int y2=i2/img.width, x2=i2%img.width;
						Color c2 = new Color(img.r[y2][x2],img.g[y2][x2],img.b[y2][x2]);if (c1.NearlySameColor(c2,marge)){
							UF.Union(x+img.width*y, x+1+img.width*y);
						}
					}
				}
			}
			//filling : chaque élément prends la couleur de la racine
			for(int i=0;i<img.width*img.height;i++){
				int j=UF.Find(i);
				img.r[i/img.width][i%img.width]=img.r[j/img.width][j%img.width];
				img.g[i/img.width][i%img.width]=img.g[j/img.width][j%img.width];
				img.b[i/img.width][i%img.width]=img.b[j/img.width][j%img.width];
			}
		}
		
		img.save("result.ppm");
		}
		
	}


	class UnionFind{
	
		int  []rank; 
		int []parent; 
	
	//
	// Create a UF for n elements
	//
	UnionFind(int n)
	{int k;
	
	parent=new int[n]; 
	rank=new int[n];
	
	
	
	for (k = 0; k < n; k++)
	      {parent[k]   = k;
	      rank[k] = 0;     }
	}
	
	//
	// Find procedures
	//
	int Find(int k)
	{
		while (parent[k]!=k ) k=parent[k];
	    return k;}
	
	
	int Union(int x, int y)
	{  
	
	x=Find(x);
	y=Find(y);// Find the root (the set it belongs to)
	
		if ( x == y ) return -1;
	
	      if (rank[x] > rank[y])  
	      {parent[y]=x; return x;}
	      else                       
	      { parent[x]=y;if (rank[x]==rank[y]) rank[y]++;return y;}
	}
	
	
	//
	// Assume x and y being roots
	//
	int UnionRoot(int x, int y)
	{  
		if ( x == y ) return -1;
	
	      if (rank[x] > rank[y])  
	      {parent[y]=x; return x;}
	      else                       
	      { parent[x]=y;if (rank[x]==rank[y]) rank[y]++;return y;}
	}
}