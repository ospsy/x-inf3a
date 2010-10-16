#include <Imagine/Images.h>

#include <iostream>
#include <fstream>
#include <queue>

#include "Hough.h"

using namespace std;

typedef FVector<double,3> vec;
typedef FMatrix<double,3,3> mat;

//constante
const double sigma=4;
const double seuilHarris=1000;
const double seuilRapport=0.96;
const double seuilGraine=0.8;
const double seuilPropagation=0.55;
const int W_length=5;


int main() {
	Image<byte> I;
	load(I,srcPath("img.png"));
	int w=I.width(),h=I.height(); // On suppose les deux images de memes dimensions

	openWindow(w,h);
	display((Image<byte>)Hough(I));
	click();
	return 0;
}
