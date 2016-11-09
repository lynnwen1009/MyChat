package com.lynn.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;

import com.lynn.chattest2.R;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class TabHostActivity extends TabActivity implements HttpCallBackListener {
    private ChatConnThread mConnThread;
    UpdateNewFriendsBroadcastReceiver receiver;
    List<ChatData.MSG> newfriendslist ;

    Handler handler;

    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tab_host);
        ActivityCollector.addActivity(this);
        newfriendslist = new ArrayList<ChatData.MSG>();
        IntentFilter filter = new IntentFilter();
     //   filter.addAction(Constants.Actions.NEW_FRIEND_LIST);
    //    filter.addAction(Constants.Actions.CHAT_LIST);
        filter.addAction(Constants.Flags.CHATTING_MSG);
        filter.addAction(Constants.Flags.ADD_FRIEND_AGREE);
        filter.addAction(Constants.Flags.ADD_FRIEND_REQUEST);
        receiver = new UpdateNewFriendsBroadcastReceiver();
        registerReceiver(receiver, filter);
        Log.d("TabHostActivity", "TabHostActivity onCreate()");
        handler = HandlerUtils.getHandler("TabHostActivity");

        mContext = this;
        mConnThread = new ChatConnThread(mContext);
       mConnThread.start();
        MsgUtils.SetConnThread(mConnThread);
    //    handler.post(mConnThread);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ChatData.MSG msg = new ChatData.MSG();
                msg.setFromId(CacheUtils.GetUserId());
                msg.setType(ChatData.Type.OFFLINE_MSG);
                MsgUtils.SendMsg(msg);
                Log.d("ChatConnThread", "send get offline msgs");
            }
        },2*1000);
        Log.d("TabHostActivity", "user id: " + CacheUtils.GetUserId());

     /*   new Thread(new Runnable() {
            @Override
            public void run() {
                DataBaseManager.init(mContext, CacheUtils.GetUserId());

            }
        }).start();
*/

   //     new Thread(new Runnable(){
   //         @Override
   //         public void run() {
                /*
                JSONObject reqJson = new JSONObject();
                try {
                    reqJson.put(Constants.GetFriendList.RequestParams.USER_ID, CacheUtils.GetUserId());
                    Log.d("TabHostActivity", "TabHostActivity oncreate() send get friends list request:"+reqJson.toString());
                    // RSA ??
              //      String data = EncryptUtils.GetRsaEncrypt(reqJson.toString());
                    String data = reqJson.toString();
                    HttpUtils.sendRequest(Constants.ID.GET_FRIEND_LIST, data,
                            TabHostActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    //????10??????????
                 //   mMultiHandler.postDelayed(this, 10000);
                }*/
/*
                ChatData.MSG msg = new ChatData.MSG();
                msg.setFromId(CacheUtils.GetUserId());
                msg.setType(ChatData.Type.OFFLINE_MSG);
                MsgUtils.SendMsg(msg);
                Log.d("ChatConnThread","send get offline msgs");
                */
      //      }
   //     }).start();


        TabHost tabHost = getTabHost();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("chats")
                .setIndicator("Chats")
                .setContent(new Intent(this, ChatsActivity.class));
        tabHost.addTab(tab1);
        //  Intent intent = new Intent()
        TabHost.TabSpec tab2 = tabHost.newTabSpec("contacts")
                .setIndicator("Contacts")
                .setContent(new Intent(this, FriendsList.class));
        tabHost.addTab(tab2);

        TabHost.TabSpec tab3 = tabHost.newTabSpec("me")
                .setIndicator("Me")
                .setContent(new Intent(this, AboutMeActivity.class));
        tabHost.addTab(tab3);

        Intent intent = getIntent();
        String string = intent.getStringExtra("fromBroad");


        if(string!=null){
            if(string.equals("fromBroadcastForChatting")){
                Log.d("TabHostActivity","TabHostActivity from broadcast for chatting");
                Intent intent1 = new Intent(TabHostActivity.this,MainActivity.class);
                User  friend = (User)intent.getSerializableExtra(Constants.Flags.CHATTING_FRIEND);
                intent1.putExtra(Constants.Flags.CHATTING_FRIEND, friend);
                startActivity(intent1);

            }else if(string.equals("fromBroadcastForNewFriends")){
                Log.d("TabHostActivity","TabHostActivity from broadcast for chatting");
                Intent intent1 = new Intent(TabHostActivity.this,NewFriendsActivity.class);
                //  intent1.putExtra(Constants.Flags.CHATTING_FRIEND, friend);
                startActivity(intent1);
            }
        }


    }
    class UpdateNewFriendsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //  ChatData.MSG msg = (ChatData.MSG)(intent.getSerializableExtra("Constants.Flags.NEW_FRIEND_LIST"));
            Bundle extras= intent.getExtras();  ;
            ChatData.MSG msg = (ChatData.MSG)extras.getSerializable(Constants.Flags.MSG);
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification  ;
            Intent it = null;
            PendingIntent pi = null;
            int unRead = 0;
            Log.d("TabHostActivity", "onReceive()");
    //        User friend = UserList.getFriendByName(msg.getName());
            String userId = msg.getFromId();
            User friend = UserList.getFriendByUserId(userId);


            int notificationId = 0;
            String ticker=null;
            String notificationTitle = null;
            String notificationContent = null;


            switch(msg.getType()){
                case ChatData.Type.CHATTING:
                    if(NotificationUtils.getResumeFlag()==1){
                        //chatting msg and chatting activity is resumed,then do not send notification
                        Log.d("TabHostActivity", "onReceive(),chatting UI resume,do not send notification ");
                    //    Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    //    vibrator.vibrate(new long[]{0,500,500,500},-1);
                        return;
                    }

                case ChatData.Type.ADD_AGREE:
                    NotificationUtils.addTounReadNumHashMap(userId);




                    it = new Intent(mContext,MainActivity.class);
                    it.putExtra(Constants.Flags.CHATTING_FRIEND, friend);
                    unRead = friend.getUnReadMsgNum();
                    notificationTitle = msg.getName();
                    String content;
                    if(msg.getChatType()==ChatData.Type.IMAGE){
                        content = "[picture]";
                        ticker = msg.getName()+":"+"[picture]";
                    }
                    else {
                        ticker = msg.getName()+":"+msg.getMsg();
                        content = msg.getMsg();
                    }
                    if(unRead>1){
                        notificationContent = "["+unRead+"]"+msg.getName()+":"+content; ;

                    }else{
                        notificationContent =  msg.getName()+":"+content;
                    }
                    notificationId = 1;
                    break;
                case ChatData.Type.ADD_FRIEND:

                    ticker = msg.getName()+" sent you a friend invitation";
                    it = new Intent(mContext,NewFriendsActivity.class);
                    notificationTitle = msg.getName();
                    notificationContent =  msg.getName();
                    notificationContent = "sent you a friend invitation";


                    notificationId = 2;

                    break;

                default:
                    break;
            }
            notification = new Notification(R.drawable.abc_btn_check_material,
                   ticker,System.currentTimeMillis());

            pi = PendingIntent.getActivity(mContext,0,it,PendingIntent.FLAG_CANCEL_CURRENT);
            long[] vibrates = {0,500,500,500};
            notification.vibrate = vibrates;
        //    Uri soundUri = Uri.fromFile(new File("system/media/audio/ringtones/Basic_tone.ogg"));
        //    notification.sound = soundUri;
            notification.ledARGB = Color.GREEN;
            notification.ledOnMS = 1000;
            notification.ledOffMS = 1000;
            notification.flags = Notification.FLAG_SHOW_LIGHTS;
            notification.setLatestEventInfo(mContext, notificationTitle, notificationContent, pi);

            manager.notify(notificationId, notification);
            //Bundle bundle = intent.getExtras();


        //    newfriendslist.add(msg);
          //  UserList.addToNewFriendsList(msg);
//            Log.d("TabHostActivity", "to id:"+msg.getToId());





        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab_host, menu);
        return true;
    }
    public void httpCallBack(int id, JSONObject resp){
        if(id==Constants.ID.GET_FRIEND_LIST){
            String resCode=resp.optString(Constants.ResponseParams.RES_CODE);
            if("0".equals(resCode)){
                Log.d("TabHostActivity", "get friends list succeed");
               UserList.UpdateFriendList(resp);
                UserList.initChattingList();
                List<User> list =UserList.getFriendsList();
                        Log.d("TabHostActivity", "friends list size: " + list.size());
                for(int i=0;i<list.size();i++){
                    Log.d("TabHostActivity","userId: "+list.get(i).getUserId());
                }
            }
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        ActivityCollector.removeActivity(this);
        Log.d("TabHostActivity", "TabHostActivity onDestroy()");
    }
    @Override
    protected void onPause() {
        super.onPause();

         Log.d("TabHostActivity", "TabHostActivity onPause()");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TabHostActivity", "TabHostActivity onResume()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("TabHostActivity", "TabHostActivity onSaveInstanceState()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TabHostActivity", "onRestart()");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TabHostActivity", "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TabHostActivity", "onStop()");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
