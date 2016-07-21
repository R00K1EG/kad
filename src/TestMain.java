import global.Tox;
import net.UDPClient;
import tox.bean.KadNode;

public class TestMain {
	private static int DEBUG = 1;

	public static void main(String[] args) {
		String ip;
		if (DEBUG == 1) {
			ip = "192.168.36.98";
		} else {
			if (args.length < 1) {
				System.out.println("Use: java -jar iie.kad.jar [ip]");
				return;
			}
			ip = args[0];
			if (Tox.matchIp(ip) == -1) {
				System.out.println("Tip: ip is error");
				return;
			}
		}
		KadNode kad = KadNode.getInstance();
		String id = "0123456789012345678901234567890123456789";
		int port = 3125;
		kad.initSelf(id, null, port + "", ip, port + "");
		UDPClient.getInstance().start();
		kad.setFirst(null);
		System.out.println("Start work");
		kad.register();
		kad.pinging();
	}
}
