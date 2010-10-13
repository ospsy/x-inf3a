package transport;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import common.MessageBuffer;
import common.MessageFormat;

public class GoBackNTransport extends TransportLayer {
	private final GoBackNTransport me=this;
	private int counterSending = 0;
	private int counterSent = 0;
	private int counterReceived = 0;
	private LinkedList<String> messagesEnEnvoi= new LinkedList<String>();
	private MessageBuffer buffer = new MessageBuffer();
	private String dest=null;
	private TimerTask task=new TimerTask() {
		@Override
		public void run() {	}
	};
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
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				sendNack(dest, counterReceived);
				
			}
		}, 0, 5000);
	}
	
	private void startThread() {
		new Thread(new Runnable() {	

			public void run() {
				while(true){
					synchronized(me){
						if(counterSending-counterSent>=messagesEnEnvoi.size()){
							final String m=buffer.receive();
							if(m==null) continue;//rien à envoyer
							messagesEnEnvoi.add(m);
						}
						counterSending++;
						sendMessage(dest, counterSending, messagesEnEnvoi.get(counterSending-counterSent-1));
						System.out.println(counterSending+" envoyé");
					}
				}
			}
		}).start();

	}

	@Override
	synchronized boolean processAck(String packet) {
		return false;
	}

	@Override
	boolean processMessage(String packet) {
		final int n=MessageFormat.getNum(packet);
		if(MessageFormat.getSource(packet).equals(dest) && received != null) {
			if (n==counterReceived+1){
				task.cancel();//si on renvoyait un nack, on arrête
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
		int n=MessageFormat.getNum(packet);
		if (MessageFormat.getSource(packet).equals(dest)) {
			synchronized (me) {
				counterSending=MessageFormat.getNum(packet);
				while(MessageFormat.getNum(messagesEnEnvoi.getFirst())<=counterSending)
					messagesEnEnvoi.remove();
				counterSent=counterSending;
			}
			System.out.println("Nack "+n);
			return true;
		}
		return false;
	}

	@Override
	public void send(String message) {
		buffer.addMessageInBuffer(message);
	}
	
	// wait until all packets are really sent
	public void flush() {
		while(true){
			if(messagesEnEnvoi.isEmpty()) break;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		closePhysicalLayer();
	}

}
