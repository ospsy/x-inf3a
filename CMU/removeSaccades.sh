#! /bin/bash 

if [ "$#" -lt 1 ]
then
	echo "Usage : <input_dir> [type1] [type2]"
	exit 0
fi

if [ -d $1 ]
then
	input_dir="$1"
	echo "Input_dir : $input_dir"
else
	echo "Not a directory...."
	exit 0
fi

if [ -z $2 ]
then
	cmd="matlab -nojvm -r addpath('segmentation');removeSaccades('$input_dir');exit;"
elif [ -z $3 ]
then
	cmd="matlab -nojvm -r addpath('segmentation');removeSaccades('$input_dir',$2);exit;"
else
	cmd="matlab -nojvm -r addpath('segmentation');removeSaccades('$input_dir',$2,$3);exit;"
fi

echo $cmd
$cmd
	
