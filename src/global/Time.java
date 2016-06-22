package global;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guo on 2016/4/12.
 */
public class Time {

    public static String covertToYMD(long ls){
        Date nowTime = new Date(ls);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;
    }

}
