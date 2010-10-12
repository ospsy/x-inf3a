import java.io.PrintWriter;


public abstract class Message {
	Type type;
	
	public Type getType(){
		return type;
	}
	
	public abstract void write(PrintWriter pw);

}
