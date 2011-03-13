#ifndef SAFETY_RADIUS_H

#define SAFETY_RADIUS_H

#include "image.h"

/**
 * Precalcule le tableau des SafetyRadius
 * P : nombre de valeurs pour theta
 */
float*** precomputation(const Image & I,int P);

#endif //SAFETY_RADIUS_H
