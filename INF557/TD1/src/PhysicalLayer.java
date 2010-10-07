import java.net.*;
import java.io.*;
import java.util.*;

//////////////////////////
// CLASS PHYSICAL LAYER //
//////////////////////////

class PhysicalLayer {
  // The received message buffer. We use
  // an object in order to be shared between
  // server and clients on the same PhysicalLayer
  private ReceivedBuffer rb;

  // The percentage of packets not dropped.
  // If a random number between 0 and 100 is
  // smaller than successRate, then we send the
  // message. Otherwise, we do nothing.
  private int successRate = 100;
  private Random ran = new Random(); // the random number generator

  // Socket, port and group for broadcasting
  private MulticastSocket client;
  private int port = 11111;
  private String group = "225.6.7.8";
  // corresponding IP address
  private InetAddress address;

  public PhysicalLayer() {
    rb = new ReceivedBuffer();
    try {
      client = new MulticastSocket();
    } catch (IOException e) {
      System.out.println(e);
      System.exit(0); // kill program
    }
    try {
      address = InetAddress.getByName(group);
    } catch (UnknownHostException e) {
      System.out.println(e);
      System.exit(0); // kill program
    }
    // here, we create and launch the listening
    // server waiting for messages. When a message
    // is caught by the server, it is enqueue in
    // the received buffer.
    Thread t_s = new Thread(new Server(rb, port, address));
    t_s.start();
  }

  public void setSuccessRate(int s) {
    // s must be between 0 and 100
    if ((s >= 0) && (s <= 100))
      successRate = s;
  }

  public void send(String message) {
    if (message == null)
      return;
    // if a random number between 0 and 100 is
    // smaller than successRate, then we send the
    // message. Otherwise, we do nothing.
    if (ran.nextInt(100) <= successRate) {
      byte[] buf = message.getBytes();
      try {
        DatagramPacket pack = new DatagramPacket(buf, buf.length, address, port);

        // send(packet, time) is deprecated.
        // This a solution proposed by Sun.
        client.setTimeToLive(4);
        client.send(pack);
        client.setTimeToLive(4);
      } catch (IOException e) {
        System.out.println(e);
        System.exit(0); // kill program
      }
    }
  }

  public String receive() {
    return rb.receive();
  }

}

// ////////////////
// CLASS SERVER //
// ////////////////

class Server implements Runnable {
  private ReceivedBuffer rb;
  private int port;
  private InetAddress address;

  public Server(ReceivedBuffer rb, int port, InetAddress address) {
    this.rb = rb;
    this.port = port;
    this.address = address;
  }

  public void run() {
    MulticastSocket server = null;
    try {
      server = new MulticastSocket(port);
      server.joinGroup(address);
    } catch (IOException e) {
      System.out.println(e);
      System.exit(0); // kill program
    }

    while (true) {
      try {
        byte[] buf = new byte[16384];
        DatagramPacket pack = new DatagramPacket(buf, buf.length);
        server.receive(pack);

        // the last two arguments are used to avoid nasty
        // String format under Windows. Huh, so surprising...
        String data = new String(pack.getData(), 0, pack.getLength());

        // if the data is not empty, we enqueue it into
        // the received message buffer
        if (data != null)
          rb.addMessageInBuffer(data);
      } catch (IOException e) {
        System.out.println(e);
        System.exit(0); // kill program
      }
    }
  }
}

// /////////////////////////
// CLASS RECEIVED BUFFER //
// /////////////////////////

class ReceivedBuffer {
  // The received message buffer used
  // in this class as a queue
  private LinkedList<String> receiveBuffer;

  ReceivedBuffer() {
    receiveBuffer = new LinkedList<String>();
  }

  // At least the Server and a client are accessing to this buffer
  // thus a synchronization is required around the operations on the list.
  // The lock (in Java sense) is the ReceivedBuffer object itself.

  public synchronized void addMessageInBuffer(String message) {
    receiveBuffer.addLast(message);
  }

  public synchronized boolean isEmpty() {
    return receiveBuffer.isEmpty();
  }

  public String receive() {
    String s = null;
    synchronized (this) {
      if (receiveBuffer.isEmpty())
        return null;
      s = receiveBuffer.removeFirst();
    }
    // Don't sleep or delay inside a critical (synchronized) section.
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      System.out.println(e);
      System.exit(0);
    }
    return s;
  }

}

// //////////////////
// CLASS KEYBOARD //
// //////////////////

class Keyboard {
  private static InputStreamReader reader = new InputStreamReader(System.in);
  private static BufferedReader input = new BufferedReader(reader);

  // A static method waiting for keyboard
  // entry finished by the return buttom
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

// /////////////////////////
// / CLASS RANDOM STRING ///
// /////////////////////////

class RandomString {
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

// ////////////////////////
// / CLASS TYPE MESSAGE ///
// ////////////////////////

class TypeMessage {
  // do not forget to expend this method
  // if new message types are defined
  // Be aware that election messages are not
  // taken into account here!
  public static boolean isGoodType(String type) {
    return (type.equals("SERVICES") || type.equals("DISCOVER")
        || type.equals("MESSAGE") || type.equals("ACK") || type.equals("NACK")
        || type.equals("JOIN") || type.equals("ACCEPT") || type.equals("election")
        || type.equals("ELECTION") || type.equals("OK") || type.equals("ok")
        || type.equals("leader") || type.equals("LEADER"));
  }

  public static boolean isOfType(String message, String type) {
    return message != null && message.split(":").length > 2
        && message.split(":")[2].equals(type);
  }

  public static boolean isSERVICES(String message) {
    return isOfType(message, "SERVICES");
  }

  public static boolean isDISCOVER(String message) {
    return isOfType(message, "DISCOVER");
  }
  
  public static boolean isELECTION(String message) {
	    return isOfType(message, "ELECTION") || isOfType(message, "election");
  }
  
  public static boolean isLEADER(String message) {
	    return isOfType(message, "LEADER") || isOfType(message, "leader");
}
  
  public static boolean isOK(String message) {
	    return isOfType(message, "OK") || isOfType(message, "ok");
}

  public static String getSource(String message) {
    return message.split(":")[1];
  }

  public static String getDest(String message) {
    return message.split(":")[0];
  }

  private static String message(String source, String dest, String type) {
    return dest + ':' + source + ':' + type + ':';
  }

  public static String services(String source, String dest) {
    return message(source, dest, "SERVICES");
  }

  public static String discover(String source, String dest) {
    return message(source, dest, "DISCOVER");
  }
  
  public static String election(String source, String dest) {
	    return message(source, dest, "ELECTION");
  }
  
  public static String ok(String source, String dest) {
	    return message(source, dest, "OK");
}
  public static String leader(String source, String dest) {
	    return message(source, dest, "LEADER");
}

  // etc
}
