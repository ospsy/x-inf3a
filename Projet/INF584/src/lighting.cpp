/*
 * lighting.cpp
 *
 *  Created on: 10 mars 2011
 *      Author: benoit
 */

#include "lighting.h"
float eps = 0.001;

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


float tangente(float x, float y, float theta, float sens, float pasX, float pasY, const Image & img){
	
	return (img.getInRealWorld(x+sens*pasX,y+sens*pasY)-img.getInRealWorld(x,y))/eps;

}

bool Negal(float tangente1,float tangente2, float ToutPetit){
	
	return (tangente1-tangente2 > -ToutPetit && tangente1-tangente2 < ToutPetit); 

}

float safetyRadius(float x, float y, float theta, const Image & img){
	
	//La valeur de la norme du pas considéré ici : nous pouvons nous permettre une valeur très inférieure à celui utilisé dans la version simpliste du programme
	float pasX=(float) (eps*cos((double)theta));
	float pasY=(float) (eps*sin((double)theta));
	
	//courantD et courantG vont nous permettre de remonter le long de la courbe suivant le plan défini par theta
	Vec3Df courantD(x,y,img.getInRealWorld(x,y));
	Vec3Df courantG(x,y,img.getInRealWorld(x,y));
	
	float tangenteD = tangente(courantD[0],courantD[1],theta,1,pasX,pasY,img);
	float tangenteG = tangente(courantG[0],courantG[1],theta,-1,pasX,pasY,img);
	
	float tailleCase = 1; /// A TROUVER
	float ToutPetit = 0.0001;

	bool SolutionTrouvee= false;
	bool SolutionPartielle = false;
	//Si GD=0, on checke à droite, sinon à gauche
	int GD=0;
	Vec3Df sol;
	
	while(!SolutionTrouvee){
		if(GD==0){
		
			//N'oublions pas que nous allons désormais vers la gauche
			Vec3Df pasD(pasX/eps,pasY/eps,tangenteD);
			pasD.normalize();
			pasD*=-tailleCase;

			Vec3Df courantDt= courantD+pasD;
			while(!estSous(courantDt,img))
				courantDt+=pasD;
			
			float T = tangente(courantDt[0],courantDt[1],theta,1,pasX,pasY,img);
			if( Negal(T,tangenteD,ToutPetit)){
				sol = courantDt;
				SolutionPartielle= true;
			}		
			GD=1;

		}else{
		
			Vec3Df pasG(-pasX/eps,-pasY/eps,tangenteG);
			pasG.normalize();
			pasG*=-tailleCase;

			Vec3Df courantGt= courantG+pasG;
			while(!estSous(courantGt,img))
				courantGt+=pasG;
			
			float T = tangente(courantGt[0],courantGt[1],theta,-1,pasX,pasY,img);
			if( Negal(T,tangenteD,ToutPetit)){
				if(SolutionPartielle){
					if(Vec3Df::distance(sol,Vec3Df(x,y,img.getInRealWorld(x,y))) < Vec3Df::distance(courantGt,Vec3Df(x,y,img.getInRealWorld(x,y)))){
						SolutionTrouvee=true;
					}else{
						sol = courantGt;
						SolutionTrouvee= true;
					}
			}else{
				if(SolutionPartielle)
					SolutionTrouvee = true;
			}
			}

			courantG[0]+=pasX;
			courantG[1]+=pasY;

			courantD[0]-=pasX;
			courantD[1]-=pasY;

			GD=0;
			
	}


	}


	return (float) sqrt((double)((sol[0]-x)*(sol[0]-x)+(sol[1]-y)*(sol[1]-y)));

}

