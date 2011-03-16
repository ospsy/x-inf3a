#include <cv.h>
#include <highgui.h>
#include <iostream>

#include "surf.h"
#include "surfCUDA.h"


int main ( int argc, char **argv )
{
  IplImage *img = cvLoadImage("lena_600.jpg",CV_LOAD_IMAGE_GRAYSCALE);
  if(!img){
  	std::cout << "impossible de charger l'image, aborting..." << std::endl;
  	return 1;
  }
  IplImage *img2 = cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_32S,1);
  cvShowImage( "Image originale", img );
  //integralImage
  makeIntegralImage(img,img2);
  cvShowImage( "Integrale", img2 );
//  for(int i=1;i<img->height;i++){
//  	for(int j=1;j<img->width;j++){
//  		i=j;
//  		//if(((int*)( imgs[0]->imageData + imgs[0]->widthStep * i)) [j] != ((int*)( imgs2[0]->imageData + imgs2[0]->widthStep * i)) [j])
//  		std::cout << i << "," << j << " "<< (int)((uchar*)( (img->imageData) + (img->widthStep) * i)) [j]<< " "<< getPixel(img2,i,j)+getPixel(img2,i-1,j-1)-getPixel(img2,i-1,j)-getPixel(img2,i,j-1) << std::endl;
//    }
//  }
  //filtres gaussiens
  int intervals=6;
  IplImage *imgs[intervals], *imgs2[intervals];
  for(int i=0;i<intervals;i++){
  	imgs[i]=cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_32S,1);
  	imgs2[i]=cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_32S,1);
  }
  clock_t timer=clock();
  calculateGaussianDerivative(img2,imgs,0,intervals);
  std::cout << "calculateGaussianDerivative : " << 1000*(float)(clock()-timer)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
  CUDAcalculateGaussianDerivative(img2,imgs2,0,intervals);
  
  int i=30;
  	for(int j=0;j<img->width;j++){
  		//if(((int*)( imgs[0]->imageData + imgs[0]->widthStep * i)) [j] != ((int*)( imgs2[0]->imageData + imgs2[0]->widthStep * i)) [j])
  		//std::cout << i << "," << j << " "<< ((int*)( imgs[0]->imageData + imgs[0]->widthStep * i)) [j] << " "<< ((int*)( imgs2[0]->imageData + imgs2[0]->widthStep * i)) [j] << std::endl;
    }
  
  //recherche d'extremas
  timer=clock();
  std::list<std::vector<int> > maxs(findExtrema(imgs,intervals));
  std::cout << "findExtrema : " << 1000*(float)(clock()-timer)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
  
  //affichage
  IplImage *imgsShow[6];
  std::list<vector<int> >::iterator tmp=maxs.begin();
  for (int i = 1; i < 4; ++i) {
  	imgsShow[i]=cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_32S,1);
  	cvScale(imgs[i],imgsShow[i],100);
  	while(tmp!=maxs.end() && (*tmp)[0]==i){
  		cvCircle(imgsShow[i], cvPoint((*tmp)[2],(*tmp)[1]), 10, cvScalar(0,255,0), 1);
  		tmp++;
  	}
  	cvShowImage( "1"+i, imgsShow[i] );
  }
  
  cvWaitKey();
  cvReleaseImage(&img);
  cvReleaseImage(&img2);
  std::cout << "plop2" << std::endl;
  return 0;
}
