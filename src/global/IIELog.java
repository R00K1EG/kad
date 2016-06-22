package global;



public class IIELog {
	
	//put the msg info linkedqueue
	private static void add(String i){
		String info = Time.covertToYMD(System.currentTimeMillis()) + ":\t" + i + "\n";
		try {
			if(WriteFileByAppend.infos == null){
				new WriteFileByAppend("./log.txt").start();
			}
			WriteFileByAppend.infos.put(info);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * log d
	 * @param tag
	 * @param content
	 */
	public static void d(String tag, String content){
		if(Configure.DEBUG == 1){
			String info = tag + ":\t" + content;
			add(info);
			System.out.println(info);
		}
	}
	
	/**
	 * only output the debug msg;
	 * @param content
	 */
	public static void d(String content){
		if(Configure.DEBUG == 1){
			add(content);
			System.out.println(content);
		}
	}
	
	/**
	 * output tag, info, and exception
	 * @param tag
	 * @param content
	 * @param e
	 */
	public static void d(String tag, String content, Exception e){
		if(Configure.DEBUG == 1){
			add(tag + ":\t" + content + "\n" + e.toString());
			System.out.println(tag + ":\t" + content + "\n" + e.toString());
		}
	}
	
	/**
	 * output information
	 * @param msg
	 */
	public static void i(String msg){
		if(Configure.INFO == 1){
			add(msg);
			System.out.println(msg);
		}
	}
	
	/**
	 * output info with tag
	 * @param tag
	 * @param msg
	 */
	public static void i(String tag, String msg){
		if(Configure.INFO == 1){
			add(tag + ":\t" + msg);
			System.out.println(tag + ":\t" + msg);
		}
	}
}
