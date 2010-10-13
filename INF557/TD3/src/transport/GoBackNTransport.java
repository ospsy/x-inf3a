package transport;

import java.util.Timer;
import java.util.TimerTask;

import common.MessageBuffer;
import common.MessageFormat;

public class GoBackNTransport extends TransportLayer {

	private int counterSend = 0;
	private int counterReceived = 0;
	private int lastAck=0;
	private MessageBuffer buffer = new MessageBuffer();
	private String dest=null;
	private TimerTask task=null;
	private static final Timer timer=new Timer();
	private final long period=1000;

	// constructor for a client connection

	public GoBackNTransport(String server) {
		super(server);
		startThread();
		dest=server;
	}

	// constructor for a server connection

	public GoBackNTransport() { // nothing to do but required by Java
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
					synchronized(this){
						final String m=buffer.receive();
						if(m==null) continue;
						counterSend++;
						sendMessage(dest, counterSend, m);
						System.out.println(counterSend+" envoyé");
					}
				}
			}
		}).start();

	}

	@Override
	synchronized boolean processAck(String packet) {
		int n=MessageFormat.getNum(packet);
		if (n==lastAck+1 && MessageFormat.getSource(packet).equals(dest)) {
			lastAck++;
			System.out.println("Ack "+n);
			task.cancel();
			return true;
		}
		return false;
	}

	@Override
	boolean processMessage(String packet) {
		final int n=MessageFormat.getNum(packet);
		if(MessageFormat.getSource(packet).equals(dest) && received != null) {
			if (n==counterReceived+1){
				task.cancel();
				counterReceived++;
				received.addMessageInBuffer(MessageFormat.getContent(packet));
			}else if(n>counterReceived+1){
				//task de relance du msg
				task=new TimerTask() {
					final int num=counterReceived;
					@Override
					public void run() {
						sendNack(dest, num);
					}
				};
				timer.schedule(task, 0, period);
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
