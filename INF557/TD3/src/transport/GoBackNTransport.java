package transport;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.SysexMessage;

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
	private TimerTask taskVeille;
	private static final Timer timer=new Timer();
	private final long period=1000;
	private static boolean sendingNack=false;

	// constructor for a client connection

	public GoBackNTransport(String server) {
		super(server);
		startThread();
		dest=server;
	}

	// constructor for a server connection

	public GoBackNTransport() {
		super();
		dest=clientName;
		taskVeille=new TimerTask() {
			@Override
			public void run() {
				sendNack(dest, counterReceived);
			}
		};
		timer.schedule(taskVeille, 5000, 5000);
		sendingNack=false;
	}
	
	public void stopAsking(){
		if(sendingNack){
			task.cancel();
			taskVeille=new TimerTask() {
				@Override
				public void run() {
					sendNack(dest, counterReceived);
				}
			};
			timer.schedule(taskVeille, 5000, 5000);
			sendingNack=false;
		}
	}
	
	public void startAsking(int n){
		if(!sendingNack){
			//coupe la veille Nack
			taskVeille.cancel();
			//commence la demande
			task=new TimerTask() {
				final int num=counterReceived;
				@Override
				public void run() {
					sendNack(dest, num);
				}
			};
			timer.schedule(task, 0, period);
			sendingNack=true;
		}
	}

	private void startThread() {
		new Thread(new Runnable() {	

			public void run() {
				while(true){
					synchronized(me){
						if(counterSending-counterSent>=messagesEnEnvoi.size()){
							final String m=buffer.receive();
							if(m!=null) messagesEnEnvoi.add(m);
						}
						if(messagesEnEnvoi.size()>counterSending-counterSent){
							counterSending++;
							sendMessage(dest, counterSending, messagesEnEnvoi.get(counterSending-counterSent-1));
							System.out.println(counterSending+" envoyé");
						}
					}
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						System.err.println("Impossible de dormir");
						System.exit(0);
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
				stopAsking();
				counterReceived++;
				received.addMessageInBuffer(MessageFormat.getContent(packet));
			}else if(n>counterReceived+1){
				//task de relance du msg
				startAsking(n);
			}
			return true;
		}
		return false;
	}

	@Override
	synchronized boolean processNack(String packet) {
		System.out.println("Nack recu");
		int n=MessageFormat.getNum(packet);
		if(MessageFormat.getSource(packet).equals(dest)){
			System.out.println("Nack "+n);
			counterSending=MessageFormat.getNum(packet);
			for(int i=counterSent;i<counterSending;i++){
				messagesEnEnvoi.removeFirst();
			}
			counterSent=counterSending;
			System.out.println("Bien envoyé jusqu'au "+counterSent);
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
			if(messagesEnEnvoi.isEmpty() && buffer.isEmpty()) break;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		closePhysicalLayer();
	}

}
