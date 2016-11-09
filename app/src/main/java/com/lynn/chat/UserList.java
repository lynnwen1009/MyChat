package com.lynn.chat;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UserList {
    private static String userId;
    //	private User user;
    private static List<User> friendsList =
            new ArrayList<User>();
    private static List<User> chatsList =
            new ArrayList<User>();
    private static List<User> newFriendsList = new ArrayList<User>();
    private static HashMap<String,List<Msg>> ChattingHashMap = new HashMap<String,List<Msg>>();
    private static HashMap<String,Msg> lastChattingMsgHashMap = new HashMap<String,Msg>();
    //private static List<User> newFriendsList = new ArrayList<User>();
   // private static HashMap<String,List<Msg>> ChattingHashMap = new HashMap<String,List<Msg>>();
   // private static HashMap<String,Msg> lastChattingMsgHashMap = new HashMap<String,Msg>();
     public static void initChattingList(){
         List<Msg> list  ;
         int num = friendsList.size();
         String friendId;
         User friend;
         Log.d("MainActivity","initChattingList,num:"+num);
         for(int i=0;i<num;i++){
             friend = friendsList.get(i);
             friendId = friend.getUserId();
             DataBaseManager.initChattingTable(friendId);
             list = DataBaseManager.readChattingTable(friendId);
             Log.d("MainActivity","initChattingList,chatting list num: "+list.size());
             Log.d("MainActivity","initChattingList,friend id: "+friendId);
             int chattingNum = list.size();
             Msg msg;
             if(chattingNum>0){

                 ChattingHashMap.put(friendId,list);
                 msg = list.get(chattingNum-1);

                 lastChattingMsgHashMap.put(friendId, msg);
                 if(msg.getType()==Msg.TYPE_RECEIVED_IMAGE||msg.getType()==Msg.TYPE_SENT_IMAGE){
                     friend.setLastMsg("[picture]");
                 }else{
                     friend.setLastMsg(msg.getContent());
                 }
                 friend.setLastMsgTimeStamp(msg.getTimeStamp());
             }

             DataBaseManager.updateFriendsTable(friend);
         }

     }

    //public static void saveToUserList
    public static void addToLastChattingMapHash(String userId,Msg msg){
        lastChattingMsgHashMap.remove(userId);
        lastChattingMsgHashMap.put(userId,msg);
    }
    public static HashMap<String,Msg> getLastChattingMapHash(){
       HashMap<String,Msg> hash = lastChattingMsgHashMap;
        return hash;

    }
    public static void addToChattingMapHash(String userId,List<Msg> chattingList){
        ChattingHashMap.remove(userId);
        ChattingHashMap.put(userId,chattingList);
    }
    public static List<Msg> getChattingList(String userId){
        List<Msg> chattingList = ChattingHashMap.get(userId);
          if(chattingList==null){
            chattingList = new ArrayList<Msg>();
        }
        return chattingList;//
        // ChattingHashMap.get(userId);
    }
    public static void UpdateFriendList(JSONObject json){

        int num = json.optInt(Constants.GetFriendList.JsonKey.NUM);
        JSONObject  friendsJson = json.optJSONObject(Constants.GetFriendList.JsonKey.FRIENDS);
        JSONArray ar  = new JSONArray();

        List<User> list = new ArrayList<User>();
        JSONArray friendArr;

        for(int i=0;i<num;i++){
            friendArr = friendsJson.optJSONArray(i+"");
            User user = new User();
            user.setUserId(friendArr.optString(0));
            user.setName(friendArr.optString(1));
            user.setGender(friendArr.optString(2));
            user.setPhoto(friendArr.optString(3));
       //     DataBaseManager.updateFriendsTable(user);
            list.add(user);
        }


        friendsList = list;
        Log.d("MainActivity","UpdatefriendList,num: "+friendsList.size());
    }
    public static void addToNewFriendsList(User user){

        newFriendsList.add(user);
    }
    public static List getNewFriendsList()
    {
        return newFriendsList;
    }
    public static void removeNewFriendsListItem(User user)
    {
        newFriendsList.remove(user);
    }
    public static void removeNewFriendsListItem(int i)
    {
        newFriendsList.remove(i);
    }
    public static int findUserInNewFriendsList(String userId)
    {
        for(int i=0;i<newFriendsList.size();i++){
            if(userId.equals(newFriendsList.get(i).getUserId())){
               //find user
                return i;
            }
        }
       return -1;
    }
    public static void modifyUserInNewFriendsList(int i,User user)
    {
        newFriendsList.remove(i);
        newFriendsList.add(i,user);

    }
public static void showNewfriendList(){
    int num = newFriendsList.size();
    Log.d("NewFriendsActivity", "show newFriendsList ,size: " + num);
    for(int i=0;i<num;i++){
        User user = newFriendsList.get(i);
        Log.d("NewFriendsActivity","userId: "+user.getName()+" type: "+user.getType());

    }
}
    public static void addToFriendList(User user){

        for(int i=0;i<friendsList.size();i++){
            User userInfo=  (User)friendsList.get(i);
            //already exist
            if(user.getUserId().equals(userInfo.getUserId())){
                friendsList.remove(i);
                friendsList.add(i,user);
                return;
            }
        }
        friendsList.add(user);
    }
    public static User getFriendByName(String name){
        User user = null;

        int num = friendsList.size();
        for(int i =0;i<num;i++){
            user = friendsList.get(i);
            if(name.equals(user.getName()))
                return user;
        }
        return null;
    }
    public static User getFriendByUserId(String userId){
        User user = null;

        int num = friendsList.size();
        for(int i =0;i<num;i++){
            user = friendsList.get(i);
            if(userId.equals(user.getUserId()))
                return user;
        }
        return null;
    }
    public static void setFriendsList(List<User> list){
        friendsList = list;
    }
    public static List getFriendsList(){
        return friendsList;
    }

}

