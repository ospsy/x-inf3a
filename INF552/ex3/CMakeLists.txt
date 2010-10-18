PROJECT(Ex3)

CMAKE_MINIMUM_REQUIRED(VERSION 2.6)
SET(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} $ENV{IMAGINEPP_ROOT}/CMake)
FIND_PACKAGE(Imagine)

SET(FEATS SIFT.cpp vl/imop.c vl/sift0.c vl/generic.c vl/host.c)

ImagineAddExecutable(Estimation estimation.cpp ${FEATS})
ImagineUseModules(Estimation Images LinAlg)
