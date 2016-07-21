package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


/**
 * Created by guo on 2016/4/21.
 */
public class PackageReceiver extends Thread{
	private UDPClient client;
    private byte[] buffer = new byte[Configure.PACKAGE_BUFFERSIZE];
    private DatagramSocket ds;
    public PackageReceiver(){
    	client = UDPClient.getInstance();
    	this.ds = client.getDs();
    }

    @Override
    public void run() {
    	DatagramPacket dpr = new DatagramPacket(this.buffer, this.buffer.length);
        while (true) {
            if(Configure.IsAppEnd){
            	this.ds.close();
                return;
            }
            try {
                ds.receive(dpr);
                new PackageProcess(dpr).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
