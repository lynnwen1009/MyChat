package com.lynn.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.lynn.chattest2.R;

import java.io.File;

/**
 * Created by Administrator on 2016/10/8.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override

        public void onReceive(Context context, Intent intent) {
            //  ChatData.MSG msg = (ChatData.MSG)(intent.getSerializableExtra("Constants.Flags.NEW_FRIEND_LIST"));
            Bundle extras= intent.getExtras();  ;
            ChatData.MSG msg = (ChatData.MSG)extras.getSerializable(Constants.Flags.MSG);
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification  ;
            Intent it = null;
            PendingIntent pi = null;
            int unRead = 0;
            Log.d("BootBroadcastReceiver", "onReceive(),msg type: "+msg.getType());
            //        User friend = UserList.getFriendByName(msg.getName());
            User friend = UserList.getFriendByUserId(msg.getFromId());
            if(friend==null){
                friend = new User();
                friend.setName(msg.getName());
                friend.setGender(msg.getGender());
                friend.setPhoto(msg.getPhoto());
                friend.setGender(msg.getGender());
                friend.setUserId(msg.getFromId());
            }
            String userId = msg.getFromId();
            int notificationId = 0;
            String ticker=null;
            String notificationTitle = null;
            String notificationContent = null;


            switch(msg.getType()){
                case ChatData.Type.CHATTING:
                    if(NotificationUtils.getResumeFlag()==1){
                        //chatting msg and chatting activity is resumed,then do not send notification
                        Log.d("BootBroadcastReceiver", "onReceive(),chatting UI resume,do not send notification ");
                        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(new long[]{0,500,500,500},-1);
                        return;
                    }
                case ChatData.Type.ADD_AGREE:
                    NotificationUtils.addTounReadNumHashMap(userId);
                    if(msg.getChatType()==ChatData.Type.IMAGE){
                        ticker = msg.getName()+":"+"[picture]";

                    }else{
                        ticker = msg.getName()+":"+msg.getMsg();
                    }

                    it = new Intent(context,TabHostActivity.class);
                    it.putExtra(Constants.Flags.CHATTING_FRIEND, friend);
                    it.putExtra("fromBroad", "fromBroadcastForChatting");
                    unRead = NotificationUtils.getUnReadNum(userId);
                    notificationTitle = msg.getName();
                    if(unRead==1){
                        notificationContent =  msg.getName();
                    }else{
                        notificationContent = "["+unRead+"]"+msg.getName() ;
                    }


                    if(msg.getChatType()==ChatData.Type.IMAGE){
                        notificationContent = notificationContent+":[picture]";
                    }
                    else{
                        notificationContent = notificationContent+":"+msg.getMsg();
                    }

                    /*
                    NotificationUtils.addTounReadNumHashMap(userId);
                    notification = new Notification(R.drawable.abc_btn_check_material,
                            msg.getName()+":"+msg.getMsg(),System.currentTimeMillis());

                    it = new Intent(mContext,MainActivity.class);
                    unRead = NotificationUtils.getUnReadNum(userId);

                    it.putExtra(Constants.Flags.CHATTING_FRIEND, friend);
                    pi = PendingIntent.getActivity(mContext,0,it,PendingIntent.FLAG_CANCEL_CURRENT);
                    notification.setLatestEventInfo(mContext,msg.getName(),"["+unRead+"]"+msg.getName()+":"+msg.getMsg(),pi);

                    manager.notify(1,notification);*/

                    break;
                case ChatData.Type.ADD_FRIEND:

                    ticker = msg.getName()+" sent you a friend invitation";
                    it = new Intent(context,TabHostActivity.class);
                    it.putExtra("fromBroad", "fromBroadcastForNewFriends");
                    notificationTitle = msg.getName();
                    notificationContent =  msg.getName();
                    notificationContent = "sent you a friend invitation";


                    notificationId = 2;
                    /*
                    notification = new Notification(R.drawable.abc_btn_check_material,
                            msg.getName()+"sent you a friend invitation",System.currentTimeMillis());

                    it = new Intent(mContext,NewFriendsActivity.class);


                    pi = PendingIntent.getActivity(mContext,0,it,PendingIntent.FLAG_CANCEL_CURRENT);
                    notification.setLatestEventInfo(mContext,msg.getName(),
                            "sent you a friend invitation",pi);

                    manager.notify(2,notification);
                    */
                    break;
                /*
                case ChatData.Type.ADD_AGREE:
                    NotificationUtils.addTounReadNumHashMap(userId);
                    notification = new Notification(R.drawable.abc_btn_check_material,
                            msg.getName()+"have added you",System.currentTimeMillis());

                    it = new Intent(mContext,MainActivity.class);
                    it.putExtra(Constants.Flags.CHATTING_FRIEND, friend);

                    pi = PendingIntent.getActivity(mContext,0,it,PendingIntent.FLAG_CANCEL_CURRENT);
                    notification.setLatestEventInfo(mContext, msg.getName(), msg.getMsg(), pi);

                    manager.notify(1,notification);

                    break;
                    */
                default:
                    break;
            }
            notification = new Notification(R.drawable.abc_btn_check_material,
                    ticker,System.currentTimeMillis());

            pi = PendingIntent.getActivity(context,0,it,PendingIntent.FLAG_CANCEL_CURRENT);
            long[] vibrates = {0,500,500,500};
            notification.vibrate = vibrates;
            Uri soundUri = Uri.fromFile(new File("system/media/audio/ringtones/Basic_tone.ogg"));
            notification.sound = soundUri;
            notification.ledARGB = Color.GREEN;
            notification.ledOnMS = 1000;
            notification.ledOffMS = 1000;
            notification.flags = Notification.FLAG_SHOW_LIGHTS;
            notification.setLatestEventInfo(context, notificationTitle, notificationContent, pi);

            manager.notify(notificationId, notification);
            //Bundle bundle = intent.getExtras();


            //    newfriendslist.add(msg);
            //  UserList.addToNewFriendsList(msg);
//            Log.d("TabHostActivity", "to id:"+msg.getToId());





        }
    }
