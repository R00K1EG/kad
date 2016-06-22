package net;

import java.net.DatagramSocket;
import java.util.concurrent.LinkedBlockingQueue;

import app.AppPackageProcess;

/**
 * Created by guo on 2016/4/18.
 */
public class UDPClient {

	private static final String TAG = "UDPClient";

	private DatagramSocket ds = null;
	private volatile static UDPClient instance;
	public static LinkedBlockingQueue<IIEPackage> packageQueue = new LinkedBlockingQueue<IIEPackage>();
	private static AppPackageProcess appPreocess = null;
	
	public UDPClient() {
		if (instance != null)
			return;
		try {
			this.ds = new DatagramSocket(Configure.CLIENT_PORT);
			instance = this;
			this.receiver();
			new PackageSingleSender().start();
		} catch (Exception e) {
			System.out.println(TAG + "create client error");
			e.printStackTrace();
			this.ds = null;
			instance = null;
		}
	}

	/**
	 * send the iie_package this function is the main sender in the net, and
	 * this function is to create a send thread;
	 * 
	 * @param iiePackage
	 * @return
	 */
	public synchronized int send(IIEPackage iiePackage) {
		if (iiePackage != null) {
			//System.out.println("Send");
			//new PackageSender(iiePackage).start();
			try {
				UDPClient.packageQueue.put(iiePackage);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return -1;
			}
			return 0;
			
		} else
			return -1;
	}

	/**
	 * the function is to start the receiver thread; receiver thread is single
	 * thread
	 * 
	 * @return
	 */
	private int receiver() {
		new PackageReceiver().start();
		return 0;
	}

	public static UDPClient getInstance() {
		if (instance == null) {
			synchronized (UDPClient.class) {
				if (instance == null) {
					instance = new UDPClient();
				}
			}
		}
		return instance;
	}

	public DatagramSocket getDs() {
		return ds;
	}

	public void setDs(DatagramSocket ds) {
		this.ds = ds;
	}

	public final void close() {
		if (this.ds != null)
			this.ds.close();
	}

	public static AppPackageProcess getAppPreocess() {
		return appPreocess;
	}

	public static void setAppPreocess(AppPackageProcess appPreocess) {
		UDPClient.appPreocess = appPreocess;
	}
	
}
