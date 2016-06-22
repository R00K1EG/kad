package net;


import java.net.DatagramPacket;


/**
 * Created by guo on 2016/4/21.
 */
public class PackageSender extends Thread{

    private IIEPackage iiePackage = null;
    public PackageSender(IIEPackage iiePackage){
        this.iiePackage = iiePackage;
    }

    @Override
    public void run() {
        if(Configure.IsAppEnd)
            return;
        DatagramPacket dpc = iiePackage.packaging();
        UDPClient udpClient = UDPClient.getInstance();
        if(udpClient == null){
            System.out.println("failed to get udp client");
            return;
        }
        if(dpc != null) {
            try {
                udpClient.getDs().send(dpc);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                udpClient = null;
            }
        }
    }
}
