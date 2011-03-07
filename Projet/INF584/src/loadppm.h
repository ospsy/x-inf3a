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
  inline const GLubyte & operator () (int x, int y) const{
  	if(x<0) x=0;
  	if(x>=sizeX) x=sizeX-1;
  	if(y<0) y=0;
  	if(y>=sizeY) y=sizeY-1;
  	return data[y+x*sizeY];
  }
};

#endif
