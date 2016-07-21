package tox.bean;

import global.Time;

/**
 * Created by guo on 2016/4/12.
 */
public class Record extends Node implements Cloneable{

    private long timestamp = 0;
    private String fingerprint = null;

    public Record(){
    	this.timestamp = System.currentTimeMillis();
    }

    public Record(long tmp){
        this.timestamp = tmp;
    }

    /**
     * copy the target record's info
     * @param re
     * @return
     */
    public int copy(Record re){
    	this.setNodeId(re.getNodeId());
    	this.setIp(re.getIp());
    	this.setPort(re.getPort());
    	this.setLocalIp(re.getLocalIp());
    	this.setLocalPort(re.getLocalPort());
    	this.setFingerprint(re.getFingerprint());
    	this.setTimestamp(re.getTimestamp());
    	return 0;
    }
    
    /**
     * check the target is same as this target or not;
     * @param re
     * @return
     */
    public boolean isSame(Record re){
    	if(!this.getNodeId().equals(re.getNodeId()))
    		return false;
    	if(!this.getLocalIp().equals(re.getLocalIp()))
    		return false;
    	if(this.getLocalPort() != re.getLocalPort())
    		return false;
    	if(this.getPort() != re.getPort())
    		return false;
    	if(this.getIp() != null && !this.getIp().equals(re.getIp()))
    		return false;
    	return true;
    }
    
    
    public String show(){
        return  "\tID: " + this.getNodeId();// +
               // "\n\tIP: " + this.getIp() +
               // "\tLocalIp: " + this.getLocalIp() +
               // "\n\t time: " + Time.covertToYMD(this.getTimestamp()) +
               // "\n\t fingerprint: " + this.getFingerprint();
    }
    
    public String toString(){
    	return  "Record{ID=" + this.getNodeId() +
         ", IP=" + this.getIp() +
         ",LocalIp=" + this.getLocalIp() +
         ",time=" + Time.covertToYMD(this.getTimestamp()) +
         ",fingerprint=" + this.getFingerprint() + "}\n";
    }

    public Record clone(){
    	Record o = null;
    	try{
    		o = (Record)super.clone();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return o;	
    }
    
    
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
