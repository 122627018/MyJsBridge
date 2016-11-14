package com.wxxiaomi.ming.myjsbridge.action.forward;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by 12262 on 2016/11/12.
 */

public class ForwardTypeAdapter implements JsonDeserializer<ForwardAction> {



    @Override
    public ForwardAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        String type_tag = json.getAsJsonObject().get("type_tag").getAsString();
        boolean isH = type_tag.equals("h5")?true:false;
        String page = json.getAsJsonObject().get("page").getAsString();
        String data  = json.getAsJsonObject().get("data").getAsString();
        boolean isRetrun = json.getAsJsonObject().get("isReturn").getAsString().equals("true")?true:false;
        String callback = json.getAsJsonObject().get("callback").getAsString();
        ForwardAction actiom = new ForwardAction(isH,page,data,isRetrun,callback);
        return actiom;
    }
    /**
     * {
     "type_tag":"h5",
     "page":"","data":"",
     "isReturn":"",
     "callback":""
     }
     */
}
