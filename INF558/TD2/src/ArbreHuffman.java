public abstract class ArbreHuffman {
    double proba; 
    static double comparer(ArbreHuffman a1, ArbreHuffman a2) {
	return a1.proba - a2.proba;
    }
    public abstract void  imprime ();
}

class TriArbreHuffman {
  private static void transposer (ArbreHuffman a[], int i, int j) {
    ArbreHuffman temp = a[i]; a[i] = a[j]; a[j] = temp;
  }

  private static int partition (ArbreHuffman [] a, int gauche, int droite, ArbreHuffman pivot) {
    int m = gauche;
    for (int i = gauche; i < droite; i ++)
      if (ArbreHuffman.comparer(a[i],pivot) <= 0)
	transposer(a, i, m++);
    return m;
  }
  public static void quickSort(ArbreHuffman[] a){
      quickSortRec(a,0,a.length);
  }
  private static void quickSortRec (ArbreHuffman [] a, int gauche, int droite) {
    if ( gauche < droite - 1 ) {
      ArbreHuffman pivot = a[gauche];
      int milieu = partition(a, gauche + 1, droite, pivot);
      transposer(a, gauche, milieu - 1);
      quickSortRec(a, gauche, milieu - 1);
      quickSortRec(a, milieu, droite);
    }
  }
}