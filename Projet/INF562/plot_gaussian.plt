# Sortie
set terminal png size 1024,1024
set output 'gaussian.png'
 
# Paramétrage
set samples 20
set isosamples 20
set hidden3d
 
# Dessin de la courbe
plot 'torus.off.dat' with lines, 'torus2.off.dat' with lines, 'torus3.off.dat' with lines, 'torus4.off.dat' with lines, 'torus5.off.dat' with lines, 'chair.off.dat' with lines
