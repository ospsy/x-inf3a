#include <iostream>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <dirent.h>
#include <errno.h>
#include <time.h>
#include <cv.h>
#include <cxcore.h>
#include <highgui.h>
#include <list>

// For File read
#include "fhead.h"

using namespace cv;

int nbOfFilesIn(const char *dir, int& count)
{
    DIR *dp;
    struct dirent *dirp;
    int tmp=0;
    if((dp  = opendir(dir)) == NULL) {
        cout << "Error(" << errno << ") opening " << dir << endl;
        return errno;
    }

    while ((dirp = readdir(dp)) != NULL) {
        tmp++;
    }
    closedir(dp);
    count=tmp;
    return 0;
}

int main(int argc, char **argv) {
	if(argc<2){
		cout << "Need a folder name to process..." << endl;
	}

		char Path_name[256];
		char Param_file_name[256];
		char Img_name[256];
		char Ext_name[10];
		char save_fname_in[256];
		char save_fname_out[256];
		char save_fname_opt[256];

		strcpy(Path_name,argv[1]);
		sprintf(Param_file_name,"%s/param_cap_mem.txt",Path_name);

		//*Read  Parameter file *//
		setfile setfile;
		string  str1,str2,str3;

		if ( setfile.open(Param_file_name) == 0 ){
			fprintf(stderr,"Error while trying to open param file.\n");
			exit(1);
		}
		int fp_i= setfile.find_header("### Capture_params ###");
		if(fp_i==0){
			printf("No File \n");
			return 1;
		}

		cout << "##  Read parameters  ";
		setfile.find_header("# Image_name");
		str1 = setfile.get_line();
		strcpy(Img_name, str1.c_str());

		setfile.find_header("# Extension");
		str3 = setfile.get_line();
		strcpy(Ext_name, str3.c_str());

		// setfile.find_header("# Eye Image size");
		// int imx = setfile.get_int();
		// int imy = setfile.get_int();

		// setfile.find_header("# Out Image size");
		// int imx_o = setfile.get_int();
		// int imy_o = setfile.get_int();

		setfile.find_header("# Rectification param");
		double fx = setfile.get_double();
		double fy = setfile.get_double();
		double r = setfile.get_double();
		double s = setfile.get_double();
		double v0 = setfile.get_double();
		double u0 = setfile.get_double();

		setfile.find_header("# Eye camera size");
		int WX_eye   = setfile.get_int();
		int WY_eye	 = setfile.get_int();
		int TYPE_eye = setfile.get_int();
		int imx      = setfile.get_int();
		int imy      = setfile.get_int();

		setfile.find_header("# Out camera size");
		int WX_out = setfile.get_int();
		int WY_out = setfile.get_int();
		int TYPE_out = setfile.get_int();
		int imx_o = setfile.get_int();
		int imy_o = setfile.get_int();

		setfile.find_header("# Wait Time");
		int wait_time = setfile.get_int();

		setfile.find_header("# Camera Switch");
		int SWITCH_cam = setfile.get_int();

		setfile.find_header("#Capture Mode");
		int Mode_cam0 = setfile.get_int();
		int Mode_cam1 = setfile.get_int();
		int Mode_cam2 = setfile.get_int();

		setfile.find_header("# Option");
		int Option_cam = setfile.get_int();

		setfile.find_header("# Option camera size");
		int WX_opt = setfile.get_int();
		int WY_opt = setfile.get_int();
		int TYPE_opt = setfile.get_int();
		int imx_opt = setfile.get_int();
		int imy_opt = setfile.get_int();

		setfile.find_header("# Audio record");
		int Flg_audio = setfile.get_int();
        
		cout << "	... Finish !" << endl;

		int count;
		int status = nbOfFilesIn(argv[1],count);
		if(status){
			cout << "Exiting..." << endl;
			return 1;
		}
		count=count/2-1;
		cout << count << " frames"<<endl;

		double ticFrequency,processTime;
		int64    startTic;        // Start
		int64    stopTic;         // Finish
		int64    triggerTic;
		ticFrequency = cvGetTickFrequency();

		IplImage *img_in;//= cvCreateImage(cvSize(WX_eye,WY_eye),8,3);        //For Eye image
		IplImage *img_out;//= cvCreateImage(cvSize(WX_out,WY_out),8,3);        //For Outsize image
		IplImage *img_opt;//= cvCreateImage(cvSize(WX_opt,WY_opt),8,3);


		//Eye image
		IplImage *img_in_orig = cvCreateImage(cvSize(imx,imy),8,3);			// Resize & Flip
		//IplImage *img_in_rect = cvCreateImage(cvSize(imx,imy),8,3);			// Rectification

		//Outside image
		IplImage *img_out_orig = cvCreateImage(cvSize((int)(imx_o),(int)(imy_o)),8,3);
		//IplImage *img_out_rect = cvCreateImage(cvSize((int)(imx_o),(int)(imy_o)),8,3);

		//Option
		IplImage *img_opt_orig = cvCreateImage(cvSize((int)(imx_opt),(int)(imy_opt)),8,3);
		//IplImage *img_opt_rect = cvCreateImage(cvSize((int)(imx_opt),(int)(imy_opt)),8,3);

		cout << "Start post-processing" << endl;
		// Correct images (resize and flip)
		startTic=cvGetTickCount();
		for(int i = 0;i<count;i++){
			sprintf(save_fname_in,"%s/%s_%08d.%s",Path_name,Img_name,i,Ext_name);
			sprintf(save_fname_out,"%s/%s_out_%08d.%s",Path_name,Img_name,i,Ext_name);
			sprintf(save_fname_opt,"%s/%s_opt_%08d.%s",Path_name,Img_name,i,Ext_name);

			//Eye cam
			img_in=cvLoadImage(save_fname_in);
			if (img_in!=NULL){
				cvResize(img_in,img_in_orig,CV_INTER_LINEAR);// Resize
				if (Mode_cam0 !=2)
					cvFlip(img_in_orig,img_in_orig,Mode_cam0);// Flip
			} else{
				fprintf(stderr,"Unable to open %s, replacing by previous frame\n",save_fname_in);
				sprintf(save_fname_in,"%s/%s_%08d.%s",Path_name,Img_name,i-1,Ext_name);
				img_in=cvLoadImage(save_fname_in);
			}
				
			cvSaveImage(save_fname_in,img_in_orig);
			cvReleaseImage(&img_in);

			//Out cam
			img_out=cvLoadImage(save_fname_out);
			cvResize(img_out,img_out_orig,CV_INTER_LINEAR);			// Resize
			if (Mode_cam1 !=2)
				cvFlip(img_out_orig,img_out_orig,Mode_cam1);
			cvSaveImage(save_fname_out,img_out_orig);
			cvReleaseImage(&img_out);

			if(Option_cam == 1){
				img_opt=cvLoadImage(save_fname_opt);
				cvResize(img_opt,img_opt_orig,CV_INTER_LINEAR);			// Resize
				if (Mode_cam2 !=2)
					cvFlip(img_opt_orig,img_opt_orig,Mode_cam2);
				cvSaveImage(save_fname_opt,img_opt_orig);
				cvReleaseImage(&img_opt);
			}


			if((i+1)%10==0){
				stopTic=cvGetTickCount();
				processTime=(stopTic-startTic)/ticFrequency;
				processTime/=1000000;
				cout << i+1 << " frames done, "<<100*(i+1)/(double)count<<"%, "<< (count-i)*processTime/i << "s to go..." << endl;
			}
		}

		cout << "done..." << endl;

		cvReleaseImage(&img_in_orig);
        //cvReleaseImage(&img_in_rect);

        cvReleaseImage(&img_out_orig);
		//cvReleaseImage(&img_out_rect);

		cvReleaseImage(&img_opt_orig);
		//cvReleaseImage(&img_opt_rect);
        return 0;
}

