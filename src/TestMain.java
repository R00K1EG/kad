import net.UDPClient;
import tox.bean.Bridge;
import tox.bean.KadNode;

public class TestMain {

	public static void main(String[] args) {
		// UDPClient.getInstance().close();
		KadNode kad = new KadNode();
		String id = "0123456789012345678901234567890123456789";
		String id2 = "0123456789012345678901234567890123456789";
		String ip = "192.168.36.98";
		int port = 3125;
		kad.initSelf(id, null, port + "", ip, port + "");
		new UDPClient();
		//Bridge b = new Bridge(id2, "192.168.119.88", port, null);
		kad.setFirst(null);
		System.out.println("Hello, world");
		kad.register();
		kad.pinging();
	}

}
