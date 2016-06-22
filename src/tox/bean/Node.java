package tox.bean;

/**
 * Created by guo on 2016/4/12.
 */
public class Node {
    private String ip = null;       //net ip
    private int port = 0;           // net port
    private String localIp = null;  //local Ip
    private int localPort = 0;      //local port
    private String nodeId = null;

    public Node(){
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

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
