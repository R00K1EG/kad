package net;

import org.json.JSONObject;

import global.IIEJSONObject;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by guo on 2016/4/12.
 */
public class IIEPackage {

	//private byte[] buffer = new byte[Configure.PACKAGE_BUFFERSIZE];
	private int type = 0;
	private String msg = null;
	private String targetIp = null;
	private int targetPort = 0;

	public IIEPackage() {
	}

	public IIEPackage(int type, String msg, String ip, int port) {
		this.type = type;
		this.msg = msg;
		this.targetIp = ip;
		this.targetPort = port;
	}

	/**
	 * package the iiePackage into datagramPacket;
	 * 
	 * @return dpc(DatagramPackage) it can be sent;
	 */
	public DatagramPacket packaging() {
		JSONObject json = new JSONObject();
		try {
			json.put("type", this.getType());
			json.put("data", this.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		byte[] data = json.toString().getBytes();
		//IIELog.d("DATA LENGTH", "" + data.length);
		DatagramPacket dpc = null;
		try {
			dpc = new DatagramPacket(data, data.length, InetAddress.getByName(this.getTargetIp()),
					this.getTargetPort());
			dpc.setData(data);
		} catch (Exception e) {
			e.printStackTrace();
			dpc = null;
		}
		return dpc;
	}

	/**
	 * receive the datagramPackage;
	 * 
	 * @param dpc
	 *            the receive the data;
	 * @return IIEPackage
	 */
	public static IIEPackage receive(DatagramPacket dpc) {
		IIEPackage iiePackage = new IIEPackage();
		iiePackage.setTargetIp(dpc.getAddress().getHostAddress());
		iiePackage.setTargetPort(dpc.getPort());
		String data = (new String(dpc.getData(), 0, dpc.getLength()));
		//IIELog.d("DATA",data);
		try {
			IIEJSONObject jsonObject = new IIEJSONObject(data);
			iiePackage.setType(jsonObject.getInt("type"));
			iiePackage.setMsg(jsonObject.getString("data"));
			//IIELog.d("Receiver", iiePackage.show());
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
		return iiePackage;
	}
	
	/**
	 * receive the datagram packet with ip, port and data
	 * @param ip
	 * @param port
	 * @param data
	 * @return
	 */
	public static IIEPackage receive(String ip, int port, String data){
		IIEPackage iiePackage = new IIEPackage();
		iiePackage.setTargetIp(ip);
		iiePackage.setTargetPort(port);
		try {
			IIEJSONObject jsonObject = new IIEJSONObject(data);
			iiePackage.setType(jsonObject.getInt("type"));
			iiePackage.setMsg(jsonObject.getString("data"));
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
		return iiePackage;
	}

	/**
	 * show the package content
	 * 
	 * @return
	 */
	public String show() {
		String packet = "";
		packet += "  type:" + this.getType() + "\n  ip:" + this.getTargetIp() + "\t port:" + this.getTargetPort()
				+ "\n  msg:" + this.getMsg();
		return packet;
	}

	/**
	 * generate a iie package;
	 * @param type
	 * @param msg
	 * @param ip
	 * @param port
	 * @return
	 */
	public static IIEPackage obtain(int type, String msg, String ip, int port) {
		return new IIEPackage(type, msg, ip, port);
	}

	public String getTargetIp() {
		return targetIp;
	}

	public void setTargetIp(String targetIp) {
		this.targetIp = targetIp;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
