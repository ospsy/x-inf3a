#! /bin/bash 

name=$2
prefix="`basename $2 .jpg`"
cmd="~/Matlab/svn/segment -i ~/shona/$1/$name -pbOnly -o ~/shona/$1/pbBoundary/$prefix"

echo $cmd
ssh vasc@buyukada.vasc.ri.cmu.edu << $cmd

exit

cmd="./segment -i ../$1/$name -pb ~/shona/$1/pbBoundary/$prefix -pos 500 500 -o ~/shona/$1/

echo $cmd
$cmd

