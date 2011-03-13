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
#include <vector>

//Ben c'est Pi quoi...
#define PI 3.1415

enum LightingMode{PASCONSTANT=0,RADIUS};
//LightingMode lightingMode = PASCONSTANT;
#define lightingMode PASCONSTANT

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

//La valeur d'epsilon est celle du pas pour avancer
Vec3Df intersection(const Rayon r, const Image & im, float epsilon, int nbPas, float *** reglage);

//La valeur d'epsilon est celle du pas pour avancer
Vec3Df intersection(const Rayon r, const Image & im, float epsilon, int nbPas);


//regard repr�sente le rayon partant du regard, lumiere la position de la lumiere
////On retourne true ssi le point vu par notre regard est �clair�
bool eclairage(Rayon regard, Vec3Df lumiere, const Image & im, float epsilon, int nbPas, Vec3Df & intersec);

//regard repr�sente le rayon partant du regard, lumiere la position de la lumiere
////On retourne true ssi le point vu par notre regard est �clair�
bool eclairage2(Rayon regard, Vec3Df lumiere, const Image & im, float epsilon, int nbPas, Vec3Df & intersec, float *** reglage);

/**
 * Renvoit la couleur obtenur suivant le rayon demandée
 * camera : Rayon étudié
 * lumieres : positions des lumières
 * couleurs : couleurs des lumières
 * relief : image representant le relief de l'objet
 * couleurs : image representant la couleur de l'objet
 * tableau : argument optionel du tableau de precalcul des safetyradius
 */
Vec3Df lumiere(Rayon camera, std::vector<Vec3Df> lumieres, std::vector<Vec3Df> couleurs, const Image& relief, const Image& couleur, float*** tableau);

/*

//x et y sont les coordonn�es sur lesquelles on veut projet notre point
// OriginalColor et poidsCumule nous informent sur la Color pour le moment allou�e au point tex(x,y) et le poids total des distances dans ce calcul
void lumiere(Vec3Df PosCam, Vec3Df PosLum, Vec3Df ColorLum, const Image & relief, const Image & couleur, float x, float y, float epsilon, int nbPas, Vec3Df & OriginalColor, float & poidsCumule);

//x et y sont les coordonn�es sur lesquelles on veut projet notre point
// OriginalColor et poidsCumule nous informent sur la Color pour le moment allou�e au point tex(x,y) et le poids total des distances dans ce calcul
void lumiere2(Vec3Df PosCam, Vec3Df PosLum, Vec3Df ColorLum, const Image & relief, const Image & couleur, float x, float y, float epsilon, int nbPas, Vec3Df & OriginalColor, float & poidsCumule, float *** reglage);

void lumiere3(Vec3Df PosCam, Vec3Df PosLum, Vec3Df ColorLum, const Image & relief, const Image & couleur, float x, float y, float epsilon, int nbPas, Vec3Df & OriginalColor, float & poidsCumule);
*/


#endif /* LIGHTING_H_ */
