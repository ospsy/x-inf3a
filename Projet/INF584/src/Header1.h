
class Rayon{
	
public :
	Vec3Df origine;
	Vec3Df direction;


	Rayon(Vec3Df o, Vec3Df d){
		origine = o;
		direction = d;
	}

	

};

bool estSous(const Vec3Df courant, const Image & im){
	return im(courant[0],courant[1])> courant[2];
}


//La valeur d'epsilon est celle du pas pour avancer
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


//regard représente le rayon partant du regard, lumiere la position de la lumiere
////On retourne true ssi le point vu par notre regard est éclairé
bool eclairage(Rayon regard, Vec3Df lumiere, const Image & im, float epsilon, int nbPas, Vec3Df & intersec){

	//On retrouve le point sur lequel le regard tombe
	intersec = intersection(regard,im,epsilon,nbPas);
	Rayon r2(lumiere,intersec-lumiere);

	return (Vec3Df::distance(intersec,intersection(r2,im,epsilon,nbPas)) < epsilon);
}

//x et y sont les coordonnées sur lesquelles on veut projet notre point
// OriginalColor et poidsCumule nous informent sur la Color pour le moment allouée au point tex(x,y) et le poids total des distances dans ce calcul
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

/*
Vec3Df tangente(float x, float y ,float theta, const Image & I1){


}


float safetyRadius(float x, float y, const Image& I1, float theta){


}*/