/*
 * safetyRadius.cpp
 *
 *  Created on: 13 mars 2011
 *      Author: benoit
 */
#include "safetyRadius.h"
#include "image.h"

//Norme du pas pour le calcul du safetyRadius
#define NORME_PAS 0.001
//Ben c'est Pi quoi...
#define PI 3.1415
//Valeur minimale du SafetyRadius
#define MINIMAL_RADIUS 0.002




float Tangente(float x, float y, float theta, float sens, float pasX, float pasY, const Image & img){

	return (img.getInRealWorld(x+sens*pasX,y+sens*pasY)-img.getInRealWorld(x,y))/(sens*NORME_PAS);

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

bool dehors(const Image& img, Vec3Df vec){
	return ((vec[0]>1 || vec[0]<0)||(vec[1]>1 || vec[1]<0));
}

Vec3Df opp(Vec3Df courant, const Image & img, Vec3Df pas){

	Vec3Df sol = courant+pas;
	while(!img.estSous(sol) && !dehors(img,sol))
		sol+=pas;

	return sol;

}

float safetyRadius(float x, float y, float theta, const Image & img){

	//La valeur de la norme du pas considéré ici : nous pouvons nous permettre une valeur très inférieure à celui utilisé dans la version simpliste du programme
	float pasX=(float) (NORME_PAS*cos((double)theta));
	float pasY=(float) (NORME_PAS*sin((double)theta));

	//courantD et courantG vont nous permettre de remonter le long de la courbe suivant le plan défini par theta
	Vec3Df courantD(x,y,0);
	Vec3Df courantG(x,y,0);


	float tailleCase = min(1/((float) img.sizeX),1/((float) img.sizeY));

	float ToutPetit = 0.001;

	bool SolutionTrouvee= false;
	//bool SolutionPartielle = false;

	bool SolutionPartielleG = false;
	bool SolutionPartielleD = false;
	float rG= 100;
	float rD= 100;

		//ce pas va "vers la droite" (il est utilisé pour les points "à gauche" du point courant)
		Vec3Df pasG(-pasX,-pasY,0);
		pasG.normalize();
		pasG*=-tailleCase;

		//ce pas va "vers la droite" (il est utilisé pour les points "à droite" du point courant)
		Vec3Df pasD(pasX,pasY,0);
		pasD.normalize();
		pasD*=-tailleCase;

		bool Problem= false;

	while(!(SolutionTrouvee||Problem)){

		//On calcule les tangente aux deux points courants ainsi que la pas qui va nous permettre d'avancer suivant la tangente
		// ATTENTION au sens de parcours !
		float tangenteD = Tangente(courantD[0],courantD[1],theta,1,pasX,pasY,img);
		float tangenteG = Tangente(courantG[0],courantG[1],theta,-1,pasX,pasY,img);

		if(dehors(img,courantG)||dehors(img,courantD)){
			Problem= true;
		}
		else{

		if(SolutionPartielleG){

			if(SolutionPartielleD){

				float rPartiel = min(rG,rD);

				while(Vec3Df::distance(courantG,Vec3Df(x,y,0))< rPartiel){
					Vec3Df oppose = opp(courantG,img,pasG);
					estCompatible(tangenteG,x,y,courantG[0],courantG[1],img,oppose[0],oppose[1],theta,-1,pasX,pasY,ToutPetit,rPartiel);
					courantG+=pasD;
				}

				while(Vec3Df::distance(courantD,Vec3Df(x,y,0))< rPartiel){
					Vec3Df oppose = opp(courantD,img,pasD);
					estCompatible(tangenteD,x,y,courantD[0],courantD[1],img,oppose[0],oppose[1],theta,1,pasX,pasY,ToutPetit,rPartiel);
					courantD+=pasG;
				}

				return rPartiel;

			}else{

				while(Vec3Df::distance(courantD,Vec3Df(x,y,0))< rG){
					Vec3Df oppose = opp(courantD,img,pasD);
					estCompatible(tangenteD,x,y,courantD[0],courantD[1],img,oppose[0],oppose[1],theta,1,pasX,pasY,ToutPetit,rG);
					courantD+=pasG;
				}

				return rG;
			}


		}else{

			if(SolutionPartielleD){

				while(Vec3Df::distance(courantG,Vec3Df(x,y,0))< rD){
					Vec3Df oppose = opp(courantG,img,pasG);
					estCompatible(tangenteG,x,y,courantG[0],courantG[1],img,oppose[0],oppose[1],theta,-1,pasX,pasY,ToutPetit,rD);
					courantG+=pasD;
				}

				return rD;

			}else{

				Vec3Df opposeG = opp(courantG,img,pasG);
				if(estCompatible(tangenteG,x,y,courantG[0],courantG[1],img,opposeG[0],opposeG[1],theta,-1,pasX,pasY,ToutPetit,rG))
					SolutionPartielleG=true;
				else
					courantG+=pasD;

				Vec3Df opposeD = opp(courantD,img,pasD);
				if(estCompatible(tangenteD,x,y,courantD[0],courantD[1],img,opposeD[0],opposeD[1],theta,1,pasX,pasY,ToutPetit,rD))
					SolutionPartielleD=true;
				else
					courantD+=pasG;

			}

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


	return MINIMAL_RADIUS;

}


float*** precomputation(const Image & I, int P){

	int N = I.sizeX;
	int M = I.sizeY;

	float min=100,max=0;
	float*** solution = new float** [N];
	for (int i=0 ; i < N ; i++){
		solution [i]= new float* [N];
		std::cout << "Precomputation : " << (float)i/N*100 <<"%"<< std::endl;
		for (int j=0 ; j < N ; j++){
			solution [i][j]= new float [M];

			for (int k = 0 ; k < P ; k++){
				solution [i][j][k]=safetyRadius(1/((float)N)*i,1/((float) M)*j,PI/((float)P)*k,I);
				if(solution [i][j][k]>max) max=solution [i][j][k];
				if(solution [i][j][k]<min) min=solution [i][j][k];
			}

		}

	}
	std::cout << "Minimum : "<< min << std::endl;
	std::cout << "Maximum : "<< max << std::endl;
	return solution;
}
