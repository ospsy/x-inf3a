#ifndef SURFCUDA_H_
#define SURFCUDA_H_

#include <cv.h>

/**
 * Crée l'image intégrale, l'image de sortie doit déjà être créé
 * @param in image en niveau de gris d'entrée (U8)
 * @param out image pour stocker la sortie en 32bits (U32)
 */
void CUDAmakeIntegralImage(const IplImage* in, IplImage* out);

void CUDAcalculateGaussianDerivative(const IplImage* imageIntegrale, IplImage** out, int octave, int intervals);

#endif /*SURFCUDA_H_*/
