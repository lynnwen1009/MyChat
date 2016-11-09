package com.lynn.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.support.v7.internal.widget.ViewUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by Administrator on 9/15/2016.
 */
public class CacheUtils
{
    private static int photoDefaultSize = 0;
    private static User mUser = null;
    private static SharedPreferences mPref = null;// = getSharedPreferences("data",MODE_PRIVATE);
    private static SharedPreferences.Editor mEditor = null;
    private static  MyLruCache myLruCache;
    public static void setMyLruCache(MyLruCache yLruCache){
        myLruCache = yLruCache;
    }
    public static MyLruCache getMyLruCache(){
        return myLruCache;
    }
    public static void AddBitmapToLruCache(String key,Bitmap bitmap){
        myLruCache.put(key,bitmap);

    }
    public static Bitmap getBitmapFromLruCache(String key){

        return myLruCache.get(key);
    }

    public static SharedPreferences.Editor getEditor(Context mContext){
        if (mEditor==null)
        {
            if(mPref!=null){
                mEditor = mPref.edit();
            }else{
                mPref = mContext.getSharedPreferences("data",Context.MODE_PRIVATE);
                mEditor = mPref.edit();
            }
        }
       return mEditor;
    }
    public static SharedPreferences getAccountSharedPreferences(Context mContext){
        if(mPref==null){
            mPref =  mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
            mEditor = mPref.edit();
        }
        return mPref;
    }
    public static void setPhotoDefaultSize(Context mContext){
        int  width = (((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay().getWidth());
        int height = (((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay().getHeight());
        photoDefaultSize = width/5;
        DisplayMetrics dm= new DisplayMetrics();
        ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay().getMetrics(dm);
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        int widthPixels = dm.widthPixels ;
        int heightPixels = dm.heightPixels ;
        Log.d("WelcomeActivity","window width: "+width);
        Log.d("WelcomeActivity","window height: "+height);
        Log.d("WelcomeActivity","window width pixels: "+widthPixels);
        Log.d("WelcomeActivity","window height pixels: "+heightPixels);
        Log.d("WelcomeActivity","window height density dpi: "+dm.densityDpi);
        Log.d("WelcomeActivity","window x dpi: "+xdpi);
        Log.d("WelcomeActivity","window y dpi: "+ydpi);

    }
    public static int getPhotoDefaultSize(){
        return photoDefaultSize;
    }
    public static void SetUserCache(User user){
        mUser = user;


    }
    public static User GetUserCache()
    {
        return mUser;
    }
    public static String GetUserId()
    {
        if(mUser != null)
            return mUser.getUserId();
        return "";
    }
    public static void SetUserId(String userId)
    {
        if(mUser != null)
            mUser.setName(userId);
    }


    public static String GetUserName()
    {
        if(mUser != null)
            return mUser.getName();
        return "";
    }

    public static void SetUserName(String name)
    {
        if(mUser != null)
            mUser.setName(name);
    }

    public static String GetGender()
    {
        if(mUser != null)
            return mUser.getGender();
        return Constants.Gender.MALE;
    }

    public static void SetGender(String gender)
    {
        if(mUser != null)
            mUser.setGender(gender);
    }


    public static void SetUserPhoto(String photo)
    {
        if(mUser != null)
            mUser.setPhoto(photo);
    }
    public static String getUserPhoto(){
        if(mUser!=null){
            return mUser.getPhoto();
        }
        return " ";
    }
/*
    urn null;
    }public static Drawable GetUserDrawable(){
        if(mUser!=null){
            return mUser.getDrawable();
        }
        ret
*/
    public static void ClearUser()
    {
        mUser = null;
    }

}
