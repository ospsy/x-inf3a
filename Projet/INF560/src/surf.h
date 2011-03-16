#ifndef SURF_H_
#define SURF_H_

#include <cv.h>
#include <highgui.h>
#include <iostream>

/**
 * Crée l'image intégrale, l'image de sortie doit déjà être créé
 * @param in image en niveau de gris d'entrée (U8)
 * @param out image pour stocker la sortie en 32bits (U32)
 */
void makeIntegralImage(const IplImage* in, IplImage* out);

// Calcul de les derivees gaussiennes d'ordre 2
void calculateGaussianDerivative(const IplImage* imageIntegrale, IplImage** out, int octave, int intervals);

// Acces au pixel x,y d'une image
#define getPixel(in, x, y) ((uint*)( ((in)->imageData) + ((in)->widthStep) * (x))) [(y)]
//inline uint getPixel(const IplImage* in, int x, int y)
//{
//	// Pixel en dehors de l'image
////	if (x < 0 || x >= in->width || y < 0 || y >= in->height){
////		std::cout << "En dehords des bords.\n" ;
////		return 0 ;
////	}
//	return ((uint*)( (in->imageData) + (in->widthStep) * x)) [y] ;
//}



#endif /*SURF_H_*/
