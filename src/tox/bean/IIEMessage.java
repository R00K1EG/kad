package tox.bean;

/**
 * Created by guo on 2016/4/21.
 */
public class IIEMessage {

    int what = 0;
    public IIEMessage(){

    }

    public static IIEMessage obtain(){
        return new IIEMessage();
    }
}
