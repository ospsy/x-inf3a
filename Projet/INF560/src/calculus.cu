#include <cutil.h>
#include <stdlib.h>
#include <iostream.h>
#include <cv.h>

const int blocksize = 4;

__global__ void CUDAmakeIntegralImageLignes(uint* a, int width, int height, int pitch ) {
  int n = blockIdx.x * blockDim.x*blockDim.y+blockIdx.y*blockDim.y*blockDim.x*gridDim.x + threadIdx.x + threadIdx.y*blockDim.x;
  uint tmp=0;
  if ( n>=0 && n<height ){
  	for(uint i=0;i<width;i++){
		tmp+= ((uint*)((char*)a + pitch*n))[i];
		((uint*)((char*)a + pitch*n))[i]=tmp;
  	}
  }
}

__global__ void CUDAmakeIntegralImageColonnes(uint* a, int width, int height, int pitch ) {
  int n = blockIdx.x * blockDim.x*blockDim.y+blockIdx.y*blockDim.y*blockDim.x*gridDim.x + threadIdx.x + threadIdx.y*blockDim.x;
  uint tmp=0;
  if ( n>=0 && n<width ){
  	for(uint i=0;i<height;i++){
		tmp+= ((uint*)((char*)a + pitch*i))[n];
		((uint*)((char*)a + pitch*i))[n]=tmp;
  	}
  }
}

void CUDAmakeIntegralImage(const IplImage* in, IplImage* out){
	uint *a, pitch;
	//allocation de la memoire device
	cudaMallocPitch((void**)&a,&pitch,in->width*sizeof(unsigned int),in->height);
	
	//copie d'une image U8 vers une S32
	cvConvert(in, out);
	//copie sur le device
	if(cudaSuccess != cudaMemcpy2D(a,pitch,out->imageData,out->widthStep,
			out->width*sizeof(unsigned int),out->height,cudaMemcpyHostToDevice))
			std::cout << "erreur allocation" << std::endl;
	std::cout <<  (int)((uchar*)(in->imageData))[1] << std::endl;
	std::cout <<  ((int*)(out->imageData))[1] << std::endl;
	((int*)(out->imageData))[1]=0;
	//lancement des calculs
	{
	  dim3 dimBlock( blocksize, blocksize );
	  int tmp=(int)(sqrt(in->height/(float)(blocksize*blocksize))+1);
      dim3 dimGrid( tmp,tmp);
      std::cout << "threads pr les lignes " << blocksize*blocksize*tmp*tmp << std::endl;
      CUDAmakeIntegralImageLignes<<<dimGrid, dimBlock>>>( a, in->width, in->height , pitch );
	  cudaThreadSynchronize();
	}
	{
	  dim3 dimBlock( blocksize, blocksize );
	  int tmp=(int)(sqrt(in->width/(float)(blocksize*blocksize))+1);
      dim3 dimGrid( tmp,tmp);
      std::cout << "threads pr les colonnes " <<  blocksize*blocksize*tmp*tmp << std::endl;
      CUDAmakeIntegralImageColonnes<<<dimGrid, dimBlock>>>( a, in->width, in->height , pitch );
	  cudaThreadSynchronize();
	}
	//recuperation depuis le device
	if(cudaSuccess != cudaMemcpy2D(out->imageData,out->widthStep,a,pitch,
			out->width*sizeof(unsigned int),out->height,cudaMemcpyDeviceToHost))
			std::cout << "erreur copie" << std::endl;
	//liberation de la memoire du device
	cudaFree(a);
	std::cout << "termine" << std::endl;
	std::cout <<  (int)((uchar*)(in->imageData))[1] << std::endl;
	std::cout <<  ((int*)(out->imageData))[1] << std::endl;
}
