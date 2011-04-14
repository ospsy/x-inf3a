/* To segment a region in a single image */
#include "segmentationLayer.h"
#include <stdio.h>
#include <algorithm>
#include "cv.h"

int main(int argc, char* argv[]){
	if (argc != 2){
		fprintf(stderr, "usage: %s <image> ",argv[0]);
		exit(1);
	}

	class segLayer frame1; 
  	frame1.readImage(argv[1]);

	// Edge detection!
  	frame1.edgeSobel();
	frame1.generatePbBoundary();
	frame1.displayPbBoundary(-1);

	//select fixation point!
	frame1.selectFixPt_interactive();

	//segment
	frame1.allocateMemForContours();
	frame1.segmentCurrFix();

	//display!
	frame1.displayCurrSegs(-1);

	//release memory!
	frame1.deallocateMemForContours();
  	return 0;
	
}
