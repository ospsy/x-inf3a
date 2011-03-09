#include <cv.h>
#include <highgui.h>
#include <iostream.h>

void makeIntegralImage(const IplImage* in, IplImage* out){
	if(in->depth!=IPL_DEPTH_8U || out->depth!=IPL_DEPTH_32S){
		std::cout << "Mauvais type d'images dans makeIntegralImage" << std::endl;
		exit(EXIT_FAILURE);
	}
	clock_t timer=clock();
	unsigned int tmp=0;
	for(int j=0;j<in->height;j++){
		tmp+=((uchar*)(in->imageData + in->widthStep*0))[j];
		((uint*)((uchar*)out->imageData + out->widthStep*0))[j]=tmp;
	}
	for(int i=1;i<in->height;i++){
		unsigned int tmp=0;
		for(int j=0;j<in->width;j++){
			tmp+=((uchar*)(in->imageData + in->widthStep*i))[j];
			((uint*)((uchar*)out->imageData + out->widthStep*i))[j]=tmp+((uint*)((uchar*)out->imageData + out->widthStep*(i-1)))[j];
		}
	}
	std::cout << "makeIntegralImage : " << 1000*(clock()-timer)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
}