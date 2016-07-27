package global;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IIELog {
	
	
	/**
	 * Log info: pattern: [TYPE] [Time] <TAG> [MAG] <EXCEPTION>
	 * 
	 */

	public static void i(String msg) {
		String info = "";
		if ((Configure.INFO + Configure.LOG_FILE) >= 1) {
			info = "[INFO]  " + covertToYMDHMS() + "  " + msg;
		}
		if (Configure.INFO == 1) {
			System.out.println(info);
		}
		if (Configure.LOG_FILE == 1) {
			write(info);
		}
	}

	public static void i(String tag, String info) {
		String msg = "";
		if ((Configure.INFO + Configure.LOG_FILE) >= 1) {
			msg = "[INFO]  " + covertToYMDHMS() + " [" + tag + "] " + msg;
		}
		if (Configure.INFO == 1) {
			System.out.println(msg);
		}
		if (Configure.LOG_FILE == 1) {
			write(msg);
		}
	}

	public static void i(String tag, String info, Exception e) {
		String msg = "";
		if ((Configure.INFO + Configure.LOG_FILE) >= 1) {
			msg = "[INFO]  " + covertToYMDHMS() + " [" + tag + "] " + msg + "\n" + e.getMessage();
		}
		if (Configure.INFO == 1) {
			System.out.println(msg);
		}
		if (Configure.LOG_FILE == 1) {
			write(msg);
		}
	}

	public static void d(String msg) {
		String info = "";
		if ((Configure.DEBUG + Configure.LOG_FILE) >= 1) {
			info = "[DEBUG]  " + covertToYMDHMS() + "  " + msg;
		}
		if (Configure.DEBUG == 1) {
			System.out.println(info);
		}
		if (Configure.LOG_FILE == 1) {
			write(info);
		}
	}

	public static void d(String tag, String info) {
		String msg = "";
		if ((Configure.DEBUG + Configure.LOG_FILE) >= 1) {
			msg = "[DEBUG]  " + covertToYMDHMS() + " [" + tag + "] " + msg;
		}
		if (Configure.DEBUG == 1) {
			System.out.println(msg);
		}
		if (Configure.LOG_FILE == 1) {
			write(msg);
		}
	}

	public static void d(String tag, String info, Exception e) {
		String msg = "";
		if ((Configure.DEBUG + Configure.LOG_FILE) >= 1) {
			msg = "[DEBUG]  " + covertToYMDHMS() + " [" + tag + "] " + msg + "\n" + e.getMessage();
		}
		if (Configure.DEBUG == 1) {
			System.out.println(msg);
		}
		if (Configure.LOG_FILE == 1) {
			write(msg);
		}
	}

	/**
	 * 时间转换
	 * 
	 * @return
	 */
	public static String covertToYMDHMS() {
		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String retStrFormatNowDate = sdFormatter.format(nowTime);
		return retStrFormatNowDate;
	}
	
	private static synchronized void write(String log){
		BufferedWriter bwriter = null;
		try {
			FileWriter writer = new FileWriter("./kad.log.txt", true);
			bwriter = new BufferedWriter(writer);
			bwriter.write(log);
			bwriter.newLine();
			bwriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(bwriter != null)
				try {
					bwriter.close();
				} catch (IOException e) {
				}
		}
	}
	
}
