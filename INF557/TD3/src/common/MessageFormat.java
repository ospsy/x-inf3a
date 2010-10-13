package common;

public class MessageFormat {
  // do not forget to expand this method
  // if new message types are defined
  // Be aware that election messages are not
  // taken into account here!
  public static boolean isGoodType(String type) {
    return type.equals("SERVICES") || type.equals("DISCOVER")
        || type.equals("CONNECT") || type.equals("ACCEPT")
        || type.equals("MESSAGE") || type.equals("ACK") || type.equals("NACK")
        || type.equals("JOIN");
  }

  public static boolean isOfType(String message, String type, int fields) {
    if (message == null)
      return false;
    String[] t = message.split(":", -1);
    return t.length >= fields && t[2].equals(type);
  }

  public static boolean isSERVICES(String message) {
    return isOfType(message, "SERVICES", 3);
  }

  public static boolean isDISCOVER(String message) {
    return isOfType(message, "DISCOVER", 3);
  }

  public static boolean isCONNECT(String message) {
    return isOfType(message, "CONNECT", 3);
  }

  public static boolean isACCEPT(String message) {
    return isOfType(message, "ACCEPT", 3);
  }

  public static boolean isMESSAGE(String message) {
    return isOfType(message, "MESSAGE", 5);
  }

  public static boolean isACK(String message) {
    return isOfType(message, "ACK", 4);
  }

  public static boolean isNACK(String message) {
    return isOfType(message, "NACK", 4);
  }

  public static String getSource(String message) {
    return message.split(":")[1];
  }

  public static String getDest(String message) {
    return message.split(":")[0];
  }

  public static int getNum(String message) {
    try {
      return Integer.parseInt(message.split(":")[3]);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  public static String getContent(String message) {
    return message.split(":", -1)[4];
  }

  private static String packet(String... fields) {
    String p = "";
    for (String s : fields)
      p += s.replace(':', ' ') + ':';
    return p;
  }

  public static String services(String source, String dest) {
    return packet(dest, source, "SERVICES");
  }

  public static String discover(String source, String dest) {
    return packet(dest, source, "DISCOVER");
  }

  public static String connect(String source, String dest) {
    return packet(dest, source, "CONNECT");
  }

  public static String accept(String source, String dest) {
    return packet(dest, source, "ACCEPT");
  }

  public static String message(String source, String dest, int num, String msg) {
    return packet(dest, source, "MESSAGE", "" + num, msg);
  }

  public static String ack(String source, String dest, int num) {
    return packet(dest, source, "ACK", "" + num);
  }

  public static String nack(String source, String dest, int num) {
    return packet(dest, source, "NACK", "" + num);
  }

  // etc
}
