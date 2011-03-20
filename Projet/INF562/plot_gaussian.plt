# Sortie
set terminal png size 800,600
set output 'gaussian.png'
 
# Paramétrage
set samples 20
set isosamples 20
set hidden3d
 
# Dessin de la courbe
plot 'g1.dat', 'g2.dat', 'g3.dat'
