
/**
Edges detection on GPU
width, height -> size of the images
imageData -> address of the aligned image data (can be IplImage.imageData), each uint represent a whole pixel
gradData, oriData -> addresses for the output, must be allocated by the user.
gPb -> whole gPb detector or only mPb (localcues based), the later being less efficient but way faster
*/
int damascene(uint width, uint height, uint* imageData, float* gradData, float* oriData, bool gPb=true);
