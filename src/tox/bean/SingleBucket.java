package tox.bean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import tox.Configure;
import tox.ToxGlobalFunc;

public class SingleBucket {
	private volatile int size;
	private final int max;
	private volatile LinkedList<Record> list;

	public SingleBucket() {
		this.list = new LinkedList<Record>();
		this.max = Configure.KAD_BUCKET_SIZE;
		this.size = 0;
	}

	public SingleBucket(int length) {
		this.max = length;
		this.size = 0;
		this.list = new LinkedList<Record>();
	}

	/**
	 * get all record;
	 * 
	 * @return
	 */
	public List<Record> all() {
		List<Record> tmplist = new ArrayList<Record>();
		if (this.size == 0) {
			return tmplist;
		}
		tmplist.addAll(this.list);
		return tmplist;
	}

	/**
	 * add record
	 * 
	 * @return
	 */
	public synchronized void add(Record r) {
		if (this.size < this.max) {
			synchronized (this) {
				this.list.add(r.clone());
				this.size++;
				return;
			}
		} else {
			synchronized (this) {
				this.list.removeFirst();
				this.list.add(r.clone());
				return;
			}
		}
	}

	/**
	 * delete a element
	 * 
	 * @param e
	 * @return
	 */
	public synchronized boolean remove(Record e) {
		synchronized (this) {
			for (Record r : this.list) {
				if (r.getNodeId().equals(e.getNodeId())) {
					this.list.remove(r);
					this.size--;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * select a record
	 * 
	 * @param id
	 * @return
	 */
	public synchronized Record select(String id) {
		for (Record r : this.list) {
			if (r.getNodeId().equals(id)) {
				synchronized (this) {
					this.list.remove(r);
					this.list.add(r.clone());
				}
				return r;
			}
		}
		return null;
	}

	/**
	 * update the bucket;
	 * 
	 * @param e
	 */
	public synchronized boolean update(Record e) {
		synchronized (this) {
			for (Record tmp : this.list) {
				if (tmp.getNodeId().equals(e.getNodeId())) {
					this.list.remove(tmp);
					this.list.add(e.clone());
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * return the oldest record;
	 * @return
	 */
	public Record check(){
		return this.list.getFirst();
	}
	
	/**
	 * return the closest;
	 * 
	 * @param id
	 * @return
	 */
	public synchronized Record findCloseRecord(String id) {
		Record r = null;
		int close = 160;
		if (this.size == 0) {
			return null;
		}
		if (this.size == 1) {
			return this.list.getFirst();
		}
		for (Record e : this.list) {
			int tmp = ToxGlobalFunc.getKofBucket(e.getNodeId(), id);
			if (tmp < close) {
				r = e;
				close = tmp;
			}
		}
		return r.clone();
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getMax() {
		return max;
	}
}
