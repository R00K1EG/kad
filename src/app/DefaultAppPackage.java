package app;

import org.json.JSONObject;

import global.IIEJSONObject;
import global.IIELog;
import net.IIEPackage;
import net.UDPClient;
import tox.bean.Record;

public class DefaultAppPackage {
	private int type = Configure.APP_DEFAULT_PACKAGE;
	private String data = null;
	private int ack = 0;

	public DefaultAppPackage() {
		this.type = Configure.APP_DEFAULT_PACKAGE;
	}

	public DefaultAppPackage(int type, String msg) {
		this.type = type;
		this.data = msg;
	}

	/**
	 * obtain a find package upon to app;
	 * 
	 * @param r
	 *            the result;
	 * @return
	 */
	public static DefaultAppPackage obtainFindAppPackage(Record r) {
		DefaultAppPackage dap = new DefaultAppPackage();
		dap.setType(Configure.APP_FIND);
		String msg = dap.packingRecordIntoJSON(r);
		dap.setData(msg);
		return dap;
	}

	/**
	 * resolve the find app package
	 * 
	 * @param record
	 * @return a record that maybe null : -1;
	 */
	public int resolveFindAppPackage(Record record) {
		int result = resolveRecordInfo(record, this.data);
		return result;
	}

	/**
	 * obtain the register app package;
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public static DefaultAppPackage obtainRegisterAppPackage(String ip, int port) {
		DefaultAppPackage dap = new DefaultAppPackage();
		dap.setType(Configure.APP_REGISTER);
		String msg = null;
		try {
			IIEJSONObject data = new IIEJSONObject();
			data.put("ip", ip);
			data.put("port", port);
			msg = data.toString();
		} catch (Exception e) {
			msg = null;
			IIELog.d("DEFAULT_APP_PACKAGE", "error 1!", e);
		}
		dap.setData(msg);
		return dap;
	}

	/**
	 * obtain the info default app package
	 * 
	 * @param info
	 * @return
	 */
	public static DefaultAppPackage obtainInfoAppPackage(String info) {
		DefaultAppPackage dap = new DefaultAppPackage();
		dap.setType(Configure.APP_INFO);
		String msg = info;
		dap.setData(msg);
		return dap;
	}

	/**
	 * send this app package to app process;
	 * 
	 * @return
	 */
	public int upload() {
		IIEPackage iie = this.obtainIIEPackage(null, 0);
		if (iie == null)
			return -1;
		if (UDPClient.getAppPreocess() != null)
			UDPClient.getAppPreocess().setApp(iie);
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
		String msg = this.packagingIntoJson();
		if (msg == null)
			return null;
		else {
			return IIEPackage.obtain(net.Configure.APP_PACKAGE, msg, ip, port);
		}
	}

	/**
	 * package this DefaultAppPackage into String;
	 * 
	 * @return null, package error; json, package success;
	 */
	private String packagingIntoJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("ack", this.getAck());
			json.put("type", this.getType());
			json.put("content", this.data);
		} catch (Exception e) {
			e.printStackTrace();
			json = null;
			return null;
		}
		return json.toString();
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

}
