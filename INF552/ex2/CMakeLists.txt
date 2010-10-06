PROJECT(Exo1)

CMAKE_MINIMUM_REQUIRED(VERSION 2.6)
SET(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} $ENV{IMAGINEPP_ROOT}/CMake)
FIND_PACKAGE(Imagine)

ImagineAddExecutable(Epipolaires epipolaires.cpp)
ImagineUseModules(Epipolaires Images)
