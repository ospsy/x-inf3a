#include <Imagine/Images.h>

#include <iostream>
#include <fstream>

using namespace std;
using namespace Imagine;

typedef FVector<double,3> vec;
typedef FMatrix<double,3,3> mat;
const int W_length=5;

class cam {
public:
	mat A;
	vec b;
	void read(const string& s);
	inline vec center() const { return -inverse(A)*b; } //TODO A v�rifier
	inline vec proj(vec M) const { return A*M+b; }
};

void cam::read(const string& s) {
	ifstream f(s.c_str());
	if (!f.is_open())
		cerr << "File not found" << endl;
	for (int r=0;r<3;r++)
		f >> A(r,0) >> A(r,1) >> A(r,2) >> b[r];
	f.close();
}

mat fundamental(cam C[2]) {
	// Vous devez trouver pour les donn�es fournies:
	// F = 0.000426009 0.0096959 -7.28753
	//     -0.0204891 0.00309733 136.434
	//     8.77836 -135.769 2853.02
	vec e2=-C[1].A*inverse(C[0].A)*C[0].b+C[1].b;
	return mat::CrossProd(e2)*C[1].A*inverse(C[0].A);
}

double moyenne(const Image<byte>& I, const IntPoint2& m){
	double result=0;
	double W_card=0;
	for(int i =-W_length;i<=W_length;i++){
		for(int j =-W_length;j<=W_length;j++){
			if(0<=m.x()+i && I.width()>m.x()+i && 0<=m.y()+j && I.height()>m.y()+j){
				result+=(double)(I(m.x()+i,m.y()+j));
				W_card++;
			}
		}
	}
	return result/W_card;
}

double produit(const Image<byte>& I1, const IntPoint2& m1, const Image<byte>& I2, const IntPoint2& m2){
	int W_card=0;
	double result=0;
	double avgI1=moyenne(I1,m1), avgI2=moyenne(I2,m2);
	for(int i =-W_length;i<=W_length;i++){
		for(int j =-W_length;j<=W_length;j++){
			IntPoint2 m1tmp= m1+IntPoint2(i,j);
			IntPoint2 m2tmp=m2+IntPoint2(i,j);
			if(0<=m1tmp.x() && I1.width()>m1tmp.x() && 0<=m1tmp.y() && I1.height()>m1tmp.y()
					&& 0<=m2tmp.x() && I2.width()>m2tmp.x() && 0<=m2tmp.y() && I2.height()>m2tmp.y())
			{
				result+=(double)((I1(m1tmp)-avgI1)*(I2(m2tmp)-avgI2));
				W_card++;
			}
		}
	}
	return result/W_card;
}

double NCC(const Image<byte>& I1, const IntPoint2& m1, const Image<byte>& I2, const IntPoint2& m2){
	return produit(I1,m1,I2,m2)/sqrt(produit(I1,m1,I1,m1)*produit(I2,m2,I2,m2));
}

int main() {
	Image<byte> I[2];
	load(I[0],srcPath("face00.gif"));
	load(I[1],srcPath("face01.gif"));
	int w=I[0].width(),h=I[0].height(); // On suppose les deux images de memes dimensions
	
	openWindow(2*w,h);
	display(I[0]);
	display(I[1],w,0);

	cam C[2];
	C[0].read(srcPath("face00.txt"));
	C[1].read(srcPath("face01.txt"));

	mat F=fundamental(C);
	cout << F << endl;

	// Completer par le trac� d'�pipolaires choisies a la souris
	while(true){
		int x=0,y=0;
		int &refx=x, &refy=y;
		getMouse(refx,refy);
		cout << x << " " << y << endl;
		
		if(x>=I[0].width()) break;
		drawCircle(x,y,10,BLUE);
		vec l =F*vec(x,y,1);
		int yl1=(int)(-l[2]/l[1]);
		int yl2=(int)(-(l[2]+l[0]*I[0].width())/l[1]);
		drawLine(I[0].width(),yl1,I[0].width()*2-1,yl2,BLUE);
		IntPoint2 init(0,yl1);
		IntPoint2 ptMax;
		double max=-1;
		for(int xx=0;xx<I[0].width();xx++){
			IntPoint2 pt1(x,y), pt2=init+IntPoint2(xx,(int)(-(l[0]*xx)/l[1]));
			double ncc = NCC(I[0],pt1,I[1],pt2);
			if(ncc>max){
				ptMax=pt2;
				max=ncc;
			}
		}
		fillCircle(ptMax+IntPoint2(I[0].width(),0),5,BLUE);

	}
	
	endGraphics();
	return 0;
}
