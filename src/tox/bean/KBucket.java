package tox.bean;

import tox.Configure;

/**
 * Created by guo on 2016/4/13.
 */
public class KBucket {

	private SingleBucket[] recordList = null;
	private int k = 0;
	private static KBucket instance;
	
	private KBucket() {
		this.recordList = new SingleBucket[160];
		initBuckets();
	}

	
	public static KBucket getBucket(){
		if(instance == null){
			synchronized(KBucket.class){
				instance = new KBucket();
			}
		}
		return instance;
	}
	
	// Initialize the buckets.
	private int initBuckets() {
		for (int i = 0; i < 160; i++) {
			this.recordList[i] = new SingleBucket(Configure.KAD_BUCKET_SIZE);
		}
		return 0;
	}

	/**
	 * find the k-th SingleBucket;
	 * @param k
	 * @return
	 */
	public SingleBucket getSingleBucket(int k){
		return this.recordList[k];
	}
	
	/**
	 * put <k, Record> into kth bucket 
	 * @param k
	 * @param e
	 */
	public void put(int k, Record e){
		if(k >= 160){
			return;
		}
		this.recordList[k].add(e);
	}
	
	/**
	 * find a record in the k bucket;
	 * @param k
	 * @param id
	 * @return
	 */
	public Record find(int k, String id){
		return this.getSingleBucket(k).select(id);
	}
	
	/**
	 * find the closest node from id in the k-th bucket
	 * @param k
	 * @param id
	 * @return
	 */
	public Record findClosestRecord(int k, String id){
		return this.getSingleBucket(k).findCloseRecord(id);
	}
	
	/**
	 * update the record;
	 * @param k
	 * @param e
	 */
	public void update(int k, Record e){
		if(!this.getSingleBucket(k).update(e)){
			this.getSingleBucket(k).add(e);
		}
	}
	
	/**
	 * remove the record in the k-th bucket.
	 * @param k
	 * @param e
	 */
	public void remove(int k, Record e){
		this.getSingleBucket(k).remove(e);
	}
	
	
	/**
	 * show the all buckets;
	 * 
	 * @return
	 */
	public String toString() {
		String buck = "KBuckets{";
		String tt = "";
		for (int i = 0; i < 160; i++) {
			tt = "\nk=" + i + ",bucket={\n";
			SingleBucket sb = this.getSingleBucket(i);
			if(sb.getSize() == 0)
				continue;
			else{
				for(Record e : sb.all()){
					tt += e.toString();
				}
			}
			tt += "}";
			buck += tt;
		}
		buck += "}\n";
		return buck;
	}

	
	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

}
