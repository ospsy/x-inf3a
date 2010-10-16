#include "Hough.h"

#include <iostream>
#include <fstream>
#include <queue>

using namespace std;
using namespace Imagine;

typedef FVector<double,3> vec;
typedef FMatrix<double,3,3> mat;

const float PI=3.1415;

Image<long> Hough(const Image<byte>& in,int T, int R){
	int w=in.width();
	int h=in.height();
	Image<long> result(T,R);
	result.fill(0);
	for(int x=0;x<w;x++){
		for(int y=0;y<h;y++){
			for(int t=0;t<T;t++){
				int r=(int)(x*cos(t)+y*sin(t));
				if(r>=0 && r<R) result(t,r)+=in(x,y);
			}
		}
		Image<byte> result2=result;
		display(result2);
	}
	return result;
}
