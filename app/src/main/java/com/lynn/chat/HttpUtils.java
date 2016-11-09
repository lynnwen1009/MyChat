package com.lynn.chat;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 9/14/2016.
 */
public class HttpUtils {
    private static final String TAG = HttpUtils.class.getSimpleName();
    public static void sendRequest(int id,String data,HttpCallBackListener listener)throws ConnectTimeoutException,
            SocketTimeoutException,Exception{
        String url = null;
        boolean isNeedPhoto = false;
        switch(id){

            case Constants.ID.REGISTER:
                url = Constants.Register.REGISTER_URL;
                break;
            case Constants.ID.LOGIN:
                url = Constants.Login.LOGIN_URL;
                break;
            case Constants.ID.MODIFY_USERINFO:
                url = Constants.UserInfo.MODIFY_USERINFO_URL;
                isNeedPhoto = true;
                String[] strArr = data.split("&");
                if(strArr.length == 2)
                {
           //??         photo = strArr[1];
                }
                data = strArr[0];
                //??      Log.d(TAG, "photo="+photo);
                break;
            case Constants.ID.MODIFY_PSD:
                url = Constants.UserInfo.MODIFY_PSD_URL;
                break;
            case Constants.ID.GET_FRIEND_LIST:
                url = Constants.GetFriendList.GET_FRIEND_LIST_URL;
                break;
            case Constants.ID.SEARCH_USER:
                url = Constants.AddFriend.SEARCH_USER_URL;
                break;
            case Constants.ID.REMOVE_FRIEND:
                url = Constants.RemoveFriend.REMOVE_FRIEND_URL;
                break;
            default:
                Log.e(TAG, "invalid request");
                return;
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(Constants.RequestParams.DATA,data));
        HttpEntity httpEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
        httpPost.setEntity(httpEntity);

      //  HttpClient httpClient = CustomerHttpClient.getHttpClient();
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = httpClient.execute(httpPost);
        if(httpResponse.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            String respStr = EntityUtils.toString(httpResponse.getEntity());
            JSONObject respJson = new JSONObject(respStr);
            listener.httpCallBack(id, respJson);
        }else{

        }










    }
}
