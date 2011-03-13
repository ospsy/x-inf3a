/*
 * lighting.cpp
 *
 *  Created on: 10 mars 2011
 *      Author: benoit
 */

#include "lighting.h"
#include <math.h>
#include <vector>
#include <cmath>


//Nombre de pas dans la dichotomie
#define NB_PAS 3
// largeur du pas dans le calcul d'intersection
#define EPSILON 0.01
//Valeur de la specularité
#define SPECULARITE 2
//Ben c'est Pi quoi...
#define PI 3.1415
// Pour que les zones non éclairées ne soient pas toutes noires
#define NOIR 0.1


Vec3Df normale(const Image& img, float x, float y){
	float dx=0.1;
	Vec3Df vec1(dx,0,(img.getInRealWorld(x+dx,y)-img.getInRealWorld(x-dx,y))/(2*dx));
	Vec3Df vec2(0,dx,(img.getInRealWorld(x,y+dx)-img.getInRealWorld(x,y-dx))/(2*dx));

	Vec3Df solution = Vec3Df::crossProduct(vec1,vec2);
	solution.normalize();

	return solution;
}

Vec3Df premierInter(Rayon r){
	std::vector<Vec3Df> normales;
	std::vector<float> d;

	normales.push_back(Vec3Df(0,0,1));
	normales.push_back(Vec3Df(1,0,0));
	normales.push_back(Vec3Df(0,1,0));
	normales.push_back(Vec3Df(1,0,0));
	normales.push_back(Vec3Df(0,1,0));
	normales.push_back(Vec3Df(0,0,1));
	d.push_back(-1);
	d.push_back(0);
	d.push_back(-1);
	d.push_back(-1);
	d.push_back(0);
	d.push_back(0);

	float lambda=100;
	Vec3Df pt;
	for(int i=0;i<6;i++){
		float tmp=Vec3Df::dotProduct(normales[i],r.direction);
		if(tmp==0) continue;
		float tmp2=-(d[i]+Vec3Df::dotProduct(normales[i],r.origine))/tmp;
		if(lambda>tmp2){
			Vec3Df tmpPt=r.origine+(tmp2+0.001)*r.direction;
			if(!(tmpPt[0]>1 || tmpPt[0]<0 || tmpPt[1]>1 || tmpPt[1]<0 || tmpPt[2]>1 || tmpPt[2]<0)){
				lambda=tmp2;
				pt=tmpPt;
			}
		}
	}
	return pt;
}


Vec3Df intersection(const Rayon r, const Image & im, float epsilon, int nbPas){

	Vec3Df courant = premierInter(r);
	while(! im.estSous(courant)){
		courant+= (r.direction)*epsilon;

		if(courant[0]>1 || courant[0]<0 || courant[1]>1 || courant[1]<0){
		
			return Vec3Df(-1,-1,-1);
		
		}
	}
	//cout << "hello" << endl;
	
	// Recherche Binaire
	float pas = epsilon/2;

	for(int i = 0 ; i < nbPas ; i++) {
		Vec3Df milieu;
		milieu= courant-(r.direction)*pas;


		if(im.estSous(milieu)){
			courant = milieu;
		}

		pas= pas/2;
	}
	

	return courant;
}

Vec3Df intersection2(const Rayon r, const Image & im, float epsilon, int nbPas, float*** reglage){
	Vec3Df courant = premierInter(r);
	
	int i,j,k;
	while(! im.estSous(courant)){
		i = (int) (courant[0]/(float)im.sizeX);
		j = (int) (courant[1]/(float)im.sizeY);
		k = (int) fmod(atan2(courant[0],courant[1])/(2*PI)+1,1);

		courant+= (r.direction)*reglage[i][j][k];

		if(courant[0]>1 || courant[0]<0 || courant[1]>1 || courant[1]<0)
		
			return Vec3Df(-1,-1,-1);
	}
	
	// Recherche Binaire
	float pas = reglage[i][j][k]/2;

	for(int i = 0 ; i < nbPas ; i++) {
		Vec3Df milieu;
		milieu= courant-(r.direction)*pas;


		if(im.estSous(milieu)){
			courant = milieu;
		}

		pas= pas/2;
	}
	

	return courant;


}


bool eclairage(Rayon regard, Vec3Df lumiere, const Image & im, float epsilon, int nbPas, Vec3Df & intersec){
	
	//On retrouve le point sur lequel le regard tombe
	intersec = intersection(regard,im,epsilon,nbPas);
	
	if (intersec[0]==-1) return false;

	Rayon r2(lumiere,intersec-lumiere);

	return (Vec3Df::distance(intersec,intersection(r2,im,epsilon,nbPas)) < epsilon);
}

bool eclairage2(Rayon regard, Vec3Df lumiere, const Image & im, float epsilon, int nbPas, Vec3Df & intersec, float *** reglage){
	
	//On retrouve le point sur lequel le regard tombe
	intersec = intersection2(regard,im,epsilon,nbPas,reglage);
	if(intersec[0]==-1){
		return false;
	}
	Rayon r2(lumiere,intersec-lumiere);

	return (Vec3Df::distance(intersec,intersection(r2,im,epsilon,nbPas)) < epsilon);
}

float puissanceS(float f){
	float sol = f;
	for (int i=1 ; i < SPECULARITE; i++)
		sol=sol*f;

	return sol;
}

Vec3Df lumiere(Rayon camera, std::vector<Vec3Df> lumieres, std::vector<Vec3Df> couleurs, const Image& relief, const Image& couleur, float*** tableau){
	if(lightingMode==PASCONSTANT){

		Vec3Df coul(0,0,0);
		float poids=0;

		for(int i=0 ; i< lumieres.size() ; i++){
			
			Vec3Df intersec;
			float poids2;
			
			if(!eclairage(camera, lumieres[i],relief,EPSILON,NB_PAS,intersec)){
				if(intersec[0]==-1){
					return Vec3Df(0,0,0);
				}
				poids2 = Vec3Df::distance(lumieres[i],intersec);
				coul=(poids*coul+NOIR*Vec3Df(couleur.getInRealWorld(intersec[0],intersec[1],0),couleur.getInRealWorld(intersec[0],intersec[1],1),couleur.getInRealWorld(intersec[0],intersec[1],2))*poids2)/(poids2+poids);
				poids = poids2+poids;
			}else{
				poids2 = Vec3Df::distance(lumieres[i],intersec);

				float xCol = intersec[0];
				float yCol = intersec[1];
			//std::cout << "x,y=" << x << ","<< y << "|"<< xCol<<","<<yCol<<std::endl;
				Vec3Df coul2 = Vec3Df(couleurs[i][0]*couleur.getInRealWorld(xCol,yCol,0),couleurs[i][1]*couleur.getInRealWorld(xCol,yCol,1),couleurs[i][2]*couleur.getInRealWorld(xCol,yCol,2));
				//float div = 10/(1+poids2);

				//Calcul du Lambertien
				Vec3Df N= normale(relief,xCol,yCol);
				Vec3Df L= lumieres[i]-intersec;
				L.normalize();
				float facteurL = /*div**/(Vec3Df::dotProduct(N,L));
				
				//Calcul de Blinn-Phong
				Vec3Df H = (camera.direction+L);
				H.normalize();
				float facteurBP = /*div**/puissanceS(Vec3Df::dotProduct(H,N));
				
				coul = (poids*coul+poids2*coul2*(facteurL+facteurBP)/2)/(poids+poids2);
				poids= poids+poids2;
				}		
		
		
		}

		return coul;

	}else{
			
		Vec3Df coul(0,0,0);
		float poids=0;

		for(int i=0 ; i< lumieres.size() ; i++){
			
			Vec3Df intersec;
			float poids2;
			
			if(!eclairage2(camera, lumieres[i],relief,EPSILON,NB_PAS,intersec,tableau)){
				if(intersec[0]==-1)
					return Vec3Df(0,0,0);
				poids2 = Vec3Df::distance(lumieres[i],intersec);
				coul=(poids*coul+NOIR*Vec3Df(couleur.getInRealWorld(intersec[0],intersec[1],0),couleur.getInRealWorld(intersec[0],intersec[1],1),couleur.getInRealWorld(intersec[0],intersec[1],2))*poids2)/(poids2+poids);
				poids = poids2+poids;
			}else{
				poids2 = Vec3Df::distance(lumieres[i],intersec);

				float xCol = intersec[0];
				float yCol = intersec[1];
			//std::cout << "x,y=" << x << ","<< y << "|"<< xCol<<","<<yCol<<std::endl;
				Vec3Df coul2 = Vec3Df(couleurs[i][0]*couleur.getInRealWorld(xCol,yCol,0),couleurs[i][1]*couleur.getInRealWorld(xCol,yCol,1),couleurs[i][2]*couleur.getInRealWorld(xCol,yCol,2));
				float div = 10/(1+poids2);

				//Calcul du Lambertien
				Vec3Df N= normale(relief,xCol,yCol);
				Vec3Df L= intersec-lumieres[i];
				L.normalize();
				float facteurL = div*(Vec3Df::dotProduct(N,L));

				//Calcul de Blinn-Phong
				Vec3Df H = (camera.direction+L)/2;
				H.normalize();
				float facteurBP = div*puissanceS(Vec3Df::dotProduct(H,N));

				coul = (poids*coul+poids2*coul2*(facteurL+facteurBP)/2)/(poids+poids2);
				poids= poids+poids2;
				}		
		
		
		}

		return coul;
	
	}


/*
void lumiere2(Vec3Df PosCam, Vec3Df PosLum, Vec3Df ColorLum, const Image & relief, const Image & couleur, float x, float y, float epsilon, int nbPas, Vec3Df & OriginalColor, float & poidsCumule, float*** reglage){
	
	Rayon regard(PosCam, Vec3Df(x,y,0)-PosCam);
	
	Vec3Df intersec;

	if(!eclairage2(regard, PosLum, relief, epsilon,nbPas,intersec,reglage)){
		//std::cout << "x,y=" << x << ","<< y << "pas éclairé" << std::endl;
		return;
	}
	float poids2 = Vec3Df::distance(PosLum,intersec);

	float xCol = intersec[0];
	float yCol = intersec[1];
	//std::cout << "x,y=" << x << ","<< y << "|"<< xCol<<","<<yCol<<std::endl;
	Vec3Df coul = Vec3Df(ColorLum[0]*couleur.getInRealWorld(xCol,yCol,0),ColorLum[1]*couleur.getInRealWorld(xCol,yCol,1),ColorLum[2]*couleur.getInRealWorld(xCol,yCol,2));

	float div = 5/(1+poids2*poids2);

	OriginalColor = (poidsCumule*OriginalColor+coul*div*poids2)/(poids2+poidsCumule);
	poidsCumule = poids2+poidsCumule;
}


void lumiere(Vec3Df PosCam, Vec3Df PosLum, Vec3Df ColorLum, const Image & relief, const Image & couleur, float x, float y, float epsilon, int nbPas, Vec3Df & OriginalColor, float & poidsCumule){
	
	Rayon regard(PosCam, Vec3Df(x,y,0)-PosCam);
	
	Vec3Df intersec;

	if(!eclairage(regard, PosLum, relief, epsilon,nbPas,intersec)){
		//std::cout << "x,y=" << x << ","<< y << "pas éclairé" << std::endl;
		return;
	}
	float poids2 = Vec3Df::distance(PosLum,intersec);

	float xCol = intersec[0];
	float yCol = intersec[1];
	//std::cout << "x,y=" << x << ","<< y << "|"<< xCol<<","<<yCol<<std::endl;
	Vec3Df coul = Vec3Df(ColorLum[0]*couleur.getInRealWorld(xCol,yCol,0),ColorLum[1]*couleur.getInRealWorld(xCol,yCol,1),ColorLum[2]*couleur.getInRealWorld(xCol,yCol,2));

	float div = 5/(1+poids2*poids2);

	OriginalColor = (poidsCumule*OriginalColor+coul*div*poids2)/(poids2+poidsCumule);
	poidsCumule = poids2+poidsCumule;
}

void lumiere3(Vec3Df PosCam, Vec3Df PosLum, Vec3Df ColorLum, const Image & relief, const Image & couleur, float x, float y, float epsilon, int nbPas, Vec3Df & OriginalColor, float & poidsCumule){
	
	Rayon regard(PosCam, Vec3Df(x,y,0)-PosCam);
	
	Vec3Df intersec;

	float poids2 = Vec3Df::distance(PosLum,intersec);
	float petiteps=0.1;

	if(!eclairage(regard, PosLum, relief, epsilon,nbPas,intersec)){
		OriginalColor=(poidsCumule*OriginalColor+petiteps*Vec3Df(couleur.getInRealWorld(intersec[0],intersec[1],0),couleur.getInRealWorld(intersec[0],intersec[1],1),couleur.getInRealWorld(intersec[0],intersec[1],2))*poids2)/(poids2+poidsCumule);
		poidsCumule = poids2+poidsCumule;
	}
	else{
		float xCol = intersec[0];
		float yCol = intersec[1];
		//std::cout << "x,y=" << x << ","<< y << "|"<< xCol<<","<<yCol<<std::endl;
		Vec3Df coul = Vec3Df(ColorLum[0]*couleur.getInRealWorld(xCol,yCol,0),ColorLum[1]*couleur.getInRealWorld(xCol,yCol,1),ColorLum[2]*couleur.getInRealWorld(xCol,yCol,2));

	float div = 10/(1+poids2);

	Vec3Df N= normale(relief,xCol,yCol);
	Vec3Df L= PosLum-Vec3Df(x,y,0);
	L.normalize();

	OriginalColor = (poidsCumule*OriginalColor+coul*div*poids2*(Vec3Df::dotProduct(N,L)))/(poids2+poidsCumule);
	poidsCumule = poids2+poidsCumule;
	}
	*/
	}
	
