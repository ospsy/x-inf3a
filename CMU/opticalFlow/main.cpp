

#include <stdio.h>
#include <ctype.h>
#include <fstream>
#include <iostream>
#include "cv.h"
#include "highgui.h"
#include "fhead.h"

#define WIN_SIZE 300

int main( int argc, char** argv )
{
    if (argc<1){
        printf("Need a folder\n");
        return 0;
    }
    const int tmpLength=300;
    char tmp[tmpLength];
    vector<CvPoint> gazePoints;
    vector<CvPoint2D32f> flows;
    sprintf(tmp,"%s/tmp.txt",argv[1]);
    
    ifstream ifs(tmp);
    if ( !ifs ) {     
        printf("No tmp.txt\n");
        exit(0);
    } else {    
        while( ifs.good() ) { 
            ifs.getline(tmp,tmpLength);
            if(!strcmp(tmp,"")) break;
            float x,y;
            sscanf(tmp,"%f %f",&x,&y);
            gazePoints.push_back(cvPoint((int)x,(int)y));
        }
        ifs.close();
    }
    
    cvNamedWindow("mainWin", CV_WINDOW_AUTOSIZE); 
    IplImage *im1, *im2, *DxVMap, *DyVMap, *eigImage, *tmpImage;
    sprintf(tmp,"%s/capture_img_out_%08i.jpg",argv[1],0);
    im1=cvLoadImage(tmp,CV_LOAD_IMAGE_GRAYSCALE);
    //DxVMap=cvCreateImage(cvSize(WIN_SIZE,WIN_SIZE), IPL_DEPTH_32F, 1);
    //DyVMap=cvCreateImage(cvSize(WIN_SIZE,WIN_SIZE), IPL_DEPTH_32F, 1);
    //DxVMap=cvCreateImage(cvSize((WIN_SIZE-17)/2,(WIN_SIZE-17)/2), IPL_DEPTH_32F, 1);
    //DyVMap=cvCreateImage(cvSize((WIN_SIZE-17)/2,(WIN_SIZE-17)/2), IPL_DEPTH_32F, 1);

    eigImage=cvCreateImage(cvSize(WIN_SIZE,WIN_SIZE), IPL_DEPTH_32F, 1);
    tmpImage=cvCreateImage(cvSize(WIN_SIZE,WIN_SIZE), IPL_DEPTH_32F, 1);
    for(int i=1;i<gazePoints.size();i++){
        int x=gazePoints[i].x , y=gazePoints[i].y;
        sprintf(tmp,"%s/capture_img_out_%08i.jpg",argv[1],i);
        im2=cvLoadImage(tmp,CV_LOAD_IMAGE_GRAYSCALE);
        if(!im1 || !im2){
            printf("Could not load image file: %s\n",tmp);
            exit(0);
        }
        CvRect rect=cvRect (max(min(x-WIN_SIZE/2,im1->width-WIN_SIZE),0), max(min(y-WIN_SIZE/2,im1->height-WIN_SIZE),0), WIN_SIZE, WIN_SIZE);
        cvSetImageROI(im1, rect);
        cvSetImageROI(im2, rect);
        
        //cvCalcOpticalFlowHS( im1, im2, 1, DxVMap, DyVMap, 0.002, cvTermCriteria(CV_TERMCRIT_ITER|CV_TERMCRIT_EPS,20,0.03));
        //cvCalcOpticalFlowLK( im1, im2, cvSize(11,11), DxVMap, DyVMap);
        //cvCalcOpticalFlowBM(im1, im2, cvSize(17,17), cvSize(2,2), cvSize(40,40), 0, DxVMap, DyVMap);
        //float dx=cvSum(DxVMap).val[0]/cvCountNonZero(DxVMap);
        //float dy=cvSum(DyVMap).val[0]/cvCountNonZero(DyVMap);
        
        int cornerCount;
        CvPoint2D32f *prevFeatures, *currFeatures;
        prevFeatures=(CvPoint2D32f*)malloc(1000*sizeof(CvPoint2D32f));
        cvGoodFeaturesToTrack(im1, eigImage, tmpImage, prevFeatures, &cornerCount, 0.01, 10);
        //printf("%i points found\n",cornerCount);
        currFeatures=(CvPoint2D32f*)malloc(cornerCount*sizeof(CvPoint2D32f));
        char* status=(char*)malloc(cornerCount*sizeof(char));
        float* track_error=(float*)malloc(cornerCount*sizeof(float));
        cvCalcOpticalFlowPyrLK(im1, im2, NULL, NULL, prevFeatures, currFeatures, cornerCount, cvSize(15,15), 3, status, track_error, cvTermCriteria( CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, .3 ), 0);
        cvResetImageROI(im1);
        float dx=0, dy=0;
        int n=0;
	for(int j=0;j<cornerCount;j++){
	    if(status[j]){
	        n++;
	        dx+=currFeatures[j].x-prevFeatures[j].x;
	        dy+=currFeatures[j].y-prevFeatures[j].y;
	    }
	}
	if(n!=0){
	    dx/=n;
	    dy/=n;
	}
	if(true){//show result at each frame
		IplImage *result=cvCreateImage(cvGetSize(im1),IPL_DEPTH_8U,3);
		cvConvertImage(im1,result);
		for(int j=0;j<cornerCount;j++){
		    if(status[j]){
			cvLine(result,cvPoint(x-WIN_SIZE/2+currFeatures[j].x,y-WIN_SIZE/2+currFeatures[j].y),cvPoint(x-WIN_SIZE/2+prevFeatures[j].x,y-WIN_SIZE/2+prevFeatures[j].y),CV_RGB(0,0,255));
		    }
		}
		cvLine(result,cvPoint(x,y),cvPoint(x+(int)dx,y+(int)dy),CV_RGB(255,0,0));
		cvShowImage("mainWin", result );
		cvWaitKey(0);
		cvReleaseImage(&result);
	}
        //printf("%i points matched \n",n);
        free(prevFeatures);
        free(currFeatures);
        free(status);
        free(track_error);
        
        //printf("%f %f\n",dx,dy);
        
        flows.push_back(cvPoint2D32f(dx,dy));

        
        cvReleaseImage(&im1);
        im1=im2;

        if(i%100==0)
            printf("%i images processed\n",i);        
    }

    sprintf(tmp,"%s/flows.txt",argv[1]);

    ofstream ofs(tmp, ios_base::out | ios_base::trunc);
    if ( !ofs ) {     
        printf("Unable to write\n");
        exit(0);
    } else {    
        for( int i=0;i<flows.size();i++ ) { 
            ofs << flows[i].x << " " << flows[i].y << endl;
        }
        ofs.close();
    }
    
    return 0;
}
