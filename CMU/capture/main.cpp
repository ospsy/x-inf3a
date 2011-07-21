#include <iostream>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>
#include <time.h>
#include <cv.h>
#include <cxcore.h>
#include <highgui.h>
#include <list>
#include <linux/videodev2.h>
#include "v4l2uvc.h"

#ifdef WITH_SOUND_SUPPORT
	#include <AL/al.h>
	#include <AL/alc.h>
	#include <AL/alut.h>
#endif


// For File read
#include "fhead.h"

// For memory leak check
/*#define _CRTDBG_MAP_ALLOC
#include <crtdbg.h>
#define new ::new(_NORMAL_BLOCK,__FILE__,__LINE__)
*/

// For OpenCV windows version
#ifdef WIN32
	#ifdef _DEBUG
		#pragma comment( lib, "cv210d.lib" )
		#pragma comment( lib, "cxcore210d.lib" )
		#pragma comment( lib, "highgui210d.lib" )
		//#pragma comment( lib, "levmar_d.lib" )
		#pragma comment( lib, "levmar_mt_d.lib" )
	#else
		#pragma comment( lib, "cv210.lib" )
		#pragma comment( lib, "cxcore210.lib" )
		#pragma comment( lib, "highgui210.lib" )
		//#pragma comment( lib, "levmar.lib" )
		#pragma comment( lib, "levmar_mt.lib" )
	#endif
	#pragma comment(lib, "OpenAL32.lib")
	#pragma comment(lib, "alut.lib")
#endif


#ifdef WITH_SOUND_SUPORT
typedef struct
{
	char			szRIFF[4];
	long			lRIFFSize;
	char			szWave[4];
	char			szFmt[4];
	long			lFmtSize;
	WAVEFORMATEX	wfex;
	char			szData[4];
	long			lDataSize;
} WAVEHEADER;
#endif




// For Capture lib windows version
//#include "ewclib.h"     //cv.h����ŃC���N���[�h����
#define FPS 30


// // For Image info
// #define WX_eye 640
// #define WY_eye 480
// #define WX_out 1280
// #define WY_out 720


#define PI 3.14159265358979323846
#define RATIO 1

// For Audio
// const int SRATE = 44100;
// const int SSIZE = 1024;
#define	OUTPUT_WAVE_FILE		"Capture.wav"
#define BUFFERSIZE				4410

// ALbyte buffer[22050];//buffer[100];//
// ALint sample;

using namespace cv;

int writeVideoData(const char *fileName, struct vdIn *vd){
	FILE* file = fopen (fileName, "wb");
	if (file != NULL) {
		fwrite (vd->tmpbuffer, vd->buf.bytesused, 1,file);
		fclose (file);
		vd->getPict = 0;
		return 0;
	}else{
		cerr << "Unable to open : "<<fileName << endl;
		return -1;
	}
}

int main(int argc, char **argv)
{	

        const char *winEye="Camera_eye";
        const char *winOut="Camera_out";
	const char *winOpt="Camera_opt";

	const char *ParamFileName="param_cap_mem.txt";
	char Path_name[256];
	char Img_name[256];
	char Ext_name[10];
	char save_fname_in[256];
	char save_fname_out[256];
	char save_fname_opt[256];

	int USE_CAM = 1;


	//*Read  Parameter file *//
	setfile setfile;
	string  str1,str2,str3;

	if ( setfile.open(ParamFileName) == 0 ){
		fprintf(stderr,"Error.\n");
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

	setfile.find_header("# Path");
	str2 = setfile.get_line();
	strcpy(Path_name, str2.c_str());

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


	double ticFrequency,processTime;
	int64    startTic;        // Start
	int64    stopTic;         // Finish
	int64    triggerTic;
	ticFrequency = cvGetTickFrequency();

	// Audio Setting
	#ifdef WITH_SOUND_SUPPORT
	FILE	*pFile;
	ALint	iSamplesAvailable;
	alutInit (NULL, NULL);
	const ALCchar	*szDefaultCaptureDevice;
	ALchar			Buffer[BUFFERSIZE];
	WAVEHEADER		sWaveHeader;
	ALint			iDataSize = 0;
	ALint			iSize;
	ALCdevice *device;
	const ALchar *pDeviceList;
	#endif

	// Get cameras number
	int n_camera=0;
	char videoFile[80];
	{
		while(true){
			sprintf(videoFile, "/dev/video%i",n_camera);
			FILE *f=fopen(videoFile,"rb");
			if(f!=NULL){
				n_camera++;
				fclose(f);
			}else{
				break;
			}
		}
	}
	printf("# Get camera info \n  %d cameras are detected. \n",n_camera);

	int key=cvWaitKey(1000);

	//int eye_cam,out_cam,opt_cam;
	int eye_cam_num=1;
	int out_cam_num=2;
	int opt_cam_num=4;

	// Camera Initialization
	if ( TYPE_eye == 1 && TYPE_out !=1 ){
		eye_cam_num = n_camera - 1; 
		out_cam_num = 0;
		opt_cam_num = n_camera - 2;
	}
	if ( TYPE_eye == 1 && TYPE_out ==1 ){
		eye_cam_num = n_camera - 2; 
		out_cam_num = n_camera - 1;
		opt_cam_num = 0;
	}
	if ( TYPE_eye != 1 && TYPE_out ==1 ){
		eye_cam_num = 0; 
		out_cam_num = n_camera - 1;
		opt_cam_num = n_camera - 2;
	}


	if( SWITCH_cam)
	{
		int tmp_num = out_cam_num;
		out_cam_num = eye_cam_num;
		eye_cam_num = tmp_num;

	}

	if(argc==3){
		sscanf (argv[1],"%d",&eye_cam_num);
		sscanf (argv[2],"%d",&out_cam_num);
	}

	struct vdIn *videoEye, *videoOut, *videoOpt;
	videoEye = (struct vdIn *) malloc (sizeof (struct vdIn));
	videoOut = (struct vdIn *) malloc (sizeof (struct vdIn));
	videoOpt = (struct vdIn *) malloc (sizeof (struct vdIn));

	sprintf(videoFile, "/dev/video%i",eye_cam_num);
	if (init_videoIn(videoEye, videoFile, 320, 240, V4L2_PIX_FMT_MJPEG, 1) < 0){
		cerr << "VideoEye : " << videoFile << " initialisation problem" << endl;
		return 1;
	}
	sprintf(videoFile, "/dev/video%i",out_cam_num);
	if (init_videoIn(videoOut, videoFile, 320, 240, V4L2_PIX_FMT_MJPEG, 1) < 0){
		cerr << "VideoOut : " << videoFile << " initialisation problem" << endl;
		return 1;
	}

	CvCapture *capture_opt;
	if (Option_cam == 1){
		sprintf(videoFile, "/dev/video%i",opt_cam_num);
		if (init_videoIn(videoOpt, videoFile, 320, 240, V4L2_PIX_FMT_MJPEG, 1) < 0){
			cerr << "VideoOpt initialisation problem" << endl;
			return 1;
		}
		if(Flg_audio){
			#ifndef WITH_SOUND_SUPPORT
				cout << "No sound support in that build..." << endl;
			#else
				// Hello world or read file
				ALuint buffer_, source_;
				alGenBuffers( 1, &buffer_ );
				alGenSources( 1, &source_ );
				//buffer = alutCreateBufferHelloWorld ();
				buffer_ = alutCreateBufferFromFile( "sa.wav" ); 
				alSourcei( source_, AL_BUFFER, buffer_ );
				alSourcePlay( source_ );
				alutSleep (1);
				//return(1);

			alGetError();
			if (alGetError() != AL_NO_ERROR) {
				return 0;
			}
			pDeviceList = alcGetString(NULL, ALC_CAPTURE_DEVICE_SPECIFIER);

			device = alcCaptureOpenDevice(NULL,  22050, AL_FORMAT_MONO16, BUFFERSIZE);

			if (device)
			{
				cout << "# Sound recorde " << endl;
				
				pFile = fopen(OUTPUT_WAVE_FILE, "wb");
				if (pFile == NULL)
					cout << "  Error: Open File: ""Capture.wav""" << endl;

				cout << "  Open File: ""Capture.wav""" << endl;

				// Prepare a WAVE file header for the captured data
				sprintf(sWaveHeader.szRIFF, "RIFF");
				sWaveHeader.lRIFFSize = 0;
				sprintf(sWaveHeader.szWave, "WAVE");
				sprintf(sWaveHeader.szFmt, "fmt ");
				sWaveHeader.lFmtSize = sizeof(WAVEFORMATEX);		
				sWaveHeader.wfex.nChannels = 1;
				sWaveHeader.wfex.wBitsPerSample = 16;
				sWaveHeader.wfex.wFormatTag = WAVE_FORMAT_PCM;
				sWaveHeader.wfex.nSamplesPerSec = 22050;
				sWaveHeader.wfex.nBlockAlign = sWaveHeader.wfex.nChannels * sWaveHeader.wfex.wBitsPerSample / 8;
				sWaveHeader.wfex.nAvgBytesPerSec = sWaveHeader.wfex.nSamplesPerSec * sWaveHeader.wfex.nBlockAlign;
				sWaveHeader.wfex.cbSize = 0;
				sprintf(sWaveHeader.szData, "data");
				sWaveHeader.lDataSize = 0;

				fwrite(&sWaveHeader, sizeof(WAVEHEADER), 1, pFile);
			}
			#endif
		}
	}

	cvNamedWindow(winEye, CV_WINDOW_AUTOSIZE);
	cvNamedWindow(winOut, CV_WINDOW_AUTOSIZE);

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

	cout << "Try to create capture directory : "<< Path_name << endl;
	int status = mkdir(Path_name, S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
	if(!status){
		cout << "Succeed" << endl;
	}else{
		cout << "Could not create directory : " << Path_name << endl;
		if (errno==EEXIST){
			cout << "Already exist" << endl;
		}
		return 1;
	}
	{
		char tmp[256];
		sprintf(tmp,"%s/%s",Path_name,ParamFileName);
		ifstream f1(ParamFileName, fstream::binary);
		ofstream f2(tmp, fstream::trunc|fstream::binary);
		f2 << f1.rdbuf();
	}


	printf(" *** Preparation Time *** \n");
	printf(" Press ESC to save mode  \n\n");
	int count = 0;
	int err=0;
	startTic=cvGetTickCount();
	while(1){
		
		int key=cvWaitKey(wait_time);
		cout << "key : "<< key << endl;
    		if (key==27) break;
		if (key==115 || key==83){
			struct vdIn *tmp;
			tmp=videoEye;
			videoEye=videoOut;
			videoOut=tmp;
		}

		if (Option_cam == 1){
			if (uvcGrab (videoOpt) < 0){
				cerr << "Grabbing impossible" << endl;
				err=1;
				break;
			}
		}
		if (uvcGrab (videoEye) < 0 || uvcGrab (videoOut) < 0) {
			cerr << "Grabbing impossible" << endl;
			err=1;
			break;
		}

		writeVideoData("tmp_eye.jpg",videoEye);
		writeVideoData("tmp_out.jpg",videoOut);
		if(Option_cam)
			writeVideoData("tmp_out.jpg",videoOpt);
		//cvWaitKey(50);

		//Eye cam
		img_in=cvLoadImage("tmp_eye.jpg");
		cvResize(img_in,img_in_orig,CV_INTER_LINEAR);			// Resize
		if(Mode_cam0 !=2)
			cvFlip(img_in_orig,img_in_orig,Mode_cam0);		// Flip

		//Out cam
		img_out=cvLoadImage("tmp_out.jpg");
		cvResize(img_out,img_out_orig,CV_INTER_LINEAR);			// Resize
		if (Mode_cam1 !=2)
			cvFlip(img_out_orig,img_out_orig,Mode_cam1);		// Flip

		if (Option_cam == 1){
			//Opt cam
			img_opt=cvLoadImage("tmp_opt.jpg");
			cvResize(img_opt,img_opt_orig,CV_INTER_LINEAR);		// Resize
			cvFlip(img_opt_orig,img_opt_orig,Mode_cam2);		// Flip
		}

		cvReleaseImage(&img_in);
		cvReleaseImage(&img_out);

		cvShowImage(winEye,img_in_orig );
		cvShowImage(winOut,img_out_orig );

		if (Option_cam == 1){
			//cvCopy(img_opt_orig,img_opt_rect[0]);
			cvShowImage(winOpt,img_opt_orig);
#ifdef WITH_SOUND_SUPPORT
			if (Flg_audio == 1)
				alcCaptureStart(device);
#endif
		}

		count++;
		if(count%10==0){
			stopTic=cvGetTickCount();
			processTime=(stopTic-startTic)/ticFrequency;
			processTime/=1000000;
			cout << "fps : " << 10/processTime << endl;
			startTic=cvGetTickCount();
		}

	}


	if(err){
		close_v4l2(videoEye);
		close_v4l2(videoOut);
		if(Option_cam)
			close_v4l2(videoOpt);
		free(videoEye);
		free(videoOut);
		free(videoOpt);
		return err;
	}


        //Main loop
	count = 0;
	startTic = cvGetTickCount();

        double pre_t0 = 0;
	double pre_t1 = 0;
	

	printf("\n Start capture ...");
	while(1){

		 int key=cvWaitKey(wait_time);
		 if (key==27) break;           //Termination for ESC key

		 //* Capture Image *//

		//int count_sync = 0;
		triggerTic = cvGetTickCount();
		if (Option_cam == 1){
			if (uvcGrab (videoOpt) < 0){
				cerr << "Grabbing impossible" << endl;
				err=1;
				break;
			}
		}
		if (uvcGrab (videoEye) < 0 || uvcGrab (videoOut) < 0) {
			cerr << "Grabbing impossible" << endl;
			err=1;
			break;
		}

		sprintf(save_fname_in,"%s/%s_%08d.%s",Path_name,Img_name,count,Ext_name);
		sprintf(save_fname_out,"%s/%s_out_%08d.%s",Path_name,Img_name,count,Ext_name);
		writeVideoData(save_fname_in,videoEye);
		writeVideoData(save_fname_out,videoOut);
		if (Option_cam == 1){
			sprintf(save_fname_opt,"%s/%s_opt_%08d.%s",Path_name,Img_name,count,Ext_name);
			writeVideoData(save_fname_out,videoOpt);
	#ifdef WITH_SOUND_SUPPORT
			if (Flg_audio){
				alcGetIntegerv(device, ALC_CAPTURE_SAMPLES, (ALCsizei)sizeof(ALint), &iSamplesAvailable);

				if (iSamplesAvailable > (BUFFERSIZE / sWaveHeader.wfex.nBlockAlign))
				{
					alcCaptureSamples(device, Buffer, BUFFERSIZE / sWaveHeader.wfex.nBlockAlign);


					fwrite(Buffer, BUFFERSIZE, 1, pFile);	// Write the audio data to a file
					iDataSize += BUFFERSIZE;				// Record total amount of data recorded
				}
			}
	#endif
		}

	
	//* Emmbed Time Stamp *//	
		//TODO
		/*char h_t = (char)(t0/3600);
	    char m_t = (char)((t0 - (double)h_t*3600)/60);
		char s_t = (char)((t0 - (double)h_t*3600 - (double)(m_t)*60));
		char ms0_t = (char)(double(t0 - int(t0))*1000/127);
		char ms1_t = (char)(double(t0 - int(t0))*1000 - (double)ms0_t*127);

		img_in_rect[count]->imageData[0] = cvRound ( s_t );
		img_in_rect[count]->imageData[1] = cvRound ( m_t );
		img_in_rect[count]->imageData[2] = cvRound ( h_t );
		img_in_rect[count]->imageData[3] = 0;
		img_in_rect[count]->imageData[4] = cvRound ( ms1_t );
		img_in_rect[count]->imageData[5] = cvRound ( ms0_t );*/

		count++;
		printf("count: %d \n",count);

	}
	stopTic = cvGetTickCount();

	processTime = (stopTic-startTic)/ticFrequency;
	printf("\n\n  Save %.3f[sec]FrameRate is [ %.3f ]fps \n\n",processTime/1000000,(double)count*1000000/(double)processTime);

#ifdef WITH_SOUND_SUPPORT
	if (Option_cam == 1 && Flg_audio == 1){
		alcCaptureStop(device);
		// Check if any Samples haven't been consumed yet
		alcGetIntegerv(device, ALC_CAPTURE_SAMPLES, 1, &iSamplesAvailable);
		while (iSamplesAvailable)
		{
			if (iSamplesAvailable > (BUFFERSIZE / sWaveHeader.wfex.nBlockAlign))
			{
				alcCaptureSamples(device, Buffer, BUFFERSIZE / sWaveHeader.wfex.nBlockAlign);
				fwrite(Buffer, BUFFERSIZE, 1, pFile);
				iSamplesAvailable -= (BUFFERSIZE / sWaveHeader.wfex.nBlockAlign);
				iDataSize += BUFFERSIZE;
			}
			else
			{
				alcCaptureSamples(device, Buffer, iSamplesAvailable);
				fwrite(Buffer, iSamplesAvailable * sWaveHeader.wfex.nBlockAlign, 1, pFile);
				iDataSize += iSamplesAvailable * sWaveHeader.wfex.nBlockAlign;
				iSamplesAvailable = 0;
			}
		}

		// Fill in Size information in Wave Header
		fseek(pFile, 4, SEEK_SET);
		iSize = iDataSize + sizeof(WAVEHEADER) - 8;
		fwrite(&iSize, 4, 1, pFile);
		fseek(pFile, 42, SEEK_SET);
		fwrite(&iDataSize, 4, 1, pFile);
		fclose(pFile);

		alcCaptureCloseDevice(device);
	}
#endif
	cvDestroyWindow(winEye);
	cvDestroyWindow(winOut);


	cvReleaseImage(&img_in_orig);

	cvReleaseImage(&img_out_orig);

	cvReleaseImage(&img_opt_orig);

	close_v4l2(videoEye);
	close_v4l2(videoOut);
	if(Option_cam)
		close_v4l2(videoOpt);
	free(videoEye);
	free(videoOut);
	free(videoOpt);

	char input[80];
	while(true){
		cout << "Name of folder?"<<endl;
		cin >> input;
		if(strcmp(input,"")){
			cout << "\tRenaming data folder to " << input << endl;
			status=rename(Path_name,input);
			if(status){
				cout << "\tCan't use that name, maybe another folder with that name already exist?" << endl;
			}
			else
				break;
		}
	}	
	
	
        return 0;
}


