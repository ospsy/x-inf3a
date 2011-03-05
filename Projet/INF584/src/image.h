/*
 * image.h
 *
 *  Created on: 5 mars 2011
 *      Author: Eisemann
 */

#ifndef IMAGEBW_H_
#define IMAGEBW_H_

#include <fstream>
#include <iostream>
using namespace std;

struct ImageBW
{
	int w;
	int h;
	std::vector<float> data;

	ImageBW(int wI=1, int hI=1)
	:w(wI)
	,h(hI)
	{
		data.resize(w*h);
	}

	void resize(int wI=1, int hI=1)
	{
		w=wI;
		h=wI;
		data.resize(w*h);
	}

	ImageBW & operator= (const ImageBW & image)
	{
		w=image.w;
		h=image.h;
		data.resize(image.nbEntries());
		for (int i=0; i<w*h;++i)
		{
			data[i]=image.data[i];
		}
		return (*this);
	}

	bool loadRAW(const char * filename)
	{
		int s[2]={-1, -1};
		int curr=0;

		for (int i=0; filename[i]!='\0'; ++i)
		{
			if ((filename[i]>='0')&&(filename[i]<='9'))
			{
				if (curr<2)
				{

					s[curr]=0;
					while ((filename[i]>='0')&&(filename[i]<='9'))
					{
						s[curr]*=10;
						s[curr]+=filename[i]-'0';
						++i;
					}
					++curr;
					--i;
				}
			}
		}

		if (s[1]==-1)
			return false;

		w=s[0];
		h=s[1];

		data.resize(w*h);
		std::vector<unsigned short> tempData(w*h);

		ifstream in(filename, ios::in | ios::binary);
		in.read( reinterpret_cast<char *>(&(tempData[0])), (w*h)*sizeof(unsigned short));

		if (in.fail()) {
			return false;
		}

		for (int i=0; i<w*h; ++i)
			data[i]=float(tempData[i])/65535;

		return true;
	};

	float & getPixel(int i, int j, int col=0)
	{
		if (i<0)
			i=0;
		if (i>w-1)
			i=w-1;

		if (j<0)
			j=0;
		if (j>h-1)
			j=h-1;

		return data[(i+w*j)+col];
	}

	float & operator[](int i)
	{
		return data[i];
	}

	int nbPixels() const
	{
		return w*h;
	}

	int nbEntries() const
	{
		return w*h;
	}
};

#endif /* IMAGEBW_H_ */
