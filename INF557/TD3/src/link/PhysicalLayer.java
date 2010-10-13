package link;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import common.Layer;
import common.MessageBuffer;

public class PhysicalLayer implements Layer, Runnable {
  // The received message buffer.
  private final MessageBuffer rb;

  // The percentage of packets not dropped.
  // If a random number between 0 and 100 is
  // smaller than successRate, then we send the
  // message. Otherwise, we do nothing.
  private int successRate = 100;
  private static final Random RAND = new Random(); // the random number
                                                   // generator

  protected final Timer timer = new Timer();
  private static final int SEND_DELAY = 400; // ms delay before sending a packet

  // port and sockets for broadcasting
  protected final int port;
  protected MulticastSocket outputSocket = null;
  protected InetAddress address;
  private MulticastSocket inputSocket = null;

  public PhysicalLayer(String group, int p) {
    port = p;
    rb = new MessageBuffer();
    try {
      outputSocket = new MulticastSocket();
    } catch (IOException e) {
      System.out.println(e);
      System.exit(0); // kill program
    }
    try {
      address = InetAddress.getByName(group); // the IP address
    } catch (UnknownHostException e) {
      System.out.println(e);
      System.exit(0); // kill program
    }
    try {
      inputSocket = new MulticastSocket(port);
      inputSocket.joinGroup(address);
    } catch (IOException e) {
      System.out.println(e);
      System.exit(0); // kill program
    }
    // here, we create and launch the listening
    // server waiting for messages. When a message
    // is caught by the server, it is enqueue in
    // the received buffer.
    new Thread(this).start();
  }

  public PhysicalLayer() {
    this("225.6.7.8", 11111);
  }

  public void setSuccessRate(int s) {
    // s must be between 0 and 100
    if ((s >= 0) && (s <= 100)) {
      successRate = s;
      System.out.println("successRate is set to " + successRate + "%");
    }
  }

  public void send(final String message) {
    if (message == null)
      return;
    // if a random number between 0 and 100 is
    // smaller than successRate, then we send the
    // message. Otherwise, we do nothing.
    if (RAND.nextInt(100) <= successRate) {
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          byte[] buf = message.getBytes();
          try {
            DatagramPacket pack = new DatagramPacket(buf, buf.length, address,
                port);
            // send(packet, time) is tagged @deprecated.
            // This a solution proposed by Sun.
            outputSocket.setTimeToLive(4);
            outputSocket.send(pack);
            outputSocket.setTimeToLive(4);
          } catch (IOException e) {
            System.out.println(e);
            System.exit(0); // kill program
          }
          timer.purge();
        }
      }, SEND_DELAY);
    }
  }

  // don't block but sleep a few when the buffer is empty
  public String receive() {
    return rb.receive();
  }

  public void run() {
    while (inputSocket != null) {
      try {
        byte[] buf = new byte[16384];
        DatagramPacket pack = new DatagramPacket(buf, buf.length);
        inputSocket.receive(pack);
        // the last two arguments are used to avoid nasty
        // String format under Windows. Huh, so surprising...
        String data = new String(pack.getData(), 0, pack.getLength());
        // if the data is not empty, we enqueue it into
        // the received message buffer
        if (data.length() > 0)
          rb.addMessageInBuffer(data);
      } catch (IOException e) {
        System.out.println(e);
        System.exit(0); // kill program
      }
    }
  }

  public void close() {
    final PhysicalLayer pl = this;
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        timer.cancel();
        synchronized (pl) {
          pl.notify();
        }
      }
    }, 2 * SEND_DELAY);
    try {
      synchronized (this) {
        wait();
      }
    } catch (InterruptedException e) {
      System.out.println(e);
      System.exit(0); // kill program
    }

    inputSocket.close();
    inputSocket = null;
  }
}
