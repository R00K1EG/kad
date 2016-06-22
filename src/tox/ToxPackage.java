package tox;

import global.IIEJSONObject;
import global.IIELog;
import net.*;
import tox.bean.KadNode;
import tox.bean.Record;

import org.json.JSONObject;

import app.DefaultAppPackage;

/**
 * Created by guo on 2016/4/21.
 */
public class ToxPackage {
	private String id = null;
	private int type = 0;
	private int ack = 0;
	private long timestamp = 0L;
	private int port = 0;
	private String ip = null;
	private String msg = null;

	public ToxPackage() {
	}

	/**
	 * use to login() to get the ip and port;
	 * 
	 * @return
	 */
	public static ToxPackage obtainLoginRequest() {
		ToxPackage ping = new ToxPackage();
		ping.setType(Configure.KAD_LOGIN);
		ping.setAck(Configure.KAD_ACK_REQUEST);
		ping.setTimestamp(System.currentTimeMillis());
		ping.setId(ping.getTimestamp() + "" + ping.getType());
		JSONObject login = ping.obtainSelfIntoJson();
		if (login == null)
			ping.setMsg(null);
		else
			ping.setMsg(login.toString());
		return ping;
	}

	/**
	 * obtain a ping toxPackage
	 * 
	 * @return a ToxPackage object
	 */
	public static ToxPackage obtainPingRequest(String rid) {
		ToxPackage ping = new ToxPackage();
		ping.setType(Configure.KAD_PING);
		ping.setAck(Configure.KAD_ACK_REQUEST);
		ping.setTimestamp(System.currentTimeMillis());
		ping.setId(ping.getTimestamp() + "" + ping.getType() + rid);
		JSONObject self = ping.obtainSelfIntoJson();
		String content = null;
		if (self != null)
			content = self.toString();
		ping.setMsg(content);
		return ping;
	}

	/**
	 * response the ping : name the ack = 1;
	 * 
	 * @param id
	 * @return
	 */
	public static ToxPackage obtainPingResponse(String id) {
		ToxPackage ping = new ToxPackage();
		ping.setType(Configure.KAD_PING);
		ping.setAck(Configure.KAD_ACK_RESPONSE);
		ping.setTimestamp(System.currentTimeMillis());
		ping.setId(id);
		JSONObject self = ping.obtainSelfIntoJson();
		String content = null;
		if (self != null)
			content = self.toString();
		ping.setMsg(content);
		return ping;
	}

	/**
	 * obtain the find package;
	 * 
	 * @param re
	 * @param id
	 * @return
	 */
	public static ToxPackage obtainFindRequest(String id) {
		ToxPackage find = new ToxPackage();
		find.setType(Configure.KAD_FIND);
		find.setAck(Configure.KAD_ACK_REQUEST);
		find.setTimestamp(System.currentTimeMillis());
		find.setId(find.getTimestamp() + "" + find.getType());
		JSONObject content = find.obtainFindInfoIntoJson(id);
		if (content == null)
			return null;
		find.setMsg(content.toString());
		return find;
	}

	/**
	 * packing the find info into jsonObject;
	 */
	private JSONObject obtainFindInfoIntoJson(String id) {
		if (id == null) {
			return null;
		}
		JSONObject json = new JSONObject();
		try {
			json.put("find_id", id);
			json.put("requestor", this.obtainSelfIntoJson().toString());
		} catch (Exception e) {
			json = null;
			;
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * resolve the find ToxPackage
	 * 
	 * @param record
	 *            return the requestor info;
	 * @param content
	 *            input the receive mag
	 * @return find id and the record;
	 */
	public String resolveFindInfo(Record record, String content) {
		String findId = null;
		try {
			IIEJSONObject iieJson = new IIEJSONObject(content);
			findId = iieJson.getString("find_id");
			this.resolveRecordInfo(record, iieJson.getString("requestor"));
		} catch (Exception e) {
			findId = null;
			record = null;
			e.printStackTrace();
		}
		return findId;
	}

	/**
	 * obtain the register toxPackage£» it is used to help requester update
	 * itself info : ip & port;
	 * 
	 * @param id
	 *            the toxPackage id;
	 * @return ToxPackage to send
	 */
	public int obtainResponseRegister(ToxPackage register) {
		String ip = this.getIp();
		register.setType(Configure.KAD_FIND);
		int port = this.getPort();
		String msg = null;
		JSONObject json = new JSONObject();
		try {
			json.put("ip", this.getIp());
			json.put("port", this.getPort());
			msg = json.toString();
		} catch (Exception e) {
			json = null;
			msg = null;
			e.printStackTrace();
		}
		register.fillToxPackage(Configure.KAD_ACK_REGISTER, this.id, ip, port, msg);
		return 0;
	}

	/**
	 * resolve the find register json object;
	 * 
	 * @return
	 */
	public int resolveFindRegister() {
		try {
			IIEJSONObject iieJson = new IIEJSONObject(this.getMsg());
			String ip = iieJson.getString("ip");
			int port = iieJson.getInt("port");
			String sip = KadNode.getInstance().getIp();
			IIELog.d("upload::::" + iieJson.toString());
			DefaultAppPackage dap = DefaultAppPackage.obtainRegisterAppPackage(ip, port);
			dap.upload();
			if (sip != null) {
				if (KadNode.getInstance().getIp().equals(ip) || KadNode.getInstance().getLocalIp().equals(ip))
					return 0;
			}
			KadNode.getInstance().setIp(ip);
			KadNode.getInstance().setPort(port);
		} catch (Exception e) {
			ip = null;
			port = 0;
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	/**
	 * obtain the find response package;
	 * 
	 * @param record
	 * @param id
	 * @return
	 */
	public ToxPackage obtainFindResponsePackage(Record record, String findId) {
		ToxPackage find = new ToxPackage();
		find.setType(Configure.KAD_FIND);
		String msg = null;
		try {
			JSONObject json = new JSONObject();
			json.put("find_id", findId);
			json.put("result", this.packingRecordIntoJSON(record));
			msg = json.toString();
		} catch (Exception e) {
			msg = null;
			e.printStackTrace();
		}
		find.fillToxPackage(Configure.KAD_ACK_RESPONSE, this.getId(), this.getIp(), this.getPort(), msg);
		return find;
	}

	/**
	 * response the find response;
	 * 
	 * @return
	 */
	public int resolveFindResponsePackage() {
		try {
			IIEJSONObject iie = new IIEJSONObject(this.getMsg());
			String findId = iie.getString("find_id");
			String result = iie.getString("result");
			Record re = new Record();
			int f = this.resolveRecordInfo(re, result);
			if (f == -1) {
				IIELog.i("not find");
				DefaultAppPackage dap = DefaultAppPackage.obtainFindAppPackage(null);
				dap.upload();
				// Send not find app package
				return -1;
			}
			if(re.getNodeId() == null)
				return -1;
			if (re.getNodeId().trim().equals(findId.trim())) {
				if (KadNode.getInstance().getNodeId().trim().equals(findId)) {
					IIELog.i("REGISTER", "K bucket updates done.");
					DefaultAppPackage dap = DefaultAppPackage.obtainInfoAppPackage(app.Configure.APP_KB_UPDATE_DONE);
					dap.upload();
					// send message to app;
				} else {
					IIELog.d(re.show());
					DefaultAppPackage dap = DefaultAppPackage.obtainFindAppPackage(re);
					dap.upload();
					// Send result to app
				}
				return 0;
			}
			if (re.getNodeId().equals(KadNode.getInstance().getNodeId()))
				return 0;
			try {
				Thread.sleep(Configure.KAD_FINE_NEXT_DELAY);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ToxPackage find = ToxPackage.obtainFindRequest(findId);
			IIEPackage iief = find.obtainIIEPackage(re.getLocalIp(), re.getLocalPort());
			UDPClient.getInstance().send(iief);
			if (re.getIp() != null && !re.getIp().equals(re.getLocalIp())) {
				iief = find.obtainIIEPackage(re.getIp(), re.getPort());
				UDPClient.getInstance().send(iief);
			}
			find = null;
			iief = null;
			iie = null;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	/**
	 * obtain the find command package;
	 * 
	 * @param record
	 *            info;
	 * @return
	 */
	public ToxPackage obtainFindCommandPackage(Record record) {
		ToxPackage command = new ToxPackage();
		String msg = null;
		command.setType(Configure.KAD_FIND);
		JSONObject json = new JSONObject();
		try {
			json.put("requestor", this.packingRecordIntoJSON(record));
			json.put("mid", this.obtainSelfIntoJson().toString());
			msg = json.toString();
		} catch (Exception e) {
			json = null;
			e.printStackTrace();
		} finally {
			json = null;
		}
		command.fillToxPackage(Configure.KAD_ACK_COMMAND, this.getId(), this.getIp(), this.getPort(), msg);
		return command;
	}

	/**
	 * process the find command packet;
	 * 
	 * @param requestor
	 *            return a requestor record
	 * @param mid
	 *            and a mid record, it means who send me the package;
	 * @return 0: success -1: error
	 */
	public int reloveFindCommandPackage(Record requestor, Record mid) {
		try {
			if (this.getMsg() == null)
				return -1;
			IIEJSONObject iie = new IIEJSONObject(this.getMsg());
			String req = iie.getString("requestor");
			this.resolveRecordInfo(requestor, req);
			req = iie.getString("mid");
			this.resolveRecordInfo(mid, req);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	/**
	 * use to fill all toxPackage
	 * 
	 * @param ack
	 * @param id
	 * @param ip
	 * @param port
	 * @param msg
	 * @return
	 */
	public int fillToxPackage(int ack, String id, String ip, int port, String msg) {
		this.setAck(ack);
		this.setId(id);
		this.setIp(ip);
		this.setPort(port);
		this.setMsg(msg);
		this.setTimestamp(System.currentTimeMillis());
		return 0;
	}

	/**
	 * obtain the iie_package
	 * 
	 * @param ip
	 *            the target ip, use to send;
	 * @param port
	 *            the target port use to send;
	 * @return null or a object
	 */
	public IIEPackage obtainIIEPackage(String ip, int port) {
		String msg = this.packagingIntoJson().toString();
		if (msg == null)
			return null;
		else {
			return IIEPackage.obtain(net.Configure.KAD_PACKAGE, msg, ip, port);
		}
	}

	/**
	 * package this ToxPackage into jsonObject;
	 * 
	 * @return null, package error; json, package success;
	 */
	private JSONObject packagingIntoJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("type", this.getType());
			json.put("ack", this.getAck());
			json.put("timestamp", this.getTimestamp());
			json.put("id", this.getId());
			json.put("content", this.msg);
		} catch (Exception e) {
			e.printStackTrace();
			json = null;
		}
		return json;
	}

	/**
	 * receive the toxPackage from IIEPackage
	 * 
	 * @param iiePackage
	 *            receiving with net
	 * @return null or toxPackage
	 */
	public static ToxPackage receiver(IIEPackage iiePackage) {
		ToxPackage tox = new ToxPackage();
		tox.setIp(iiePackage.getTargetIp());
		tox.setPort(iiePackage.getTargetPort());
		IIEJSONObject iieJson = null;
		try {
			iieJson = new IIEJSONObject(iiePackage.getMsg());
			tox.setId(iieJson.getString("id"));
			tox.setType(iieJson.getInt("type"));
			tox.setAck(iieJson.getInt("ack"));
			tox.setTimestamp(iieJson.getLong("timestamp"));
			tox.setMsg(iieJson.getString("content"));
		} catch (Exception e) {
			tox = null;
			e.printStackTrace();
		}
		return tox;
	}

	/**
	 * package self info into json and then send to other node;
	 * 
	 * @return json or null;
	 */
	private JSONObject obtainSelfIntoJson() {
		KadNode kad = KadNode.getInstance();
		if (kad == null)
			return null;
		JSONObject json = new JSONObject();
		try {
			json.put("id", kad.getNodeId());
			json.put("ip", kad.getIp());
			json.put("port", kad.getPort());
			json.put("lip", kad.getLocalIp());
			json.put("lport", kad.getLocalPort());
			json.put("fingerprint", kad.getPb_k());
		} catch (Exception e) {
			e.printStackTrace();
			json = null;
		}
		return json;
	}

	/**
	 * resolve the content json, maybe only node info, and then generate a
	 * record;
	 * 
	 * @param msg
	 *            contain the node info;
	 * @return a record or null;
	 */
	public int resolveRecordInfo(Record record, String msg) {
		// Record record = new Record();
		if (msg == null) {
			record = null;
			return -1;
		}
		IIEJSONObject iieJson = null;
		try {
			iieJson = new IIEJSONObject(msg);
			record.setNodeId(iieJson.getString("id"));
			record.setIp(iieJson.getString("ip"));
			record.setPort(iieJson.getInt("port"));
			record.setLocalIp(iieJson.getString("lip"));
			record.setLocalPort(iieJson.getInt("lport"));
			record.setFingerprint(iieJson.getString("fingerprint"));
		} catch (Exception e) {
			record = null;
			return -1;
		}
		return 0;
	}

	/**
	 * @param record
	 * @return string,jsonObject.toString()
	 */
	public String packingRecordIntoJSON(Record record) {
		String result = null;
		if (record == null)
			return null;
		JSONObject json = new JSONObject();
		try {
			json.put("id", record.getNodeId());
			json.put("ip", record.getIp());
			json.put("port", record.getPort());
			json.put("lip", record.getLocalIp());
			json.put("lport", record.getLocalPort());
			json.put("fingerprint", record.getFingerprint());
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
			return null;
		}
		result = json.toString();
		return result;
	}

	/**
	 * show the tox package;
	 * 
	 * @return
	 */
	public String show() {
		String self = "";
		self += "ToxPackage:" + this.id + "\n" + this.ip + "\t" + this.getPort() + "\n" + this.getTypeStr() + "\t"
				+ this.getAckStr() + "\n" + this.getMsg();
		return self;
	}

	/**
	 * get the type using string style;
	 * 
	 * @return
	 */
	private String getTypeStr() {
		String stype = "";
		switch (this.type) {
		case Configure.KAD_FIND:
			stype = "KAD_FIND";
			break;
		case Configure.KAD_PING:
			stype = "KAD_PING";
			break;
		default:
			stype = "KAD_ERROR";
		}
		return stype;
	}

	private String getAckStr() {
		String sack = "";
		switch (this.ack) {
		case Configure.KAD_ACK_REGISTER:
			sack = "KAD_ACK_REGISTER";
			break;
		case Configure.KAD_ACK_REQUEST:
			sack = "KAD_ACK_REQUEST";
			break;
		case Configure.KAD_ACK_RESPONSE:
			sack = "KAD_ACK_RESPONSE";
			break;
		case Configure.KAD_ACK_COMMAND:
			sack = "KAD_ACK_COMMAND";
			break;
		default:
			sack = "ERROR_ACK";
		}
		return sack;
	}

	/**
	 * getter and setter;
	 */

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
