package com.lynn.chat;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 9/15/2016.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 3200153623822190057L;
    private String userId;
    private String name;
    private String gender;
    private String photo; //head small image
    private String type;
    private int unReadMsgNum  ;
    private String lastMsg ;
   // private transient Drawable drawable;
    private String lastMsgTimeStamp;

    private List<String> friendList = new ArrayList<String>();
    public List getFriendList(){
        return friendList;
    }
    public void setLastMsgTimeStamp(String lastMsgTimeStamp){
        this.lastMsgTimeStamp = lastMsgTimeStamp;
    }
    public String getLastMsgTimeStamp(){
        return lastMsgTimeStamp;
    }
    public void setFriendList(String userId){
        friendList.add(userId);
    }

   /* public void setDrawable(Drawable drawable){
        this.drawable = drawable;
    }
  //  public Drawable getDrawable(){
  //      return drawable;
    }
*/
    public String getLastMsg(){
        return lastMsg;
    }
    public void setLastMsg(String lastMsg){
        this.lastMsg = lastMsg;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
     //   drawable = PhotoUtils.StringToDrawable(photo);
    }
    public void setUnReadMsgNum(int unRead ){
        unReadMsgNum = unRead ;
    }
    public int getUnReadMsgNum(){
        return unReadMsgNum;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}

