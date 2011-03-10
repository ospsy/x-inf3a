/* loadppm.h
 *  v1.3 11.10.2005
 */

#ifndef __LOADPPM_H
#define __LOADPPM_H

#include <iostream>
#include <fstream>
#include <math.h>
#include "Vec3D.h"
#include <GL/glut.h>

using namespace std;

class Image {
public:
  int sizeX, sizeY;
  GLubyte *data;
  Image(const char *filename){
  	load(filename);
  }
  Image() : sizeX(0),sizeY(0) {};
  Image(int x, int y): sizeX(x),sizeY(y) {
  	data= new  GLubyte[3*sizeX * sizeY];
  };
  void resize(int x,int y){
  	if (data)
      delete [] data;
    sizeX=x;
    sizeY=y;
    data= new GLubyte[3*sizeX * sizeY];
  }
  void load(const char *filename);
  ~Image(){
    if (data)
      delete [] data;
  }
  inline float operator () (float x, float y, int color=0) const{
  	if(x<0) x=0;
  	if(x>=sizeX) x=sizeX-1;
  	if(y<0) y=0;
  	if(y>=sizeY) y=sizeY-1;
  	int x0=(int)x;
  	int y0=(int)y;
  	return data[3*(y0+x0*sizeY)+color]*(x0+1-x)*(y0+1-y)+
	  	data[3*(y0+1+x0*sizeY)+color]*(x0+1-x)*(y0-y)+
	  	data[3*(y0+1+(x0+1)*sizeY)+color]*(x0-x)*(y0-y)+
	  	data[3*(y0+(x0+1)*sizeY)+color]*(x0-x)*(y0+1-y);
  }
  inline unsigned char& operator () (int x, int y, int color=0){
  	if(x<0) x=0;
  	if(x>=sizeX) x=sizeX-1;
  	if(y<0) y=0;
  	if(y>=sizeY) y=sizeY-1;
  	return data[3*(y+x*sizeY)+color];
  }
  inline void set(int x, int y, const Vec3Df& color){
  	data[3*(y+x*sizeY)]=(unsigned char)(color[0]);
  	data[3*(y+x*sizeY)+1]=(unsigned char)(color[1]);
  	data[3*(y+x*sizeY)+2]=(unsigned char)(color[2]);
  }
  inline float getInRealWorld(float x, float y, int color=0) const{
	  return operator()(x*sizeX,y*sizeY,color);
  }
};

#endif
