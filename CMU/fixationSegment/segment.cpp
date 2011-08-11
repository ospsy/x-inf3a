
#include "segmentationLayer.h"
#include <stdio.h>
#include <algorithm>
#include "cv.h"
#include "CmdLine.h"

#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
int fdlock;
int get_lock(void)
{
  struct flock fl;
  fl.l_type = F_WRLCK;
  fl.l_whence = SEEK_SET;
  fl.l_start = 0;
  fl.l_len = 1;

  if((fdlock = open("GPU.lock", O_WRONLY|O_CREAT, 0666)) == -1)
    return 0;

  if(fcntl(fdlock, F_SETLKW, &fl) == -1)
    return 0;

  return 1;
}

int release_lock(void)
{
  struct flock fl;
  fl.l_type = F_UNLCK;
  fl.l_whence = SEEK_SET;
  fl.l_start = 0;
  fl.l_len = 1;

  if(fcntl(fdlock, F_SETLK, &fl) == -1)
    return 0;

  return 1;
}

int main(int argc, char* argv[]){
	CCmdLine cmdLine;
	
	cmdLine.SplitLine(argc, argv);

	if ( !(cmdLine.HasSwitch("-i") && cmdLine.HasSwitch("-o") && (cmdLine.HasSwitch("-pos") || cmdLine.HasSwitch("-f") || cmdLine.HasSwitch("-pbOnly"))) ){
		fprintf(stderr, "usage: %s -i <image> -o <output-directory> < -pos <x> <y> | -f <fixation-points-file> > [ -pb <probabilistic-boundary-prefix ] [ -flow <optical-flow-file> ] [ -sobel ]\n",argv[0]);
		fprintf(stderr, "OR \t %s -pbOnly -i <image> -o <output-probabilistic-boundary-prefix>\n",argv[0]);
		exit(1);
	}
	class segLayer frame1;
	char tmp[80];
	strcpy (tmp, cmdLine.GetArgument("-i", 0).c_str());

	int64 tic1,tic2,tic3,tic4;
	double ticFrequency = cvGetTickFrequency()*1000000;
	tic1=cvGetTickCount();

	IplImage *im=cvLoadImage(tmp), *im2;
	int maxWidth=900;
	bool resized=false;
	float scale=1;
	if(cvGetSize(im).width>maxWidth){
		scale=maxWidth/(double)(cvGetSize(im).width);
		printf("Image too big, resizing it for the segmentation...\n");
	    	int newHeight=(int)(cvGetSize(im).height*scale);
	    	im2=cvCreateImage( cvSize(maxWidth,newHeight), IPL_DEPTH_8U, 3 );
	    	cvResize(im,im2);
		resized=true;
	}else{
		im2=im;
	}
  	frame1.setImage(im2);

	if (cmdLine.HasSwitch("-pb")){
		strcpy (tmp, cmdLine.GetArgument("-pb", 0).c_str());
		frame1.readPbBoundary(tmp);
	}else{

		// Edge detection!
		if (cmdLine.HasSwitch("-sobel"))
	  		frame1.edgeSobel();
		else{
	#ifdef CUDA_SUPPORT
			if(!get_lock()){
				fprintf(stderr,"Impossible to get the lock...\n");
				exit(1);
			}
			frame1.edgeGPU(false);
			if(!release_lock()){
				fprintf(stderr,"Impossible to release the lock...\n");
				exit(1);
			}
	#else
			frame1.edgeCGTG();
	#endif
		}

		tic2=cvGetTickCount();

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
	}
	
	if (cmdLine.HasSwitch("-pbOnly")){
		strcpy (tmp, cmdLine.GetArgument("-o", 0).c_str());
		frame1.savePbBoundary(tmp);
	}else{
		frame1.allocateMemForContours();// Don't forget to allocate memory to store the region contours.
		//select fixation point!
		if(cmdLine.HasSwitch("-pos")){
			float x,y;
			sscanf(cmdLine.GetArgument("-pos", 0).c_str(),"%f",&x);
			sscanf(cmdLine.GetArgument("-pos", 1).c_str(),"%f",&y);
			frame1.assignFixPt((int)(x*scale), (int)(y*scale));
		}else{
			strcpy (tmp, cmdLine.GetArgument("-f", 0).c_str());
			frame1.readFixPts(tmp,scale);
		}
		//segment
		frame1.segmentAllFixs();		
		
		tic3=cvGetTickCount();

		//display!
		//frame1.displayCurrSegs(-1);
		strcpy (tmp, cmdLine.GetArgument("-o", 0).c_str());
		//sprintf(tmp,"%s/",tmp);
		if(resized)
			frame1.saveResizedRegions(tmp,cvGetSize(im).width,cvGetSize(im).height);
		else
			frame1.saveRegions(tmp);
		//release memory!
		frame1.deallocateMemForContours();
	}

	tic4=cvGetTickCount();
	printf("\n\nTotal time = %f\n",(tic4-tic1)/ticFrequency);
	if(!cmdLine.HasSwitch("-pb"))
		printf("\t edges detection = %f\n",(tic2-tic1)/ticFrequency);
	if(!cmdLine.HasSwitch("-pbOnly"))
		printf("\t segmentation = %f\n",(tic3-tic2)/ticFrequency);
  	return 0;
	
}
