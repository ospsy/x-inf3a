#! /bin/bash 

if [ "$#" -lt 2 ]
then
	echo "Usage : <input_dir/input_file> <output_dir> [offsetX] [offsetY]"
	exit 0
fi

if [ -d $1 ]
then
	fileName="$1/Log_data.txt"
	echo "Not a file, assuming $fileName"
else
	fileName="$1"
fi

if ! [ -e "$fileName" -a -f "$fileName" -a -r "$fileName" ]
then
	echo "$fileName doesn't exist, is not a file or not readable \n Aborting"
	exit 0
fi

if [ -e "$2" -a -d "$2" -a -r "$2" ]
then
	output_dir=$2
	echo "Output is in $2"
else
	echo "Need a correct ouput_directory..."
	exit 0
fi 

if [ -n "$4" -a -n "$3" ]
then
	ofsX=$3
	ofsY=$4
else
	ofsX=0
	ofsY=0
fi
echo "Offset : $ofsX $ofsY"

m=0
while read line
do
	m=`expr ${m} + 1`
	if [ $m -ge 19 ]
	then
		n=0
		tmp=""
		for i in $line
		do
			n=`expr ${n} + 1`
			if [ $n -eq 8 ]
			then
				tmp=`echo $i - $ofsX | bc`
			elif [ $n -eq 9 ]
			then
				tmp="${tmp} `echo $i - $ofsY | bc`"
			fi
		done
		echo $tmp
	fi
done < $fileName >"${output_dir}/eye_positions.txt"

echo "done"
	
