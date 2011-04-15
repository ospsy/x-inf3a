#! /bin/bash 

if [ "$#" -lt 3 ]
then
	echo "Usage : <input_dir> <output_dir> <number_of_threads>"
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

output_dir=$2

nb_threads=$3
echo "$nb_threads instances"

tmp=`ls ${input_dir}/*.ppm | wc -l`
if [ $tmp -eq 0 ]
then
	tmp=`ls ${input_dir}/*.png | wc -l`
fi
if [ $tmp -eq 0 ]
then
	tmp=`ls ${input_dir}/*.jpg | wc -l`
fi
if [ $tmp -eq 0 ]
then
	echo "No images in that directory"
	exit 0
fi

nb_images=$tmp
echo "$nb_images images to process"
tmp=`echo "scale=2; ${nb_images}/${nb_threads}" | bc`
echo $tmp
cd flow
for i in `seq 1 ${nb_threads} `
do
	first=`echo "scale=0; ((${i}-1)*${tmp}+1)/1" | bc`
	last=`echo "scale=0; (${i}*${tmp})/1" | bc`
	cmd="matlab -nojvm -r generate_optical_flow('../$input_dir','../$output_dir',$first,$last);exit;"
	echo $cmd
	$cmd & > /dev/null
done
cd ..
	
