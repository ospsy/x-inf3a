package message;

public class testMessage {

	
	public static void main(String[] args) {
		Result[] resultSet = new Result[2];
		Result r1 = new Result(11, 512, "toto.txt", Message.getRandomId());
		Result r2 = new Result(22, 3512, "panda.mp3");
		resultSet[0] = r1;
		resultSet[1] = r2;
		
		Message m1 = new Ping(5, 6);
		Message m2 = new Pong(Message.getRandomId(),5,2,8080,"129.104.227.1",2,520);
		Message m3 = new Query(5, 6, "banane jaune   singe".split(" "), 1000);
		Message m4 = new QueryHit(Message.getRandomId(), 5, 6, 8080, "129.104.127.1", 1000, resultSet, Message.getRandomId());
		Message m5 = new Push(Message.getRandomId(), 5, 6, 8080, "129.104.127.1", 2, Message.getRandomId());
		
		System.out.println(m1);
		System.out.println(m2);
		System.out.println(m3);
		System.out.println(m4);
		System.out.println(m5);

	}

}
