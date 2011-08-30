
#ifndef __fhead_h__
#define __fhead_h__

/*------------ ¥Ø¥Ã¥À¡¼¸¡º÷¥·¥¹¥Æ¥E  C++ ÈÇ ------------

¡E»È¤¤Êı ¡E

	setfile fobj;

	fobj.open("fname");          ¥Õ¥¡¥¤¥Eò¥ª¡¼¥×¥ó¤¹¤E
	
	fobj.find_header("header");  ¥Ø¥Ã¥À¡¼¡Ê1¹Ô¡Ë¤ò¸«¤Ä¤±¤E
	fobj.find_word("word");      Ã±¸EÊ¶õÇò¤Ç¶èÀÚ¤é¤E¿Ê¸»úÎó¡Ë¤ò¸«¤Ä¤±¤E

	i = fobj.get_int();          À°¿ô¤òÆÉ¤ß¹ş¤E
	f = fobj.get_float();        ¼Â¿ô¤òÆÉ¤ß¹ş¤E
	d = fobj.get_dluble();       ÇÜÀºÅÙ¼Â¿ô¤òÆÉ¤ß¹ş¤E
	string = fobj.get_string()   C++ ¤ÎÊ¸»úÎó¤òÆÉ¤ß¹ş¤E
	string = fobj.get_line()     1 ¹ÔÆÉ¤ß¹ş¤E( ²ş¹Ôµ­¹æ¤ÏÍûÀÈ¤¹ )

	fobj.get_string(str* str,int len)  C ¤ÎÊ¸»úÎó¤òÆÉ¤ß¹ş¤E
	fobj.get_line(str* str, int len)   1 ¹ÔÆÉ¤ß¹ş¤E( ²ş¹Ôµ­¹æ¤ÏÍûÀÈ¤¹ )

	fobj.close();

	ÀßÄEÕ¥¡¥¤¥EÎÃæ¤Ë¥³¥á¥ó¥È¤òÆş¤EE³¤È¤¬½ĞÍè¤E
	';' ¤È²ş¹Ô¥Ş¡¼¥¯¤Î´Ö¤ò¥³¥á¥ó¥È¤È¤ß¤Ê¤¹¡£

	¶èÀÚ¤E¸»E: ¡Ö²ş¹Ô¡×¡Ö¥¿¥Ö¡×¡Ö¶õÇò¡×¡Ö; ¤Ç»Ï¤Ş¤E³¥á¥ó¥È¡×

 ver 1.0  1992?       C ÈÇ¤Î½éÈÇºûÜ®

     2.0  2003.8.12   C++ version
                      ¥á¥½¥Ã¥É¤ÎÌ¾Á°¤Ê¤É¥¤¥ó¥¿¡¼¥Õ¥§¡¼¥¹¤òÊÑ¹¹
                      get_string ¤ÇÆÉ¤ß¹ş¤ß¤ÎÅÓÃæ¤ËºÇÂçÊ¸»úÎó¤ò¥ª¡¼¥Ğ¡¼
                      ¤·¤¿¤È¤­¡¢ÆÉ¤ß¹ş¤ßÃæ¤ÎÊ¸»úÎó¤Ï¥¹¥­¥Ã¥×¤¹¤E

	 2.1       8.14   Ê£ÁÇ¿ô¤Ï stream ¤«¤é¤·¤«ÆÉ¤ß¤È¤EÊ¤¤¤¿¤E
	                  FILE ¤Ç¤Ï¤Ê¤¯ifstream ¤ò»È¤¦¡£

	 2.2       9.3    g++ 2.95.3 ¤Î getline ¤Î¥Ğ¥°¤ò²óÈò¤¹¤E¿¤E
                      my_getline ¤ò¼«Ê¬¤Ç¼ÂÁõ¤¹¤E£

     2.3  2007.10.23  ¡¦¥Õ¥¡¥¤¥E¨¥ó¥É¤ËÅşÃ£¤·¤¿¤È¤­¤Î my_getline ¤ÎÊÖ¤EÍ¤E
                        EOF ¤Èºø³Ğ¤·¤Æ¤¤¤¿²Õ½ê¤ò½¤Àµ¡ÊÀµ¤·¤¤ÃÍ¤Ï 0¡Ë
                      ¡¦get_double ¤Ê¤É¤Ç¡¢¡Öret = ¡×¤òËº¤EÆ¤¤¤¿¤Î¤ò½¤Àµ
                      ¡¦Windows ¤Ç¤âÆ°ºûÀ¹¤Eè¤¦¤Ë '\r' ¤ËÂĞ¤¹¤EèÍı¤òÉÕ²Ã

     2.4       11.13  ifstream ¤Î»È¤¤Êı¤¬¸úÀÃ¤Æ¤¤¤¿¤Î¤ò½¤Àµ
                      fp.open ¤Î·EÌ¡¢¥Õ¥¡¥¤¥E¬³«¤¤¤¿¤«Èİ¤«¤Ï is_open() ¤Ç
                      È½Äê¤¹¤E¬Í×¤¬¤¢¤E£

¡EÃúÌÕ¡ª¡E

     g++ 2.95.3 ¤Î getline ¤Ï maxlen ¤òÄ¶¤¨¤EÔ¤òÆÉ¤ß¹ş¤ó¤À¤È¤­¡¢
     °Ê¸å¤Î½èÍı¤¬¤ª¤«¤·¤¯¤Ê¤E£¼¡²ó°Ê¹ß¤ÎÆÉ¤ß¹ş¤ß¤Ç¤Ï str ¤Ë¤Ï²¿¤E
     Æş¤é¤º¡¢fp.eof() ¤Etrue ¤Ë¤Ê¤é¤Ê¤¤¡£Ìµ¸Â¥E¼¥×¤Ë¤Ê¤E£
*/

#include <stdio.h>
#include <string>

#include <fstream>

using namespace std;

class setfile{
public:
	setfile();
	setfile(const char fname[]);
	int   open(const char fname[]);
	void  close();

	int  find_header(const char keyword[]);
	int  find_word  (const char keyword[]);

	int    get_int   ();
	float  get_float ();
	double get_double();
	string get_string();
	string get_line();    // str.c_str() ¤Ï const ¤Ç¤¢¤ê¡¢¤³¤EÇº¤¤E
                          // ¤È¤­¤Ï²¼¤Î get_line(str[],len) ¤òÍÑ¤¤¤E
	int    get_string(char str[], int maxlen);
	int    get_line(char str[], int maxlen);

	ifstream fp;  // ËÜÅö¤Ï private ¤Ë¤·¤¿¤¤¤¬¡¢¥Õ¥£¡¼¥EÉ¤ÎÆÉ¤ß¹ş¤ß¤Ê¤É
                  // ³°¤«¤é¥¢¥¯¥»¥¹¤·¤¿¤¤¾Eç¤â¤¢¤EÎ¤Ç public ¤Ë¤¹¤E
	
private:
	int  my_getline(char str[], int maxlen);
	int  open_yn;
	int  skip_comment();
	void rm_right_space(char str[]);
	void check_int(char str[]);
	void check_double(char str[]);
};

#endif
