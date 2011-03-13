/*
 * lighting.cpp
 *
 *  Created on: 10 mars 2011
 *      Author: benoit
 */

#include "lighting.h"
#include <math.h>

float eps = 0.001;

float PI = 3.1415;
float *** reglage;
float MaxLong,MaxLarg;

Vec3Df intersection2(const Rayon r, const Image & im, float epsilon, int nbPas){
	Vec3Df courant = r.origine;
	
	while(! estSous(courant,im)){
		int i = (int) (courant[0]/MaxLong);
		int j = (int) (courant[1]/MaxLarg);
		int k = (int) (atan((double) (courant[1]/courant[0]))/PI);

		courant+= (r.direction)*reglage[i][j][k];
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
	//intersec = intersection2(regard,im,epsilon,nbPas);
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

///// CALCUL DU SAFETY RADIUS



float Tangente(float x, float y, float theta, float sens, float pasX, float pasY, const Image & img){
	
	return (img.getInRealWorld(x+sens*pasX,y+sens*pasY)-img.getInRealWorld(x,y))/eps;

}

bool Negal(float tangente1,float tangente2, float ToutPetit){
	
	return (tangente1-tangente2 > -ToutPetit && tangente1-tangente2 < ToutPetit); 

}


bool estCompatible(float tangente, float xOr, float yOr,float xTest,float yTest, const Image& img, float x, float y, float theta, float sens, float pasX, float pasY, float ToutPetit, float & r){

	float tan = Tangente(x,y,theta,sens,pasX,pasY,img);
	
	if (Negal(tan,tangente,ToutPetit)){
		r = min((float) min(sqrt((double)((xOr-x)*(xOr-x)+(yOr-y)*(yOr-y))),sqrt((double)((xTest-x)*(xTest-x)+(yTest-y)*(yTest-y)))),r);
		return true;
	}
	return false;
}

Vec3Df opp(Vec3Df courant, const Image & img, Vec3Df pas){

	Vec3Df sol = courant+pas;
	while(!estSous(sol,img))
		sol+=pas;

	return sol;

}

float safetyRadius(float x, float y, float theta, const Image & img){
	
	//La valeur de la norme du pas considéré ici : nous pouvons nous permettre une valeur très inférieure à celui utilisé dans la version simpliste du programme
	float pasX=(float) (eps*cos((double)theta));
	float pasY=(float) (eps*sin((double)theta));
	
	//courantD et courantG vont nous permettre de remonter le long de la courbe suivant le plan défini par theta
	Vec3Df courantD(x,y,0);
	Vec3Df courantG(x,y,0);
	
		
	float tailleCase = 1; /// A TROUVER
	float ToutPetit = 0.0001;

	bool SolutionTrouvee= false;
	//bool SolutionPartielle = false;

	bool SolutionPartielleG = false;
	bool SolutionPartielleD = false;
	float rG= 100;
	float rD= 100;

	while(!SolutionTrouvee){

		//On calcule les tangente aux deux points courants ainsi que la pas qui va nous permettre d'avancer suivant la tangente
		// ATTENTION au sens de parcours !
		float tangenteD = Tangente(courantD[0],courantD[1],theta,1,pasX,pasY,img);
		float tangenteG = Tangente(courantG[0],courantG[1],theta,-1,pasX,pasY,img);

		Vec3Df pasG(-pasX/eps,-pasY/eps,tangenteG);
		pasG.normalize();
		pasG*=-tailleCase;

		Vec3Df pasD(pasX/eps,pasY/eps,tangenteD);
		pasD.normalize();
		pasD*=-tailleCase;

		if(SolutionPartielleG){
		
			if(SolutionPartielleD){

				float rPartiel = min(rG,rD);
				
				while(Vec3Df::distance(courantG,Vec3Df(x,y,0))< rPartiel){
					Vec3Df oppose = opp(courantG,img,pasG);
					estCompatible(tangenteG,x,y,courantG[0],courantG[1],img,oppose[0],oppose[1],theta,-1,pasX,pasY,ToutPetit,rPartiel);
					courantG+=pasG;
				}

				while(Vec3Df::distance(courantD,Vec3Df(x,y,0))< rPartiel){
					Vec3Df oppose = opp(courantD,img,pasD);
					estCompatible(tangenteD,x,y,courantD[0],courantD[1],img,oppose[0],oppose[1],theta,1,pasX,pasY,ToutPetit,rPartiel);
					courantD+=pasD;
				}

				return rPartiel;

			}else{
			
				while(Vec3Df::distance(courantD,Vec3Df(x,y,0))< rG){
					Vec3Df oppose = opp(courantD,img,pasD);
					estCompatible(tangenteD,x,y,courantD[0],courantD[1],img,oppose[0],oppose[1],theta,1,pasX,pasY,ToutPetit,rG);
					courantD+=pasD;
				}

				return rG;
			}
		
		
		}else{
		
			if(SolutionPartielleD){
			
				while(Vec3Df::distance(courantG,Vec3Df(x,y,0))< rD){
					Vec3Df oppose = opp(courantG,img,pasG);
					estCompatible(tangenteG,x,y,courantG[0],courantG[1],img,oppose[0],oppose[1],theta,-1,pasX,pasY,ToutPetit,rD);
					courantG+=pasG;
				}

				return rD;

			}else{
				
				Vec3Df opposeG = opp(courantG,img,pasG);
				if(estCompatible(tangenteG,x,y,courantG[0],courantG[1],img,opposeG[0],opposeG[1],theta,-1,pasX,pasY,ToutPetit,rG))
					SolutionPartielleG=true;
				else
					courantG+=pasG;

				Vec3Df opposeD = opp(courantD,img,pasD);
				if(estCompatible(tangenteD,x,y,courantD[0],courantD[1],img,opposeD[0],opposeD[1],theta,1,pasX,pasY,ToutPetit,rD))
					SolutionPartielleD=true;
				else
					courantD+=pasD;
			
			}
		
		}
	
	
	
	
	}



	//Si GD=0, on checke à droite, sinon à gauche
	//int GD=0;
	//Vec3Df sol;




	/*
	while(!SolutionTrouvee){
		if(GD==0){
		
			//N'oublions pas que nous allons désormais vers la gauche
			Vec3Df pasD(pasX/eps,pasY/eps,tangenteD);
			pasD.normalize();
			pasD*=-tailleCase;

			Vec3Df courantDt= courantD+pasD;
			while(!estSous(courantDt,img))
				courantDt+=pasD;

			// souci : et si ça sort du cadre ???
			
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
	*/


	return min(rD,rG);

}


float*** precomputation(const Image & I){

	int N=255;
	float*** solution = new float** [N];
	for (int i=0 ; i < N ; i++){
		solution [i]= new float* [N];

		for (int j=0 ; j < N ; j++){
			solution [i][j]= new float [N];

			for (int k = 0 ; k < N ; k++){
				solution [i][j][k]=safetyRadius(MaxLong/N*i,MaxLarg/N*j,PI/N*k,I);
			
			}

		}
	
	}
	
	return solution;
}
