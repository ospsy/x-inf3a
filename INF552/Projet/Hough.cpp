#include "Hough.h"

#include <iostream>
#include <fstream>
#include <queue>

using namespace std;
using namespace Imagine;

typedef FVector<double,3> vec;
typedef FMatrix<double,3,3> mat;

const float PI=3.1415;

Image<byte> Hough(const Image<byte>& in,int T, int R){
	int w=in.width();
	int h=in.height();
	Image<long> result(T,R);
	const float rFactor=R/sqrt(w*w+h*h);
	const float rFactor2=1/rFactor;
	const float tFactor=T/(2*PI);
	const float tFactor2=1/tFactor;
	result.fill(0);
	for(int x=0;x<w;x++){
		for(int y=0;y<h;y++){
			for(int t=0;t<T;t++){
				int r=(int)((x*cos(t*tFactor2)+y*sin(t*tFactor2))*rFactor);
				if(in(x,y)<127 && r>=0 && r<R) result(t,r)++;
			}
		}
		if(x%10==0) {
			display((Image<byte>)result);
			cout << x << endl;
		}
	}
	cout << "blur" << endl;
	//result=blur(result,2);
	cout << "blur effectué" << endl;
	for(int t=1;t<T;t++){
		for(int r=1;r<R;r++){
			if(result(t,r)>200 && result(t,r)>result((t+1)%T,r) && result(t,r)>result((t-1)%T,r)
			&& result(t,r)>result(t,(r+1)%R) && result(t,r)>result(t,(r-1)%R)){
				cout << t << ',' << r << endl;
				drawLine(0,(int)(r*rFactor2*asin(t*tFactor2)),w,(int)(R,(r*rFactor2-w*cos(t*tFactor2))*asin(t*tFactor2)),RED,2);

			}

		}
	}
	return result;
}
