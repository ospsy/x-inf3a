package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Keyboard {
  private static InputStreamReader reader = new InputStreamReader(System.in);
  private static BufferedReader input = new BufferedReader(reader);

  // A static method waiting for keyboard
  // entry ended by the return key
  // and return this entry through a String
  public static String readString() {
    String readLine = null;
    try {
      readLine = input.readLine();
    } catch (IOException e) {
      System.out.println(e);
      System.exit(0); // kill program
    }
    return readLine;
  }
}
