package transport;

import java.util.Timer;
import java.util.TimerTask;

import link.PhysicalLayer;

import common.Layer;
import common.MessageFormat;
import common.MessageBuffer;

public abstract class TransportLayer implements Layer, Runnable {

  // static initialization = system configuration

  private static final int CONNECT_TIMEOUT = 1000; // ms = 1 second
  private static final int DISCONNECT_TIMEOUT = 5000; // ms = 5 seconds

  private static String myName;
  private static final PhysicalLayer PL = new PhysicalLayer();
  private static TransportProtocol protocol;
  private static final Timer TIMER = new Timer();

  public static void init(String name, int successRate, TransportProtocol tp) {
    myName = name;
    PL.setSuccessRate(successRate);
    protocol = tp;
  }

  // factory for a client connection, return null if connection failed
  public static TransportLayer connect(String serverName) {
    TransportLayer layer = null;
    switch (protocol) {
    case BASIC:
      layer = new BasicTransport(serverName);
      break;
    case GO_BACK_N:
    	layer = new GoBackNTransport(serverName);
      break;
    case SLIDING_WINDOW:
      break;
    case STOP_AND_WAIT:
      layer = new StopAndWaitTransport(serverName);
      break;
    }
    if (layer != null && layer.isConnected)
      return layer;
    return null;
  }

  // factory for a server connection
  public static TransportLayer connect() {
    TransportLayer layer = null;
    switch (protocol) {
    case BASIC:
      layer = new BasicTransport();
      break;
    case GO_BACK_N:
    	layer = new GoBackNTransport();
      break;
    case SLIDING_WINDOW:
      break;
    case STOP_AND_WAIT:
      layer = new StopAndWaitTransport();
      break;
    }
    return layer;
  }

  // a TransportLayer object will instantiate one side of a connection

  // client side state
  protected final String serverName;
  private boolean isConnected;

  // server side state
  protected String clientName;
  protected final MessageBuffer received;
  private boolean isOpen;

  private class TimeOut extends TimerTask {
    TransportLayer layer;

    public TimeOut(TransportLayer tl) {
      this.layer = tl;
    }

    @Override
    public void run() {
      System.err.println("... no answer from " + layer.serverName);
      synchronized (layer) {
        layer.notify();
        // the constructor will terminate but connected remains as false
      }
    }
  }

  // instantiate a client side connection
  // this constructor will wait until receiving an ACCEPT packet or a TIMEOUT
  // event
  public TransportLayer(String server) {
    clientName = null;
    received = null;
    isOpen = true;
    serverName = server;
    isConnected = false;
    new Thread(this).start();
    PL.send(MessageFormat.connect(myName, serverName));
    System.out.println("opening connection with " + serverName + " ...");
    // launch the timeout task
    TimerTask timeout = new TimeOut(this);
    TIMER.schedule(timeout, CONNECT_TIMEOUT);
    try {
      synchronized (this) {
        wait();
      }
    } catch (InterruptedException e) {
      System.out.println(e);
      System.exit(0); // kill program
    }
    timeout.cancel();
    if (!isConnected)
      flush();
  }

  private boolean processAccept(String packet) {
    if (serverName != null
        && serverName.equals(MessageFormat.getSource(packet))) {
      isConnected = true;
      System.out.println("... connection is open");
      synchronized (this) {
        notify();
        // then, the constructor will terminate
      }
      return true;
    }
    return false;
  }

  // instantiate a server side connection
  // this constructor will wait until receiving a CONNECT packet
  public TransportLayer() {
    serverName = null;
    isConnected = false;
    clientName = null;
    received = new MessageBuffer();
    isOpen = true;
    new Thread(this).start();
    System.out.println("waiting for a connection ...");
    try {
      synchronized (this) {
        wait();
      }
    } catch (InterruptedException e) {
      System.out.println(e);
      System.exit(0); // kill program
    }
  }

  private boolean processConnect(String packet) {
    if (clientName != null) {
      System.err.println("client " + clientName + " is already connected");
      return false;
    }
    clientName = MessageFormat.getSource(packet);
    PL.send(MessageFormat.accept(myName, clientName));
    System.out.println("... connection from " + clientName + " accepted");
    synchronized (this) {
      notify();
      // then, the constructor will terminate
    }
    return true;
  }

  // receiving loop and abstract handlers for the different kinds of packet
  // a handler should return true when the packet makes sense
  // and false otherwise
  abstract boolean processMessage(String packet);

  abstract boolean processAck(String packet);

  abstract boolean processNack(String packet);

  public void run() {
    long timeout = 0;
    while (isOpen) {
      String packet = PL.receive();
      if (packet != null && myName.equals(MessageFormat.getDest(packet))) {
        boolean isValid = false;
        if (MessageFormat.isCONNECT(packet))
          isValid = processConnect(packet);
        else if (MessageFormat.isACCEPT(packet))
          isValid = processAccept(packet);
        else if (MessageFormat.isMESSAGE(packet))
          isValid = processMessage(packet);
        else if (MessageFormat.isACK(packet))
          isValid = processAck(packet);
        else if (MessageFormat.isNACK(packet))
          isValid = processNack(packet);
        else
          System.err.println("wrong packet " + packet);
        if (isValid)
          timeout = System.currentTimeMillis() + DISCONNECT_TIMEOUT;
      } else if (clientName != null && System.currentTimeMillis() > timeout) {
        System.out.println("connection timeout");
        isOpen = false;
      }

    }
  }

  public boolean isOpen() {
    return isOpen;
  }

  // the Layer's send is kept abstract for protocol specific implementation
  public abstract void send(String message);

  // for finally sending the different kinds of packet down to the stack

  public void sendMessage(String dest, int num, String msg) {
    PL.send(MessageFormat.message(myName, dest, num, msg));
  }

  public void sendAck(String dest, int num) {
    PL.send(MessageFormat.ack(myName, dest, num));
  }

  public void sendNack(String dest, int num) {
    PL.send(MessageFormat.nack(myName, dest, num));
  }

  // the Layer's receive() is implemented now
  public String receive() {
    if (received == null) // no buffer !
      return null;
    // don't block but sleep a few when the buffer is empty
    return received.receive();
  }

  protected void closePhysicalLayer(){
	  PL.close();
  }
  // wait until all packets are really sent
  public void flush() {
    closePhysicalLayer();
  }

}
