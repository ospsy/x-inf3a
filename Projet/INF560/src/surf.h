#ifndef SURF_H_
#define SURF_H_

/**
 * Crée l'image intégrale, l'image de sortie doit déjà être créé
 * @param in image en niveau de gris d'entrée (U8)
 * @param out image pour stocker la sortie en 32bits (U32)
 */
void makeIntegralImage(const IplImage* in, IplImage* out);

#endif /*SURF_H_*/
