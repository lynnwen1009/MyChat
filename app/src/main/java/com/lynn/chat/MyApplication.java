package com.lynn.chat;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/11/17.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static Context context;
    private static int memoryCache ;
    private static int photoDefaultSize;


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        context = getApplicationContext();
        memoryCache = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int  width = (((WindowManager)getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay().getWidth());
        int height = (((WindowManager)getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay().getHeight());
        photoDefaultSize = width/5;



    }
    public static Context getContext(){
        return context;
    }
    public static int getMemoryCache(){
        return memoryCache;
    }
    public static int getPhotoDefaultSize(){
       return photoDefaultSize;
    }
}
