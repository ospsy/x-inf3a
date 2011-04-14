#include "cv.h"
#include "cxcore.h"
#include "highgui.h"
#include "savgol.h"
#include "textureGradient.h"
#include "sobelEdge.h"
#include <sys/time.h>
//---------------
// MAIN FUNCTION
//--------------
int main(int argc, char *argv[]){
    
	// check the arguments!
	if (argc < 2 ){
		fprintf(stderr,"needs at least one argument: <color image name>");
		exit(1);
	}
	
	char* inputImgName = argv[1];
	IplImage* inputImg_uchar = cvLoadImage(inputImgName);
	if( inputImg_uchar == NULL){
		fprintf(stderr,"\n unable to read %s",inputImgName);
		exit(1);
    	}

	//Show the original image
	cvNamedWindow("Original image"); cvShowImage("Original image",inputImg_uchar);
	cvWaitKey(-1);

	IplImage *img_float = cvCreateImage(cvGetSize(inputImg_uchar),IPL_DEPTH_32F,inputImg_uchar->nChannels);
	cvConvertScale(inputImg_uchar, img_float,1.0);


	//Output images
	IplImage* grad_float = cvCreateImage(cvGetSize(img_float),IPL_DEPTH_32F,1);	cvSetZero(grad_float);
	IplImage* ori_float  = cvCreateImage(cvGetSize(img_float),IPL_DEPTH_32F,1);	cvSetZero(ori_float);

	// the color gradient based edge detector!!
	struct timeval start_time, end_time;
	gettimeofday(&start_time,NULL);

	pbCG(img_float, grad_float, ori_float);
	gettimeofday(&end_time,NULL);
	printf("\n it took %2.5f", (end_time.tv_sec - start_time.tv_sec)+ (end_time.tv_usec -start_time.tv_usec)/1000000.0f);

	// Show the gradient image 
	cvNamedWindow("gradient image"); cvShowImage("gradient image",grad_float);
	cvWaitKey(-1);
	
	//release images	
	cvReleaseImage(&inputImg_uchar);	
	cvReleaseImage(&img_float);	
	cvReleaseImage(&grad_float);	
	cvReleaseImage(&ori_float);
	return 0;
  }
