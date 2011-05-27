C      = gcc
CC     = g++  
CFLAGS = -g -Wall  
ED     = ./edgeDetection
SD     = ./segmentation
MD     = ./misc
pbD    = ./pbBoundary
INC    = `pkg-config --cflags opencv` -I. -I$(MD) -I$(SD) -I$(ED) -I$(pbD)

#DETECTS kernel type (32 or 64 bit)!
KERNAL_NAME = $(shell uname -m)
#ifeq (X86_64,$(KERNAL_NAME))
	SEG_LIB = segCore_64
#else
#	SEG_LIB = segCore_32
#endif

LIB    = -L$(SD) -Bstatic -l$(SEG_LIB) -Bdynamic `pkg-config --libs opencv` -L/usr/lib -lpthread -lm 

# list of generated object files
OBJS_single  = $(pbD)/pbBoundary.o $(ED)/pbCGTG.o $(ED)/sobel.o $(ED)/pbBG.o $(ED)/savgol.o $(ED)/textureGradient.o $(MD)/misc.o segmentationLayer.o example1.o
OBJS_moving  = $(pbD)/pbBoundary.o $(ED)/pbCGTG.o $(ED)/sobel.o $(ED)/pbBG.o $(ED)/savgol.o $(ED)/textureGradient.o $(MD)/misc.o segmentationLayer.o example2.o
OBJS_static  = $(pbD)/pbBoundary.o $(ED)/pbCGTG.o $(ED)/sobel.o $(ED)/pbBG.o $(ED)/savgol.o $(ED)/textureGradient.o $(MD)/misc.o segmentationLayer.o example3.o
OBJS_segment  = $(pbD)/pbBoundary.o $(ED)/pbCGTG.o $(ED)/sobel.o $(ED)/pbBG.o $(ED)/savgol.o $(ED)/textureGradient.o $(MD)/misc.o segmentationLayer.o CmdLine.o segment.o

all: segment

segmentSingleImg:$(OBJS_single)
	$(CC) $(OBJS_single) $(CFLAGS) $(LIB)  -o segmentSingleImg 

segmentMovingObj:$(OBJS_moving)
	$(CC) $(OBJS_moving) $(CFLAGS) $(LIB)  -o segmentMovingObj 

segmentStaticObj:$(OBJS_static)
	$(CC) $(OBJS_static) $(CFLAGS) $(LIB)  -o segmentStaticObj 

segment:$(OBJS_segment)
	$(CC) $(OBJS_segment) $(CFLAGS) $(LIB)  -o segment

calcOpticalFlow:opticalFlow.o
	$(CC) opticalFlow.o $(CFLAGS) $(LIB)  -o calcOpticalFlow

example1.o : example1.cpp 
	$(CC) $(CFLAGS) $(INC) -c example1.cpp -o example1.o

example2.o : example2.cpp 
	$(CC) $(CFLAGS) $(INC) -c example2.cpp -o example2.o

example3.o : example3.cpp 
	$(CC) $(CFLAGS) $(INC) -c example3.cpp -o example3.o

segment.o : segment.cpp 
	$(CC) $(CFLAGS) $(INC) -c segment.cpp -o segment.o

CmdLine.o : CmdLine.cpp 
	$(CC) $(CFLAGS) $(INC) -c CmdLine.cpp -o CmdLine.o

opticalFlow.o : opticalFlow.cpp
	$(CC) $(CFLAGS) $(INC) -c opticalFlow.cpp -o opticalFlow.o

segmentationLayer.o: segmentationLayer.cpp segmentationLayer.h
	$(CC) $(CFLAGS) $(INC) -c segmentationLayer.cpp -o segmentationLayer.o

$(ED)/pbCGTG.o : $(ED)/pbCGTG.cpp $(ED)/savgol.h $(ED)/textureGradient.h
	$(CC) $(CFLAGS) $(INC) -c $(ED)/pbCGTG.cpp -o $(ED)/pbCGTG.o

$(ED)/sobel.o : $(ED)/sobelEdge.cpp $(ED)/sobelEdge.h 
	$(CC) $(CFLAGS) $(INC) -c $(ED)/sobelEdge.cpp -o $(ED)/sobel.o

$(ED)/pbBG.o : $(ED)/pbBG.cpp $(ED)/savgol.h $(ED)/textureGradient.h
	$(CC) $(CFLAGS) $(INC) -c $(ED)/pbBG.cpp -o $(ED)/pbBG.o

$(ED)/savgol.o : $(ED)/savgol.cpp $(ED)/savgol.h
	$(CC) $(CFLAGS) $(INC) -c $(ED)/savgol.cpp -o $(ED)/savgol.o

$(ED)/textureGradient.o : $(ED)/textureGradient.cpp $(ED)/textureGradient.h 	
	$(CC) $(CFLAGS) $(INC) -c $(ED)/textureGradient.cpp -o $(ED)/textureGradient.o

$(pbD)/pbBoundary.o : $(pbD)/pbBoundary.cpp $(pbD)/pbBoundary.h
	$(CC) $(CFLAGS) $(INC) -c $(pbD)/pbBoundary.cpp -o $(pbD)/pbBoundary.o 

$(MD)/misc.o : $(MD)/misc.cpp $(MD)/misc.h
	$(CC) $(CFLAGS) $(INC) -c $(MD)/misc.cpp -o $(MD)/misc.o 
clean : 
	/bin/rm -f $(OBJS)

