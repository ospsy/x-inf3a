/*
 * Hough.h
 *
 *  Created on: 16 oct. 2010
 *      Author: benoit
 */

#ifndef HOUGH_H_
#define HOUGH_H_

#include <Imagine/Images.h>
using namespace Imagine;

Image<long> Hough(const Image<byte>& in,int T, int R);


#endif /* HOUGH_H_ */
