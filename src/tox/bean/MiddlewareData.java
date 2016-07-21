package tox.bean;

import java.util.concurrent.ConcurrentHashMap;

public class MiddlewareData {
	private ConcurrentHashMap<String, Integer> pingTimeRecord;
	private static MiddlewareData instance;
	private volatile int writeIntoPingTimeRecordMap;

	private MiddlewareData() {
		this.pingTimeRecord = new ConcurrentHashMap<String, Integer>();
		this.writeIntoPingTimeRecordMap = 0;
	}

	public synchronized static MiddlewareData getMiddleware() {
		if (instance == null) {
			synchronized (MiddlewareData.class) {
				instance = new MiddlewareData();
			}
		}
		return instance;
	}

	/**
	 * put the Record into map;
	 * 
	 * @param key
	 * @param value
	 */
	public void putTimeRecord(String key, Integer value) {
		if (this.writeIntoPingTimeRecordMap == 1) {
			this.pingTimeRecord.put(key, value);
		}
	}

	/**
	 * remove the time record.
	 * 
	 * @param key
	 */
	public void removeTimeRecord(String key) {
		this.pingTimeRecord.remove(key);
	}

	/**
	 * get the record is in the map or not.
	 * 
	 * @param key
	 * @return
	 */
	public Integer getTimeRecord(String key) {
		return this.pingTimeRecord.get(key);
	}

	/**
	 * start write into pingTime Map
	 */
	public void startPintTask() {
		this.writeIntoPingTimeRecordMap = 1;
	}

	/**
	 * stop write into pingTime Map
	 */
	public void stopPingTask(){
		this.writeIntoPingTimeRecordMap = 0;
		this.pingTimeRecord.clear();
	}
	
}
