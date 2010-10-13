package transport;

import common.MessageFormat;

public class BasicTransport extends TransportLayer {

  private int counter = 0;

  // constructor for a client connection

  public BasicTransport(String server) {
    super(server);
  }

  // constructor for a server connection

  public BasicTransport() { // nothing to do but required by Java
  }

  // implements the Layer interface

  @Override
  public void send(String message) {
    sendMessage(serverName, ++counter, message); // send immediately and forget
  }

  // implements the packet handlers
  // a handler should return true when the packet makes sense
  // and false otherwise

  @Override
  public boolean processMessage(String packet) {
    if (MessageFormat.getSource(packet).equals(clientName) && received != null) {
      received.addMessageInBuffer(MessageFormat.getContent(packet));
      return true;
    }
    return false;
  }

  @Override
  public boolean processAck(String packet) {
    return false;
  }

  @Override
  public boolean processNack(String packet) {
    return false;
  }

}
