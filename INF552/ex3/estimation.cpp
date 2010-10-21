#include <iostream>
#include <Imagine/Images.h>
#include <Imagine/LinAlg.h>

#include "Imagine/Features.h"
#include "Imagine/Optim.h"

using namespace std;
using namespace Imagine;

typedef FVector<double,2> Vec2;
typedef FVector<double,3> Vec3;
typedef FVector<double,8> Homography;
typedef SIFTDetector Detector;
typedef Detector::Feature Feat;

Vec2 applyHomography(const Homography& H,const Vec2& m) {
	// Completer: calculer l'image de m par H
	return Vec2(0.);
}

Homography homographyFrom4Pairs(const Vec2 m1[4],const Vec2 m2[4]) {
	Matrix<double> A(8,8);
	Vector<double> B(8);
	A.fill(0);
	// Completer: remplir A et B pour que H verifie AH=B avec H(m1[k])=m2[k] 
	for(int k=0;k<4;k++){
		B[2*k]=m2[k][0];
		B[2*k+1]=m2[k][1];
		A(0,0)=m1[k][0];
		A(0,1)=m1[k][1];
		A(0,2)=1;
		A(1,3)=m1[k][0];
		A(1,4)=m1[k][1];
		A(1,5)=1;
	}
	Matrix<double> C=inverse(A);
	if(norm(C)==0)	// non inversible
		return Homography(0.);
	Vector<double> H=C*B;
	return Homography(H.data());	// Painlesss Vector -> FVector conversion
}

Homography meanHomography(const Array<Feat>& feats1,const Array<Feat>& feats2,const MatchList& matches) {
	int nb=int(matches.size());
	Matrix<double> A(2*nb,8);
	Vector<double> B(2*nb);
	A.fill(0);
	int k=0;
	// Completer: remplir A et B pour que H verifie AH=B 
	for (MatchList::const_iterator it=matches.begin();it!=matches.end();k++,it++){
		Vec2 m1=feats1[it->first].pos;
		Vec2 m2=feats2[it->second].pos;
		// ...
	}
	Matrix<double> C=pseudoInverse(A);	// Moindres carr�s
	if(norm(C)==0)	// non invertible
		return Homography(0.);
	Vector<double> H=C*B;
	return Homography(H.data());	// Painlesss Vector -> FVector conversion
}

Homography manualHomography(int w) {
	Vec2 m1[4],m2[4];
	// Completer: Entrer les m1 et les m2 � la souris tels que m1[k] et m2[k] se correspondent
	// ...
	return homographyFrom4Pairs(m1,m2);
}

// Functor: estimates an Homography from 4 pairs
class HomEstimator {
	const Array<Feat>& feats1,feats2;
public:	
	HomEstimator(const Array<Feat>& ft1,const Array<Feat>& ft2):feats1(ft1),feats2(ft2){}
	template <class H_IT>
	void operator() (const FArray<MatchList::iterator,4>& pairs, H_IT homographies) const {
		Vec2 m1[4],m2[4];
		for(int k=0;k<4;k++){
			m1[k]=feats1[pairs[k]->first].pos;
			m2[k]=feats2[pairs[k]->second].pos;
		}
		Homography H;
		// Completer: calculer H a partir de m1 et m2
		// ...
		if (norm(H)!=0)	
			*homographies++ = H;
	}
};

// Functor: estimates the square residual of a pair w.r.t. an Homography
class HomResidual {
	const Array<Feat>& feats1,feats2;
public:	
	HomResidual(const Array<Feat>& ft1,const Array<Feat>& ft2):feats1(ft1),feats2(ft2){}
	double operator() (const Homography& H,const pair<size_t,size_t>& p) const {
		Vec2 m1(feats1[p.first].pos);
		Vec2 m2(feats2[p.second].pos);
		// Corriger: retourner la distance au carr� entre m2 et l'image de m1 par H
		// ...
		return 0; 
	}
};

Homography autoHomography(const Image<Color>&I1,const Image<Color>&I2,bool ransac=false) {
	Detector d;
	Array<Feat> feats1=d.run(I1);
	drawFeatures(feats1);
	Array<Feat> feats2=d.run(I2);	
	drawFeatures(feats2,IntPoint2(I1.width(),0));
	MatchList matches=loweMatch(feats1,feats2,.5,true);
	cout << matches.size() << " matches" << endl;
	drawMatches(feats1,feats2,matches,IntPoint2(0,0),IntPoint2(I1.width(),0),1.,true);
	click();
	if (ransac) {
		Homography H;
		double outlier_thres;
		double median_res = leastMedianOfSquares<4>(matches.begin(), matches.end(), HomEstimator(feats1,feats2), HomResidual(feats1,feats2),H,&outlier_thres);
		MatchList inliers;
		for (MatchList::const_iterator it=matches.begin();it!=matches.end();it++){
			if ( HomResidual(feats1,feats2)(H,*it) < outlier_thres )
				inliers.push_front(*it);
		}
		cout << inliers.size() << " inliers" << endl;
		matches=inliers;
	}
	return meanHomography(feats1,feats2,matches);
}

void displayStitch(const Image<Color>&I1,const Image<Color>&I2,Homography H) {
	int w=I1.width(),h=I1.height();
	Image<Color> BIG(2*w,2*h); // Big image storing (-h/2<i<3*w/2) x (-h/2<j<3h/2)
	BIG.fill(BLACK);
	for (int j=-h/2;j<3*h/2;j++) { 
		for (int i=-w/2;i<3*w/2;i++) {
			// Completer
			// remplir BIG(i+w/2,j+h/2) avec si possible:
			// - I1(i,j)
			// - I2(H(i,j))
			// - la moyenne des deux 
		}
	}
	Window W2=openWindow(2*w,2*h);
	setActiveWindow(W2);
	display(BIG);
	click();
	closeWindow(W2);
}

int main() {
	Image<Color> I1,I2;
	load(I1,srcPath("image0006.jpg"));
	load(I2,srcPath("image0007.jpg"));
	int w=I1.width(),h=I1.height();
	Window W1=openWindow(2*w,h);
	display(I1);
	display(I2,w,0);

	Homography H;
	// Supprimer progressivement les commentaires
	H=manualHomography(w);
	// displayStitch(I1,I2,H);setActiveWindow(W1);
	// H=autoHomography(I1,I2);
	// displayStitch(I1,I2,H);setActiveWindow(W1);
	// H=autoHomography(I1,I2,true);
	// displayStitch(I1,I2,H);
	
	
	endGraphics();
	return 0;
}
