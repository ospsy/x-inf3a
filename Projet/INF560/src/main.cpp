#include <cv.h>
#include <highgui.h>
#include <iostream>

#include "surf.h"
#include "surfCUDA.h"


int main ( int argc, char **argv )
{
  cvNamedWindow( "My Window", 1 );
  cvNamedWindow( "My Window 2", 1 );
  IplImage *img = cvLoadImage("lena_600.jpg",CV_LOAD_IMAGE_GRAYSCALE);
  if(!img){
  	std::cout << "impossible de charger l'image, aborting..." << std::endl;
  	return 1;
  }
  IplImage *img2 = cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_32S,1);
  cvShowImage( "My Window", img );
  //integralImage
  makeIntegralImage(img,img2);
  cvShowImage( "My Window 2", img2 );
  //filtres gaussiens
  IplImage *imgs[6], *imgs2[6];
  for(int i=0;i<6;i++){
  	imgs[i]=cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_32S,1);
  	imgs2[i]=cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_32S,1);
  }
  clock_t timer=clock();
  calculateGaussianDerivative(img2,imgs,0,6);
  std::cout << "calculateGaussianDerivative : " << 1000*(float)(clock()-timer)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
  CUDAcalculateGaussianDerivative(img2,imgs2,0,6);
  cvShowImage( "My Window 3", imgs[0] );
  //cvShowImage( "My Window 4", imgs[1] );
  //cvShowImage( "My Window 5", imgs[2] );
  for(int i=0;i<img->height;i++){
  	for(int j=0;j<img->width;j++){
  		i=j;
  		//if(((int*)( imgs[0]->imageData + imgs[0]->widthStep * i)) [j] != ((int*)( imgs2[0]->imageData + imgs2[0]->widthStep * i)) [j])
  		std::cout << i << "," << j << " "<< ((int*)( imgs[0]->imageData + imgs[0]->widthStep * i)) [j] << " "<< ((int*)( imgs2[0]->imageData + imgs2[0]->widthStep * i)) [j] << std::endl;
    }
  }
  
  cvWaitKey();
  cvReleaseImage(&img);
  cvReleaseImage(&img2);
  std::cout << "plop2" << std::endl;
  return 0;
}
