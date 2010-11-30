package connexion;

import message.Identifiant;
import message.Result;

public class QueryResult {
	private String IP;
	private int port;
	private long speed;
	private long index;
	private long size;
	private String name;
	private Identifiant serventId;

	public QueryResult(String ip, int port, long speed, Result result, Identifiant serventId) {
		index=result.getFileIndex();
		size=result.getFileSize();
		name=result.getSharedFileName();
		this.port=port;
		this.IP=ip;
		this.speed=speed;
		this.serventId=serventId;
	}

	public String getIP() {
		return IP;
	}
	public int getPort() {
		return port;
	}
	public long getSpeed() {
		return speed;
	}
	public long getIndex() {
		return index;
	}
	public long getSize() {
		return size;
	}
	public String getName() {
		return name;
	}
	public Identifiant getServentId() {
		return serventId;
	}
	
	@Override
	public String toString() {
		return name+", "+size+"Kb, index="+index+" | "+IP+":"+port+", speed="+speed+"Kb/s | serventId="+serventId;
	}
}
