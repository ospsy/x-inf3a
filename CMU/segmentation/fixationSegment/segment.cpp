
#include "segmentationLayer.h"
#include <stdio.h>
#include <algorithm>
#include "cv.h"
#include "CmdLine.h"

int main(int argc, char* argv[]){
	CCmdLine cmdLine;
	
	cmdLine.SplitLine(argc, argv);

	if ( !(cmdLine.HasSwitch("-i") && cmdLine.HasSwitch("-o") && (cmdLine.HasSwitch("-pos") || cmdLine.HasSwitch("-f"))) ){
		fprintf(stderr, "usage: %s -i <image> -o <output-directory> < -pos <x> <y> | -f <fixation-points-file> > [ -flow <optical-flow-file> ] [-sobel]\n",argv[0]);
		exit(1);
	}
	class segLayer frame1;
	char tmp[80];
	strcpy (tmp, cmdLine.GetArgument("-i", 0).c_str());
  	frame1.readImage(tmp);

	// Edge detection!
	if (cmdLine.HasSwitch("-sobel"))
  		frame1.edgeSobel();
	else
		frame1.edgeCGTG();

	if (cmdLine.HasSwitch("-flow")){
		strcpy (tmp, cmdLine.GetArgument("-flow", 0).c_str());
		IplImage *flow=cvLoadImage(tmp);
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
	}

	frame1.generatePbBoundary();

	frame1.allocateMemForContours();// Don't forget to allocate memory to store the region contours.
	//select fixation point!
	if(cmdLine.HasSwitch("-pos")){
		float x,y;
		sscanf(cmdLine.GetArgument("-pos", 0).c_str(),"%f",&x);
		sscanf(cmdLine.GetArgument("-pos", 1).c_str(),"%f",&y);
		frame1.assignFixPt((int)x, (int)y);
	}else{
		strcpy (tmp, cmdLine.GetArgument("-f", 0).c_str());
		frame1.readFixPts(tmp);
	}

	//segment
	frame1.segmentAllFixs();

	//display!
	//frame1.displayCurrSegs(-1);
	strcpy (tmp, cmdLine.GetArgument("-o", 0).c_str());
	sprintf(tmp,"%s/",tmp);
	frame1.saveRegions(tmp);
	//release memory!
	frame1.deallocateMemForContours();
  	return 0;
	
}
