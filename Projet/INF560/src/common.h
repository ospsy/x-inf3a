#ifndef COMMON_H_
#define COMMON_H_

#include <cv.h>

void CUDAinit(uint width, uint height);

void CUDAclose();

int borderSize(int octave,int interval){
	int power = 1 ;
	for (int t=0 ; t<octave+1 ; t++)
	{
		power *= 2 ;
	}
	return (3*(power*interval + 1))  ;
}

#endif
