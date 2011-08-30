/*
    C++ Header retriaval
     Reference in fhead.h
*/
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "fhead.h"

#define NUM_BUFSIZE    40
#define STR_BUFSIZE   100

/*-------- constructor, open , close -----------*/

setfile::setfile()
{
	open_yn = 0;
}

setfile::setfile(const char fname[])
{
	open(fname);
}

int setfile::open(const char fname[])
{
	fp.open(fname);

	if ( fp.is_open() == 0 ){
		open_yn = 0;
		return(0);
	} else {
		open_yn = 1;
		return(1);
	}
}

void setfile::close()
{
	if ( open_yn == 1 ){
		open_yn = 0;
		fp.close();
	}
}

/*-----------  Read one Integer -----------*/

int setfile::get_int()
{
	int  i,ret;
	char str[NUM_BUFSIZE];

	ret = get_string(str,NUM_BUFSIZE);
	if ( ret == EOF ){
		fprintf(stderr,"cannot get_int. EOF.\n");
		exit(1);
	}

	check_int(str);
	sscanf(str,"%d",&i);
	return(i);
}

/*----------  Read one real-numeric-------------*/

float setfile::get_float()
{
	int   ret;
	float f;
	char  str[NUM_BUFSIZE];

	ret = get_string(str,NUM_BUFSIZE);
	if ( ret == EOF ){
		fprintf(stderr,"cannot get_float. EOF.\n");
		exit(1);
	}

	check_double(str);
	sscanf(str,"%f",&f);
	return(f);
}

/*----------- read a double number --------*/

double setfile::get_double()
{
	int    ret;
	double d;
	char   str[NUM_BUFSIZE];

	ret = get_string(str,NUM_BUFSIZE);
	if ( ret == EOF ){
		fprintf(stderr,"cannot get_double. EOF.\n");
		exit(1);
	}

	check_double(str);
	sscanf(str,"%lf",&d);
	return(d);
}

/*----------- C++ read a charactor(String)----------*/

string setfile::get_string()
{
	int    ret;
	string str;
	char   cstr[256];

	ret = get_string(cstr,256);
	if ( ret == EOF ){
		fprintf(stderr,"cannot get_string. EOF.\n");
		exit(1);
	}

	str = cstr;
	return(str);
}

/*-----------Read 1-line   C++ like ----------*/

string setfile::get_line()
{
	int  ret;
	char c_str[STR_BUFSIZE];
	string str;

	ret = my_getline(c_str,STR_BUFSIZE);
	if ( ret == 0 ){
		fprintf(stderr,"cannot get_line. exit.\n");
		exit(1);
	}

	str = c_str;
	return(str);
}

/*----------- read 1-line  C like ----------

    Ã·§ÅEÕ  0 : EOF
            1 : ¿µæÅEÀ∆…§ﬂπ˛§ﬂ
*/

int setfile::get_line(char str[], int maxlen)
{
	int ret;
	ret = my_getline(str,maxlen);
	return(ret);
}

/*----------------- Read 1-line ----------------

     Ã·§ÅEÕ  0  : Reach EOF, can't find any charactor
             1  : Normal terminsation

     return is same as fgets
*/

int setfile::my_getline(char str[], int maxlen)
{
	int i,cha;

	for( i = 0; i < maxlen-1; i++ ){
		cha = fp.get();
		if ( cha == EOF  ) break;
		if ( cha == '\n' ) break;
		str[i] = cha;
	}

	str[i] = '\0';

	// For Windows-fale end/ 0d,0a

	if ( i >= 1 && str[i-1] == '\r' ){
		str[i-1] = '\0';
	}

	if ( cha == EOF ){
		return(0);
	} else {
		return(1);
	}
}


/*-------------------- Find header -----------------------*/

int setfile::find_header(const char keyword[])
/*
	input     keyword
	output   1 : Find header  0 no Header
*/
{
	char char_buf[STR_BUFSIZE];
	char keyword_buf[STR_BUFSIZE];
	int  len,i,ret;

	strcpy(keyword_buf,keyword);
	rm_right_space(keyword_buf);  /* Remove right side space  */

	fp.clear();  // rewind
	fp.seekg(0);

	for(;;){
		ret = my_getline(char_buf,STR_BUFSIZE);
		if ( ret == 0 ) break;
		rm_right_space(char_buf);       /* Remove right side space & sign*/
		len = strlen(char_buf);
		if ( len == 0 ) continue;       /* This line can be delete */
		for ( i = 0 ; i < len ; i++ ){  /*Remove left side spaceØ */
			if ( char_buf[i] != ' ' ) break;
		}
		if ( strcmp(&char_buf[i],keyword_buf)==0){
			return(1);
			break;
		}
	}
	fprintf(stderr,"cannot find header.  key word : |%s|\n",keyword);
	return(0);
}

/*--------------- Find keyword(header) ---------------*/

int setfile::find_word(const char keyword[])
/*
	Input     keyword
	Output   1 : Find header  0 no Header
*/
{
	char char_buf[STR_BUFSIZE];
	int  condition;

	fp.clear();  // rewind
	fp.seekg(0);

	while(1){
		condition = get_string(char_buf,STR_BUFSIZE);
		if ( condition == EOF ) break;
		if ( strcmp(char_buf,keyword)==0){
			return(1);
			break;
		}
	}
    fprintf(stderr,"cannot find word |%s|\n",keyword);
	return(0);
}

/*----------- Read one charactor ------------*/

int setfile::get_string(char str[], int maxlen)

/*	Input     maxlen  maximum length of strings
	Output     str    srings
	return          0 : Normal termination
                  EOF ( -1 ) File end
*/
{
	int  input,count,ret;
	char string;

		/* find a head charactor */
		/* No charactor : space,;,\n,Tab,EOF */

	for(;;){
		input  = fp.get();
		string = (char)input;

		if ( input == EOF ) return(EOF); /* File end  */
		if ( string == '#') {            /* when #, stepout comments */
			ret = skip_comment();
			if ( ret == EOF ) return(EOF) ;
			continue;
		}

		/* Stepout \n , Tab, space */

		if ( string == ' '  || string == '\n' ||
			 string == '\r' || string == '\t' ) continue;

		break;     /*  ∏ª˙ŒÛ§Œ£± ∏ª˙Ã‹§Ú∏´§ƒ§±§ø   */
	}

	str[0] = string;
	count = 1;

		/*  ∏ª˙ŒÛ§ÚºË∆¿§π§ÅE*/
		/*  ∏ª˙ŒÛ§ŒΩ™§ÅEÍ§»§ﬂ§ §π§‚§Œ§œ°¢EOF°¢≤˛π‘°¢;°¢∂ı«Ú°¢•ø•÷ */

	for(;;){

		if ( count >= maxlen-1 ){   /* ∫«¬Á ∏ª˙øÙ§Ú•™°º•–°º */
			while(1){               /* ∆…§Û§«§§§ÅE«√Ê§Œ ∏ª˙ŒÛ§œ */
				input  = fp.get();  /* •π•≠•√•◊§π§ÅE*/
				string = (char)input;
				if ( input == EOF ) break;
				if ( string == ' '  || string == '\n' ||
					 string == '\r' || string == '\t' ) break;
			}
			break;
		}

		input  = fp.get();
		string = (char)input;

		if ( input == EOF ) break;
		if ( string == '#') {
			skip_comment();
			break;
		}

		if ( string == ' '  || string == '\n' ||
			 string == '\r' || string == '\t' ) break;

		str[count] = string;
		count++;
	}
	str[count] = '\0';

	return(0);
}


/* ≤˛π‘•ﬁ°º•Ø§Ú∏´§ƒ§±§ÅEﬁ§«•’•°•§•ÅE›•§•Û•ø§Úø §·§ÅE*/

int setfile::skip_comment()
{
	int  input;
	char string;

	for(;;){
		input  = fp.get();
		string = (char)input;
		if ( input  == EOF ) return(EOF);
		if ( string == '\n') return(0);
	}
}

/*  ∏ª˙ŒÛ§Œ±¶¬¶§Œ∂ı«Ú§Ú∫ÅEÅEπ§ÅE   ≤˛π‘µ≠πÊ§¨§¢§ÅEÅEÁ§œ§Ω§ÅE‚ΩÅE˚¿π§ÅE

    1999.1.20   ≤˛π‘µ≠πÊ§¨§¢§ÅEÅEÁ§œ°¢§Ω§ÅE‚ΩÅE˚¿∑
                •¢•ÅE¥•ÅE∫•‡§Ú¡¥ÃÃ≤˛ƒÅE
*/

void setfile::rm_right_space(char str[])
{
	int i;

	i = strlen(str)-1;
	if ( str[i] == '\n' ) i--;

	while(i>=0){
		if ( str[i] != ' ')  break;
		i--;
	}
	str[i+1] = '\0';
}


/*---------  ∏ª˙ŒÛ§¨¿∞øÙ∑ø§Œ scanf §À§’§µ§ÅE∑§§§´ƒ¥§Ÿ§ÅE-------*/

void setfile::check_int(char str[])
{
	int i,len;

	len = strlen(str);

	for( i=0 ; i<len ; i++ ){
		switch( str[i] ){
			case '0': ; case '1': ; case '2': ; case '3': ; case '4': ;
			case '5': ;	case '6': ;	case '7': ;	case '8': ;	case '9': ;
			case '+': ; case '-':
			break;
		default :
			fprintf(stderr,"check_int error.   str : %s \n",str);
			exit(1);
			break;
		}
	}
}


/*---------  ∏ª˙ŒÛ§¨º¬øÙ∑ø scanf §À§’§µ§ÅE∑§§§´ƒ¥§Ÿ§ÅE-------*/

void setfile::check_double(char str[])
{
	int i,len;

	len = strlen(str);

	for( i=0 ; i<len ; i++ ){
		switch( str[i] ){
			case '0': ; case '1': ; case '2': ; case '3': ; case '4': ;
			case '5': ;	case '6': ;	case '7': ;	case '8': ;	case '9': ;
			case '+': ; case '-': ; case '.': ; case 'e':
			break;
		default :
			fprintf(stderr,"check_double error.   str : %s \n",str);
			exit(1);
			break;
		}
	}
}


/*--------------------- •∆•π•»Õ—•ÅEº•¡•ÅE---------------------

header
#  •≥•·•Û•»§‚ΩÒ§±§ÅE
1012                  # §≥§ÅEœ¿∞øÙ§ŒŒÅE
1.23456789012345   # º¬øÙ§ŒŒÅE §≥§ÅEœ∑ÂÕ˚¿¡§¨¿∏§∏§ÅEœ§∫
1.23456789012345   # º¬øÙ§ŒŒÅE §≥§ÅEœ¬ÁæÊ…◊§´°©
abcdefghi          #  ∏ª˙ŒÛ§Œ∆…§ﬂπ˛§ﬂ
zxc                #  ∏ª˙ŒÛ§Œ∆…§ﬂπ˛§ﬂ
c++ string         #  ∏ª˙ŒÛ§Œ∆…§ﬂπ˛§ﬂ
------

main()
{
	setfile setfile;
	string  str;

	char moji[4],moji2[4];
	int  i;
	float  f;
	double d;

	if ( setfile.open("fhead.cpp") == 0 ){
		fprintf(stderr,"Error.\n");
		exit(1);
	}

	setfile.find_header("header");

	i = setfile.get_int();
	f = setfile.get_float();
	d = setfile.get_double();
	setfile.get_string(moji ,4);
	setfile.get_string(moji2,4);
	str = setfile.get_string();

	printf("<---%d---%12.10f---%12.10f--->\n",i,f,d);
	printf("<---%s---%s--->\n",moji,moji2);

	cout << "<---" << str << "--->" << endl;
}

*/
