
class Rayon{
	
public :
	Vec3Df origine;
	Vec3Df direction;


	Rayon(Vec3Df o, Vec3Df d){
		origine = o;
		direction = d;
	}

	

};

bool estSous(const Vec3Df courant, Image & im){
	return im(courant[0],courant[1])> courant[2];
}


//La valeur d'epsilon est celle du pas pour avancer
Vec3Df intersection(const Rayon r, const Image & im, float epsilon, int nbPas){

	int nbPas = 10;

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

bool eclairage(Vec3Df regard, Vec3Df lumiere, const Image & im, float x, float y, float epsilon, int nbPas){

	Rayon r1(regard,Vec3Df(x,y,im(x,y))-regard);
	Rayon r2(lumiere,Vec3Df(x,y,im(x,y))-lumiere);

	return (distance(intersection(r1,im,epsilon,nbPas),intersection(r2,im,epsilon,nbPas)) < epsilon);
}


Vec3Df lumiere(Vec3Df PosCam, Vec3Df PosLum, Vec3Df ColorLum, const Image & im, float x, float y, float epsilon, int nbPas, Vec3Df OriginalColor, float & poidsCumule){

	if(!eclairage(PosCam, PosLum, im, x,y,epsilon,nbPas))
		return OriginalColor;

	Rayon r(PosCam,Vec3Df(x,y,im(x,y))-camPos);
	Vec3Df inter = intersection(r,im,epsilon,nbPas);

	float poids2 = Vec3Df:: distance(PosLum,inter);
	Vec3Df coul = Vec3Df(ColorLum[0]*im(x,y,0),ColorLum[1]*im(x,y,1),ColorLum[2]*im(x,y,2));

	Vec3Df solution(poidsCumule*Original+coul*poids2)/(poids2+poidsCumule);
	poidsCumule = poids2+poidsCumule;

	return solution;
}
