import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

class Source{
    static long [] proba = {
        1, 1, 1, 1, 1, 1, 1, 1, 
	1, 1, 1688, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1, 
	1, 1, 1, 1, 1, 1, 1, 1,
        10402, 377, 22, 1, 1, 1, 1, 662,
        2, 3, 1, 1, 1002, 552, 936, 2,
        1, 3, 1, 1, 1, 1, 1, 1, 
	1, 3, 47, 123, 4, 2, 4, 131,
        1, 439, 81, 485, 157, 549, 25, 92,
	100, 317, 99, 1, 216, 189, 344, 296,
        120, 67, 403, 170, 199, 189, 85, 2,
        76, 113, 3, 1, 1, 1, 1, 1,
        1, 3493, 430, 1187, 1655, 6617, 452, 455,
	427, 3154, 250, 3, 2284, 1240, 	3291, 2547, 
	1102, 517, 2963, 3608, 3126, 3047, 	813, 1, 
	211, 150, 148, 1, 1, 1, 	1, 1, 
	1, 1, 1, 1, 1, 1, 	1, 1, 
	1, 1, 1, 1, 1, 1, 	1, 1, 
	1, 1, 1, 1, 1, 1, 	1, 1, 
	1, 1, 1, 1, 1, 1, 	1, 1, 
	1, 1, 1, 1, 1, 1, 	1, 1, 
	1, 1, 1, 35, 1, 1, 	1, 1, 
	1, 1, 1, 1, 1, 1,	1, 1, 
	1, 1, 1, 32, 1, 1,	1, 1, 
	4, 1, 1, 1, 1, 1,	1, 1, 
	5, 7, 1, 1, 1, 1,	1, 1, 
	1, 1, 1, 1, 1, 1,	1, 1, 
	1, 1, 1, 1, 1, 1,	1, 1, 
	213, 1, 39, 1, 1, 1,	1, 24,
	172, 682, 99, 1, 1, 1,	33, 5, 
	1, 1, 1, 1, 29, 1, 1,	1, 
	1, 16, 1, 19, 1, 1, 1, 1    };

    static int M = 65536;
    static int M2 ;
    static int M4 ;
    static int M34;
    static int precision;
    static int masque;
    
    static int Q = 256;

    static long [] S ;

    static void initialise(){

	S = new long[Q];

	for ( int i = 0 ; i < Q; ++i ) {
	    proba [i] = 4 * proba[i];

	    S[i] = 0;
	    for ( int j = 0 ; j < i ; ++ j) S[i] = S[i] + proba [j];
	}

	M = 4 *M;
	M2 = M/2;
	M4 = M/4;
	M34 = M2 + M4;

	for (precision = 0 ; (1<<precision) < M; ++ precision);

	masque = ( 1<<precision )-1;
    }

    static int my_chercher1 ( long r, long a, long b){
	double delta = b - a;
	double ra = (r-a)*M;
	int i = 0;

	while ( (i+1<256) && ( (S[i+1] * delta) <= ra)){
	    ++i;
	}

	return i;		    
    }

    static int my_chercher0 (long r, long a, long b) {
	double delta = b - a;
	double rd = r;
	double Md = M;
	rd = (rd-a)*Md / delta;

	int i = 0;

	while ( i+1 < 256 && (S[i+1] /rd )<= 1) {
	    ++ i;
	}

	return i;
    }

    static int my_chercher(int r, int a, int b) {
	long delta = b - a;

	long rd = ((long)(r -a)) * M ;

	int i = 0;

	while ( (i+1 < 256) && ( (S[i+1] * delta) <= rd) ) {
	    ++ i;
	}

	return i;
    }
    
    public static void main(String[] args){
    	if(args.length==0){
    		System.out.println("Pas de fichier source.");
    		System.exit(-1);
    	}
    	String fileName=args[0];
    	FileInputStream streami =null;
    	try {
    		streami=new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Fichier introuvable");
			System.exit(-1);
		}
		//initialisation
		initialise();
		//codage
		try {
			code(streami);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Erreur à la lecture du fichier, fonction code()");
		}
    }

	private static void code(FileInputStream streami) throws IOException {
		int i;
		int a=0;
		int b=M;
		int k=0;
		//on lit les caractères un à un
		while((i=streami.read())!=-1){
			assert(i>=0 && i<256);
			//modification de l'intervalle
			int a1=(int) (a+S[i]*(b-a)/M);
			int b1=(int) (a+(S[i]+proba[i])*(b-a)/M);
			a=a1;
			b=b1;
			//reduction
			while( (M/4<=a && b<=3*M/4) || (b<=M/2) || (a>=M/2)){
				if(M/4<=a && b<=3*M/4){
					k++;
					a=2*a-M/2;
					b=2*b-M/2;
				}else if(b<=M/2){
					a*=2;
					b*=2;
					System.out.print("0");
					for(;k>0;k--)
						System.out.print("1");
					assert(k==0);
				}else if(a>=M/2){
					a=2*a-M;
					b=2*b-M;
					System.out.print("1");
					for(;k>0;k--)
						System.out.print("0");
					assert(k==0);
				}
			}
		}
		//quand tout a été lu
		if(a<M/4){
			System.out.print("0");
			for(;k>=0;k--)
				System.out.print("1");
			assert(k==0);
		}else{
			System.out.print("1");
			for(;k>=0;k--)
				System.out.print("0");
			assert(k==0);
		}
	}
}