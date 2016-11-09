package com.lynn.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 9/15/2016.
 */
public class MsgUtils {
    private static ChatConnThread connThread;
    private static String currBroadcast;
    public static void SetConnThread(ChatConnThread thread){
        connThread = thread;

    }
    public static String GetCurrBroadCast()
    {
       return currBroadcast;
    }
    public static void SetCurrBroadCast(String broadcastName)
    {
        currBroadcast = broadcastName;
    }
    public static void GetOfflineMsg(){
        ChatData.MSG msg = new ChatData.MSG();
        msg.setType(ChatData.Type.OFFLINE_MSG);
        //???msg.setFromId(CacheUtils.GetUserId());
        connThread.sendMsg(msg);
    }
    public static void SendMsg(ChatData.MSG msg)
    {
        if(msg.getType()==ChatData.Type.CHATTING){
            User user = UserList.getFriendByUserId(msg.getToId());
            if(msg.getChatType()==ChatData.Type.CHARACTER)
                user.setLastMsg(msg.getMsg() );
            else
                user.setLastMsg("[picture]");
            user.setLastMsgTimeStamp(msg.getTime());

        }

        connThread.sendMsg(msg);
    }
    public static void CloseConn(){
        if(connThread.isOnLine()){
            ChatData.MSG  msg = new ChatData.MSG();
            msg.setFromId(CacheUtils.GetUserId());
            msg.setType(ChatData.Type.LOGOUT);
            SendMsg(msg);

        }
        connThread.closeConn();
    }
public static void ShowBigImage(Context ctx,ChatData.MSG msg){
    Intent intent = new Intent(Constants.Actions.GET_BIG_IMAGE);
  //  intent.putExtra(Constants.Flags.MSG, msg);
    intent.putExtra("seqNum",msg.getSeqNum());
    Bitmap bitmap1 = BytesToBitmap(msg.getBigBitmap());
    CacheUtils.AddBitmapToLruCache(msg.getFromId() + "#" + msg.getSeqNum(), bitmap1);
    Log.d("PictureViewActivity", "send broad cast to picture view activity,from id: " + msg.getFromId());
    Log.d("PictureViewActivity", "send broad cast to picture view activity,seq num: "+msg.getSeqNum());
    ctx.sendBroadcast(intent);

}
    public static Bitmap BytesToBitmap(byte[] bitmapArray){
        Bitmap bitmap = null;
        try{
            //    byte[] bitmapArray;
            //   bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);


        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
    public static void ShowChattingMsg(Context ctx,ChatData.MSG msg){
        String action;
        String friendFrom;
        friendFrom = msg.getFromId();
        List<Msg> chattingList = UserList.getChattingList(friendFrom);

        User user = UserList.getFriendByUserId(msg.getFromId());
        if(msg.getChatType()==ChatData.Type.IMAGE){
            user.setLastMsg("[picture]");
        }
        else{
            user.setLastMsg(msg.getMsg());
        }
        user.setUnReadMsgNum(user.getUnReadMsgNum()+1);
        user.setLastMsgTimeStamp(msg.getTime());

        Log.d("MsgUtils", "ShowChattingMsg(),chatting message from: " + friendFrom);
        Log.d("MsgUtils", "ShowChattingMsg(),currBroadcast: " + currBroadcast);
        Log.d("MsgUtils", "ShowChattingMsg(),send chatting broadcast");
        //  Msg message = new Msg( msg.getMsg(), Msg.TYPE_RECEIVED,msg.getFromId(),msg.getToId(),MsgUtils.GetFormatTime(),false);
        Msg message;
        if(msg.getChatType()==ChatData.Type.CHARACTER){
            //message is character
            //   message = new Msg(textMessage, Msg.TYPE_RECEIVED,msg.getFromId(),msg.getToId(),MsgUtils.GetFormatTime(),false);
            message = new Msg( msg.getMsg(), Msg.TYPE_RECEIVED,msg.getFromId(),msg.getToId(),msg.getTime(),false);
        }else{
            //message is image
            message = new Msg(msg.getMsg(), Msg.TYPE_RECEIVED_IMAGE,msg.getFromId(),msg.getToId(),msg.getTime(),false);
            int seqNum = msg.getSeqNum();
            message.setSeqNumImage(seqNum);
            Bitmap bm = PhotoUtils.StringToBitmap(msg.getMsg());//small photo bitmap
            CacheUtils.AddBitmapToLruCache(msg.getFromId()+"_"+seqNum,bm);//add to LruCache

        }

        chattingList.add(message);

        UserList.addToChattingMapHash(msg.getFromId(), chattingList);
        UserList.getLastChattingMapHash().remove(msg.getFromId());
        UserList.addToLastChattingMapHash(msg.getFromId(), message);
        UserList.addToLastChattingMapHash(msg.getFromId(), message);

        if((friendFrom).equals(currBroadcast)){


            Intent intent = new Intent(Constants.Actions.CHATTING_PREFIX+msg.getFromId());
            intent.putExtra(Constants.Flags.MSG, msg);
            ctx.sendBroadcast(intent);


        }else{
            //send broadcast to tabhostActivity
            Intent intent1 = new Intent(Constants.Flags.CHATTING_MSG);
            intent1.putExtra(Constants.Flags.MSG,msg);
            ctx.sendBroadcast(intent1);
        }


     //   if(Constants.Actions.CHAT_LIST.equals(currBroadcast))
//        {
        /*
            Msg message;
            if(msg.getChatType()==ChatData.Type.CHARACTER){
                //message is character
                //   message = new Msg(textMessage, Msg.TYPE_RECEIVED,msg.getFromId(),msg.getToId(),MsgUtils.GetFormatTime(),false);
                message = new Msg( msg.getMsg(), Msg.TYPE_RECEIVED,msg.getFromId(),msg.getToId(),MsgUtils.GetFormatTime(),true);
            }else{
                //message is image
                message = new Msg(msg.getMsg(), Msg.TYPE_RECEIVED_IMAGE,msg.getFromId(),msg.getToId(),MsgUtils.GetFormatTime(),true);
            }

          //  Msg message = new Msg( msg.getMsg(), Msg.TYPE_RECEIVED,msg.getFromId(),msg.getToId(),MsgUtils.GetFormatTime(),true);
            message.setSeqNumImage(msg.getSeqNum());
            chattingList.add(message);
            UserList.addToChattingMapHash(msg.getFromId(), chattingList);
            UserList.getLastChattingMapHash().remove(msg.getFromId());
            UserList.addToLastChattingMapHash(msg.getFromId(), message);
*/


      //      Intent intent = new Intent(Constants.Actions.CHAT_LIST);
     //       intent.putExtra(Constants.Flags.MSG, msg);
     //       ctx.sendBroadcast(intent);
   //     }

    }

    public static void showAddFriendMsg(Context ctx,ChatData.MSG msg){
        Intent intent = new Intent(Constants.Actions.NEW_FRIEND_LIST);
        intent.putExtra(Constants.Flags.NEW_FRIEND_LIST, msg);
      /*  Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.Flags.NEW_FRIEND_LIST,msg);
        intent.putExtras(bundle);*/
        String photo = msg.getPhoto();
        if(photo==null){
            Log.d("MsgUtils", "showAddFriend,receive add friend request photo null " );
        }
        else
        {
            Log.d("MsgUtils", "showAddFriend,receive add friend request photo len: "+photo.length() );
        }
        Log.d("MsgUtils", "showAddFriend,receive add friend request from: "+msg.getFromId());
        User user = new User();
        user.setName(msg.getName());
        user.setUserId(msg.getFromId());
        user.setPhoto(msg.getPhoto());
        user.setType(Constants.UserType.USER_RECEIVE_ADD_FRIEND);
        UserList.addToNewFriendsList(user);


        //UserList.addToNewFriendsList(msg);
        ctx.sendBroadcast(intent);

        Intent intent1 = new Intent(Constants.Flags.ADD_FRIEND_REQUEST);
        intent1.putExtra(Constants.Flags.MSG, msg);
        ctx.sendBroadcast(intent1);
    }
    public static void AddNewFriend(Context ctx,ChatData.MSG msg){//receive add agree msg
        User user = new User();
        user.setGender(msg.getGender());
        user.setUserId(msg.getFromId());
        user.setName(msg.getName());
        user.setPhoto(msg.getPhoto());
        Bitmap bitmap = PhotoUtils.StringToBitmap(user.getPhoto());
        CacheUtils.AddBitmapToLruCache(user.getUserId(), bitmap);
        user.setType(Constants.UserType.USER_RECEIVE_ADD_AGREE);
        user.setLastMsg("I've accepted your friend request.Now let's chat!");
        user.setLastMsgTimeStamp(msg.getTime());
        user.setUnReadMsgNum(0);
        UserList.addToFriendList(user);
        DataBaseManager.updateFriendsTable(user);
        int size = UserList.getFriendsList().size();

        int i = UserList.findUserInNewFriendsList(msg.getFromId());
        if(i!=-1){
            UserList.modifyUserInNewFriendsList(i,user);
        }
        else{
            UserList.addToNewFriendsList(user);
        }
        List<Msg> chattingList = UserList.getChattingList(msg.getName());
        String firstMessage = "I've accepted your friend request.Now let's chat!";
        Msg message = new Msg(firstMessage, Msg.TYPE_RECEIVED,msg.getFromId(),msg.getToId(),MsgUtils.GetFormatTime(),true);
        chattingList.add(message);
        UserList.addToChattingMapHash(msg.getFromId(), chattingList);
        UserList.addToLastChattingMapHash(msg.getFromId(), message);



        //send broadcast to add new friends activity
        Intent intent = new Intent(Constants.Actions.NEW_FRIEND_LIST);
        intent.putExtra(Constants.Flags.NEW_FRIEND_LIST, msg);
        ctx.sendBroadcast(intent);
        Log.d("FriendsList", "add friends list succeed ,num of friends: " + size);
        //send broadcast to FriendsList activity
        Log.d("MsgUtils", "AddNewFriend,send broadcast NEW_FRIEND_LIST ");
        Intent intent1 = new Intent(Constants.Actions.CONTACTS_LIST);
        intent1.putExtra(Constants.Flags.NEW_FRIEND_LIST, msg);

        ctx.sendBroadcast(intent1);

        msg.setMsg(firstMessage);
        Intent intent2 = new Intent(Constants.Flags.ADD_FRIEND_AGREE);
        intent2.putExtra(Constants.Flags.MSG,msg);
        ctx.sendBroadcast(intent2);
    }
    public static String GetFormatTime(){
        long currTime = System.currentTimeMillis();
        SimpleDateFormat formatter  = new SimpleDateFormat("MM-dd HH:mm");
        Date curdate = new Date(currTime);
        String time = formatter.format(curdate);
        return time;
    }
    public static long GetTimeMilis(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
        try {
            long timeMilis = formatter.parse(time).getTime();
            return timeMilis;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
