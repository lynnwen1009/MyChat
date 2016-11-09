package com.lynn.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2016/10/20.
 */
public class DataBaseManager {
    private static MyDataBaseHelper dbHelper;
    private static SQLiteDatabase sqLiteDatabase;
    private static int version = 15;

    public static void init(Context context,String userId){
        dbHelper = new MyDataBaseHelper(context,userId,null,version);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        Log.d("TabHostActivity","init data base");
    }
    public static void initChattingTable(String friendId){
        dbHelper.createChattingDataBase(sqLiteDatabase,friendId);
        Log.d("TabHostActivity", "init chatting table");
    }
     public void updateChatsTable(){

     }

    public static void updateChatsTable(User friend){

        Cursor cursor = sqLiteDatabase.query("chats",new String[]{"friendId"},null,null,null,null,null);
        cursor.moveToFirst();
        String  Id = cursor.getString(cursor.getColumnIndex("friendId"));
        if( Id.equals(friend.getUserId())){
            ContentValues values1 = new ContentValues();
            values1.put("unread",friend.getUnReadMsgNum()+"");
            values1.put("msg",friend.getLastMsg());
            values1.put("timeStamp",friend.getLastMsgTimeStamp());
            sqLiteDatabase.update("chats",values1,"friendId = ?",new String[]{Id});
        }else{
            ContentValues values = new ContentValues();
            values.put("friendId",friend.getUserId());
     //       values.put("photo", friend.getPhoto());
            values.put("unread", friend.getUnReadMsgNum() + "");
            values.put("msg", friend.getLastMsg());
            values.put("timeStamp", friend.getLastMsgTimeStamp());
            sqLiteDatabase.insert("chats", null, values);
        }
        cursor.close();
    }

    public static void updateFriendsTable(User friend){

        Log.d("TabHostActivity", "updateFriendsTable---1");
            ContentValues values = new ContentValues();
            values.put("friendId",friend.getUserId());
            values.put("name", friend.getName());
       //     values.put("photo", friend.getPhoto());
            values.put("gender", friend.getGender());
            values.put("lastMsg", friend.getLastMsg());
            sqLiteDatabase.insert("friends", null, values);
        Log.d("TabHostActivity", "updateFriendsTable--2");

    }

    public static List<User> readFriendsTable(){
        List<User>  list = new ArrayList<User>();

        Cursor cursor = sqLiteDatabase.query("friends",null,null,null,null,null,null);
        while(cursor.moveToNext()) {
            Log.d("TabHostActivity", "readChattingTable--1");
            String friendId = cursor.getString(cursor.getColumnIndex("friendId"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
        //    String photo = cursor.getString(cursor.getColumnIndex("photo"));
            String gender = cursor.getString(cursor.getColumnIndex("gender"));
            String lastMsg = cursor.getString(cursor.getColumnIndex("lastMsg"));
            Log.d("TabHostActivity", "readChattingTable--2");

            User user= new User();
            user.setUserId(friendId);
            user.setName(name);
      //      user.setPhoto(photo);
            user.setGender(gender);
            user.setLastMsg(lastMsg);
            list.add(user);
        }


        cursor.close();
        return list;


    }
    public static void deleteFriendsTable(User friend){
        sqLiteDatabase.delete("friends", "friendId = ?", new String[]{friend.getUserId()});
    }
    public static void createChattingTable(String friendId){
        dbHelper.createChattingDataBase(sqLiteDatabase, friendId);
    }

    public static List<Msg> readChattingTable(String friendId){
        List<Msg> list = new ArrayList<Msg>();
    //    sqLiteDatabase = dbHelper.getWritableDatabase();
        Log.d("MainActivity","readChattingTable,oldVersion = "+dbHelper.version());
        Log.d("MainActivity","readChattingTable,newVersion = "+version);
        if(dbHelper.version()==1)//database has update
         {
                dbHelper.updateChattingDataBase(sqLiteDatabase, friendId);
         }

        Log.d("MainActivity","readChattingTable,friend id: "+friendId);
        Cursor cursor = sqLiteDatabase.query(friendId,null,null,null,null,null,null);
        Log.d("MainActivity","readChattingTable,cursor read finish ");
        if(cursor==null){
            Log.d("MainActivity","readChattingTable,cursor null ");
        }
      //  if(cursor.moveToFirst()){

        while(cursor.moveToNext()) {
            Log.d("MainActivity", "readChattingTable");
            String message = cursor.getString(cursor.getColumnIndex("msg"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String picPath = cursor.getString(cursor.getColumnIndex("picturePath"));
            int seqNum = cursor.getInt(cursor.getColumnIndex("seqNum"));
            String timeStamp = cursor.getString(cursor.getColumnIndex("timeStamp"));
            Msg msg;
            if (type == Msg.TYPE_RECEIVED_IMAGE || type == Msg.TYPE_RECEIVED || type == Msg.TYPE_RECEIVED_MAP) {
                msg = new Msg(message, type, friendId, CacheUtils.GetUserId(), timeStamp, true);
            } else {
                msg = new Msg(message, type, CacheUtils.GetUserId(), friendId, timeStamp, true);
            }
            msg.setPicUri(picPath);
            msg.setSeqNumImage(seqNum);
            list.add(msg);
        }

       // }
        cursor.close();
        return list;
    }
    public static void storeChattingTable(List<User> list){
        int num = list.size();
        String friendId;
        for(int i=0;i<num;i++){
            friendId = list.get(i).getUserId();
            List<Msg> msgList = UserList.getChattingList(friendId);
            updateChattingTable(friendId,msgList);
        }

    }
    public static void updateChattingTable(String friendId,List<Msg> list){
        int num = list.size();
        Msg msg;

        Log.d("MainActivity", "update chatting table,table name: "+friendId);
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.delete(friendId, null, null);
        ContentValues values = new ContentValues();
        for(int i=0;i<num;i++){
            msg = list.get(i);
            values.put("friendId",friendId);
            values.put("msg",msg.getContent());
            if(msg.getType()==Msg.TYPE_RECEIVED_IMAGE||msg.getType()==Msg.TYPE_SENT_IMAGE){
                values.put("picturePath",msg.getPicUri());
                values.put("seqNum",msg.getSeqNumImage());
            }else{
                values.put("picturePath","");
                values.put("seqNum",0);
            }

            values.put("type",msg.getType());
            values.put("timeStamp", msg.getTimeStamp());
            long re = sqLiteDatabase.insert(friendId, null, values);
            Log.d("MainActivity", "update chatting table,re: "+re);
            values.clear();
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
     //   sqLiteDatabase.close();

   }
}