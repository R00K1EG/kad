package security;


import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * Created by guo on 2015/11/3.
 */
public class Base64Helper {

    public static String encode(byte[] bytes){
        return new BASE64Encoder().encode(bytes);
    }

    public static byte[] decode(String encodeStr) {
        byte[] bt = null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            bt = decoder.decodeBuffer(encodeStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bt;
    }
}
