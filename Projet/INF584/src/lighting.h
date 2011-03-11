/*
 * lighting.h
 *
 * Header des fonctions d'éclairage
 *
 *  Created on: 10 mars 2011
 *      Author: Benoit SEGUIN (dit "La Chèvre")
 */

#ifndef LIGHTING_H_
#define LIGHTING_H_

#include "Vec3D.h"
#include "image.h"

class Rayon{

public :
	Vec3Df origine;
	Vec3Df direction;

	Rayon(Vec3Df o, Vec3Df d){
		origine = o;
		direction = d;
		direction.normalize();
	}

};

inline bool estSous(const Vec3Df courant, const Image & im){
	return im.getInRealWorld(courant[0],courant[1])/255> courant[2];
}


//La valeur d'epsilon est celle du pas pour avancer
Vec3Df intersection(const Rayon r, const Image & im, float epsilon, int nbPas);


//regard repr�sente le rayon partant du regard, lumiere la position de la lumiere
////On retourne true ssi le point vu par notre regard est �clair�
bool eclairage(Rayon regard, Vec3Df lumiere, const Image & im, float epsilon, int nbPas, Vec3Df & intersec);

//x et y sont les coordonn�es sur lesquelles on veut projet notre point
// OriginalColor et poidsCumule nous informent sur la Color pour le moment allou�e au point tex(x,y) et le poids total des distances dans ce calcul
void lumiere(Vec3Df PosCam, Vec3Df PosLum, Vec3Df ColorLum, const Image & relief, const Image & couleur, float x, float y, float epsilon, int nbPas, Vec3Df & OriginalColor, float & poidsCumule);

/*
Vec3Df tangente(float x, float y ,float theta, const Image & I1){


}


float safetyRadius(float x, float y, const Image& I1, float theta){


}*/

#endif /* LIGHTING_H_ */
