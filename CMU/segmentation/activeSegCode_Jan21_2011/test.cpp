
#include "segmentationLayer.h"
#include <stdio.h>
#include <algorithm>
#include "cv.h"
#include <string>

int main(int argc, char* argv[]){
	if (argc != 5){
		fprintf(stderr, "usage: %s <image> <x> <y> <output-prefix>",argv[0]);
		exit(1);
	}

	class segLayer frame1;
	char imageName[30];

	//sprintf(imageName,"%s%08d.ppm",argv[1],1);
  	frame1.readImage(argv[1]);


	// Edge detection!
  	frame1.edgeSobel();

	frame1.generatePbBoundary();

	frame1.allocateMemForContours();// Don't forget to allocate memory to store the region contours.

	//select fixation point!
	float x,y;
	sscanf(argv[2],"%f",&x);
	sscanf(argv[3],"%f",&y);
	frame1.assignFixPt((int)x, (int)y);

	//segment
	frame1.segmentCurrFix();

	//display!
	frame1.displayCurrSegs(-1);
	frame1.saveRegions(argv[4]);
	//release memory!
	frame1.deallocateMemForContours();
  	return 0;
	
}
