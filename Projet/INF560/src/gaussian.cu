#include "surfCUDA.h"
#include <cutil.h>
#include <stdlib.h>
#include <iostream>

#include <ctime>

const int blocksize = 10;

__global__ void CUDAcalculateGaussianDerivative2(uint* integral, uint** out, int width, int height, int pitch ,int intervals, int power, int borderSize) {
  int x = blockIdx.x * blockDim.x + threadIdx.x; 
  int y = blockIdx.y * blockDim.y + threadIdx.y;
  if ( x < width-borderSize && y < height-borderSize && x >= borderSize && y >= borderSize ){
  	for(int inter=0;inter<intervals;inter++){
  	int lobe = power*(inter+1) + 1 ;
	int area = (3*lobe) * (3*lobe) ;
  	// Derivee selon x
				int lobeGauche = 0, lobeCentre = 0, lobeDroit = 0 ;
				lobeGauche += unsignedGetPixel(integral,pitch, x-(lobe+1)/2, y + lobe-1) ;
				lobeGauche -= unsignedGetPixel(integral,pitch, x-(lobe+1)/2, y - lobe) ;
				lobeGauche += unsignedGetPixel(integral,pitch, x-(lobe+1)/2 - lobe, y - lobe) ;
				lobeGauche -= unsignedGetPixel(integral,pitch, x-(lobe+1)/2 - lobe, y + lobe-1) ;

				lobeCentre += unsignedGetPixel(integral,pitch, x-(lobe+1)/2, y - lobe) ;
				lobeCentre -= unsignedGetPixel(integral,pitch, x-(lobe+1)/2, y + lobe-1) ;
				lobeCentre += unsignedGetPixel(integral,pitch, x+(lobe-1)/2, y + lobe-1) ;
				lobeCentre -= unsignedGetPixel(integral,pitch, x+(lobe-1)/2, y - lobe) ;
				
				lobeDroit += unsignedGetPixel(integral,pitch, x+(lobe-1)/2, y - lobe) ;
				lobeDroit -= unsignedGetPixel(integral,pitch, x+(lobe-1)/2, y + lobe-1) ;
				lobeDroit += unsignedGetPixel(integral,pitch, x+(lobe-1)/2 + lobe, y + lobe-1) ;
				lobeDroit -= unsignedGetPixel(integral,pitch, x+(lobe-1)/2 + lobe, y - lobe) ;
				
				int dxx = lobeCentre - lobeDroit - lobeGauche ;
				
				// Derivee selon y
				int lobeHaut = 0, lobeBas = lobeCentre = 0 ;
				lobeHaut += unsignedGetPixel(integral,pitch, x-lobe, y - (3*lobe +1)/2) ;
				lobeHaut -= unsignedGetPixel(integral,pitch, x+lobe-1, y - (3*lobe +1)/2) ;
				lobeHaut += unsignedGetPixel(integral,pitch, x+lobe-1, y - (lobe +1)/2) ;
				lobeHaut -= unsignedGetPixel(integral,pitch, x-lobe, y - (lobe +1)/2) ;
				
				lobeCentre += unsignedGetPixel(integral,pitch, x-lobe, y - (lobe +1)/2) ;
				lobeCentre -= unsignedGetPixel(integral,pitch, x+lobe-1, y - (lobe +1)/2) ;
				lobeCentre += unsignedGetPixel(integral,pitch, x+lobe-1, y + (lobe -1)/2) ;
				lobeCentre -= unsignedGetPixel(integral,pitch, x-lobe, y + (lobe -1)/2) ;
				
				lobeBas += unsignedGetPixel(integral,pitch, x-lobe, y + (lobe -1)/2) ;
				lobeBas -= unsignedGetPixel(integral,pitch, x+lobe-1, y + (lobe -1)/2) ;
				lobeBas += unsignedGetPixel(integral,pitch, x+lobe-1, y + (3*lobe -1)/2) ;
				lobeBas -= unsignedGetPixel(integral,pitch, x-lobe, y + (3*lobe -1)/2) ;
				
				int dyy = lobeCentre - lobeHaut - lobeBas ;
				
				// Derivee selon xy
				int lobe00=0, lobe01=0, lobe10=0, lobe11=0;
				
				lobe00 += unsignedGetPixel(integral,pitch, x-lobe-1, y-lobe -1) ;
				lobe00 -= unsignedGetPixel(integral,pitch, x-1, y-lobe -1) ;
				lobe00 += unsignedGetPixel(integral,pitch, x-1, y-1) ;
				lobe00 -= unsignedGetPixel(integral,pitch, x-lobe-1, y-1) ;
				
				lobe01 += unsignedGetPixel(integral,pitch, x, y-lobe-1) ;
				lobe01 -= unsignedGetPixel(integral,pitch, x, y-1) ;
				lobe01 += unsignedGetPixel(integral,pitch, x+lobe, y-1) ;
				lobe01 -= unsignedGetPixel(integral,pitch, x+lobe, y-lobe-1) ;
				
				lobe10 += unsignedGetPixel(integral,pitch, x-lobe-1, y) ;
				lobe10 -= unsignedGetPixel(integral,pitch, x-1, y) ;
				lobe10 += unsignedGetPixel(integral,pitch, x-1, y+lobe) ;
				lobe10 -= unsignedGetPixel(integral,pitch, x-lobe-1, y+lobe) ;

				lobe11 += unsignedGetPixel(integral,pitch, x, y) ;
				lobe11 -= unsignedGetPixel(integral,pitch, x, y+lobe) ;
				lobe11 -= unsignedGetPixel(integral,pitch, x+lobe, y) ;
				lobe11 += unsignedGetPixel(integral,pitch, x+lobe, y+lobe) ;
				
				int dxy = lobe00 + lobe11 - lobe10 - lobe01 ;
				
				unsignedGetPixel(out[inter],pitch,x,y) = (int)((dxx*dyy- (0.9*dxy)*(0.9*dxy))/(area*area)) ;
  	}
  }
  else{
  	for(int inter=0;inter<intervals;inter++)
  		unsignedGetPixel(out[inter], pitch, x, y) =0;
  }
}

void CUDAcalculateGaussianDerivative(const IplImage* imageIntegrale, int octave, int intervals){
	if(imageIntegrale->depth!=IPL_DEPTH_32S){
		std::cout << "Mauvais type d'images dans CUDAcalculateGaussianDerivative" << std::endl;
		exit(EXIT_FAILURE);
	}
	

	//copie sur le device
	if(cudaSuccess != cudaMemcpy2D(CUDAintegral,CUDAintegralPitch,imageIntegrale->imageData,imageIntegrale->widthStep,
			imageIntegrale->width*sizeof(unsigned int),imageIntegrale->height,cudaMemcpyHostToDevice))
			std::cout << "erreur copie" << std::endl;
	// Calcul de la taille du filtre et des bordures
	int power = 1 ;
	for (int t=0 ; t<octave+1 ; t++)
	{
		power *= 2 ;
	}
	int borderSize = (3*(power*intervals + 1))/2+1  ;

	
	dim3 dimBlock( blocksize, blocksize );
    dim3 dimGrid( imageIntegrale->width/blocksize, imageIntegrale->height/blocksize);
	//lancement des calculs
     CUDAcalculateGaussianDerivative2<<<dimGrid, dimBlock>>>(CUDAintegral, (uint**)CUDAadressImgs, imageIntegrale->width, imageIntegrale->height , CUDAimgsPitch, intervals, power , borderSize);

}

void CUDAretrieveGaussianDerivative(IplImage** out, int intervals){
	cudaThreadSynchronize();
	for (int inter=0 ; inter<intervals ; inter++){
		//recuperation depuis le device
		if(cudaSuccess != cudaMemcpy2D(out[inter]->imageData,out[inter]->widthStep,CUDAimgs[inter],CUDAimgsPitch,
			out[inter]->width*sizeof(unsigned int),out[inter]->height,cudaMemcpyDeviceToHost))
			std::cout << "erreur récupération" << std::endl;
	}
}
