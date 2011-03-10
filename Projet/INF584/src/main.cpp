#ifdef WIN32
#include <Windows.h>
#endif
#include <GL/glut.h>
#include <stdlib.h>
#include <math.h>
#include <assert.h>
#include "traqueboule.h"
#include "loadppm.h"
#include "lighting.h"

using namespace std;
void computeLighting();
void dealWithUserInput(int x, int y);
Image img;
Image tex;
unsigned int W_fen = 800;  // largeur fenetre
unsigned int H_fen = 500;  // hauteur fenetre
int nbPas = 10; // précision de la dichotomie
float epsilon = 0.01; // largeur du pas



//couleur du d�cors
float BackgroundColor[]={0,0,0};
// Diff�rents modes d'affichage
enum Mode{ MESH=0,TEXTURE};
int NB_MODES=2;
Mode mode=MESH;

GLuint idTexture;

//selon la vitesse de l'ordinateur on recalcule l'eclairage a chaque frame.
//toggle avec la touche "U" pendant l'execution du programme.
//"u" en miniscule effectue un seul calcul pour la configuration courrante.
bool updateAlways=true;

//devient plus int�ressant plus tard dans l'exercice.
//pour le moment, utilisez toujours LightPos[0]
std::vector<Vec3Df> LightPos;
//la lumi�re courrante (au d�but ceci vaut toujours 0)
unsigned int SelectedLight=0;
//pas encore necessaire, plus tard, ca va servir pour avoir des lumi�res color�es
std::vector<Vec3Df> LightColor;



//la position de la cam�ra COURRANTE!
Vec3Df CamPos = Vec3Df(0.0f,0.0f,-4.0f);



void remplissagTex(){
	std::cout << "Debut remplissage" << std::endl;
	
	for (int i=0 ; i < tex.sizeX ; i++)
		for(int j=0 ; j < tex.sizeY;j++){
		
			Vec3Df Couleur(0,0,0);
			float poids=0;

			float eni=i;
			float enj=j;
			eni=eni/tex.sizeX;
			enj=enj/tex.sizeY;

			for(int it = 0 ; it < LightPos.size() ; it++){
				lumiere(CamPos,LightPos[it],LightColor[it],img,eni,enj,epsilon,nbPas,Couleur,poids);
			}
			std::cout << Couleur << std::endl;
			tex.set(i,j,Couleur);
			
		}

		std::cout << "Fin remplissage" << std::endl;
}




//pour g�rer les interactions avec l'utilisateur
void userInteraction(const Vec3Df & selectedPos, const Vec3Df & selectedNormal, int selectedIndex, Vec3Df origin, Vec3Df direction)
{
	if(selectedIndex<0)
		return;
	//LightPos[0]=selectedPos-direction/direction.getSquaredLength();
	direction.normalize();
	LightPos[0]-=Vec3Df::dotProduct(direction,selectedPos+LightPos[0])*direction;
	LightPos[0].normalize();
	LightPos[0]*=1.5;
	std::cout << LightPos[0] << std::endl;
}



// prise en compte du clavier
//Vous allez ajouter quelques fonctionalites pendant le TP
//ce qui est important pour vous: key contient le caract�re correspondant � la touche appuy� par l'utilisateur

void keyboard(unsigned char key, int x, int y)
{
	printf("key %d pressed at %d,%d\n",key,x,y);
	fflush(stdout);
	if (key>'0'&& key<='7')
	{
		mode=Mode(key-'1');
		computeLighting();
		return;
	}

	switch (key)
	{
	case 'm':
	{
		mode=Mode((mode+1)%NB_MODES);
		return;
	}
	case 'r':
		break;
	case 'R':
		break;
	case 'g':
		break;
	case 'G':
		break;
	case 'b':
		break;
	case 'B':
		break;

		//a pas y toucher!!!

		//ARRETEZ DE LIRE � PARTIR D'ICI!!!
		//________________________________
		//________________________________
		//________________________________
		//________________________________
		//________________________________


	case 'l':
	{
		LightPos[SelectedLight]=getCameraPosition();
		return;
	}
	case 'L':
	{
		LightPos.push_back(getCameraPosition());
		LightColor.push_back(Vec3Df(1,1,1));
		return;
	}
	case '+':
	{
		++SelectedLight;
		if (SelectedLight>=LightPos.size())
			SelectedLight=0;
		return;
	}
	case '-':
	{
		--SelectedLight;
		if (SelectedLight<0)
			SelectedLight=LightPos.size()-1;
		return;
	}
	case 'U':
	{
		updateAlways=!updateAlways;
		return;
	}

	case 'N':
	{
		for (unsigned int i=0; i<MyMesh.vertices.size();++i)
		{
			customData[i]=Vec3Df(0,0,0);
		}
		LightPos.resize(1);
		LightPos[0]=Vec3Df(0,0,3);
		LightColor.resize(1);
		LightColor[0]=Vec3Df(1,1,1);
		SelectedLight=0;
	}

	case 'u':
	{
		//mise a jour de l'eclairage
		computeLighting();
		return;
	}
	}

}



/************************************************************
 * Fonction pour initialiser la scène
 ************************************************************/
void init(const char * fileName){
	img.load(fileName);

	LightPos.resize(1);
	LightPos[0]=Vec3Df(0,0,3);
	LightColor.resize(1);
	LightColor[0]=Vec3Df(1,1,1);
	SelectedLight=0;
	
	tex.resize(10,10);
	glGenTextures(1, &idTexture);
	glBindTexture(GL_TEXTURE_2D, idTexture);
	gluBuild2DMipmaps(GL_TEXTURE_2D, GL_RGB, tex.sizeX, tex.sizeY,
			  GL_RGB, GL_UNSIGNED_BYTE, tex.data);
}



/************************************************************
 * Appel des diff�rentes fonctions de dessin
 ************************************************************/

void dessiner( )
{
	if(mode==MESH){
		int w=img.sizeX;
		int h=img.sizeY;
		for(int y=0;y<h-1;y++){
			for(int x=0;x<w-1;x++){
				glBegin(GL_QUADS);
				glVertex3f((float)x/w,(float)y/h,img(x,y)/255);
				glVertex3f((float)(x+1)/w,(float)y/h,img((x+1),y)/255);
				glVertex3f((float)(x+1)/w,(float)(y+1)/h,img((x+1),(y+1))/255);
				glVertex3f((float)x/w,(float)(y+1)/h,img(x,(y+1))/255);
				glEnd();
			}
		}
	}else{
		remplissagTex();
		gluBuild2DMipmaps(GL_TEXTURE_2D, GL_RGB, tex.sizeX, tex.sizeY,
			  GL_RGB, GL_UNSIGNED_BYTE, tex.data);
		glEnable(GL_TEXTURE_2D);
	    glBindTexture(GL_TEXTURE_2D, idTexture);
		glBegin(GL_QUADS);
	    glTexCoord2f(0,0);
	    glVertex2f(0,0);
	    glTexCoord2f(0,1);
	    glVertex2f(0,1);
	    glTexCoord2f(1,0);
	    glVertex2f(1,1);
	    glTexCoord2f(1,0);
	    glVertex2f(1,0);
	    glEnd();
	    glDisable(GL_TEXTURE_2D);
		mode==MESH;
	}

}




void idle()
{
	CamPos=getCameraPosition();

	if (updateAlways)
		computeLighting();

	glutPostRedisplay();
}

void display(void);
void reshape(int w, int h);
void keyboard(unsigned char key, int x, int y);



/************************************************************
 * Programme principal
 ************************************************************/
int main(int argc, char** argv)
{
	glutInit (&argc, argv);


	// couches du framebuffer utilisees par l'application
	glutInitDisplayMode( GLUT_DOUBLE | GLUT_RGBA | GLUT_DEPTH );

	// position et taille de la fenetre
	glutInitWindowPosition(200, 100);
	glutInitWindowSize(W_fen,H_fen);
	glutCreateWindow(argv[0]);

	// Initialisation du point de vue
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	glTranslatef(0,0,-4);
	tbInitTransform();     // initialisation du point de vue
	tbHelp();                      // affiche l'aide sur la traqueboule

	glDisable( GL_LIGHTING );
	glEnable(GL_COLOR_MATERIAL);
	glEnable(GL_NORMALIZE);

	if(argc == 2){
		init(argv[1]);
	}else{
		init("damier.ppm");
	}

	// cablage des callback
	glutReshapeFunc(reshape);
	glutKeyboardFunc(keyboard);
	glutDisplayFunc(display);
	glutMouseFunc(tbMouseFunc);    // traqueboule utilise la souris
	glutMotionFunc(tbMotionFunc);  // traqueboule utilise la souris
	glutIdleFunc(idle);


	// Details sur le mode de trac�
	glEnable( GL_DEPTH_TEST );            // effectuer le test de profondeur
	glShadeModel(GL_SMOOTH);

	// Effacer tout
	glClearColor (BackgroundColor[0],BackgroundColor[1], BackgroundColor[2], 0.0);
	glClear( GL_COLOR_BUFFER_BIT  | GL_DEPTH_BUFFER_BIT); // la couleur et le z


	// lancement de la boucle principale
	glutMainLoop();

	return 0;  // instruction jamais ex�cut�e
}


/************************************************************
 * Fonctions de gestion opengl � ne pas toucher
 ************************************************************/
// Actions d'affichage
// Ne pas changer
void display(void)
{
	glClear( GL_COLOR_BUFFER_BIT  | GL_DEPTH_BUFFER_BIT); // la couleur et le z

	glLoadIdentity();  // repere camera

	tbVisuTransform(); // origine et orientation de la scene

	dessiner( );

	glutSwapBuffers();
}

// pour changement de taille ou desiconification
void reshape(int w, int h)
{
	glViewport(0, 0, (GLsizei) w, (GLsizei) h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluPerspective (50, (float)w/h, 1, 10);
	glMatrixMode(GL_MODELVIEW);
}

