package com.lynn.chat;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by Administrator on 2016/10/12.
 */
public class MyLruCache extends LruCache<String,Bitmap> {
    private final static String TAG = "MyLruCache";


    public MyLruCache(int maxSize) {

        super(maxSize);
        Log.d(TAG, "MyLruCache() is called,maxSize: "+maxSize);
    }
    protected int sizeOf(String key,Bitmap bitmap){

        Log.d(TAG, bitmap.getByteCount() / 1024 + "");
        return bitmap.getByteCount()/1024;
    }

    @Override
    public void trimToSize(int maxSize) {
        Log.d(TAG,"trimToSize() is called,maxSize: "+maxSize);

        super.trimToSize(maxSize);
    }
    public void addBitmapToLruCache(String key,Bitmap bitmap){
        put(key, bitmap);
    }
    public Bitmap getBitmapFromLruCache(String key){
        return get(key);
    }
}
