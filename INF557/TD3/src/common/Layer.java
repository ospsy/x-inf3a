package common;
// Any layer has only two basic function

public interface Layer {

  public void send(String message);

  public String receive();

}
