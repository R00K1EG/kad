package global;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tox {

	public static int matchIp(String ipa){
		 if(ipa == null)
	            return 0;
	        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
	                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
	                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
	                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
	        Pattern pattern = Pattern.compile(ip);
	        Matcher matcher = pattern.matcher(ipa);
	        if(matcher.find())
	            return 1;
	        return 0;
	}
	
}
