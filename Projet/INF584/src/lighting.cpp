/*
 * lighting.cpp
 *
 *  Created on: 10 mars 2011
 *      Author: benoit
 */

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
		milieu= courant-(r.direction)*pas;


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


void lumiere(Vec3Df PosCam, Vec3Df PosLum, Vec3Df ColorLum, const Image & relief, const Image & couleur, float x, float y, float epsilon, int nbPas, Vec3Df & OriginalColor, float & poidsCumule){
	
	Rayon regard(PosCam, Vec3Df(x,y,0)-PosCam);
	
	Vec3Df intersec;

	if(!eclairage(regard, PosLum, relief, epsilon,nbPas,intersec)){
		std::cout << "x,y=" << x << ","<< y << "pas éclairé" << std::endl;
		return;
	}
	float poids2 = Vec3Df::distance(PosLum,intersec);

	float xCol = intersec[0];
	float yCol = intersec[1];
	//std::cout << "x,y=" << x << ","<< y << "|"<< xCol<<","<<yCol<<std::endl;
	Vec3Df coul = Vec3Df(ColorLum[0]*couleur.getInRealWorld(xCol,yCol,0),ColorLum[1]*couleur.getInRealWorld(xCol,yCol,1),ColorLum[2]*couleur.getInRealWorld(xCol,yCol,2));

	OriginalColor = (poidsCumule*OriginalColor+coul*poids2)/(poids2+poidsCumule);
	poidsCumule = poids2+poidsCumule;
}
