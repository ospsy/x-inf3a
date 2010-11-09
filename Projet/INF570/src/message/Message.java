package message;


public abstract class Message {
	MessageHeader header;
	
}

class Ping extends Message {
	
}

class Pong extends Message {
	private short port;
	private short[] ip;
	private short[] sharedFiles;
	private short[] sharedFilesSize;
}