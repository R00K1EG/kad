package global;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guo on 2016/4/21.
 */
public class IIEJSONObject extends JSONObject {
    public IIEJSONObject(){
        super();
    }

    public IIEJSONObject(String json) throws JSONException{
        super(json);
    }

    @Override
    public String getString(String key){
        String result = null;
        try{
            result = super.getString(key);
        }catch (Exception e){
            result = null;
        }
        return result;
    }

    @Override
    public int getInt(String key){
        int result = -1;
        try{
            result = super.getInt(key);
        }catch (Exception e){
            result = -1;
        }
        return result;
    }


}
