package tox.bean;

import net.IIEPackage;
import net.UDPClient;
import tox.Configure;
import tox.ToxGlobalFunc;
import tox.ToxPackage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import app.DefaultAppPackage;
import global.IIELog;

/**
 * Created by guo on 2016/4/13.
 */
public class KadNode extends Node {

	public static ConcurrentHashMap<String, IIEMessage> mq = null;
	private KBucket bucket = null; // k bucket
	private UDPClient uc = null;
	private String pb_k = null;
	private String pr_k = null;
	private Bridge first = null;

	private ScheduledThreadPoolExecutor executor;
	private MiddlewareData mdata;

	private volatile static KadNode instance;

	private KadNode() {
		this.bucket = KBucket.getBucket();
		mq = new ConcurrentHashMap<String, IIEMessage>();
		this.executor = new ScheduledThreadPoolExecutor(1);
		this.mdata = MiddlewareData.getMiddleware();
	}

	public static KadNode getKadNode() {
		if (instance == null) {
			synchronized (KadNode.class) {
				instance = new KadNode();
			}
		}
		return instance;
	}

	/**
	 * query the node using specify the id;
	 * 
	 * @parameter id: the target
	 * @return null: can not find the node
	 * @return record: the record.id = id
	 * @return record: the record.id closest the id
	 */
	public synchronized Record query(String id) {
		if (this.getNodeId().equals(id))
			return null;
		int kt = this.getKofBucket(id);

		Record result = null;
		result = this.bucket.find(kt, id);

		int tmp = kt;
		while (result == null && kt >= 0) {
			result = this.bucket.findClosestRecord(kt, id);
			kt--;
		}
		kt = tmp;
		while (result == null && kt < 160) {
			result = this.bucket.findClosestRecord(kt, id);
			kt++;
		}
		IIELog.d("KAD_FIND", "closest node:" + result);
		return result;
	}

	/**
	 * update the record;
	 * 
	 * @parameter record : in the bucket @return 0;
	 * @parameter record : out of the bucket; add the record and @return -1
	 */
	public int update(Record record) {
		if (record == null) {
			return 0;
		}
		if (record.getNodeId() != null && record.getNodeId().equals(this.getNodeId()))
			return 0;
		int kt = this.getKofBucket(record.getNodeId());
		this.bucket.update(kt, record);
		return 0;
	}

	// update the k buckets;
	public int pinging() {
		// new PingThread().start();
		this.executor.scheduleWithFixedDelay(new PingTask(), 0, Configure.EXECUTE_PING_TASK_TIME / 1000,
				TimeUnit.SECONDS);
		return 0;
	}

	/**
	 * ping between nodes
	 * 
	 * @param record
	 * @return
	 */
	public int ping(Record record) {
		if (record != null && record.getNodeId().equals(this.getNodeId())) {
			return 0;
		}
		ToxPackage ping = ToxPackage.obtainPingRequest(record.getNodeId());
		IIEPackage iie = null;
		if (record.getIp() != null && !record.getIp().equals(record.getLocalIp())) {
			iie = ping.obtainIIEPackage(record.getIp(), record.getPort());
			UDPClient.getInstance().send(iie);
		}
		iie = ping.obtainIIEPackage(record.getLocalIp(), record.getLocalPort());
		UDPClient.getInstance().send(iie);
		iie = null;
		int time = 0;
		while (time < Configure.SINGLE_PING_TIME) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mq.get(ping.getId()) != null) {
				mq.remove(ping.getId());
				return 0;
			}
			time += 100;
		}
		return -1;
	}

	/**
	 * ping task;
	 * 
	 * @author guo
	 *
	 */
	class PingTask implements Runnable {

		private Map<Record, Integer> list;

		public PingTask() {
			this.list = new HashMap<Record, Integer>();
		}

		@Override
		public void run() {
			mdata.startPintTask();
			for (int i = 0; i < 160; i++) {
				SingleBucket tmp = bucket.getSingleBucket(i);
				if (tmp.getSize() != 0) {
					for (Record r : tmp.all()) {
						if (ping(r) == -1) {
							this.list.put(r, i);
						}
					}
				}
			}
			for (Map.Entry<Record, Integer> entry : this.list.entrySet()) {
				Record tmp = entry.getKey();
				int k = entry.getValue();
				if (mdata.getTimeRecord(tmp.getNodeId()) == null) {
					bucket.remove(k, tmp);
				}
			}
			this.list.clear();
			mdata.stopPingTask();
		}

	}


	/**
	 * update thread when need to wait the target ack ping
	 */
	class UpdateThread extends Thread {
		private int k = 0;
		private Record record = null;

		public UpdateThread(int k, Record r) {
			this.k = k;
			this.record = r;
		}

		@Override
		public void run() {
			SingleBucket tmpLink = KadNode.getInstance().getBucket().getSingleBucket(k);
			Record re = tmpLink.check();
			// tmpLink.remove();
			if (re == null) {
				KadNode.getInstance().getBucket().getSingleBucket(k).add(record);
				return;
			} else {
				String ip = re.getIp();
				int port = re.getPort();
				String id = null;
				ToxPackage ping = ToxPackage.obtainPingRequest(re.getNodeId());
				id = ping.getId();
				IIEPackage iiePackage = ping.obtainIIEPackage(ip, port);
				UDPClient.getInstance().send(iiePackage);
				int time = 0;
				while (time <= Configure.KAD_PING_DELAY) {
					if (KadNode.mq.get(id) != null) {
						tmpLink.update(re);
						KadNode.mq.remove(id);
						return;
					}
					try {
						Thread.sleep(10);
					} catch (Exception e) {
						return;
					}
					time += 10;
				}
				tmpLink.add(record);
			}
		}
	}

	/**
	 * register the kadNode into the web; Maybe there will generate the public
	 * key and private key;
	 * 
	 * @parameter null; default:self
	 * 
	 * @return 0 : register succeed, back a reachable node and add it into
	 *         bucket;
	 * 
	 * @return -1 : register failed, need to register into the network manually;
	 */
	public int register() {
		if (this.first == null) {
			// send message to app
			return 0;
		}
		if (this.first.getId().equals(this.getNodeId())) {
			IIELog.d("here 2");
			return -1;
		}
		IIELog.d("here 3");
		Record re = this.first.convertToRecord();
		this.update(re);
		// re = this.query(this.getNodeId());
		// if (re == null) {
		// return -1;
		// }
		if (re.getNodeId() != this.getNodeId()) {
			this.find(re, this.getNodeId());
			// IIELog.d("here 4");
		}
		return 0;
	}

	/**
	 * find the target record;
	 * 
	 * @param obj
	 *            requested object;
	 * @param target
	 *            query target;
	 * @return null or record;
	 */
	public Record find(Record obj, String target) {
		Record result = null;
		if (obj == null)
			return null;
		if (target == null)
			return null;
		if (obj.getNodeId() == target) {
			return obj;
		} else {
			ToxPackage find = ToxPackage.obtainFindRequest(target);
			IIEPackage iie = find.obtainIIEPackage(obj.getIp(), obj.getPort());
			UDPClient.getInstance().send(iie);
			if (obj.getIp() == obj.getLocalIp()) {
				iie = null;
				return result;
			}
			iie = find.obtainIIEPackage(obj.getLocalIp(), obj.getLocalPort());
			UDPClient.getInstance().send(iie);
			iie = null;
		}
		return result;
	}

	/**
	 * find interface;
	 * 
	 * @param id
	 * @return
	 */
	public int find(String id) {
		DefaultAppPackage find = null;
		Record re = this.query(id);
		if (re == null || re.getNodeId().equals(id)) {
			find = DefaultAppPackage.obtainFindAppPackage(re);
			find.upload();
			return 0;
		}
		this.find(re, id);
		return 0;
	}

	/**
	 * check the record is exist or not if exist, check that they are same or
	 * not; if same, return 0; else, return 1,
	 * 
	 * @param record
	 * @return
	 */
	public int check(Record record, Record result) {
		Record re = this.query(record.getNodeId());
		if (re == null) {
			return Configure.SELF_K_BUCKET_NULL;
		}
		if (re.getNodeId().equals(record.getNodeId()))
			if (re.isSame(record))
				return Configure.RECORD_NOT_CHANGED;
			else {
				result.setNodeId(record.getNodeId());
				return Configure.RECORD_CHANGED;
			}
		else {
			result.copy(re);
			return Configure.RECORD_CHANGED;
		}
	}

	/**
	 * remove the record form k-buckets
	 * 
	 * @param r
	 */
	public void remove(Record r) {
		String id = r.getNodeId();
		if (this.getNodeId().equals(id))
			return;
		int kt = this.getKofBucket(id);
		this.bucket.getSingleBucket(kt).remove(r);
	}

	/*
	 * login, initialize kad node;
	 * 
	 * @parameter null; default:self
	 * 
	 * @return -1: login error, maybe there this no kad node can connected then
	 * need @register
	 * 
	 * @return 0: login succeed;
	 */
	public int login() {
		if (this.first != null) {
			ToxPackage login = ToxPackage.obtainLoginRequest();
			Record re = this.first.convertToRecord();
			IIEPackage iie = login.obtainIIEPackage(re.getIp(), re.getPort());
			UDPClient.getInstance().send(iie);
		}
		return 0;
	}

	public int login(Bridge first) {
		if (first == null)
			return -1;
		else
			this.setFirst(first);
		ToxPackage login = ToxPackage.obtainLoginRequest();
		Record re = this.first.convertToRecord();
		IIEPackage iie = login.obtainIIEPackage(re.getIp(), re.getPort());
		UDPClient.getInstance().send(iie);
		return 0;
	}

	/**
	 * initialize the node values;
	 * 
	 * @param id:
	 *            the node id
	 * @param values:
	 *            values{ ip,port,localIp, localPort }
	 */
	public int initSelf(String id, String... values) {
		this.setNodeId(id);
		if (values == null)
			return 1;
		if (values.length == 4) {
			this.setIp(values[0]);
			this.setPort(Integer.parseInt(values[1]));
			this.setLocalIp(values[2]);
			this.setLocalPort(Integer.parseInt(values[3]));
		} else {
			return 2;
		}
		return 0;
	}

	/*
	 * obtain a null KadNode; of cause, it can set the attributes of the kad
	 * node
	 */
	public static KadNode obtain() {
		return new KadNode();
	}

	/*
	 * test function;
	 */
	public String site(String id) {
		int k = this.getKofBucket(id);
		return "distance : " + k;
	}

	/*
	 * calculate the distance between self and the target
	 * 
	 * @param id : target id
	 * 
	 * @return k : the distance
	 * 
	 * @return -1 : error
	 */
	private int getKofBucket(String id) {
		return ToxGlobalFunc.getKofBucket(id, this.getNodeId());
	}

	/*
	 * getter and setter
	 */

	/**
	 * single instance of this class;
	 * 
	 * @return
	 */
	public static KadNode getInstance() {
		if (instance == null) {
			synchronized (KadNode.class) {
				if (instance == null) {
					instance = new KadNode();
				}
			}
		}
		return instance;
	}

	/**
	 * show the k-buckets;
	 * @return
	 */
	public String showKBuckets(){
		return this.bucket.toString();
	}
	
	
	public KBucket getBucket() {
		return bucket;
	}

	public UDPClient getUc() {
		return uc;
	}

	public void setUc(UDPClient uc) {
		this.uc = uc;
	}

	public String getPb_k() {
		return pb_k;
	}

	public void setPb_k(String pb_k) {
		this.pb_k = pb_k;
	}

	public String getPr_k() {
		return pr_k;
	}

	public void setPr_k(String pr_k) {
		this.pr_k = pr_k;
	}

	public Bridge getFirst() {
		return first;
	}

	public void setFirst(Bridge first) {
		this.first = first;
	}

}
