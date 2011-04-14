
#include "segmentationLayer.h"
#include <stdio.h>
#include <algorithm>
#include "cv.h"
#include "CmdLine.h"

int main(int argc, char* argv[]){
	CCmdLine cmdLine;
	
	cmdLine.SplitLine(argc, argv);

	if ( !(cmdLine.HasSwitch("-i") && cmdLine.HasSwitch("-o") && (cmdLine.HasSwitch("-pos") || cmdLine.HasSwitch("-f"))) ){
		fprintf(stderr, "usage: %s -i <image> -o <output-prefix> < -pos <x> <y> | -f <fixation-points-file> > [ -flow <optical-flow-file> ] [-sobel]\n",argv[0]);
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

	strcpy (tmp, cmdLine.GetArgument("-flow", 0).c_str());
	frame1.readFlow_flo(tmp);	

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
