import tox.bean.KBucket;
import tox.bean.Record;


public class TestSingleBucket {

	public static void main(String[] args){
		
//		Record[] rs = new Record[10];
//		for(int i = 0; i < 10; i++){
//			rs[i] = new Record();
//			rs[i].setNodeId("" + i);
//		}
//		
//		rs[5].setLocalIp("123.123.123.123");
//		
//		Record r = rs[5];
//		r.setIp("111.111.111.111");
//		Record t = rs[5];
//		System.out.print(t.toString());
		
		KBucket kb = KBucket.getBucket();
		Record re = new Record();
		re.setNodeId("123");
		kb.put(1, re);
		re.setNodeId("456");
		kb.put(2, re);
		re.setNodeId("789");
		kb.put(1, re);
		System.out.print(kb.toString());
	}
	
}
