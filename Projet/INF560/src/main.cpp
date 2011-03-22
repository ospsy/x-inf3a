#include <cv.h>
#include <highgui.h>
#include <iostream>

uint* CUDAintegral;
uint CUDAintegralPitch;
uint* CUDAimg;
uint CUDAimgPitch;
uint** CUDAimgs;
uint CUDAimgsPitch;
uint* CUDAadressImgs;

#include "surf.h"
#include "surfCUDA.h"
#include "common.h"


int main ( int argc, char **argv )
{
	int intervals=5;
  int octave=0;
  /*CvCapture *capture;
  capture = cvCreateFileCapture("/users/eleves-a/x2008/benoit.seguin/INF560/Projet/cuda.avi");
  if (!capture) {
   printf("Ouverture du flux vidéo impossible !\n");
   return 1;
  }
  IplImage *img = cvQueryFrame(capture);*/
  IplImage *img = cvLoadImage("image1.jpg");
  uint width=img->width;
  uint height=img->height;
  IplImage *imgGrayscale = cvCreateImage( cvSize( width, height ), IPL_DEPTH_8U, 1 );
  cvCvtColor( img, imgGrayscale, CV_RGB2GRAY );
  //initialisation de la memoire CUDA
  CUDAinit(imgGrayscale->width,imgGrayscale->height,intervals);
  
  //initialisation des variables
  char s[255];
  clock_t totaltime;
  IplImage *integral = cvCreateImage(cvSize(imgGrayscale->width,imgGrayscale->height),IPL_DEPTH_32S,1);
  IplImage *imgs[intervals];
  IplImage *imgsShow[intervals];
   for(int i=0;i<intervals;i++){
	  	imgs[i]=cvCreateImage(cvSize(imgGrayscale->width,imgGrayscale->height),IPL_DEPTH_32S,1);
	  	imgsShow[i]=cvCreateImage(cvSize(imgGrayscale->width,imgGrayscale->height),IPL_DEPTH_32S,1);
	  }
  //initialisation de la boucle
  makeIntegralImage(imgGrayscale,integral);
  CUDAcalculateGaussianDerivative(integral,octave,intervals);
  //boucle de traitement
  for(int frame=1;frame<=90;frame++){
	  totaltime=clock();
	  sprintf(s,"image%i.jpg",frame);
	  img = cvLoadImage(s);
	  cvCvtColor( img, imgGrayscale, CV_RGB2GRAY );
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
	  timer=clock();
	  
	  CUDAretrieveGaussianDerivative(imgs,intervals);
	  CUDAcalculateGaussianDerivative(integral,octave,intervals);
	  
	  //calculateGaussianDerivative(integral,imgs,octave,intervals);
	  std::cout << "calculateGaussianDerivative : " << 1000*(float)(clock()-timer)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
	  
	  
	  //recherche d'extremas
	  timer=clock();
	  std::list<std::vector<int> > maxs(findExtrema(imgs,intervals));
	  std::cout << "findExtrema : " << 1000*(float)(clock()-timer)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
	  
	  std::cout << "tmps total avt affichage : " << 1000*(float)(clock()-totaltime)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
	  
	  //affichage
	  //cvShowImage( "Integrale", img2 );
	  
	  std::list<vector<int> >::iterator tmp=maxs.begin();
	  for (int i = 0; i < intervals; ++i) {
	  	cvScale(imgs[i],imgsShow[i],100);
	  	while(tmp!=maxs.end() && (*tmp)[0]==i){
	  		cvCircle(imgsShow[i], cvPoint((*tmp)[2],(*tmp)[1]), borderSize(octave,i), cvScalar(0,255,0), 1);
	  		cvCircle(img, cvPoint((*tmp)[2],(*tmp)[1]), borderSize(octave,i), cvScalar(0,255,0), 2);
	  		tmp++;
	  	}
	  	sprintf(s,"%i",i);
	  	cvShowImage( s, imgsShow[i] );
	  }
	  cvShowImage( "Image originale", img );
	  cvWaitKey();
  }
  //FIN
  cvWaitKey();
  cvReleaseImage(&imgGrayscale);
  cvReleaseImage(&integral);
  //cvReleaseCapture(&capture);
  CUDAclose(intervals);
  return 0;
}
