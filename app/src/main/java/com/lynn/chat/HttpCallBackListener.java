package com.lynn.chat;
import org.json.JSONObject;

/**
 * Created by Administrator on 9/14/2016.
 */
public interface HttpCallBackListener {
    void httpCallBack(int id, JSONObject resp);
}
