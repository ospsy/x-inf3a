#include <cv.h>
#include <highgui.h>
#include <iostream.h>

#include "surf.h"
#include "surfCUDA.h"

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


// Acces au pixel x,y d'une image
inline uint getPixel(const IplImage* in, int x, int y)
{
	// Pixel en dehors de l'image
	if (x < 0 || x >= in->width || y < 0 || y >= in->height){
		std::cout << "En dehords des bords.\n" ;
		return 0 ;
	}
	return ((uint*)( (in->imageData) + (in->widthStep) * x)) [y] ;
}	

// Calcul de les derivees gaussiennes d'ordre 2
void calculateGaussianDerivative(const IplImage* imageIntegrale, IplImage** out, int octave, int intervals)
{
	// Calcul de la taille du filtre et des bordures
	int power = 1 ;
	for (int t=0 ; t<octave+1 ; t++)
	{
		power *= 2 ;
	}
	int borderSize = (3*(power*intervals + 1) + 1) /2 ;
	
	for (int inter=0 ; inter<intervals ; inter++)
	{
		// Calcul de la surface pour normalisation d'echelle
		int lobe = power*(inter+1) + 1 ;
		int area = (3*lobe) * (3*lobe) ;
		
		// Construction du filtre
		//int filtre[filterSize][filterSize] ;
		for (int y=borderSize ; y<(imageIntegrale->height)-borderSize ; y++)
			for (int x=borderSize ; x<(imageIntegrale->width)-borderSize ; x++)
			{
				IplImage* current = out[inter] ;
				
				// On calcule la reponse des differents filtres
				
				// Derivee selon x
				// Lobe de gauche
				int lobeGauche = 0, lobeCentre = 0, lobeDroit = 0 ;
				lobeGauche += getPixel(imageIntegrale, x-(lobe+1)/2, y + lobe-1) ;
				lobeGauche -= getPixel(imageIntegrale, x-(lobe+1)/2, y - lobe) ;
				lobeGauche += getPixel(imageIntegrale, x-(lobe+1)/2 - lobe, y + lobe) ;
				lobeGauche -= getPixel(imageIntegrale, x-(lobe+1)/2 - lobe, y + lobe-1) ;

				lobeCentre += getPixel(imageIntegrale, x-(lobe+1)/2, y - lobe) ;
				lobeCentre -= getPixel(imageIntegrale, x-(lobe+1)/2, y + lobe-1) ;
				lobeCentre += getPixel(imageIntegrale, x+(lobe-1)/2, y + lobe-1) ;
				lobeCentre -= getPixel(imageIntegrale, x+(lobe-1)/2, y - lobe) ;
				
				lobeDroit += getPixel(imageIntegrale, x+(lobe-1)/2, y - lobe) ;
				lobeDroit -= getPixel(imageIntegrale, x+(lobe-1)/2, y + lobe-1) ;
				lobeDroit += getPixel(imageIntegrale, x+(lobe-1)/2 + lobe, y + lobe-1) ;
				lobeDroit -= getPixel(imageIntegrale, x+(lobe-1)/2 + lobe, y - lobe) ;
				
				int dxx = lobeCentre - lobeDroit - lobeGauche ;
				
				// Derivee selon y
				int lobeHaut = 0, lobeBas = lobeCentre = 0 ;
				lobeHaut += getPixel(imageIntegrale, x-lobe, y - (3*lobe +1)/2) ;
				lobeHaut -= getPixel(imageIntegrale, x+lobe-1, y - (3*lobe +1)/2) ;
				lobeHaut += getPixel(imageIntegrale, x+lobe-1, y - (lobe +1)/2) ;
				lobeHaut -= getPixel(imageIntegrale, x-lobe, y - (lobe +1)/2) ;
				
				lobeCentre += getPixel(imageIntegrale, x-lobe, y - (lobe +1)/2) ;
				lobeCentre -= getPixel(imageIntegrale, x+lobe-1, y - (lobe +1)/2) ;
				lobeCentre += getPixel(imageIntegrale, x+lobe-1, y + (lobe -1)/2) ;
				lobeCentre += getPixel(imageIntegrale, x-lobe, y + (lobe -1)/2) ;
				
				lobeBas += getPixel(imageIntegrale, x-lobe, y + (lobe -1)/2) ;
				lobeBas -= getPixel(imageIntegrale, x+lobe-1, y + (lobe -1)/2) ;
				lobeBas += getPixel(imageIntegrale, x+lobe-1, y + (3*lobe -1)/2) ;
				lobeBas -= getPixel(imageIntegrale, x-lobe, y + (3*lobe -1)/2) ;
				
				int dyy = lobeCentre - lobeHaut - lobeBas ;
				
				// Derivee selon xy
				int lobe00=0, lobe01=0, lobe10=0, lobe11=0;
				
				lobe00 += getPixel(imageIntegrale, x-lobe-1, y-lobe -1) ;
				lobe00 -= getPixel(imageIntegrale, x-1, y-lobe -1) ;
				lobe00 += getPixel(imageIntegrale, x-1, y-1) ;
				lobe00 -= getPixel(imageIntegrale, x-lobe-1, y-1) ;
				
				lobe01 += getPixel(imageIntegrale, x, y-lobe-1) ;
				lobe01 -= getPixel(imageIntegrale, x, y-1) ;
				lobe01 += getPixel(imageIntegrale, x+lobe, y-1) ;
				lobe01 -= getPixel(imageIntegrale, x+lobe, y-lobe-1) ;
				
				lobe10 += getPixel(imageIntegrale, x-lobe-1, y) ;
				lobe10 -= getPixel(imageIntegrale, x-1, y) ;
				lobe10 += getPixel(imageIntegrale, x-1, y+lobe) ;
				lobe10 -= getPixel(imageIntegrale, x-lobe-1, y+lobe) ;

				lobe11 += getPixel(imageIntegrale, x, y) ;
				lobe11 -= getPixel(imageIntegrale, x, y+lobe) ;
				lobe11 += getPixel(imageIntegrale, x+lobe, y) ;
				lobe11 -= getPixel(imageIntegrale, x+lobe, y+lobe) ;
				
				int dxy = lobe00 + lobe11 - lobe10 - lobe01 ;
				
				((int*)(current->imageData + current->widthStep*x))[y] = (int)((dxx*dyy- (0.9*dxy)*(0.9*dxy))/area) ;
			}
	}
}

int main ( int argc, char **argv )
{
  cvNamedWindow( "My Window", 1 );
  cvNamedWindow( "My Window 2", 1 );
  IplImage *img = cvLoadImage("lena_600.jpg",CV_LOAD_IMAGE_GRAYSCALE);
  if(!img){
  	std::cout << "impossible de charger l'image, aborting..." << std::endl;
  	return 1;
  }
  IplImage *img2 = cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_32S,1);
  IplImage *img3 = cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_32S,1);
  cvShowImage( "My Window", img );
  //integralImage
  CUDAmakeIntegralImage(img,img2);
  makeIntegralImage(img,img3);
  cvShowImage( "My Window 2", img2 );
  //filtres gaussiens
  IplImage *imgs[6];
  for(int i=0;i<6;i++){
  	imgs[i]=cvCreateImage(cvSize(img->width,img->height),IPL_DEPTH_32S,1);
  }
  clock_t timer=clock();
  calculateGaussianDerivative(img3,imgs,0,6);
  std::cout << "calculateGaussianDerivative : " << 1000*(float)(clock()-timer)/(float)CLOCKS_PER_SEC <<"ms"<< std::endl;
  CUDAcalculateGaussianDerivative(img3,imgs,0,6);
  cvShowImage( "My Window 3", imgs[0] );
  //cvShowImage( "My Window 4", imgs[1] );
  //cvShowImage( "My Window 5", imgs[2] );
  for(int i=0;i<img->height;i++){
  	for(int j=0;j<img->width;j++){
  		i=j;
  		//std::cout << i << "," << j << " "<< ((int*)( imgs[0]->imageData + imgs[0]->widthStep * i)) [j] << std::endl;
    }
  }
  
  cvWaitKey();
  cvReleaseImage(&img);
  cvReleaseImage(&img2);
  std::cout << "plop2" << std::endl;
  return 0;
}
