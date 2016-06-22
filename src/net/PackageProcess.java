package net;

import tox.KadPackageProcess;

import java.net.DatagramPacket;

import global.IIELog;

/**
 * Created by guo on 2016/4/21.
 */
public class PackageProcess extends Thread{
    public DatagramPacket datagramPacket = null;
    String data = null, ip = null;
    int port = 0;
    public PackageProcess(DatagramPacket dp){
        this.datagramPacket = dp;
        this.data = new String(dp.getData(), 0, dp.getLength());
        this.ip = dp.getAddress().getHostAddress();
        this.port = dp.getPort();
        IIELog.d("data", data);
        IIELog.d("ip", ip);
        IIELog.d("port", port + "");
        
    }

    @Override
    public void run() {
        IIEPackage iiePackage = IIEPackage.receive(this.ip, this.port, this.data);
        if(iiePackage == null)
            return;
        int type = iiePackage.getType();
        switch (type){
            case Configure.APP_PACKAGE:
            	UDPClient.getAppPreocess().setApp(iiePackage);
                break;
            case Configure.KAD_PACKAGE:
                new KadPackageProcess(iiePackage).start();
                break;
        }
    }
}
