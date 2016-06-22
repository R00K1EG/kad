package global;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import net.Configure;

public class WriteFileByAppend extends Thread {
	FileWriter file = null;
	String name = "";
	// thread security queue;
	public static LinkedBlockingQueue<String> infos = new LinkedBlockingQueue<String>();

	public WriteFileByAppend(String name) {
		this.name = name;
		File f = new File(name);
		if(!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			file = new FileWriter(name, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * write the info into file;
	 * 
	 * @param info
	 */
	public void writeFile(String info) {
		try {
			if(file == null)
				file = new FileWriter(name, true);
			file.write(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			if(Configure.IsAppEnd){
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
			try {
				String info = infos.take();
				if(info != null && !info.equals("")){
					writeFile(info);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
