#The name of the project
PROJECT(SEGMENTATION)

#CMAKE_MINIMUM_REQUIRED(VERSION 2.8)

set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/cmake")

enable_language(Fortran)

find_library(SEGMENTATION_LIB segCore_64 ${CMAKE_CURRENT_SOURCE_DIR}/segmentation)

option(CUDA_SUPPORT "Use the power of CUDA")

FIND_PACKAGE(OpenCV REQUIRED)
include_directories(segmentation pbBoundary misc edgeDetection)

if(CUDA_SUPPORT)
	add_subdirectory(damascene)
	include_directories(damascene)
endif()

add_executable(segment segment.cpp CmdLine.cpp segmentationLayer.cpp pbBoundary/pbBoundary.cpp misc/misc.cpp edgeDetection/pbCGTG.cpp edgeDetection/pbCG.cpp edgeDetection/pbBG.cpp edgeDetection/savgol.cpp edgeDetection/sobelEdge.cpp edgeDetection/textureGradient.cpp)
target_link_libraries(segment ${SEGMENTATION_LIB} m pthread ${OpenCV_LIBS})

if(CUDA_SUPPORT)
	target_link_libraries(segment damascene)
	set_target_properties(segment PROPERTIES COMPILE_DEFINITIONS "CUDA_SUPPORT=1")
endif()

#add_executable(test example1.cpp CmdLine.cpp segmentationLayer.cpp pbBoundary/pbBoundary.cpp misc/misc.cpp edgeDetection/pbCGTG.cpp edgeDetection/pbCG.cpp edgeDetection/pbBG.cpp edgeDetection/savgol.cpp edgeDetection/sobelEdge.cpp edgeDetection/textureGradient.cpp)
#target_link_libraries(test ${SEGMENTATION_LIB} m pthread ${OpenCV_LIBS} damascene)
