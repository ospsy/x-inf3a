#ifndef COMMON_H_
#define COMMON_H_

#include <cutil.h>
#include <stdlib.h>
#include <iostream>
#include <cv.h>
#include <ctime>

#include "surfCUDA.h"


void CUDAinit(uint width, uint height){
	//allocation de la memoire device
	if(cudaSuccess != cudaMallocPitch((void**)&CUDAintegral,&CUDAintegralPitch,width*sizeof(uint),height))
			std::cout << "erreur allocation CUDAintegral" << std::endl;
	if(cudaSuccess != cudaMallocPitch((void**)&CUDAimg,&CUDAimgPitch,width*sizeof(uint),height))
			std::cout << "erreur allocation CUDAimg" << std::endl;
}

void CUDAclose(){
	cudaFree(CUDAintegral);
	cudaFree(CUDAimg);
}

#endif
