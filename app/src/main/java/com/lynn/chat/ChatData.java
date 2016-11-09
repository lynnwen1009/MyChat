package com.lynn.chat;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 9/15/2016.
 */
public class ChatData implements  Serializable {
    private static final long serialVersionUID = -5598765559893512679L;
    public static class Type implements Serializable{
        private static final long serialVersionUID = 2783457974102879966L;
        public static final int CHATTING = 0;
        public static final int LOGOUT = 1;
        public static final int OFFLINE_MSG = 2;
        public static final int ADD_FRIEND = 3;
        public static final int ADD_AGREE = 4;
        public static final int CHARACTER = 5;
        public static final int IMAGE = 6;
        public static final int GET_BIGIMAGE = 7;
    }
    public static class ID implements Serializable {
        private static final long serialVersionUID = 2788053974101111966L;
        private String userId;
        public String getUserId() {
            return userId;
        }
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class MSG implements Serializable{
        private static final long serialVersionUID = 7266838079022652922L;
        private int type = Type.CHATTING;
        private int chatType = Type.CHARACTER;
        private String fromId;
        private String toId;
        private String msg;
        private byte[] bigBitmap;
        private String time;
        private String photo;
        private String name ;
        private  String gender;
        private boolean hasRead = false;
        private int seqNum;

        public void setSeqNum(int seqNum){
            this.seqNum = seqNum;
        }
        public int getSeqNum(){
            return seqNum;
        }

        public void setBigBitmap(byte[] bigBitmap){
            this.bigBitmap = bigBitmap;
        }
        public byte[] getBigBitmap(){
            return bigBitmap;
        }
        public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
        public void setChatType(int chatType){
            this.chatType = chatType;
        }
        public int getChatType(){
            return chatType;
        }

        public boolean getHashRead() {
            return hasRead;
        }
        public void setHasRead(boolean hasRead) {
            this.hasRead = hasRead;
        }


        public String getFromId() {
            return fromId;
        }
        public void setFromId(String fromId) {
            this.fromId = fromId;
        }
        public String getToId() {
            return toId;
        }
        public void setToId(String toId) {
            this.toId = toId;
        }
        public String getMsg() {
            return msg;
        }
        public void setMsg(String msg) {
            this.msg = msg;
        }
        public String getTime() {
            return time;
        }
        public void setTime(String time) {
            this.time = time;
        }
        public String getPhoto() {
            return photo;
        }
        public void setPhoto(String photo) {
            this.photo = photo;
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
    }

}
