#include <cv.h>
#include <highgui.h>
#include <iostream.h>

template<class T> class Image
{
  public:
  IplImage* imgp;
  Image(IplImage* img=0) {imgp=img;}
  ~Image(){imgp=0;}
  void operator=(IplImage* img) {imgp=img;}
  inline T* operator[](const int rowIndx) const {
    return ((T *)(imgp->imageData + rowIndx*imgp->widthStep));}
};

typedef Image<unsigned char>  BwImage;

void makeIntegralImage(const IplImage* in, IplImage* out){
	if(in->depth!=IPL_DEPTH_8U || out->depth!=IPL_DEPTH_32S){
		std::cout << "Mauvais type d'images dans makeIntegralImage" << std::endl;
		exit(EXIT_FAILURE);
	}
	unsigned int tmp=0;
	for(int i=0;i<in->height;i++){
		tmp+=((uchar*)(in->imageData + in->widthStep*i))[0];
		((uint*)(out->imageData + out->widthStep*i))[0]=tmp;
	}
	for(int i=1;i<in->height;i++){
		unsigned int tmp=0;
		for(int j=0;j<in->width;j++){
			tmp+=((uchar*)(in->imageData + in->widthStep*i))[j];
			((uint*)(out->imageData + out->widthStep*i))[j]=tmp+((uint*)(out->imageData + out->widthStep*(i-1)))[j];
		}
	}
}


int main ( int argc, char **argv )
{
	

  cvNamedWindow( "My Window", 1 );
  cvNamedWindow( "My Window 2", 1 );
  IplImage *img = cvLoadImage("lena.jpg",CV_LOAD_IMAGE_GRAYSCALE);
  IplImage *img2 = cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_32S,1);
  cvShowImage( "My Window", img );
  makeIntegralImage(img,img2);
  cvShowImage( "My Window 2", img2 );
  cvWaitKey();
  cvReleaseImage(&img);
  cvReleaseImage(&img2);
  return 0;
}
