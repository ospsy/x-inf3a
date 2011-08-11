
#include <stdlib.h>
#include <stdio.h>
#include <cuda.h>
#include <cutil.h>
#include <fcntl.h>
#include <float.h>
#include <unistd.h>
#include "texton.h"
#include "convert.h"
#include "intervening.h"
#include "lanczos.h"
#include "stencilMVM.h"

#include "localcues.h"
#include "combine.h"
#include "nonmax.h"
#include "spectralPb.h"
#include "globalPb.h"
#include "skeleton.h"

#define __TIMER_SPECFIC

#define TEXTON64 2
#define TEXTON32 1


int damascene(uint width, uint height, uint* imageData, float* gradData, float* oriData, bool gPb) {
  cuInit(0);
  chooseLargestGPU(true);

  int nEigNum = 9;
  float fEigTolerance = 1e-3;
  int nTextonChoice = TEXTON32;

  uint* devRgbU;
  int nPixels = width * height;
  cudaMalloc((void**)(&devRgbU), nPixels*sizeof(uint));
  cudaMemcpy(devRgbU, imageData, nPixels*sizeof(uint), cudaMemcpyHostToDevice);

  uint timer;
#ifdef __TIMER_SPECFIC
  uint timer_specific;
#endif

  size_t totalMemory, availableMemory;
  cuMemGetInfo(&availableMemory,&totalMemory );
  printf("Available %zu bytes on GPU\n", availableMemory);

  cutCreateTimer(&timer);
  cutStartTimer(timer);
 
#ifdef __TIMER_SPECFIC
  cutCreateTimer(&timer_specific);
  cutStartTimer(timer_specific);
#endif

  float* devGreyscale;
  rgbUtoGreyF(width, height, devRgbU, &devGreyscale);

#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< rgbUtoGrayF | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif

  int* devTextons;
  findTextons(width, height, devGreyscale, &devTextons, nTextonChoice);

#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< texton | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif

  float* devL;
  float* devA;
  float* devB;
  rgbUtoLab3F(width, height, 2.5, devRgbU, &devL, &devA, &devB);

#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< rgbUtoLab3F | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif
  normalizeLab(width, height, devL, devA, devB);
#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< normalizeLab | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif
  int border = 30;
  int borderWidth = width + 2 * border;
  int borderHeight = height + 2 * border;
  float* devLMirrored;
  mirrorImage(width, height, border, devL, &devLMirrored);
/*   float* hostLMirrored = (float*)malloc(borderWidth * borderHeight * sizeof(float)); */
/*   cudaMemcpy(hostLMirrored, devLMirrored, borderWidth * borderHeight * sizeof(float), cudaMemcpyDeviceToHost); */
/*   writeFile("L.pb", borderWidth, borderHeight, hostLMirrored); */
 
  cudaThreadSynchronize();
  cudaFree(devRgbU);
  cudaFree(devGreyscale);
#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< mirrorImage | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif
  float* devBg;
  float* devCga;
  float* devCgb;
  float* devTg;
  int matrixPitchInFloats;
 
 uint localcuestimer; 
 cutCreateTimer(&localcuestimer);
 cutStartTimer(localcuestimer);

  localCues(width, height, devL, devA, devB, devTextons, &devBg, &devCga, &devCgb, &devTg, &matrixPitchInFloats, nTextonChoice);

  cutStopTimer(localcuestimer);
  printf("localcues time: %f seconds\n", cutGetTimerValue(localcuestimer)/1000.0);

#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< localcues | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif
   //float* hostG = (float*)malloc(sizeof(float) * nPixels); 
   //CUDA_SAFE_CALL(cudaMemcpy(hostG, devBg, height*width*sizeof(float),cudaMemcpyDeviceToHost));
   //cutSavePGMf("Bg.pgm", hostG, width, height);
   //free(hostG);

  cudaFree(devTextons);
  cudaFree(devL);
  cudaFree(devA);
  cudaFree(devB);
  

  float* devMPbO;
  float *devCombinedGradient;
  combine(width, height, matrixPitchInFloats, devBg, devCga, devCgb, devTg, &devMPbO, &devCombinedGradient, nTextonChoice);

#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< combine | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif

  CUDA_SAFE_CALL(cudaFree(devBg));
  CUDA_SAFE_CALL(cudaFree(devCga));
  CUDA_SAFE_CALL(cudaFree(devCgb));
  CUDA_SAFE_CALL(cudaFree(devTg));

  float* devMPb;
  cudaMalloc((void**)&devMPb, sizeof(float) * nPixels);
  int* devOri;
  cudaMalloc((void**)&devOri, sizeof(int) * nPixels);
  nonMaxSuppression(width, height, devMPbO, matrixPitchInFloats, devMPb, devOri);

  int* oriDataInd;
  oriDataInd=(int*)malloc(sizeof(int) * nPixels);
  cudaMemcpy(oriDataInd, devOri, nPixels*sizeof(int), cudaMemcpyDeviceToHost);
  for(int l=0;l<nPixels;l++){
    oriData[l]=oriDataInd[l]*M_PIl/8;
  }
  free(oriDataInd);
  CUDA_SAFE_CALL(cudaFree(devOri));

#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< nonmaxsupression | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif

  if(!gPb){
    cudaMemcpy(gradData, devMPb, nPixels*sizeof(float), cudaMemcpyDeviceToHost);
    CUDA_SAFE_CALL(cudaFree(devMPb));
    return 0;
  }
  
  //int devMatrixPitch = matrixPitchInFloats * sizeof(float);
  int radius = 5;
  //int radius = 10;

  Stencil theStencil(radius, width, height, matrixPitchInFloats);
  int nDimension = theStencil.getStencilArea();
  float* devMatrix;
  intervene(theStencil, devMPb, &devMatrix);
  printf("Intervening contour completed\n");
 
#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< intervene | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif

  float* eigenvalues;
  float* devEigenvectors;
  //int nEigNum = 17;
  generalizedEigensolve(theStencil, devMatrix, matrixPitchInFloats, nEigNum, &eigenvalues, &devEigenvectors, fEigTolerance);

#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< generalizedEigensolve | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif
  float* devSPb = 0;
  size_t devSPb_pitch = 0;
  CUDA_SAFE_CALL(cudaMallocPitch((void**)&devSPb, &devSPb_pitch, nPixels *  sizeof(float), 8));
  cudaMemset(devSPb, 0, matrixPitchInFloats * sizeof(float) * 8);

  spectralPb(eigenvalues, devEigenvectors, width, height, nEigNum, devSPb, matrixPitchInFloats);

#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< spectralPb | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif
  float* devGPb = 0;
  CUDA_SAFE_CALL(cudaMalloc((void**)&devGPb, sizeof(float) * nPixels));
  float* devGPball = 0;
  CUDA_SAFE_CALL(cudaMalloc((void**)&devGPball, sizeof(float) * matrixPitchInFloats * 8));
  //StartCalcGPb(nPixels, matrixPitchInFloats, 8, devbg1, devbg2, devbg3, devcga1, devcga2, devcga3, devcgb1, devcgb2, devcgb3, devtg1, devtg2, devtg3, devSPb, devMPb, devGPball, devGPb);
  StartCalcGPb(nPixels, matrixPitchInFloats, 8, devCombinedGradient, devSPb, devMPb, devGPball, devGPb);
 
#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< StartCalcGpb | %f | ms\n", cutGetTimerValue(timer_specific));
  cutResetTimer(timer_specific);
  cutStartTimer(timer_specific);
#endif
  float* devGPb_thin = 0;
  CUDA_SAFE_CALL(cudaMalloc((void**)&devGPb_thin, nPixels * sizeof(float) ));
  PostProcess(width, height, width, devGPb, devMPb, devGPb_thin); //note: 3rd param width is the actual pitch of the image
  NormalizeGpbAll(nPixels, 8, matrixPitchInFloats, devGPball);
  
  cudaThreadSynchronize();
  cutStopTimer(timer);
  printf("CUDA Status : %s\n", cudaGetErrorString(cudaGetLastError()));

#ifdef __TIMER_SPECFIC
  cutStopTimer(timer_specific);
  printf(">+< PostProcess | %f | ms\n", cutGetTimerValue(timer_specific));
#endif
  printf(">+< Computation time: | %f | seconds\n", cutGetTimerValue(timer)/1000.0);
  float* hostGPb = (float*)malloc(sizeof(float)*nPixels);
  memset(hostGPb, 0, sizeof(float) * nPixels);
  cudaMemcpy(hostGPb, devGPb, sizeof(float)*nPixels, cudaMemcpyDeviceToHost);

  /* thin image */
  cudaMemcpy(gradData, devGPb_thin, sizeof(float)*nPixels, cudaMemcpyDeviceToHost);
  /* end thin image */

  free(hostGPb);

  CUDA_SAFE_CALL(cudaFree(devEigenvectors));
  CUDA_SAFE_CALL(cudaFree(devCombinedGradient));
  CUDA_SAFE_CALL(cudaFree(devSPb));
  CUDA_SAFE_CALL(cudaFree(devGPb));
  CUDA_SAFE_CALL(cudaFree(devGPb_thin));
  CUDA_SAFE_CALL(cudaFree(devGPball));
}
