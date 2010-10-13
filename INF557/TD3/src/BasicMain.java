import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import transport.TransportLayer;
import transport.TransportProtocol;

// To simplify, we consider an asymmetric system, where 
// - a server accepts a (only one at a time) connection to receive a file
// - a client initiates a connection to send a file to the server

public class BasicMain {

  public static void doServer() {
    while (true) {
      TransportLayer tl = TransportLayer.connect();
      while (tl.isOpen()) {
        String packet = tl.receive();
        if (packet != null)
          System.out.println(packet);
        else
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            System.err.println(e);
            System.exit(0);
          }
      }
    }
  }

  public static void doClient(String serverName, String fileName) {
    TransportLayer tl = TransportLayer.connect(serverName);
    if (tl == null) {
      System.err.println("-- connection failed --");
      return;
    }
    Scanner sc = null;
    try {
      sc = new Scanner(new File(fileName));
    } catch (FileNotFoundException e) {
      System.err.println(e);
      System.exit(0);
    }
    if (sc != null)
      while (sc.hasNextLine())
        tl.send(sc.nextLine()); // send all ASAP
    System.out.println("-- all are in the pipe --");
    tl.flush();
  }

  public static void main(String[] args) {
    if (args.length != 2 && args.length != 4) {
      System.err
          .println("syntax : java BasicTest myName successRate [ serverName fileName ]");
      return;
    }
    String myName = args[0];
    int successRate = Integer.parseInt(args[1]);
    TransportLayer.init(myName, successRate, TransportProtocol.STOP_AND_WAIT); // change
                                                                       // of
                                                                       // protocol
                                                                       // here
    if (args.length == 2) {
      System.out.println("launched as server (receiver)");
      doServer();
    } else {
      System.out.println("launched as client (sender)");
      doClient(args[2], args[3]);
    }
  }

}
