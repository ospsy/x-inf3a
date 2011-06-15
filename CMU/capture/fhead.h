
#ifndef __fhead_h__
#define __fhead_h__

/*------------ �إå������������ƥ�E  C++ �� ------------

��E�Ȥ��� ��E

	setfile fobj;

	fobj.open("fname");          �ե�����E򥪡��ץ󤹤�E
	
	fobj.find_header("header");  �إå�����1�ԡˤ򸫤Ĥ���E
	fobj.find_word("word");      ñ��Eʶ���Ƕ��ڤ餁E�ʸ����ˤ򸫤Ĥ���E

	i = fobj.get_int();          �������ɤ߹���E
	f = fobj.get_float();        �¿����ɤ߹���E
	d = fobj.get_dluble();       �����ټ¿����ɤ߹���E
	string = fobj.get_string()   C++ ��ʸ������ɤ߹���E
	string = fobj.get_line()     1 ���ɤ߹���E( ���Ե�������Ȥ� )

	fobj.get_string(str* str,int len)  C ��ʸ������ɤ߹���E
	fobj.get_line(str* str, int len)   1 ���ɤ߹���E( ���Ե�������Ȥ� )

	fobj.close();

	��āEե�����E���˥����Ȥ�����E�E��Ȥ����褁E
	';' �Ȳ��ԥޡ����δ֤򥳥��ȤȤߤʤ���

	���ڤ�E���E: �ֲ��ԡס֥��֡סֶ���ס�; �ǻϤޤ�E����ȡ�

 ver 1.0  1992?       C �Ǥν��Ǻ�ܮ

     2.0  2003.8.12   C++ version
                      �᥽�åɤ�̾���ʤɥ��󥿡��ե��������ѹ�
                      get_string ���ɤ߹��ߤ�����˺���ʸ����򥪡��С�
                      �����Ȥ����ɤ߹������ʸ����ϥ����åפ���E

	 2.1       8.14   ʣ�ǿ��� stream ���餷���ɤߤȤ�Eʤ�����E
	                  FILE �ǤϤʤ�ifstream ��Ȥ���

	 2.2       9.3    g++ 2.95.3 �� getline �ΥХ�����򤹤�E���E
                      my_getline ��ʬ�Ǽ�������E�

     2.3  2007.10.23  ���ե�����E���ɤ���ã�����Ȥ��� my_getline ���֤�Eͤ�E
                        EOF �Ⱥ�Ф��Ƥ����ս�������������ͤ� 0��
                      ��get_double �ʤɤǡ���ret = �פ�˺��EƤ����Τ���
                      ��Windows �Ǥ�ư������E褦�� '\r' ���Ф���E�����ղ�

     2.4       11.13  ifstream �λȤ�����äƤ����Τ���
                      fp.open �η�E̡��ե�����E����������ݤ��� is_open() ��
                      Ƚ�ꤹ��E��פ�����E�

��E���ա���E

     g++ 2.95.3 �� getline �� maxlen ��Ķ����EԤ��ɤ߹�����Ȥ���
     �ʸ�ν���������ʤ�E�����ʹߤ��ɤ߹��ߤǤ� str �ˤϲ���E
     ���餺��fp.eof() ��Etrue �ˤʤ�ʤ���̵�¥�E��פˤʤ�E�
*/

#include <stdio.h>
#include <string>

#include <fstream>

using namespace std;

class setfile{
public:
	setfile();
	setfile(char fname[]);
	int   open(const char fname[]);
	void  close();

	int  find_header(const char keyword[]);
	int  find_word  (char keyword[]);

	int    get_int   ();
	float  get_float ();
	double get_double();
	string get_string();
	string get_line();    // str.c_str() �� const �Ǥ��ꡢ����EǺ���E
                          // �Ȥ��ϲ��� get_line(str[],len) ���Ѥ���E
	int    get_string(char str[], int maxlen);
	int    get_line(char str[], int maxlen);

	ifstream fp;  // ������ private �ˤ����������ե�����Eɤ��ɤ߹��ߤʤ�
                  // �����饢��������������E�⤢��EΤ� public �ˤ���E
	
private:
	int  my_getline(char str[], int maxlen);
	int  open_yn;
	int  skip_comment();
	void rm_right_space(char str[]);
	void check_int(char str[]);
	void check_double(char str[]);
};

#endif
