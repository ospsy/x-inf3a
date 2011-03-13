#ifdef WIN32
#include <Windows.h>
#endif
#include <GL/glut.h>
#include <stdlib.h>
#include <math.h>
#include <assert.h>
#include "traqueboule.h"
#include "image.h"
#include "lighting.h"
#include "Vec3D.h"
#include <vector>

using namespace std;
void dealWithUserInput(int x, int y);
Image relief;
Image tex;
Image couleur;
unsigned int W_fen = 800;  // largeur fenetre
unsigned int H_fen = 500;  // hauteur fenetre
int nbPas = 10; // précision de la dichotomie
float epsilon = 0.01; // largeur du pas


float *** tableau;

//couleur du d�cors
float BackgroundColor[]={0,0,0};
// Diff�rents modes d'affichage
enum Mode{ MESH=0,TEXTURE};
int NB_MODES=2;
Mode mode=MESH;

GLuint idCalculatedTexture;
GLuint idTextureCouleur;

//selon la vitesse de l'ordinateur on recalcule l'eclairage a chaque frame.
//toggle avec la touche "U" pendant l'execution du programme.
//"u" en miniscule effectue un seul calcul pour la configuration courrante.
bool updateAlways=true;

//pour le moment, utilisez toujours LightPos[0]
std::vector<Vec3Df> LightPos;
//la lumi�re courrante (au d�but ceci vaut toujours 0)
unsigned int SelectedLight=0;
//pas encore necessaire, plus tard, ca va servir pour avoir des lumi�res color�es
std::vector<Vec3Df> LightColor;



//la position de la cam�ra COURRANTE!
Vec3Df CamPos = Vec3Df(0.0f,0.0f,-4.0f);

#include "interaction.h"

void remplissageTex(){
	std::cout << "Debut remplissage" << std::endl;

	std::cout << "Caméra " << CamPos << std::endl;
	std::cout << "Lumière " << LightPos[0] << std::endl;
	std::cout << "Lumière couleur " << LightColor[0] << std::endl;

	for (int i=0 ; i < tex.sizeX ; i++)
		for(int j=0 ; j < tex.sizeY;j++){

			float realX = i/(float)(tex.sizeX);
			float realY = j/(float)(tex.sizeY);

			Vec3Df col(0,0,0);
			float poids=0;

			for(unsigned int it = 0 ; it < LightPos.size() ; it++){
				//lumiere(CamPos,LightPos[it],LightColor[it],relief,couleur,realX,realY,epsilon,nbPas,col,poids);
				lumiere2(CamPos,LightPos[it],LightColor[it],relief,couleur,realX,realY,epsilon,nbPas,col,poids,tableau);

			}
			tex.set(i,j,col);

		}

	std::cout << "Fin remplissage" << std::endl;
}



/************************************************************
 * Fonction pour initialiser la scène
 ************************************************************/
int readTab(const std::string& s, int sizeX, int sizeY, int sizeTheta){
	ifstream f(s.c_str());
	if (!f.is_open()){
		cerr << "File not found" << endl;
		return 1;
	}
	tableau = new float** [sizeX];
	for (int i=0 ; i < sizeX ; i++){
		tableau [i]= new float* [sizeY];
		for (int j=0 ; j < sizeY ; j++){
			tableau [i][j]= new float [sizeTheta];
			for (int k = 0 ; k < sizeTheta ; k++){
				f >> tableau[i][j][k] ;
			}
		}
	}
	f.close();
	return 0;
}

int writeTab(const string& s, int sizeX, int sizeY, int sizeTheta){
	ofstream f(s.c_str());
	if (!f.is_open()){
		cerr << "File not found" << endl;
		return 1;
	}
	for (int i=0 ; i < sizeX ; i++){
		for (int j=0 ; j < sizeY ; j++){
			for (int k = 0 ; k < sizeTheta ; k++){
				f << tableau[i][j][k] << " ";
			}
		}
	}
	f.close();
	return 0;
}

void init(const char * fileNameRelief,const char * fileNameCouleur){
	relief.load(fileNameRelief);
	couleur.load(fileNameCouleur);
	LightPos.resize(1);
	LightPos[0]=Vec3Df(0,0,3);
	LightColor.resize(1);
	LightColor[0]=Vec3Df(1,1,1);
	SelectedLight=0;

	tex.resize(100,100);
	int P=100;
	if(readTab("precomputation",relief.sizeX,relief.sizeY,P)!=0){
		tableau = precomputation(relief,P);
		writeTab("precomputation",relief.sizeX,relief.sizeY,P);
		cout << "Sauvegarde terminée" << endl;
	}else{
		cout << "Lecture de precomputation réussie" << endl;
	}
	glGenTextures(1, &idCalculatedTexture);

	glGenTextures(1, &idTextureCouleur);
	glBindTexture(GL_TEXTURE_2D, idTextureCouleur);
	gluBuild2DMipmaps(GL_TEXTURE_2D, GL_RGB, couleur.sizeX, couleur.sizeY,
			GL_RGB, GL_UNSIGNED_BYTE, couleur.data);
}


/************************************************************
 * Appel des diff�rentes fonctions de dessin
 ************************************************************/

void dessiner( )
{
	if(mode==MESH){
		int w=relief.sizeX;
		int h=relief.sizeY;
		for(int y=0;y<h-1;y++){
			for(int x=0;x<w-1;x++){
				glBindTexture(GL_TEXTURE_2D, idTextureCouleur);
				glEnable(GL_TEXTURE_2D);
				glBegin(GL_QUADS);
				glTexCoord2f((float)x/w,(float)y/h);
				glVertex3f((float)x/w,(float)y/h,relief(x,y)/255.);
				glTexCoord2f((float)(x+1)/w,(float)y/h);
				glVertex3f((float)(x+1)/w,(float)y/h,relief((x+1),y)/255.);
				glTexCoord2f((float)(x+1)/w,(float)(y+1)/h);
				glVertex3f((float)(x+1)/w,(float)(y+1)/h,relief((x+1),(y+1))/255.);
				glTexCoord2f((float)x/w,(float)(y+1)/h);
				glVertex3f((float)x/w,(float)(y+1)/h,relief(x,(y+1))/255.);
				glEnd();
			}
		}
	}else{

		remplissageTex();
		glBindTexture(GL_TEXTURE_2D, idCalculatedTexture);
		gluBuild2DMipmaps(GL_TEXTURE_2D, GL_RGB, tex.sizeX, tex.sizeY,
				GL_RGB, GL_UNSIGNED_BYTE, tex.data);
		glEnable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		glTexCoord2f(0,0);
		glVertex2f(0,0);
		glTexCoord2f(0,1);
		glVertex2f(0,1);
		glTexCoord2f(1,1);
		glVertex2f(1,1);
		glTexCoord2f(1,0);
		glVertex2f(1,0);
		glEnd();
		glDisable(GL_TEXTURE_2D);
		//mode=MESH;
	}

}




void idle()
{
	CamPos=getCameraPosition();

	if (updateAlways)
		//computeLighting();

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

	if(argc == 3){
		init(argv[1],argv[2]);
	}else{
		init("relief.ppm","couleur.ppm");
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

