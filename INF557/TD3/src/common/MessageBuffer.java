package common;

import java.util.LinkedList;

public class MessageBuffer {
  // The received message buffer used
  // in this class as a queue
  private LinkedList<String> receiveBuffer;

  public MessageBuffer() {
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

  public synchronized String firstMessage() {
    return receiveBuffer.peekFirst();
  }

  // don't block but sleep a few when the buffer is empty
  public String receive() {
    String s = null;
    synchronized (this) {
      if (!receiveBuffer.isEmpty())
        s = receiveBuffer.removeFirst();
    }
    // don't sleep or delay inside a critical (synchronized) section.
    if (s == null)
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        System.out.println(e);
        System.exit(0);
      }
    return s;
  }

}
