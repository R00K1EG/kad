package net;

import java.net.DatagramPacket;

import global.IIELog;

public class PackageSingleSender extends Thread{

	/**
	 * single thread sender
	 * 
	 * @param iie
	 * @return
	 */
	@Override
	public void run() {
		IIEPackage iie = null;
		while (true) {
			try {
				iie = UDPClient.packageQueue.take();
				DatagramPacket dpc = iie.packaging();
				if (UDPClient.getInstance() == null) {
					System.out.println("failed to get udp client");
					return ;
				}
				if (dpc != null) {
					try {
						IIELog.d("Send_data", new String(dpc.getData(), 0, dpc.getData().length));
						IIELog.d("Send_ip", dpc.getAddress().getHostAddress());
						IIELog.d("Send_port", dpc.getPort() + "");
						UDPClient.getInstance().getDs().send(dpc);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				return;
			}
		}
	}
}
