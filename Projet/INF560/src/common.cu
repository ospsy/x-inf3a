#ifndef COMMON_H_
#define COMMON_H_

#include <cutil.h>
#include <stdlib.h>
#include <iostream>
#include <cv.h>
#include <ctime>

#include "surfCUDA.h"


void CUDAinit(uint width, uint height, uint intervals){
	//allocation de la memoire device
	if(cudaSuccess != cudaMallocPitch((void**)&CUDAintegral,&CUDAintegralPitch,width*sizeof(uint),height))
			std::cout << "erreur allocation CUDAintegral" << std::endl;
	if(cudaSuccess != cudaMallocPitch((void**)&CUDAimg,&CUDAimgPitch,width*sizeof(uint),height))
			std::cout << "erreur allocation CUDAimg" << std::endl;
	CUDAimgs = (uint**) malloc(intervals*sizeof(uint*));
	for(int i=0;i<intervals;i++){
		if(cudaSuccess != cudaMallocPitch((void**)&(CUDAimgs[i]),&CUDAimgsPitch,width*sizeof(uint),height))
			std::cout << "erreur allocation CUDAimgs " << i << std::endl;
	}
	CUDAadressImgs;
	cudaMalloc((void**)&CUDAadressImgs,sizeof(uint*)*intervals);
	cudaMemcpy(CUDAadressImgs,CUDAimgs,sizeof(uint*)*intervals,cudaMemcpyHostToDevice);
}

void CUDAclose(uint intervals){
	cudaFree(CUDAintegral);
	cudaFree(CUDAimg);
	cudaFree(CUDAadressImgs);
	for(int i=0;i<intervals;i++)
		cudaFree(CUDAimgs[i]);
	free(CUDAimgs);
}

#endif
