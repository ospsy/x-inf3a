#include <cv.h>
#include <highgui.h>
#include <fstream>
#include <iostream>

bool is_readable( const char* fileName ) 
{ 
    std::ifstream file( fileName ); 
    return !file.fail(); 
} 

void writeOpticalFlow(const char* fileName, const IplImage* velx, const IplImage* vely){
	std::ofstream file( fileName );
	if(!file){
		std::cout << "Unable to open file"<<std::endl;
		return;
	}
	float TAG_FLOAT = 202021.25;
	file.write((const char*)&TAG_FLOAT,sizeof(float));
	int height = velx->height;
	int width = velx->width;
	file.write((const char*)&width,sizeof(int));
	file.write((const char*)&height,sizeof(int));
	for (int y = 0; y < height; y++){
    	for(int x= 0; x < width ; x++){
      		file.write( (const char*)((float*)((velx)->imageData + y*((velx)->widthStep)) +x),sizeof(float));
		file.write( (const char*)((float*)((vely)->imageData + y*((vely)->widthStep)) +x),sizeof(float));
		}
	}
	file.close();
}

int main(int argc, char* argv[]){
	if (argc != 2){
		fprintf(stderr, "usage: %s <image>",argv[0]);
		exit(1);
	}

	int i=0;
	IplImage *img1,*img2,*velx,*vely;
	char imageName[40], fileName[40];
	sprintf(imageName,"%s%08d.ppm",argv[1],++i);
	img1=cvLoadImage(imageName,CV_LOAD_IMAGE_GRAYSCALE);
	sprintf(imageName,"%s%08d.ppm",argv[1],++i);
	img2=cvLoadImage(imageName,CV_LOAD_IMAGE_GRAYSCALE);
	velx=cvCreateImage(cvGetSize(img1),IPL_DEPTH_32F,1);
	vely=cvCreateImage(cvGetSize(img1),IPL_DEPTH_32F,1);
	cvNamedWindow("test",CV_WINDOW_AUTOSIZE);
	while(true){
		std::cout << "Processing " << imageName << std::endl;
		//LK
		//cvCalcOpticalFlowLK( img1, img2, cvSize(8,8), velx, vely );
		//HS
		CvTermCriteria criteria = cvTermCriteria( CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, .3 );
		cvCalcOpticalFlowHS( img1, img2, -1, velx, vely, 1.8, criteria );
		sprintf(fileName,"%s%08d.flo",argv[1],i);
		writeOpticalFlow(fileName,velx,vely);
		cvShowImage("test",velx);
		cvWaitKey(-1);
		break;
		sprintf(imageName,"%s%08d.ppm",argv[1],++i);
		if(is_readable(imageName)){
			cvReleaseImage(&img1);
			img1=img2;
			img2=cvLoadImage(imageName,CV_LOAD_IMAGE_GRAYSCALE);
		}else{
			std::cout << "No more image to process..." << std::endl;
			break;
		}
	}
	cvReleaseImage(&img1);
	cvReleaseImage(&img2);

  	return 0;
}
