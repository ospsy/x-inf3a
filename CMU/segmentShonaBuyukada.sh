#! /bin/bash 

name=$2
prefix="`basename $2 .jpg`"
cmd="\$HOME/Matlab/svn/segment -i \$HOME/shona/$1/$name -pbOnly -o \$HOME/shona/$1/pbBoundary/$prefix"

echo $cmd
echo "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/cuda/lib:/usr/local/cuda/lib64:/opt/acml4.4.0/gfortran64/lib; $cmd" | ssh vasc@buyukada.vasc.ri.cmu.edu


exit

cmd="./segment -i ../$1/$name -pb ~/shona/$1/pbBoundary/$prefix -pos 500 500 -o ~/shona/$1/

echo $cmd
$cmd

