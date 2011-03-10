/*
 * lighting.cpp
 *
 *  Created on: 10 mars 2011
 *      Author: benoit
 */

#include "Vec3D.h"
#include "lighting.h"

Vec3Df intersection(const Rayon r, const Image & im, float epsilon, int nbPas){

	Vec3Df courant = r.origine;
	while(! estSous(courant,im)){
		courant+= (r.direction)*epsilon;
	}

	// Recherche Binaire
	float pas = epsilon/2;

	for(int i = 0 ; i < nbPas ; i++) {
		Vec3Df milieu;
		milieu= courant-r.direction*pas;


		if(estSous(milieu,im)){
			courant = milieu;
		}

		pas= pas/2;
	}

	return courant;
}


bool eclairage(Rayon regard, Vec3Df lumiere, const Image & im, float epsilon, int nbPas, Vec3Df & intersec){
	//On retrouve le point sur lequel le regard tombe
	intersec = intersection(regard,im,epsilon,nbPas);
	Rayon r2(lumiere,intersec-lumiere);

	return (Vec3Df::distance(intersec,intersection(r2,im,epsilon,nbPas)) < epsilon);
}


void lumiere(Vec3Df PosCam, Vec3Df PosLum, Vec3Df ColorLum, const Image & im, float x, float y, float epsilon, int nbPas, Vec3Df & OriginalColor, float & poidsCumule){

	Rayon regard(PosCam, Vec3Df(x,y,0)-PosCam);
	Vec3Df intersec;

	if(!eclairage(regard, PosLum, im, epsilon,nbPas,intersec))
		return;
	std::cout << "plop" << std::endl;
	float poids2 = Vec3Df::distance(PosLum,intersec);
	std::cout << poids2 << std::endl;
	Vec3Df coul = Vec3Df(ColorLum[0]*im(x,y,0),ColorLum[1]*im(x,y,1),ColorLum[2]*im(x,y,2));
	std::cout << coul << std::endl;
	OriginalColor = (poidsCumule*OriginalColor+coul*poids2)/(poids2+poidsCumule);
	poidsCumule = poids2+poidsCumule;
	std::cout << OriginalColor << std::endl;
}
