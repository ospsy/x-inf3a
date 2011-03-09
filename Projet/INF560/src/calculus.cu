#include <cutil.h>
#include <stdlib.h>
#include <stdio.h>
#include <cv.h>



void CUDAmakeIntegralImage(const IplImage* in, IplImage* out){
	unsigned int *a, pitch;
	//allocation de la memoire device
	cudaMallocPitch(&a,&pitch,in->width*4,in->height);
	
	//copie d'une image U8 vers une S32
	cvConvertScale(in, out, 1.);
	//copie sur le device
	cudaMemcpy2D(a,pitch,out->imageData,out->widthStep,
			out->width,out->height,cudaMemcpyHostToDevice);
	
	//lancement des calculs
	
	//on attend aue tout le monde ait fini
	cudaThreadSynchronize();
	//recuperation du device
	cudaMemcpy2D(out->imageData,out->widthStep,a,pitch,
			out->width,out->height,cudaMemcpyDeviceToHost);
	//liberation de la memoire du device
	cudaFree(a);
}