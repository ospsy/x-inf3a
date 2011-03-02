#include <cv.h>
#include <highgui.h>

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

void makeIntegralImage(const BwImage in, BwImage out){
	int tmp=0;
	for(int i=0;i<in.imgp->height;i++){
		tmp+=in[i][0];
		cvSet2D(out,i,0,cvScalar(tmp));
	}
	for(int i=1;i<in.imgp->height;i++){
		int tmp=0;
		for(int j=0;j<in.imgp->width;j++){
			tmp+=in[i][j];
			cvSet2D(out,i,j,tmp+out[i-1][j]));
		}
	}
}


int main ( int argc, char **argv )
{
	

  cvNamedWindow( "My Window", 1 );
  IplImage *img = cvCreateImage( cvSize( 640, 480 ), IPL_DEPTH_8U, 1 );
  CvFont font;
  double hScale = 1.0;
  double vScale = 1.0;
  int lineWidth = 1;
  cvInitFont( &font, CV_FONT_HERSHEY_SIMPLEX | CV_FONT_ITALIC,
              hScale, vScale, 0, lineWidth );
  cvPutText( img, "Hello World!", cvPoint( 200, 400 ), &font,
             cvScalar( 255, 255, 0 ) );
  cvShowImage( "My Window", img );
  cvWaitKey();
  return 0;
}
