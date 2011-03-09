#include <cutil_inline.h>
#include <stdlib.h>
#include <stdio.h>
#include <cv.h>

const int blocksize = 16;

__global__ void CUDAmakeIntegralImageLignes(uint* a, int width, int height, int pitch ) {
  int n = blockIdx.x * blockDim.x*blockDim.y+blockIdx.y*blockDim.y*blockDim.x*gridDim.x + threadIdx.x + threadIdx.y*blockDim.x;
  unsigned int tmp=0;
  if ( n>=0 && n<height ){
  	for(uint i=0;i<width;i++){
		tmp+= ((uint*)(a + pitch*n))[i];
		((uint*)(a + pitch*n))[i]=tmp;
  	}
  }
}

__global__ void CUDAmakeIntegralImageColonnes(uint* a, int width, int height, int pitch ) {
  int n = blockIdx.x * blockDim.x*blockDim.y+blockIdx.y*blockDim.y*blockDim.x*gridDim.x + threadIdx.x + threadIdx.y*blockDim.x;
  unsigned int tmp=0;
  if ( n>=0 && n<width ){
  	for(uint i=0;i<height;i++){
		tmp+= ((uint*)(a + pitch*i))[n];
		((uint*)(a + pitch*i))[n]=tmp;
  	}
  }
}

void CUDAmakeIntegralImage(const IplImage* in, IplImage* out){
	unsigned int *a, pitch;
	//allocation de la memoire device
	cudaMallocPitch(&a,&pitch,in->width*sizeof(unsigned int),in->height);
	
	//copie d'une image U8 vers une S32
	cvConvertScale(in, out, 1.);
	//copie sur le device
	cudaMemcpy2D(a,pitch,out->imageData,out->widthStep,
			out->width,out->height,cudaMemcpyHostToDevice);
	
	//lancement des calculs
	{
	  dim3 dimBlock( blocksize, blocksize );
	  int tmp=(int)(sqrt(in->height/(float)(blocksize*blocksize))+1);
      dim3 dimGrid( tmp,tmp); 
      CUDAmakeIntegralImageLignes<<<dimGrid, dimBlock>>>( a, in->width, in->height , pitch );
	  cudaThreadSynchronize();
	}
	{
	  dim3 dimBlock( blocksize, blocksize );
	  int tmp=(int)(sqrt(in->width/(float)(blocksize*blocksize))+1);
      dim3 dimGrid( tmp,tmp); 
      CUDAmakeIntegralImageColonnes<<<dimGrid, dimBlock>>>( a, in->width, in->height , pitch );
	  cudaThreadSynchronize();
	}
	
	//recuperation du device
	cudaMemcpy2D(out->imageData,out->widthStep,a,pitch,
			out->width,out->height,cudaMemcpyDeviceToHost);
	//liberation de la memoire du device
	cudaFree(a);
}
