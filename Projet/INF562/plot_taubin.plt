# Sortie
set terminal png size 800,600
set output 'taubin.png'
 
# Paramétrage
set samples 20
set isosamples 20
set hidden3d
set view 45,315
 
# Dessin de la courbe
splot [0:] [0:] [0:] 't1.dat' matrix with lines#, 't2.dat' matrix with lines#, 't3.dat' matrix with lines
