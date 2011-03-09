#include <cutil.h>
#include <stdlib.h>
#include <stdio.h>

const int N = 2048; 
const int blocksize = 16;
const int MAX = 100;

__host__ void add_matrix_cpu(float* a, float *b, float *c, int N) {
  int i, j;
  for (i=0; i<N; i++)
    for (j=0; j<N; j++)
      c[i*N+j]=a[i*N+j]+b[i*N+j];
}

__global__ void add_matrix(float* a, float *b, float *c, int N ) {
  int i = blockIdx.x * blockDim.x + threadIdx.x; 
  int j = blockIdx.y * blockDim.y + threadIdx.y; 
  int index = i + j*N; 
  if ( i < N && j < N )
    c[index] = a[index] + b[index];
}

void add_matrix(){
	
  int k;
  float *a = new float[N*N]; 
  float *b = new float[N*N]; 
  float *c = new float[N*N];
  unsigned int timer;
  unsigned int timer2;
  for ( int i = 0; i < N*N; ++i ) { 
    a[i] = 1.0f; b[i] = 3.5f; }
  float *ad, *bd, *cd; 
  const int size = N*N*sizeof(float); 
  cudaMalloc( (void**)&ad, size ); 
  cudaMalloc( (void**)&bd, size ); 
  cudaMalloc( (void**)&cd, size );
  cudaMemcpy( ad, a, size, cudaMemcpyHostToDevice ); 
  cudaMemcpy( bd, b, size, cudaMemcpyHostToDevice );
  dim3 dimBlock( blocksize, blocksize ); 
  dim3 dimGrid( N/dimBlock.x, N/dimBlock.y ); 
  //CUT_SAFE_CALL(cutCreateTimer(&timer));
  //CUT_SAFE_CALL(cutStartTimer(timer));
  for (k=1;k<=MAX;k++) {
    add_matrix<<<dimGrid, dimBlock>>>( ad, bd, cd, N );
    cudaThreadSynchronize();
  }
  //CUT_SAFE_CALL(cutStopTimer(timer));
  //printf("Processing time on GPU: %f (ms)\n",cutGetTimerValue(timer)/MAX);
  //CUT_SAFE_CALL(cutDeleteTimer(timer));
  cudaMemcpy( c, cd, size, cudaMemcpyDeviceToHost );
  cudaFree( ad ); 
  cudaFree( bd ); 
  cudaFree( cd ); 
  printf("Valeur: %f\n",c[0]);
  //CUT_SAFE_CALL(cutCreateTimer(&timer2));
  //CUT_SAFE_CALL(cutStartTimer(timer2));
  for (k=1;k<=MAX;k++)
    add_matrix_cpu(a,b,c, N);
  //CUT_SAFE_CALL(cutStopTimer(timer2));
  //printf("Processing time on CPU: %f (ms)\n",cutGetTimerValue(timer2)/MAX);
  //CUT_SAFE_CALL(cutDeleteTimer(timer2));  
  delete[] a; 
  delete[] b; 
  delete[] c;
}

/*int main() { 
	add_matrix();
  return EXIT_SUCCESS;
}*/