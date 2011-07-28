#!/usr/bin/python

import threading, sys, os, subprocess, glob
from math import floor,ceil


class MyThread ( threading.Thread ):

	def __init__ ( self, images):
		self.images = images
		threading.Thread.__init__ ( self )
	
	def run ( self):
		for im in self.images:
			subprocess.call("nice -10 ./segment -i "+im+" -o "+directory+"/output/segs/"+os.path.splitext(os.path.basename(im))[0]+" -f "+directory+"/output/fix/"+os.path.splitext(os.path.basename(im))[0]+"_fix.txt",shell=True)

			

if len(sys.argv)<3:
	print "Not enough arguments"
	exit;
directory=sys.argv[1]
files=glob.glob(directory+"capture*.jpg")
print `len(files)`+" JPEG images to process"
nbThreads=int(sys.argv[2])
print "using "+`nbThreads`+" threads"

tmp=len(files)/float(nbThreads)
for i in xrange(nbThreads):
	start=round(i*tmp)
	stop=round((i+1)*tmp)
	print `start`+" "+`stop`
	t=MyThread(files[int(start):int(stop)])
	t.start()
