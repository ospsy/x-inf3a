
#Searching CUDA
FIND_PACKAGE(CUDA)
#Searching ACML
FIND_PACKAGE(LAPACK)
SET(ACML_DIR /opt/acml4.4.0/gfortran64)

SET(CUDA_NVCC_FLAGS "-arch=sm_12")

find_library(CUDA_CUTIL_LIBRARY cutil_x86_64 "${CUDA_SDK_ROOT_DIR}/C/lib")

include_directories(${CUDA_SDK_ROOT_DIR}/C/common/inc ${CMAKE_CURRENT_SOURCE_DIR} ${CMAKE_CURRENT_SOURCE_DIR}/src ${CMAKE_CURRENT_SOURCE_DIR}/include ${ACML_DIR}/include)

CUDA_ADD_LIBRARY(damascene src/damascene.cu src/nonmax.cu src/gradient.cu src/parabola.cu src/localcuesmain.cu src/localcues.cu src/rotate.cu src/Stencil.cpp src/filters.cpp src/stencilMVM.cu src/convert.cu src/texton.cu src/kmeans.cu src/combine.cu src/globalPb.cu src/skeleton.cu src/spectralPb.cu src/intervening.cu src/lanczos.cu)

target_link_libraries(damascene ${LAPACK_acml_LIBRARY} ${CUDA_CUTIL_LIBRARY} ${CUDA_cublas_LIBRARY} )
