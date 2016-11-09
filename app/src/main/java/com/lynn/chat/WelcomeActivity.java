package com.lynn.chat;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.lynn.chattest2.R;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

public class WelcomeActivity extends Activity implements HttpCallBackListener {
    ImageView welcomeImage ;

    private static final String TAG = "WelcomeActivity";
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ActivityCollector.addActivity(this);
        mContext = this;

        welcomeImage = (ImageView)findViewById(R.id.welcome_view);
        welcomeImage.setImageResource(R.drawable.chatting_bg);
        int memoryCache = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024*1024*memoryCache/8;
        Log.d("WelcomeActivity","memoryCache:"+memoryCache);
        MyLruCache myLruCache = new MyLruCache(cacheSize);
        CacheUtils.setMyLruCache(myLruCache);

        CacheUtils.setPhotoDefaultSize(mContext);
//test



        SharedPreferences pref = CacheUtils.getAccountSharedPreferences(mContext);
    //    CacheUtils.setSharedPreferences(pref);
        final String id = pref.getString(Constants.AutoLogin.ID, "");
        final  String pwd = pref.getString(Constants.AutoLogin.PWD,"");

        Log.d(TAG," id: "+id);
        Log.d(TAG," pwd: "+pwd);
        if((id=="")||(pwd=="")){
            Log.d(TAG,"account not remembered can not login automatically,go to login activity");
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }
        else{
            new Thread(new Runnable() {



                @Override
                public void run() {
                    JSONObject reqJson = new JSONObject();
                    try {
                        reqJson.put(Constants.Login.RequestParams.USER_ID, id);
                        reqJson.put(Constants.Login.RequestParams.PASSWORD, pwd);
                        String data = reqJson.toString();
                        Log.d(TAG, "login data:" + data);
                        HttpUtils.sendRequest(Constants.ID.LOGIN, data, WelcomeActivity.this);


                    } catch (ConnectTimeoutException e) {
                        Log.d(TAG,"exception error,ConnectTimeoutException");
                    } catch (SocketTimeoutException e) {
                        Log.d(TAG,"exception error,SocketTimeoutException");

                    } catch (Exception e) {
                        Log.d(TAG,"exception error,go to login activity: ");
                        Intent intent = new Intent(mContext,LoginActivity.class);
                        startActivity(intent);
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
    public void httpCallBack(int id, JSONObject resp){
        if(id==Constants.ID.LOGIN){
            String resCode = resp.optString(Constants.ResponseParams.RES_CODE);
            if(resCode.equals("0")){
                Log.d(TAG, "auto login success,to to tab host activity");
                User user = new User();
                String userId = resp.optString(Constants.Login.ResponseParams.USER_ID);
                user.setUserId(userId);

                user.setName(resp.optString(Constants.Login.ResponseParams.NAME));
                user.setGender(resp.optString(Constants.Login.ResponseParams.GENDER));

                String photoString = resp.optString(Constants.Login.ResponseParams.PHOTO);
                user.setPhoto(photoString);
                CacheUtils.SetUserCache(user);

                Bitmap bitmap = PhotoUtils.StringToBitmap(photoString);
                CacheUtils.AddBitmapToLruCache(userId, bitmap);
                DataBaseManager.init(mContext, CacheUtils.GetUserId());
                UserList.UpdateFriendList(resp);
                UserList.initChattingList();
                Intent intent = new Intent(WelcomeActivity.this, TabHostActivity.class);
                finish();
                startActivity(intent);
            }
            else{
                Log.d(TAG,"auto login failed,go to login activity");
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        ActivityCollector.removeActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
