#include <cv.h>
#include <highgui.h>
#include <iostream>

uint* CUDAintegral;
uint CUDAintegralPitch;
uint* CUDAimg;
uint CUDAimgPitch;

#include "surf.h"
#include "surfCUDA.h"
#include "common.h"


int main ( int argc, char **argv )
{
  /*CvCapture *capture;
  capture = cvCreateFileCapture("lena.jpg");
  if (!capture) {
   printf("Ouverture du flux vidéo impossible !\n");
   return 1;
  }
  IplImage *img = cvQueryFrame(capture);*/
  IplImage *img = cvLoadImage("lena_600.jpg");
  uint width=img->width;
  uint height=img->height;
  IplImage *imgGrayscale = cvCreateImage( cvSize( width, height ), IPL_DEPTH_8U, 1 );
  cvCvtColor( img, imgGrayscale, CV_RGB2GRAY );
  //initialisation de la memoire CUDA
  CUDAinit(imgGrayscale->width,imgGrayscale->height);
  clock_t totaltime=clock();
  IplImage *integral = cvCreateImage(cvSize(imgGrayscale->width,imgGrayscale->height),IPL_DEPTH_32S,1);
  //integralImage
  clock_t timer=clock();
  makeIntegralImage(imgGrayscale,integral);
  std::cout << "integrale : " << 1000*(float)(clock()-timer)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
//  for(int i=1;i<img->height;i++){
//  	for(int j=1;j<img->width;j++){
//  		i=j;
//  		//if(((int*)( imgs[0]->imageData + imgs[0]->widthStep * i)) [j] != ((int*)( imgs2[0]->imageData + imgs2[0]->widthStep * i)) [j])
//  		std::cout << i << "," << j << " "<< (int)((uchar*)( (img->imageData) + (img->widthStep) * i)) [j]<< " "<< getPixel(img2,i,j)+getPixel(img2,i-1,j-1)-getPixel(img2,i-1,j)-getPixel(img2,i,j-1) << std::endl;
//    }
//  }
  //filtres gaussiens
  int intervals=6;
  int octave=1;
  IplImage *imgs[intervals], *imgs2[intervals];
  for(int i=0;i<intervals;i++){
  	imgs[i]=cvCreateImage(cvSize(imgGrayscale->width,imgGrayscale->height),IPL_DEPTH_32S,1);
  	imgs2[i]=cvCreateImage(cvSize(imgGrayscale->width,imgGrayscale->height),IPL_DEPTH_32S,1);
  }
  timer=clock();
  //calculateGaussianDerivative(img2,imgs,0,intervals);
  std::cout << "calculateGaussianDerivative : " << 1000*(float)(clock()-timer)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
  calculateGaussianDerivative(integral,imgs2,octave,intervals);
  
  //recherche d'extremas
  timer=clock();
  std::list<std::vector<int> > maxs(findExtrema(imgs2,intervals));
  std::cout << "findExtrema : " << 1000*(float)(clock()-timer)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
  
  std::cout << "tmps total avt affichage : " << 1000*(float)(clock()-totaltime)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
  
  //affichage
  //cvShowImage( "Integrale", img2 );
  IplImage *imgsShow[6];
  std::list<vector<int> >::iterator tmp=maxs.begin();
  for (int i = 1; i < 4; ++i) {
  	imgsShow[i]=cvCreateImage(cvSize(imgGrayscale->width,imgGrayscale->height),IPL_DEPTH_32S,1);
  	cvScale(imgs2[i],imgsShow[i],100);
  	while(tmp!=maxs.end() && (*tmp)[0]==i){
  		cvCircle(imgsShow[i], cvPoint((*tmp)[2],(*tmp)[1]), borderSize(octave,i), cvScalar(0,255,0), 1);
  		cvCircle(img, cvPoint((*tmp)[2],(*tmp)[1]), borderSize(octave,i), cvScalar(0,255,0), 2);
  		tmp++;
  	}
  	cvShowImage( "1"+i, imgsShow[i] );
  }
  cvShowImage( "Image originale", img );
  
  cvWaitKey();
  cvReleaseImage(&imgGrayscale);
  cvReleaseImage(&integral);
  //cvReleaseCapture(&capture);
  CUDAclose();
  return 0;
}
