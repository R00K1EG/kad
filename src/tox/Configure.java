package tox;

/**
 * Created by guo on 2016/4/13.
 */
public class Configure {

    public static final int KAD_PING = 0x10;
    public static final int KAD_FIND = 0x11;
    public static final int KAD_LOGIN = 0x12;
    
    public static final int KAD_ACK_RESPONSE = 1;
    public static final int KAD_ACK_REQUEST = 0;
    public static final int KAD_ACK_COMMAND = 2;
    public static final int KAD_ACK_REGISTER = 3;
    
    
    public static final int KAD_PING_DELAY = 1000;
    
    public static final int KAD_FINE_NEXT_DELAY = 50;
    
    //Kbuckets set
    public static final int KAD_BUCKET_SIZE = 6;
    public static final int KAD_BUCKET_BACK_A = 1;
    
    //record set
    public static final int RECORD_NOT_CHANGED = 0;
    public static final int RECORD_CHANGED = 1;
    public static final int SELF_K_BUCKET_NULL = 2;

    
    public static final int SINGLE_PING_TIME = 500;
    
}
