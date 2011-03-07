/* loadppm.h
 *  v1.3 11.10.2005
 */

#ifndef __LOADPPM_H
#define __LOADPPM_H

#include <iostream>
#include <fstream>
#include <math.h>
#include <GL/gl.h>

using namespace std;

class ImageBW {
public:
  int sizeX, sizeY;
  GLubyte *data;
  ImageBW(const char *filename){
  	load(filename);
  }
  ImageBW() : sizeX(0),sizeY(0) {};
  void load(const char *filename);
  ~ImageBW(){
    if (data)
      delete [] data;
  }
  inline float operator () (float x, float y) const{
  	if(x<0) x=0;
  	if(x>=sizeX) x=sizeX-1;
  	if(y<0) y=0;
  	if(y>=sizeY) y=sizeY-1;
  	int x0=(int)x;
  	int y0=(int)y;
  	return data[y0+x0*sizeY]*(x0+1-x)*(y0+1-y)+
	  	data[y0+1+x0*sizeY]*(x0+1-x)*(y0-y)+
	  	data[y0+1+(x0+1)*sizeY]*(x0-x)*(y0-y)+
	  	data[y0+(x0+1)*sizeY]*(x0-x)*(y0+1-y);
  }
};

#endif
