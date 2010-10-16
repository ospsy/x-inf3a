PROJECT(Projet)

CMAKE_MINIMUM_REQUIRED(VERSION 2.6)
SET(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} $ENV{IMAGINEPP_ROOT}/CMake)
FIND_PACKAGE(Imagine)

ImagineAddExecutable(Main main.cpp Hough.cpp)
ImagineUseModules(Main Images)
