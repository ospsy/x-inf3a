#include <Imagine/Images.h>

#include <iostream>
#include <fstream>

using namespace std;
using namespace Imagine;

typedef FVector<double,3> vec;
typedef FMatrix<double,3,3> mat;

class cam {
public:
	mat A;
	vec b;
	void read(const string& s);
	inline vec center() const { return -inverse(A)*b; } //TODO A vérifier
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
	// Vous devez trouver pour les données fournies:
	// F = 0.000426009 0.0096959 -7.28753
	//     -0.0204891 0.00309733 136.434
	//     8.77836 -135.769 2853.02
	vec e2=-C[1].A*inverse(C[0].A)*C[0].b+C[1].b;
	return mat::CrossProd(e2)*C[1].A*inverse(C[0].A);
}

float NCC(const Image<byte>& I1, const IntPoint2& m1, const Image<byte>& I2, const IntPoint2& m2){
	const int W_length=2;
	float I1avg=0, I2avg=0;
	for(int i =-W_length;i<=W_length;i++){
		for(int j =-W_length;j<=W_length;j++){
			//I1avg+=I1
		}
	}
	return 0.;
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

	// Completer par le tracé d'épipolaires choisies a la souris
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

	}
	
	endGraphics();
	return 0;
}
