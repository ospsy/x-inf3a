#include "surf.h"

std::list<std::vector<int> > findExtrema(IplImage** data, int intervals){
	std::list<std::vector<int> > result;
	for(int i=1;i<intervals-1;i++){
		for (int x = 1; x < data[i]->width-1; ++x) {
			for (int y = 1; y < data[i]->height-1; ++y) {
				if(getPixel(data[i],x,y)<THRESOLD)
					continue;
				//pour chaque pixel
				bool max=true;
				for (int j = -1; j <= 1; ++j) {
					for (int a = -1; a <= 1; ++a) {
						for (int b = -1; b <= 1; ++b) {
							if(j==0 && a==0 && b==0)
								continue;
							if(getPixel(data[i],x,y)<getPixel(data[i+j],x+a,y+b))
								max=false;
						}
					}
					if(max==false)
						break;
				}
				if(max==true){
					std::vector<int> v;
					v.push_back(i);
					v.push_back(x);
					v.push_back(y);
					result.push_back(v);
				}
			}
		}
	}
	return result;
}
