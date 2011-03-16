#include <cutil.h>
#include <stdlib.h>
#include <iostream>
#include <cv.h>
#include <ctime>

const int blocksize = 10;

// Acces au pixel x,y d'une image
#define getPixel(in,pitch,x,y) ( ((uint*)((char*)(in) + (pitch)*(x)))[(y)] )

__global__ void CUDAcalculateGaussianDerivative2(uint* integral, uint* out, int width, int height, int pitch , int lobe, int area, int borderSize) {
  int x = blockIdx.x * blockDim.x + threadIdx.x; 
  int y = blockIdx.y * blockDim.y + threadIdx.y;
  if ( x < width-borderSize && y < height-borderSize && x >= borderSize && y >= borderSize ){
  	// Derivee selon x
				int lobeGauche = 0, lobeCentre = 0, lobeDroit = 0 ;
				lobeGauche += getPixel(integral,pitch, x-(lobe+1)/2, y + lobe-1) ;
				lobeGauche -= getPixel(integral,pitch, x-(lobe+1)/2, y - lobe) ;
				lobeGauche += getPixel(integral,pitch, x-(lobe+1)/2 - lobe, y - lobe) ;
				lobeGauche -= getPixel(integral,pitch, x-(lobe+1)/2 - lobe, y + lobe-1) ;

				lobeCentre += getPixel(integral,pitch, x-(lobe+1)/2, y - lobe) ;
				lobeCentre -= getPixel(integral,pitch, x-(lobe+1)/2, y + lobe-1) ;
				lobeCentre += getPixel(integral,pitch, x+(lobe-1)/2, y + lobe-1) ;
				lobeCentre -= getPixel(integral,pitch, x+(lobe-1)/2, y - lobe) ;
				
				lobeDroit += getPixel(integral,pitch, x+(lobe-1)/2, y - lobe) ;
				lobeDroit -= getPixel(integral,pitch, x+(lobe-1)/2, y + lobe-1) ;
				lobeDroit += getPixel(integral,pitch, x+(lobe-1)/2 + lobe, y + lobe-1) ;
				lobeDroit -= getPixel(integral,pitch, x+(lobe-1)/2 + lobe, y - lobe) ;
				
				int dxx = lobeCentre - lobeDroit - lobeGauche ;
				
				// Derivee selon y
				int lobeHaut = 0, lobeBas = lobeCentre = 0 ;
				lobeHaut += getPixel(integral,pitch, x-lobe, y - (3*lobe +1)/2) ;
				lobeHaut -= getPixel(integral,pitch, x+lobe-1, y - (3*lobe +1)/2) ;
				lobeHaut += getPixel(integral,pitch, x+lobe-1, y - (lobe +1)/2) ;
				lobeHaut -= getPixel(integral,pitch, x-lobe, y - (lobe +1)/2) ;
				
				lobeCentre += getPixel(integral,pitch, x-lobe, y - (lobe +1)/2) ;
				lobeCentre -= getPixel(integral,pitch, x+lobe-1, y - (lobe +1)/2) ;
				lobeCentre += getPixel(integral,pitch, x+lobe-1, y + (lobe -1)/2) ;
				lobeCentre -= getPixel(integral,pitch, x-lobe, y + (lobe -1)/2) ;
				
				lobeBas += getPixel(integral,pitch, x-lobe, y + (lobe -1)/2) ;
				lobeBas -= getPixel(integral,pitch, x+lobe-1, y + (lobe -1)/2) ;
				lobeBas += getPixel(integral,pitch, x+lobe-1, y + (3*lobe -1)/2) ;
				lobeBas -= getPixel(integral,pitch, x-lobe, y + (3*lobe -1)/2) ;
				
				int dyy = lobeCentre - lobeHaut - lobeBas ;
				
				// Derivee selon xy
				int lobe00=0, lobe01=0, lobe10=0, lobe11=0;
				
				lobe00 += getPixel(integral,pitch, x-lobe-1, y-lobe -1) ;
				lobe00 -= getPixel(integral,pitch, x-1, y-lobe -1) ;
				lobe00 += getPixel(integral,pitch, x-1, y-1) ;
				lobe00 -= getPixel(integral,pitch, x-lobe-1, y-1) ;
				
				lobe01 += getPixel(integral,pitch, x, y-lobe-1) ;
				lobe01 -= getPixel(integral,pitch, x, y-1) ;
				lobe01 += getPixel(integral,pitch, x+lobe, y-1) ;
				lobe01 -= getPixel(integral,pitch, x+lobe, y-lobe-1) ;
				
				lobe10 += getPixel(integral,pitch, x-lobe-1, y) ;
				lobe10 -= getPixel(integral,pitch, x-1, y) ;
				lobe10 += getPixel(integral,pitch, x-1, y+lobe) ;
				lobe10 -= getPixel(integral,pitch, x-lobe-1, y+lobe) ;

				lobe11 += getPixel(integral,pitch, x, y) ;
				lobe11 -= getPixel(integral,pitch, x, y+lobe) ;
				lobe11 -= getPixel(integral,pitch, x+lobe, y) ;
				lobe11 += getPixel(integral,pitch, x+lobe, y+lobe) ;
				
				int dxy = lobe00 + lobe11 - lobe10 - lobe01 ;
				
				getPixel(out,pitch,x,y) = (int)((dxx*dyy- (0.9*dxy)*(0.9*dxy))/(area*area)) ;
  }
  else{
  	getPixel(out, pitch, x, y) =0;
  }
}

void CUDAcalculateGaussianDerivative(const IplImage* imageIntegrale, IplImage** out, int octave, int intervals){
	if(imageIntegrale->depth!=IPL_DEPTH_32S){
		std::cout << "Mauvais type d'images dans CUDAcalculateGaussianDerivative" << std::endl;
		exit(EXIT_FAILURE);
	}
	
	uint *integralInCUDA, *a;
	uint pitch;
	//allocation de la memoire device
	if(cudaSuccess != cudaMallocPitch((void**)&integralInCUDA,&pitch,imageIntegrale->width*sizeof(unsigned int),imageIntegrale->height))
			std::cout << "erreur allocation" << std::endl;
	if(cudaSuccess != cudaMallocPitch((void**)&a,&pitch,imageIntegrale->width*sizeof(unsigned int),imageIntegrale->height))
			std::cout << "erreur allocation" << std::endl;
	
	clock_t timer=clock();
	//copie sur le device
	if(cudaSuccess != cudaMemcpy2D(a,pitch,imageIntegrale->imageData,imageIntegrale->widthStep,
			imageIntegrale->width*sizeof(unsigned int),imageIntegrale->height,cudaMemcpyHostToDevice))
			std::cout << "erreur copie" << std::endl;
			
	std::cout << "CUDAcalculateGaussianDerivative : " << 1000*(clock()-timer)/CLOCKS_PER_SEC <<"ms"<< std::endl;
	
	// Calcul de la taille du filtre et des bordures
	int power = 1 ;
	for (int t=0 ; t<octave+1 ; t++)
	{
		power *= 2 ;
	}
	int borderSize = (3*(power*intervals + 1))/2+1  ;
	
	dim3 dimBlock( blocksize, blocksize );
    dim3 dimGrid( imageIntegrale->width/blocksize, imageIntegrale->height/blocksize);
	for (int inter=0 ; inter<intervals ; inter++){
		// Calcul de la surface pour normalisation d'echelle
		int lobe = power*(inter+1) + 1 ;
		int area = (3*lobe) * (3*lobe) ;
		//lancement des calculs
      	CUDAcalculateGaussianDerivative2<<<dimGrid, dimBlock>>>(integralInCUDA, a, imageIntegrale->width, imageIntegrale->height , pitch, lobe, area, borderSize);
	  	cudaThreadSynchronize();

		//recuperation depuis le device
		if(cudaSuccess != cudaMemcpy2D(out[inter]->imageData,out[inter]->widthStep,a,pitch,
			out[inter]->width*sizeof(unsigned int),out[inter]->height,cudaMemcpyDeviceToHost))
			std::cout << "erreur copie" << std::endl;
	}
			
	std::cout << "CUDAcalculateGaussianDerivative : " << 1000*(clock()-timer)/(double)CLOCKS_PER_SEC <<"ms"<< std::endl;
	
	//liberation de la memoire du device
	cudaFree(a);
	cudaFree(integralInCUDA);
}
