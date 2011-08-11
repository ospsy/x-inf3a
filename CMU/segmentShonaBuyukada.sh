#! /bin/bash 

ssh vasc@buyukada.vasc.ri.cmu.edu

name=$2
prefix=basename $2
cmd="~/Matlab/svn/segment -i ~/shona/$1/$name -pbOnly -o ~/shona/$1/pbBoundary/$prefix"

echo $cmd
$cmd

exit

cmd="./segment -i ../$1/$name -pb ~/shona/$1/pbBoundary/$prefix -pos 500 500"

echo $cmd
$cmd
