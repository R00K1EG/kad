package tox;

import global.IIELog;
import net.*;
import tox.bean.IIEMessage;
import tox.bean.KadNode;
import tox.bean.MiddlewareData;
import tox.bean.Record;

/**
 * Created by guo on 2016/4/18.
 */
public class KadPackageProcess extends Thread {

	private ToxPackage tox = null;
	private KadNode kad;
	private MiddlewareData mdata;
	private UDPClient client;
	public KadPackageProcess(IIEPackage iiePackage) {
		this.tox = ToxPackage.receiver(iiePackage);
		this.mdata = MiddlewareData.getMiddleware();
		this.kad = KadNode.getInstance();
		this.client = UDPClient.getInstance();
	}

	@Override
	public void run() {
		if (net.Configure.IsAppEnd)
			return;
		if (this.tox == null)
			return;
		int type = this.tox.getType();
		switch (type) {
		case Configure.KAD_LOGIN:
			processLogin();
			break;
		case Configure.KAD_PING:
			processKadPing(this.tox);
			break;
		case Configure.KAD_FIND:
			processKadFind(this.tox);
			break;
		}
	}

	/**
	 * process the login and request the ip and port;
	 * 
	 * @return
	 */
	private int processLogin() {
		int ack = tox.getAck();
		switch (ack) {
		case Configure.KAD_ACK_REQUEST:
			processLoginRequest();
		}

		return 0;
	}

	/**
	 * process the login package; in this function: we need to check that the
	 * Record is changed or not; if changed, we need to update the record in
	 * k-bucket with finding itself; if not changed, just tell it the ip and
	 * port;
	 * 
	 * @return
	 */
	private int processLoginRequest() {
		Record record = new Record();
		int result = this.tox.resolveRecordInfo(record, this.tox.getMsg());
		if (result == 0) {
			// response login info;
			responseRegister();
			IIEPackage iie = null;
			// find the closest node of record;
			Record closestor = new Record();
			result = this.kad.check(record, closestor);

			// update the record info;
			record.setIp(this.tox.getIp());
			record.setPort(this.tox.getPort());

			// if changed, broadcast;
			if (result == Configure.RECORD_CHANGED) {
				// if changed, update
				ToxPackage command = null;

				// record is not in the k-buckets;
				if (closestor.getNodeId().equals(record.getNodeId())) {
					this.kad.remove(record);
					closestor = this.kad.query(record.getNodeId());
				}
				if (closestor != null) {
					String mip = closestor.getIp();
					String mlip = closestor.getLocalIp();
					int mport = closestor.getPort();
					int mlport = closestor.getLocalPort();

					command = this.tox.obtainFindCommandPackage(record);
					iie = command.obtainIIEPackage(mip, mport);
					this.client.send(iie);
					if (mip != mlip) {
						command = this.tox.obtainFindCommandPackage(record);
						iie = command.obtainIIEPackage(mlip, mlport);
						this.client.send(iie);
					}
					// send the record (login node to update its info)
					command = this.tox.obtainFindResponsePackage(closestor, record.getNodeId());
					iie = command.obtainIIEPackage(this.tox.getIp(), this.tox.getPort());
					this.client.send(iie);
					return 0;
				}
			}
			this.kad.update(record);
			this.mdata.putTimeRecord(record.getNodeId(), 1);
		}
		return 0;
	}

	/**
	 * just response a tox_package filled with node network_info
	 */
	private void responseRegister() {
		ToxPackage loginResponse = new ToxPackage();
		this.tox.obtainResponseRegister(loginResponse);
		IIEPackage iie = loginResponse.obtainIIEPackage(this.tox.getIp(), this.tox.getPort());
		this.client.send(iie);
	}

	/**
	 * process the ping package. just two value: ack == 0, sb. ping me; ack ==
	 * 1, sb. response my ping;
	 * 
	 * @param tox
	 *            the received packet
	 * @return
	 */
	private int processKadPing(ToxPackage tox) {
		int ack = tox.getAck();
		String id = tox.getId();
		long timestamp = tox.getTimestamp();
		String content = tox.getMsg();
		switch (ack) {
		case Configure.KAD_ACK_REQUEST:
			Record record = new Record();
			tox.resolveRecordInfo(record, content);
			String st = timestamp + "" + tox.getType() + this.kad.getNodeId();
			if (!id.equals(st))
				return 0;
			if(record.getNodeId().equals(this.kad.getNodeId()))
				return 0;
			ToxPackage response = ToxPackage.obtainPingResponse(id);
			response.obtainIIEPackage(tox.getIp(), tox.getPort());
			this.kad.update(record);
			this.mdata.putTimeRecord(record.getNodeId(), 1);
			break;
		case Configure.KAD_ACK_RESPONSE:
			if (System.currentTimeMillis() - timestamp > Configure.KAD_PING_DELAY)
				break;
			KadNode.mq.put(id, IIEMessage.obtain());
			break;
		}
		return 0;
	}

	/**
	 * process the find package. just three value :sb.1 ack == 0, sb0. request
	 * me find sb.2 sb.0 ack == 1, sb1. response me maybe sb.3(maybe sb.2) sb.3
	 * ack == 2, sb1. command me receive sb.1's package
	 * 
	 * @param tox
	 * @return
	 */
	private int processKadFind(ToxPackage tox) {
		int ack = tox.getAck();
		switch (ack) {
		case Configure.KAD_ACK_REQUEST:
			processKadFindRequest();
			break;
		case Configure.KAD_ACK_RESPONSE:
			processKadFindResponse();
			break;
		case Configure.KAD_ACK_REGISTER:
			processKadFindRegister();
			break;
		case Configure.KAD_ACK_COMMAND:
			processKadFindCommand();
			break;
		default:
			System.out.println("Error Packet.");
		}
		return 0;
	}

	/**
	 * process the kad find request();
	 * 
	 * @return
	 */
	private int processKadFindRequest() {
		Record requestor = new Record();
		String findId = this.tox.resolveFindInfo(requestor, this.tox.getMsg());
		if (findId == null) {  //resolve error;
			return -1;
		}
		if (requestor.getNodeId() == null)  //receive info error;
			return -1;
		if(requestor.getNodeId().equals(this.kad.getNodeId()))  //requestor is myself;
			return -1;
		IIELog.d("Requestor", requestor.show());
		String rip = requestor.getIp();
		if (rip == null) {
			responseRegister(); // response the network info;
			requestor.setIp(this.tox.getIp());
			requestor.setPort(this.tox.getPort());
		}
		if(rip != null && !rip.equals(this.tox.getIp())){
			String lip = requestor.getLocalIp();
			String slip = this.kad.getLocalIp();
			String sip = this.kad.getIp();
			if(!ToxGlobalFunc.isInLAN(lip, slip, rip, sip)){
				responseRegister(); // response the network info;
				requestor.setIp(this.tox.getIp());
				requestor.setPort(this.tox.getPort());
			}
			
		}
		Record find = this.kad.query(findId);
		ToxPackage command = null;
		IIEPackage iie = null;
		if (find != null) {
			command = this.tox.obtainFindCommandPackage(requestor);
			iie = command.obtainIIEPackage(find.getIp(), find.getPort());
			this.client.send(iie);
			if (find.getIp() != find.getLocalIp()) {
				command = this.tox.obtainFindCommandPackage(requestor);
				iie = command.obtainIIEPackage(find.getLocalIp(), find.getLocalPort());
				this.client.send(iie);
			}
		}
		command = this.tox.obtainFindResponsePackage(find, findId);
		iie = command.obtainIIEPackage(this.tox.getIp(), this.tox.getPort());
		this.client.send(iie);
		this.kad.update(requestor);
		this.mdata.putTimeRecord(requestor.getNodeId(), 1);
		return 0;
	}

	/**
	 * process the kad find response();
	 * 
	 * @return
	 */
	private int processKadFindResponse() {
		this.tox.resolveFindResponsePackage();
		return 0;
	}

	/**
	 * process the kad register package
	 * 
	 * @return
	 */
	private int processKadFindRegister() {
		int result = this.tox.resolveFindRegister();
		if (result == 0) {
			// push a success app package
			// System.out.println("Update ok.");
		} else {
			// push a error app package
			// System.out.println("Update error.");
		}
		return 0;
	}

	/**
	 * process the kad find command package: update the mid record; ping the
	 * requestor;
	 * 
	 * @return
	 */
	private int processKadFindCommand() {
		Record requestor = new Record();
		Record mid = new Record();
		int result = this.tox.reloveFindCommandPackage(requestor, mid);
		if (result == -1)
			return -1;
		if (requestor.getNodeId() != KadNode.getInstance().getNodeId()) {
			// ping 5 times;
			for (int i = 0; i < 5; i++) {
				this.kad.ping(requestor);
			}
			this.kad.update(mid);
			this.mdata.putTimeRecord(mid.getNodeId(), 1);
		}
		return 0;
	}

}
