package transport;

import java.util.Timer;
import java.util.TimerTask;

import common.MessageBuffer;
import common.MessageFormat;

public class StopAndWaitTransport extends TransportLayer {

	private int counterSend = 0;
	private int counterReceived = 0;
	private int lastAck=0;
	private MessageBuffer buffer = new MessageBuffer();
	private String dest=null;
	private TimerTask task=null;
	private static final Timer timer=new Timer();
	private final long period=1000;

	// constructor for a client connection

	public StopAndWaitTransport(String server) {
		super(server);
		startThread();
		dest=server;
	}

	// constructor for a server connection

	public StopAndWaitTransport() { // nothing to do but required by Java
		super();
		startThread();
		dest=clientName;
	}

	private synchronized int getLastAck(){
		return lastAck;
	}
	
	private void startThread() {
		new Thread(new Runnable() {	

			public void run() {
				while(true){
					if(getLastAck()==counterSend){
						final String m=buffer.receive();
						if(m==null) continue;
						counterSend++;
						//task de relance du msg
						task=new TimerTask() {
							@Override
							public void run() {
								sendMessage(dest, counterSend, m);
							}
						};
						timer.schedule(task, 0, period);
						System.out.println(counterSend+" envoyé");
					}
				}
			}
		}).start();

	}

	@Override
	synchronized boolean processAck(String packet) {
		int n=MessageFormat.getNum(packet);
		if (MessageFormat.getSource(packet).equals(dest)) {
			if(n==lastAck+1){
				lastAck++;
				System.out.println("Ack "+n);
				task.cancel();
			}
			return true;
		}
		return false;
	}

	@Override
	boolean processMessage(String packet) {
		final int n=MessageFormat.getNum(packet);
		if(MessageFormat.getSource(packet).equals(dest) && received != null) {
			sendAck(dest, n);
			if (n>counterReceived){
				counterReceived++;
				received.addMessageInBuffer(MessageFormat.getContent(packet));
			}
			return true;
		}
		return false;
	}

	@Override
	boolean processNack(String packet) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void send(String message) {
		buffer.addMessageInBuffer(message);
	}
	
	// wait until all packets are really sent
	public void flush() {
		while(true){
			if(buffer.isEmpty()) break;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		closePhysicalLayer();
	}

}
