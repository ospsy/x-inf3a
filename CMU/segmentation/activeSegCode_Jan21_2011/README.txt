To compile:
> make

To execute:
> ./segmentSingleImg IMG_0223.JPG


EXPLANATION:
There are three important steps:
1. Generating probabilistic boundary edge map.
   	
2. Selecting a fixation point.

3. Segmenting the region containing that fixation point

The segmentationLayer is a class that has the functions dedicated for these steps.
For step 1, you need to first read all the available cues and then call generatePbBoundary()
member function to generate the probabilistic boundary edge map.

For step 2, you can either assign a fixation point interactively as given in the example files,
or use assignFixPt(x,y) to directly write to it or read it from a text file containing the fixation points
using readFixPts() function.

For the last step, if there is only one fixation point, you can use segmentCurrFixPt(). However,
if you have multiple fixation points and want to segment for all of them at once, use segmentAllFixs(). The
output is a binary mask.

CAUTION:  Don't forget to allocate memory using allocateMemForContours() to store the region contours. 

To read about the segmentation process, refer to Ajay Mishra, Yiannis Aloimonos, C.L. Fah "Active Segmentation With Fixation", in ICCV 2009. 
Also cite this paper, if you use the code in your research.

For any help, feel free to write to mishraka@umiacs.umd.edu
