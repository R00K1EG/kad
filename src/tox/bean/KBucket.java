package tox.bean;

import tox.Configure;

/**
 * Created by guo on 2016/4/13.
 */
public class KBucket {

	private DoubleLinkedList[] recordList = null;
	private int k = 0;

	public KBucket() {

		this.recordList = new DoubleLinkedList[160];
		initBuckets();
		// Log.d("KBUCKET", "Create the KBucket success.");
	}

	// Initialize the buckets.
	private int initBuckets() {
		for (int i = 0; i < 160; i++) {
			this.recordList[i] = new DoubleLinkedList(Configure.KAD_BUCKET_SIZE);
		}
		return 0;
	}

	/**
	 * show the all buckets;
	 * 
	 * @return
	 */
	public String show() {
		String buck = "";
		String tmp = "";
		String tt = "";
		for (int i = 0; i < 160; i++) {
			tt = "\n" + i + " bucket\t";
			int size = 0;
			if((size = this.getRecordList()[i].getSize()) != 0){			
				tmp = this.getRecordList()[i].show();
				tmp = tt + "size:" + size + "\t" + tmp;
				buck += tmp;
			}else{
				tt = "";
			}
		}
		return buck;
	}

	public DoubleLinkedList[] getRecordList() {
		return recordList;
	}

	public void setRecordList(DoubleLinkedList[] recordList) {
		this.recordList = recordList;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

}
