/* To segment a moving object */
#include "segmentationLayer.h"
#include <stdio.h>
#include <algorithm>
#include "cv.h"

int main(int argc, char* argv[]){
	if (argc != 3){
		fprintf(stderr, "usage: %s <image_1> <flowMap/disparity>",argv[0]);
		exit(1);
	}

	class segLayer frame1; 
  	frame1.readImage(argv[1]);

	// Edge detection!
  	frame1.edgeCGTG();
	/*frame1.saveEdgeMap(NULL);
	return 0;*/

	//Biasing with optic flow!
	//frame1.readFlow_flo(argv[2]);
	IplImage *flow=cvLoadImage(argv[2]);
	IplImage *flow32 = cvCreateImage(cvGetSize(flow), IPL_DEPTH_32F,3);
	IplImage *flowU = cvCreateImage(cvGetSize(flow), IPL_DEPTH_32F,1);
	IplImage *flowV = cvCreateImage(cvGetSize(flow), IPL_DEPTH_32F,1);
	cvConvertScale(flow, flow32, 40/255.,-20);
	cvSplit(flow32,flowU,NULL,NULL,NULL);
	cvSplit(flow32,NULL,flowV,NULL,NULL);
	frame1.setU(flowU);
	frame1.setV(flowV);
	cvReleaseImage(&flow);
	cvReleaseImage(&flow32);

	frame1.displayFlowMag(-1);
	frame1.generatePbBoundary();
	frame1.displayPbBoundary(-1);


	frame1.allocateMemForContours();

	//select fixation point!
	frame1.selectFixPt_interactive();

	//segment
	frame1.segmentCurrFix();

	//display!
	frame1.displayCurrSegs(-1);
	
	//release memory!
	frame1.deallocateMemForContours();
  	return 0;
}
