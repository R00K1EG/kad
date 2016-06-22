package security;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 Algorithm
 * Created by guo on 2015/10/13.
 */
public class MD5 {

    public static String TAG = "MD5";

    //Get the 132bit result.
    public static byte[] getMD5Code(String s){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(s.getBytes());
        } catch (NoSuchAlgorithmException ex) {
          //  Log.d(TAG, "generate the node id error.");
            ex.printStackTrace();
        }
        return null;
    }

    //Get the 160bit result.
    public static byte[] getNodeIdByMD5(String s){
        byte[] result = new byte[20];
        byte[] tmp = new byte[16];
        byte[] tmp1 = new byte[16];
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            tmp1 = md.digest(s.getBytes());
            tmp = md.digest(tmp1);
            //Log.d("MD5", tmp1.length + ":" + bytesToHexString(tmp1));
            //Log.d("MD5", tmp.length + ":" + bytesToHexString(tmp));
            System.arraycopy(tmp1, 0, result, 0, tmp1.length);
            System.arraycopy(tmp, 8, result, tmp1.length, 4);
        }catch (Exception e){
           // Log.d(TAG, "generate the 160 bits node id error");
            e.printStackTrace();
            return null;
        }
        return result;
    }

    /**
     * Convert byte[] to hex string.
     * First. translate the byte to Integer
     * Then.  Use the Integer.toHexString
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    //char to byte
    private static byte charToByte(char c){
        return (byte)"0123456789ABCDEF".indexOf(c);
    }

}
