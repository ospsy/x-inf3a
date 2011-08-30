PROJECT(Optical)

CMAKE_MINIMUM_REQUIRED(VERSION 2.6)
SET(CMAKE_BUILD_TYPE Release)
FIND_PACKAGE( OpenCV REQUIRED )

ADD_EXECUTABLE(trackingGaze trackingGaze.cpp fhead.cpp)
TARGET_LINK_LIBRARIES(trackingGaze ${OpenCV_LIBS})

ADD_EXECUTABLE(trackingFixations trackingFixations.cpp fhead.cpp)
TARGET_LINK_LIBRARIES(trackingFixations ${OpenCV_LIBS})
