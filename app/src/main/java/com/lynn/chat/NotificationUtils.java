package com.lynn.chat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;

import java.util.HashMap;

/**
 * Created by Administrator on 9/29/2016.
 */
public class NotificationUtils {
  private static HashMap<String,Integer> unReadNumHashMap = new HashMap<String,Integer>();
    public static void increoUnReadNumHashMap(String userId){
        int num = unReadNumHashMap.get(userId).intValue();
        num++;
        unReadNumHashMap.remove(userId);
        unReadNumHashMap.put(userId,num);
    }
    public static int getUnReadNum(String userId){
        if(existInUnreadHashMap(userId)){
            return unReadNumHashMap.get(userId).intValue();
        }else
        {
            return 0;
        }

    }
    public static void deleteUnReadNum(String userId){
       unReadNumHashMap.remove(userId);

    }
    public static void addTounReadNumHashMap(String userId){
        if(existInUnreadHashMap(userId)==false){
            unReadNumHashMap.put(userId,1);
        }else
        {
            increoUnReadNumHashMap(userId);
        }

    }
    public static boolean existInUnreadHashMap(String userId){
       if( unReadNumHashMap.get(userId)==null)
           return false;
        else
           return true;
    }
    private static int resumeFlag = 0;
    public static void setResumeFlagForChattingActivity(int flag){
        resumeFlag = flag;


    }
    public static int getResumeFlag(){
        return resumeFlag;
    }
    /*
    public static void NotificationCancel(int NotificationId,String friendId){
        NotificationManager manager;
        manager = (NotificationManager) Activity.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NotificationId);
        NotificationUtils.deleteUnReadNum(friendId);
    }
    */

}
