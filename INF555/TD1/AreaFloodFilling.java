import java.awt.List;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Stack;

// INF555 2010. Frank Nielsen


class Color
{
int R,G,B;
Color(int r, int g, int b){R=r;G=g;B=b;}	


public boolean SameColor(Color A)
{
if ((R!=A.R) || (G!=A.G) || (B!=A.B))
	return false; 
	else return true;
}

public boolean NearlySameColor(Color A, int marge){
	return marge>=(Math.abs(A.R-R)+Math.abs(A.G-G)+Math.abs(A.B-B));
}

}

// Let us fight Exception in thread "main" java.lang.StackOverflowError !!!

public class AreaFloodFilling
{
static int nbrec=0;
	
public static void Flood(PPM img, int posx, int posy, Color background, Color fill)
{
	nbrec++;
		
	if ((posx<0) || (posx>=img.width))	
		return;
	if ((posy<0) || (posy>=img.height))	
		return;	
		
	//System.out.println("x"+posx+" y"+posy);	
		
	//
	// A remplir (une dizaine de lignes)
	//
	
	//Version récursive
	/*if (pixel.SameColor(background)){
		img.b[posy][posx]=fill.B;
		img.r[posy][posx]=fill.R;
		img.g[posy][posx]=fill.G;
		Flood(img, posx+1, posy, background, fill);
		Flood(img, posx-1, posy, background, fill);
		Flood(img, posx, posy+1, background, fill);
		Flood(img, posx, posy-1, background, fill);
	}*/
	
	//Version avec pile
	/*
	Stack stack = new Stack();
	stack.push(new Point(posx,posy));
	while(!stack.isEmpty()){
		Point p = (Point) stack.pop();
		Color pixel=new Color(img.r[p.y][p.x],img.g[p.y][p.x],img.b[p.y][p.x]);
		if (pixel.SameColor(background) && !((posx<0) || (posx>=img.width)) && !((posy<0) || (posy>=img.height))){
			img.b[p.y][p.x]=fill.B;
			img.r[p.y][p.x]=fill.R;
			img.g[p.y][p.x]=fill.G;
			stack.push(new Point(p.x+1,p.y));
			stack.push(new Point(p.x-1,p.y));
			stack.push(new Point(p.x,p.y+1));
			stack.push(new Point(p.x,p.y-1));
		}
	}*/
	
	//version avec liste
	java.util.List<Point> list = new LinkedList<Point>();
	list.add(new Point(posx,posy));
	while(!list.isEmpty()){
		Point p = (Point) list.remove(0);
		if(!((p.x<0) || (p.x>=img.width)) && !((p.y<0) || (p.y>=img.height))){
			Color pixel=new Color(img.r[p.y][p.x],img.g[p.y][p.x],img.b[p.y][p.x]);
			if (pixel.SameColor(background)){
				img.b[p.y][p.x]=fill.B;
				img.r[p.y][p.x]=fill.R;
				img.g[p.y][p.x]=fill.G;
				list.add(new Point(p.x+1,p.y));
				list.add(new Point(p.x-1,p.y));
				list.add(new Point(p.x,p.y+1));
				list.add(new Point(p.x,p.y-1));
			}
		}	
	}
}	
	
	
public static void main(String [] a)
{
PPM img=new PPM("forfloodfilling.ppm");
int x=212;
int y=132;
Color fill=new Color(255,0,0); // red
Color background=new Color(255,255,255); // white

Flood(img, x,y, background, fill);

img.save("result.ppm");
}
	
}