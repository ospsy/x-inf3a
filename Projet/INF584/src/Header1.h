
class Rayon{
	
public :
	Vec3Df origine;
	Vec3Df direction;


	Rayon(Vec3Df o, Vec3Df d){
		origine = o;
		direction = d;
	}

	

};

bool estSous(const Vec3Df courant, ImageBW & im){
	return im(courant[0],courant[1])> courant[2];
}


//La valeur d'epsilon est celle du pas pour avancer
Vec3Df intersection(const Rayon r, const ImageBW & im, float epsilon, int nbPas){

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

bool eclairage(Vec3Df regard, Vec3Df lumiere, const ImageBW & im, float x, float y, float epsilon, int nbPas){

	Rayon r1(regard,Vec3Df(x,y,im(x,y))-regard);
	Rayon r2(lumiere,Vec3Df(x,y,im(x,y))-lumiere);

	return (distance(intersection(r1,im,epsilon,nbPas),intersection(r2,im,epsilon,nbPas)) < epsilon);
}


Vec3Df 
