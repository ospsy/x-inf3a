#! /bin/bash 

if [ "$#" -lt 1 ]
then
	echo "Usage : <input_dir>"
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

cmd="matlab -nojvm -r addpath('segmentation');fixation2('$input_dir');exit;"
echo $cmd
$cmd &
	
