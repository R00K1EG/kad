package security;

import java.util.Map;

/**
 * Created by guo on 2016/4/14.
 */
public class RSATest {

    private String publicKey;
    private String privateKey;


    public void setUp() throws Exception {
        Map<String, Object> keyMap = RSACoder.initKey("hello,world");

        publicKey = RSACoder.getPublicKey(keyMap);
        privateKey = RSACoder.getPrivateKey(keyMap);
        System.err.println("Public key: \n\r" + publicKey);
        System.err.println("Private Key\n\r" + privateKey);
    }

    public void test() throws Exception {
        System.err.println("encrypt by publick key ->decrypt by private key:");
        String inputStr = "abc";
        byte[] data = inputStr.getBytes();

        byte[] encodedData = RSACoder.encryptByPublicKey(data, publicKey);

        byte[] decodedData = RSACoder.decryptByPrivateKey(encodedData,
                privateKey);

        String outputStr = new String(decodedData);
        System.err.println("input: " + inputStr + "\n\r" + "output: " + outputStr);

    }

    public void testSign() throws Exception {
        System.err.println("sign:");
        String inputStr = "sign";
        byte[] data = inputStr.getBytes();

        byte[] encodedData = RSACoder.encryptByPrivateKey(data, privateKey);

        byte[] decodedData = RSACoder
                .decryptByPublicKey(encodedData, publicKey);

        String outputStr = new String(decodedData);
        System.err.println("input: " + inputStr + "\n\r" + "output: " + outputStr);


        System.err.println("signed:");

        String sign = RSACoder.sign(encodedData, privateKey);
        System.err.println("signed:\r" + sign);

        boolean status = RSACoder.verify(encodedData, publicKey, sign);
        System.err.println("result:\r" + status);

    }

    public static void main(String[] argv){
        RSATest rsaTest = new RSATest();
        try {
            rsaTest.setUp();
            rsaTest.test();
            rsaTest.testSign();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
