package com.lynn.chat;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by Administrator on 2016/10/10.
 */
public class HandlerUtils {
    private static Handler  addFriendHandler = null;
    private static Handler chattingHandler=  null;
    private static Handler pictureViewHandler = null;
    private static HandlerThread pictureViewThread = null;
    private static HandlerThread chattingThread = null;
    private static HandlerThread addFriendThread = null;
    public static Handler getPictureViewHandler(){
        if(pictureViewThread==null){
            pictureViewThread  = new HandlerThread("pictureView");
            pictureViewThread.start();
            pictureViewHandler = new Handler(pictureViewThread.getLooper());
        }
        return pictureViewHandler;
    }
    public static Handler getChattingHandler(){
        if(chattingThread==null){
            chattingThread = new HandlerThread("chattingHandler");
            chattingThread.start();
            chattingHandler = new Handler(chattingThread.getLooper());
        }

        return chattingHandler;
    }
    public static Handler getAddfriendHandler(){
        if(addFriendHandler==null){
            addFriendThread= new HandlerThread("AddfriendHandler");
            addFriendThread.start();
            addFriendHandler = new Handler(addFriendThread.getLooper());
        }
        return addFriendHandler;
    }

    public static Handler getHandler(String name){
        HandlerThread thread= new HandlerThread(name);
        thread.start();
        return new Handler(thread.getLooper());
    }
    public static void quitHandler(){
        chattingThread.getLooper().quit();
        addFriendThread.getLooper().quit();
    }

}
