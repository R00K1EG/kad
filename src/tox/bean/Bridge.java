package tox.bean;

public class Bridge {

	private String id = null;
	private String ip = null;
	private int port = 0;
	private String fingerprint = null;

	public Bridge() {
	}
	
	public Bridge(String id, String ip, int port, String fingerprint){
		this.id = id;
		this.ip = ip;
		this.port = port;
		this.fingerprint = fingerprint;
	}
	
	public Record convertToRecord() {
		Record re = new Record();
		re.setNodeId(this.id);
		re.setIp(this.ip);
		re.setPort(this.port);
		re.setLocalIp(this.ip);
		re.setLocalPort(this.port);
		re.setFingerprint(this.fingerprint);
		return re;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

}
