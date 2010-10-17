#include <Imagine/Images.h>

#include <iostream>
#include <fstream>
#include <queue>

using namespace std;
using namespace Imagine;

typedef FVector<double,3> vec;
typedef FMatrix<double,3,3> mat;

//constante
const double sigma=4;
const double seuilHarris=1000;
const double seuilRapport=0.96;
const double seuilGraine=0.8;
const double seuilPropagation=0.55;
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


inline int disp(const IntPoint2& pt1, const IntPoint2& pt2){
	return pt2.x()-pt1.x();
}

struct Graine{
	IntPoint2 pt;
	int disp;
};

queue<Graine> graines(const Image<byte>& I1, const Image<byte>& I2){
	//Harris Stephen
	Image<double> A = I1;
	Image<double> B = deriche(A,0.2,1,1);
	A=deriche(A,0.2,1,0,false);
	Image<double> C = blur(A*B,sigma);
	A=blur(A*A,sigma);
	B=blur(B*B,sigma);
	Image<double> H=A*B-C*C-0.04*(A+B)*(A+B);
	//seuillage
	for(int i=0;i<I1.width();i++){
		for(int j=0;j<I1.height();j++){
			if(H(i,j)<seuilHarris)H(i,j)=0;
		}
	}
	//on garde que les max locaux
	queue<IntPoint2> maximaux;
	for(int i=1;i<I1.width()-1;i++){
		for(int j=1;j<I1.height()-1;j++){
			if(H(i,j)>H(i+1,j) && H(i,j)>H(i-1,j) && H(i,j)>H(i,j+1) && H(i,j)>H(i,j-1)){
				maximaux.push(IntPoint2(i,j));
				drawCircle(IntPoint2(i,j),5,BLUE,2);
			}
		}
	}
	//on vérifie qu'ils sont valides
	queue<Graine> result;
	while(!maximaux.empty()){
		IntPoint2 pt = maximaux.front();
		maximaux.pop();
		double max1=0;
		double max2=0;
		IntPoint2 ptMax(pt);
		for(int x=0;x<I1.width();x++){
			IntPoint2 tmp(x,pt.y());
			double ncc=NCC(I1,pt,I2,tmp);
			if(ncc>max1){
				max2=max1;
				max1=ncc;
				ptMax=tmp;
			}else if(ncc>max2)
				max2=ncc;
		}
		int d = disp(pt,ptMax);
		if(max2/max1<seuilRapport && max1>seuilGraine && abs(d)<90){
			cout << dist(pt,ptMax) << endl;
			drawCircle(pt,2,RED);
			drawCircle(ptMax+IntPoint2(I1.width(),0),5,RED,2);
			Graine g;
			g.disp=d;
			g.pt=pt;
			result.push(g);
		}
	}
	return result;
}

void semence(const Image<byte>& I1, const Image<byte>& I2, queue<Graine> graines, Image<double>& result){
	queue<Graine> file;
	Image<bool> empilation(I1.width(),I1.height());
	empilation.fill(false);
	result.fill(0);
	while(!graines.empty()){
		Graine g=graines.front();
		graines.pop();
		cout << g.pt.x() <<","<< g.pt.y() <<","<< g.disp << endl;
		result(g.pt)=g.disp;
		if(g.pt.x()>0 && g.pt.x()+g.disp-1>0){
			Graine a; a.pt=g.pt+IntPoint2(-1,0); a.disp=g.disp;
			file.push(a);
			empilation(a.pt)=true;
		}
		if(g.pt.x()<I1.width()-1 && g.pt.x()+g.disp+1<I1.width()-1){
			Graine a; a.pt=g.pt+IntPoint2(1,0); a.disp=g.disp;
			file.push(a);
			empilation(a.pt)=true;
		}
		if(g.pt.y()>0){
			Graine a; a.pt=g.pt+IntPoint2(0,-1); a.disp=g.disp;
			file.push(a);
			empilation(a.pt)=true;
		}
		if(g.pt.y()<I1.height()-1){
			Graine a; a.pt=g.pt+IntPoint2(0,1); a.disp=g.disp;
			file.push(a);
			empilation(a.pt)=true;
		}
	}
	int n=0;
	while(!file.empty()){
		n++;
		if(n%2000==0){
			n=0;
			Image<byte> tmp=result;
			display(tmp);
		}
		Graine g=file.front();
		file.pop();
		double x_1=NCC(I1,g.pt,I2,g.pt+IntPoint2(g.disp-1,0));
		double x0=NCC(I1,g.pt,I2,g.pt+IntPoint2(g.disp,0));
		double x1=NCC(I1,g.pt,I2,g.pt+IntPoint2(g.disp+1,0));
		double max;
		if(x_1>x0 && x_1>x1){
			g.disp--;
			max=x_1;
		}else if (x1>x0 && x1>x_1){
			g.disp++;
			max=x1;
		}else{
			max=x0;
		}
		if(max>=seuilPropagation){
			result(g.pt)=g.disp;
			if(g.pt.x()>0 && g.pt.x()+g.disp-1>0){
				if(empilation(g.pt+IntPoint2(-1,0))==false){
					Graine a; a.pt=g.pt+IntPoint2(-1,0); a.disp=g.disp;
					file.push(a);
					empilation(a.pt)=true;
				}
			}
			if(g.pt.x()<I1.width()-1 && g.pt.x()+g.disp+1<I1.width()-1){
				if(empilation(g.pt+IntPoint2(1,0))==false){
					Graine a; a.pt=g.pt+IntPoint2(1,0); a.disp=g.disp;
					file.push(a);
					empilation(a.pt)=true;
				}
			}
			if(g.pt.y()>0){
				if(empilation(g.pt+IntPoint2(0,-1))==false){
					Graine a; a.pt=g.pt+IntPoint2(0,-1); a.disp=g.disp;
					file.push(a);
					empilation(a.pt)=true;
				}
			}
			if(g.pt.y()<I1.height()-1){
				if(empilation(g.pt+IntPoint2(0,1))==false){
					Graine a; a.pt=g.pt+IntPoint2(0,1); a.disp=g.disp;
					file.push(a);
					empilation(a.pt)=true;
				}
			}
		}
	}
}

int main() {
	Image<byte> I[2];
	load(I[0],srcPath("face00R.png"));
	load(I[1],srcPath("face01R.png"));
	int w=I[0].width(),h=I[0].height(); // On suppose les deux images de memes dimensions

	openWindow(2*w,h);
	display(I[0]);
	display(I[1],w,0);

	queue<Graine> g = graines(I[0],I[1]);

	setActiveWindow(openWindow(2*w,h));
	Image<double> result(I[0].width(),I[0].height());
	Image<double>& refResult=result;
	display(I[0],w,0);
	semence(I[0],I[1],g,refResult);

	result=blur(result,6);

	Image<byte> tmp=result;
	for(int i=0;i<result.width();i++){
			for(int j=0;j<result.height();j++){
				tmp(i,j)=tmp(i,j)+ 20;
			}
		}
	display(tmp);

	for(int i=0;i<result.width();i++){
		for(int j=0;j<result.height();j++){
			result(i,j)=result(i,j)*5;
		}
	}
	int Npoints=result.width()*result.height();
	int Ntriangles=2*(result.width()-1)*(result.height()-1);
	DoublePoint3 *P=new DoublePoint3[Npoints];
	Triangle *T=new Triangle[Ntriangles];
	Color *col = new Color[Npoints];
	for(int x=0;x<result.width();x++){
		for(int y=0;y<result.height();y++){
			P[y*result.width()+x]=DoublePoint3(x,y,result(x,y));
			byte b=I[0](x,y);
			col[y*result.width()+x]=Color(b,b,b);
		}
	}
	for(int x=0;x<result.width()-1;x++){
			for(int y=0;y<result.height()-1;y++){
				T[2*(y*result.width()+x)]=Triangle(y*result.width()+x,y*result.width()+x+1,(y+1)*result.width()+x);
				T[2*(y*result.width()+x)+1]=Triangle((y+1)*result.width()+x+1,y*result.width()+x+1,(y+1)*result.width()+x);
			}
		}
	VtkMesh M(P,Npoints,T,Ntriangles,VERTEX_COLOR);
	M.setColors(col);
	Window win = openWindow(600,600,"3D",WINDOW_VTK);
	setActiveWindow(win);
	showMesh(M);
	click();
	delete P;
	delete T;
	return 0;
}
