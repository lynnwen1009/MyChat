package com.lynn.chat;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Administrator on 9/15/2016.
 */
public class ChatConnThread extends Thread {
    private static final String TAG = ChatConnThread.class.getSimpleName();
    private Context mContext;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private volatile  boolean flag = true;

    public ChatConnThread(Context ctx){
        mContext = ctx;
        /*
        try{
            Log.d("ChatConnThread","check socket connect");
            socket = new Socket(Constants.SERVER_IP,Constants.SERVER_PORT);
            if(socket==null){
                Log.d("ChatConnThread","socket null");
            }
            socket.setKeepAlive(true);
            out = new ObjectOutputStream(socket.getOutputStream());
            if(out==null){
                Log.d("ChatConnThread","out null");
            }
            //    ChatData.ID id = new ChatData.ID();
            //      id.setUserId(CacheUtils.GetUserId());
            Log.d("ChatConnThread", "id: " + CacheUtils.GetUserId());
            String id =  CacheUtils.GetUserId();
            out.writeObject(id);
            out.flush();

            in  = new ObjectInputStream(socket.getInputStream());
            Thread.sleep(250);
        }catch(Exception e){
            e.printStackTrace();
        }*/

    }
    public void closeConn()
    {
        flag = false;
    }

    public void sendMsg(ChatData.MSG msg){
        try{
             checkSocketConnect();
            if(out==null)
                Log.d("ChatConnThread","out==null");
            if(msg==null)
                Log.d("ChatConnThread", "msg==null");
            out.writeObject(msg);
            out.flush();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public synchronized void checkSocketConnect(){
      //  while(flag&&!isOnLine()){
            if(!flag){
                Log.d("ChatConnThread","ChatConnThread,flag=false");
            }
            while(flag&&!isOnLine()){
            try{
                Log.d("ChatConnThread","check socket connect");
                socket = new Socket(Constants.SERVER_IP,Constants.SERVER_PORT);
                if(socket==null){
                    Log.d("ChatConnThread","socket null");
                     continue;

                }
                socket.setKeepAlive(true);
                out = new ObjectOutputStream(socket.getOutputStream());
                if(out==null){
                    Log.d("ChatConnThread","out null");
                }
                ChatData.ID id = new ChatData.ID();
                id.setUserId(CacheUtils.GetUserId());
                Log.d("ChatConnThread", "id: " + CacheUtils.GetUserId());
       //         String id =  CacheUtils.GetUserId();
                out.writeObject(id);
                out.flush();

                in  = new ObjectInputStream(socket.getInputStream());
                Thread.sleep(250);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
    public boolean isOnLine(){
        if(socket==null){
            Log.d(TAG,"socket ==null");
            return false;
        }
        boolean ret = true;
        try{
            Log.d(TAG, "socket send urgent data ");
            socket.sendUrgentData(0xff);
            Log.d(TAG, "socket send urgent data success");
        }catch(Exception e){
            Log.d(TAG,"socket send urgent data failed");
            ret  = false;
            closeSocket();
        }
        return ret;
    }
    private void closeSocket(){
        try{
            if(socket!=null){
                if(out!=null) {
                    out.close();
                    out = null;
                }
                if(in!=null){
                    in.close();
                    in = null;
                }
                socket.close();
                socket = null;

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
       // while(flag){
            {
            try{
               //    checkSocketConnect();

                    Log.d("ChatConnThread","run start");
                    socket = new Socket(Constants.SERVER_IP,Constants.SERVER_PORT);
                    socket.setSoTimeout(2000);
                    Log.d("ChatConnThread","run start---1");
                    if(socket==null){
                        Log.d("ChatConnThread","socket null");
                    }
                    socket.setKeepAlive(true);
                    out = new ObjectOutputStream(socket.getOutputStream());
                    Log.d("ChatConnThread","run start---2");
                    if(out==null){
                        Log.d("ChatConnThread","out null");
                    }
                    ChatData.ID id = new ChatData.ID();
                    id.setUserId(CacheUtils.GetUserId());
                    // String id = CacheUtils.GetUserId();
                    Log.d("ChatConnThread", "id: " + CacheUtils.GetUserId());
                    out.writeObject(id);
                    out.flush();
                    in = new ObjectInputStream(socket.getInputStream());
                    Thread.sleep(250);
                    //    in  = new ObjectInputStream(socket.getInputStream());
                    Object obj = null;
               while(flag){
                   try{
                       checkSocketConnect();
                       obj = in.readObject();
                       if(obj instanceof ChatData.MSG){
                           // ??? TipsUtils.MsgNotificaction();

                           ChatData.MSG msg = (ChatData.MSG)obj;
                           Log.d("ChatConnThread", "ChatConnThread receive msg type "+msg.getType());
                           Log.d("ChatConnThread","ChatConnThread receive msg type from "+msg.getFromId());
                           Log.d("ChatConnThread", "ChatConnThread receive msg type to "+msg.getToId());
                           Log.d("ChatConnThread", "ChatConnThread receive msg type time "+msg.getTime());
                           Log.d("ChatConnThread", "ChatConnThread receive msg type seq "+msg.getSeqNum());
                           switch(msg.getType()){
                               case ChatData.Type.CHATTING:
                                   msg.setHasRead(false);
                                   Log.d("ChatConnThread",msg.getFromId()+" send chatting msg to "+msg.getToId());
                                   MsgUtils.ShowChattingMsg(mContext,msg);
                                   break;
                               case ChatData.Type.ADD_FRIEND:
                                   Log.d("ChatConnThread",msg.getFromId()+"send add friend request from "+msg.getFromId());
                                   //        Log.d("ChatConnThread",msg.getFromId()+"send add friend request photo size "+msg.getPhoto().length());
                                   Log.d("ChatConnThread",msg.getFromId()+"send add friend request to "+msg.getToId());

                                   MsgUtils.showAddFriendMsg(mContext, msg);
                                   //    MsgUtils.showAddFriendMsg(mContext, msg);
                                   break;
                               case ChatData.Type.ADD_AGREE:
                                   Log.d("ChatConnThread",msg.getFromId()+"send add friend agree from "+msg.getFromId());
                                   //    Log.d("ChatConnThread",msg.getFromId()+"send add friend agree photo size "+msg.getPhoto().length());
                                   Log.d("ChatConnThread",msg.getFromId()+"send add friend agree to "+msg.getToId());
                                   MsgUtils.AddNewFriend(mContext, msg);
                                   break;
                               case ChatData.Type.GET_BIGIMAGE:
                                   Log.d("PictureViewActivity", " receive big image from "+msg.getFromId());
                                   Log.d("PictureViewActivity", " receive big image to "+msg.getToId());
                                   //     Log.d("PictureViewActivity", " receive big image: "+msg.getBigBitmap());
                                   MsgUtils.ShowBigImage(mContext,msg);

                           }

                       }
                   }catch(Exception e){
                       Log.d("ChatConnThread","read data Exception occur");
                       e.printStackTrace();

                   }


               }


            }catch(Exception e){
                Log.d("ChatConnThread","exception occur");
                e.printStackTrace();
            }
        }
    }

}
