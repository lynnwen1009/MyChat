package com.lynn.chat;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Administrator on 8/25/2016.
 */
public class Msg implements Serializable {

    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT=1;
    public static final int TYPE_REGISTER = 2;
    public static final int TYPE_Login = 3;
    public static final int TYPE_RECEIVED_IMAGE = 4;
    public static final int TYPE_SENT_IMAGE = 5;
    public static final int TYPE_RECEIVED_MAP = 6;
    public static final int TYPE_SENT_MAP = 7;




    private String content;
    private byte [] imageBytes;
    private String picUri;
    private int type;
    private String accountFrom;
    private String accountTo;
    private String timeStamp;
    private boolean isReaded = false;
    private int seqNumImage;

    public Msg(String content,int type,String accountFrom,String accountTo,String timeStamp,boolean isReaded) {
        this.content = content;
        this.type = type;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.timeStamp = timeStamp;
        this.isReaded = isReaded;

    }
    public void setImageBytes(byte[] imageBytes){
        this.imageBytes = imageBytes;
    }
    public byte[] getImageBytes(){
        return imageBytes;
    }
    public void setSeqNumImage(int seqNumImage){
        this.seqNumImage = seqNumImage;
    }
    public int getSeqNumImage(){
        return seqNumImage;
    }
    public void setHasRead(boolean hasRead){
        this.isReaded = hasRead;

    }
    public void setPicUri(String picUri){
        this.picUri = picUri;
    }
    public String getPicUri(){
        return picUri;
    }
    public boolean getIsReaded(){
        return isReaded;
    }

    public String getContent(){
        return content;

    }
    public String getAccountFrom(){
        return accountFrom;
    }
    public String getAccountTo(){
        return accountTo;
    }
    public int getType(){
        return type;
    }
    public String getTimeStamp(){
        return timeStamp;
    }
}
