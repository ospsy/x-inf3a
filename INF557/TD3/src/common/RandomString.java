package common;
import java.util.Random;

public class RandomString {
  private static Random gen = new Random();

  public static String nextString(int n) {
    // n is the length of the requested string
    char[] buf = new char[n];

    // prevents of having non alpha-numerical characters in the string
    for (int i = 0; i < n; i++)
      switch (gen.nextInt(3)) {
      case 0:
        // pick something between a and z
        buf[i] = (char) (gen.nextInt(26) + 65);
        break;
      case 1:
        // pick something between A and Z
        buf[i] = (char) (gen.nextInt(26) + 97);
        break;
      case 2:
        // pick a number
        buf[i] = (char) (gen.nextInt(10) + 48);
      }
    return new String(buf);
  }
}
