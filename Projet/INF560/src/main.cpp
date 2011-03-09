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


// Acces au pixel x,y d'une image
uint getPixel(const IplImage* in, int x, int y)
{
	// Pixel en dehors de l'image
	if (x < 0 || x >= in->width || y < 0 || y >= in->height)
		std::cout << "En dehords des bords.\n" ;
		return 0 ;
	
	return (((uint*)(out->imageData + out->widthStep*x))[y])* ;
}	

// Calcul de les derivees gaussiennes d'ordre 2
enum type_derivative
{
	GAUSSIAN_DERIVATIVE_X = 0 ;
	GAUSSIAN_DERIVATIVE_Y = 1 ;
	GAUSSIAN_DERIVATIVE_XY = 2 ;
};
void calculateGaussianDerivative(const IplImage* imageIntegrale, IplImage** out, uint octave, uint intervals)
{
	// Calcul de la taille du filtre et des bordures
	int power = 1 ;
	for (int i=0 ; i<octave+1 ; i++)
	{
		power *= 2 ;
	}
	int borderSize = (3*(power*intervals + 1) + 1) /2 ;
	
	for (int inter=0 ; inter<intervals ; i++)
		// Calcul de la surface pour normalisation d'echelle
		int lobe = power*(inter+1) + 1 ;
		int area = (3*lobe) * (3*lobe) ;
		
		// Construction du filtre
		//int filtre[filterSize][filterSize] ;
		for (int y=borderSize ; y<(in->height)-borderSize ; y++)
			for (int x=borderSize ; x<(in->width-borderSize ; x++)
			{
				IplImage* current = *(out + inter) ;
				
				// On calcule la reponse des differents filtres
				
				// Derivee selon x
				// Lobe de gauche
				int lobeGauche = 0 ;
				lobeGauche += getPixel(imageIntegrale, x-(lobe+1)/2, y) ;
				
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
