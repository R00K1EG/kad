package tox.bean;

import java.util.ArrayList;
import java.util.List;

import tox.Configure;
import tox.ToxGlobalFunc;

/**
 * Created by guo on 2016/4/12.
 */
public class DoubleLinkedList {
	private int size = 0;
	private int max = Configure.KAD_BUCKET_SIZE;
	private ListNode head = null;
	private ListNode rear = null;

	public DoubleLinkedList(int length) {
		this.max = length;
		head = new ListNode();
		rear = new ListNode();
		this.size = 0;
		head.next = rear;
		head.pre = null;
		rear.next = null;
		rear.pre = head;
	}

	//return all records;
	public List<Record> all() {
		List<Record> list = new ArrayList<Record>();
		if (this.size == 0)
			return null;
		ListNode pointer = null;
		pointer = head.next;
		Record tmp = null;
		while (pointer.node != null) {
			tmp = pointer.node;
			list.add(tmp);
			pointer = pointer.next;
		}
		return list;
	}

	// node class
	private class ListNode {
		Record node = null; // saved
		ListNode next = null;
		ListNode pre = null;
	}

	// insert into a node
	public synchronized int add(Record node) {
		this.select(node.getNodeId());
		if (this.size < this.max) {
			ListNode ln = new ListNode();
			ln.node = node;
			ln.next = this.head.next;
			ln.pre = head;
			ln.next.pre = ln;
			this.head.next = ln;
			this.size++;
			if (size == 1) {
				this.rear.pre = ln;
			}
		} else {
			this.remove();
			this.add(node);
			return -1;
		}
		return 0;
	}

	//

	// return least recent node and remove it;
	public Record poll() {
		Record node = null;
		ListNode ln = this.head.next;
		node = ln.node;
		ln.next.pre = head;
		head.next = ln.next;
		this.size--;
		return node;
	}

	// remove the oldest node
	public int remove() {
		if (this.size > 0) {
			ListNode ln = this.rear.pre.pre;
			this.rear.pre = ln;
			ln.next = this.rear;
			this.size--;
			return 0;
		} else
			return -1;
	}

	// remove the record, if it exists;
	public synchronized int remove(Record r) {
		if (r == null)
			return -1;
		String id = r.getNodeId();
		ListNode ln = null, pointer = null;
		pointer = head.next;
		Record tmp = null;
		while (pointer.node != null) {
			tmp = pointer.node;
			if (tmp.getNodeId().equals(id)) {
				ln = pointer.next;
				ln.pre = pointer.pre;
				pointer.pre.next = ln;
				this.size--;
				return 0;
			}
			pointer = pointer.next;
		}
		return -1;
	}

	// select a node as node and remove it
	public synchronized Record select(String id) {
		// if(this.size == 0)
		// return null;
		ListNode ln = null, pointer = null;
		pointer = head.next;
		Record tmp = null;
		while (pointer.node != null) {
			tmp = pointer.node;
			if (tmp.getNodeId().equals(id)) {
				ln = pointer.next;
				ln.pre = pointer.pre;
				pointer.pre.next = ln;
				this.size--;
				return tmp;
			}
			pointer = pointer.next;
		}
		return null;
	}

	// element, return a Node info in the list head;
	public Record element() {
		Record node = null;
		ListNode ln = null;
		if (head.next != null) {
			ln = head.next;
			node = ln.node;
		}
		return node;
	}

	// show the list
	public String show() {
		ListNode ln = null;
		ln = this.head.next;
		String str = null;
		Record node = null;
		while (ln.node != null) {
			node = ln.node;
			str += node.show();
			ln = ln.next;
		}
		return str;
	}

	/*
	 * find the node.id = id;
	 * 
	 * @param id : target;
	 * 
	 * @return true: yes; false: no;
	 */
	public boolean find(String id) {
		ListNode pointer = head.next;
		while (pointer != null) {
			if (pointer.node != null) {
				if (pointer.node.getNodeId().equals(id))
					return true;
			}
			pointer = pointer.next;
		}
		return false;
	}

	// element a node with node.id != id
	public Record seed(String id) {
		Record node = null;
		ListNode pointer = head.next;
		while (pointer != null) {
			if (pointer.node != null) {
				if (pointer.node.getNodeId().equals(id))
					pointer = pointer.next;
				else {
					node = pointer.node;
					return node;
				}
			}
		}
		return null;
	}

	// update the sort of the node named id;
	public void update(Record n) {
		// this.select(n.getNodeId());
		this.add(n);
	}

	/**
	 * find the closest node with the given id;
	 * 
	 * @param id:the
	 *            target id;
	 * @return null: not find the closest node; node: the result;
	 */
	public Record findCloseRecord(String id) {
		Record node = null;
		ListNode pointer = head.next;
		int close = 160;
		while (pointer != null) {
			// IIELog.d("findCloseRecord", "" + pointer);
			if (pointer.node != null) {
				int tmp = ToxGlobalFunc.getKofBucket(pointer.node.getNodeId(), id);
				if (tmp < close) {
					node = pointer.node;
					close = tmp;
				}
			}
			pointer = pointer.next;
		}
		return node;
	}

	/**
	 * this function is to check that the oldest record is alive;
	 * 
	 * @return null: no record; record: oldest record;
	 */
	public Record check() {
		if (this.size > 0) {
			ListNode ln = this.rear.pre;
			Record record = null;
			record = ln.node;
			return record;
		} else
			return null;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
