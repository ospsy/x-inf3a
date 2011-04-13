/* To segment a static object on a table or known surface */
#include "segmentationLayer.h"
#include <stdio.h>
#include <algorithm>
#include "cv.h"
#include <string>

int main(int argc, char* argv[]){
	if (argc != 2){
		fprintf(stderr, "usage: %s <image>",argv[0]);
		exit(1);
	}

	class segLayer frame1;
	char imageName[30];

	sprintf(imageName,"%s%08d.ppm",argv[1],1);
  	frame1.readImage(imageName);


	// Edge detection!
  	frame1.edgeSobel();

	frame1.generatePbBoundary();
	frame1.displayPbBoundary(-1);

	frame1.allocateMemForContours();// Don't forget to allocate memory to store the region contours.

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
