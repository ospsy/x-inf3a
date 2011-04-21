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
  	frame1.edgeSobel();

	//Biasing with optic flow!
	frame1.readFlow_flo(argv[2]);
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
