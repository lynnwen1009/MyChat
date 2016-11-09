package com.lynn.chat;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lynn.chattest2.R;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements View.OnFocusChangeListener,View.OnLayoutChangeListener {
    private ListView msgListView;
    private EditText historyEdit;
    private EditText inputText;
    private Button send;
    private Button loginButton;
    private Button leaveButton;

    private EditText usernameEdit;
    private EditText ipEdit;
    private String lastMsg;

    private MsgAdatpter adapter;
    private List<Msg> msgList = new ArrayList<Msg>();
    private Context mContext;

    Socket socket;
    Thread thread;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
   // ObjectInputStream dataInputStream;
   // ObjectOutputStream dataOutputStream;

    String Yourself;
    String Target;
    User mFriend;
    String mFriendId;
    boolean flag = false;
    MessageFromLoginThreadBroadcastReceiver receiver;
    public static final int CROP_PHOTO = 2;
    public static final int TAKE_PHOTO = 1;
    Uri imageUri;
    Bitmap bitmap;
    String username,ip,chat_txt,chat_in,chat_content,accountFrom,accountTo;
    int seqNumImage = 0;
    Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            //    histroyEdit.append(chat_in);
            adapter.notifyDataSetChanged();
            msgListView.setSelection(msgList.size());
            super.handleMessage(msg);
        }
    };
    LinearLayout send_layout;
    Button images;
    Button location;
    Button sights;
    LinearLayout layout_chatting;
    Handler handler;
    private final static String TAG = "MainActivity";

    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationManager locationManager;
    private String provider;
    private boolean isFirstLocate = true;
    private TextView title;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
     //   mapView = (MapView)findViewById(R.id.right_map_view);
       // baiduMap = mapView.getMap();
      //  baiduMap.setMyLocationEnabled(true);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
     /*   List<String> providerList = locationManager.getProviders(true);
        if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
        }else if(providerList.contains(LocationManager.GPS_PROVIDER)){
            provider = LocationManager.GPS_PROVIDER;
        }else{
            Toast.makeText(this,"No location provider to use",Toast.LENGTH_SHORT).show();
        }
        */
   //     Location location = locationManager.getLastKnownLocation(provider);
    //    if(location!=null){

    //        navigateTo(location);
  //      }
        ActivityCollector.addActivity(this);
        mContext = this;
        layout_chatting = (LinearLayout) findViewById(R.id.layout_chatting);
        send_layout = (LinearLayout)findViewById(R.id.layout_send_choose);
        images = (Button) findViewById(R.id.choose_images);
         location = (Button) findViewById(R.id.choose_location);
         sights = (Button) findViewById(R.id.choose_sights);
        title = (TextView)findViewById(R.id.chatting_title);
        back = (Button)findViewById(R.id.chatting_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        handler = HandlerUtils.getChattingHandler();

        Log.d("TabHostActivity","mainActivity oncreate()");
   //     send_layout.setVisibility(View.GONE);
        layout_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.layout_chatting:
                        Log.d("MainActivity", "text input lost focus hide soft input");
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        break;
                }
            }
        });
        Intent intent = getIntent();

        Intent intent1 = getIntent();

        mFriend = (User) intent1.getSerializableExtra(Constants.Flags.CHATTING_FRIEND);
        mFriendId = mFriend.getUserId();
        title.setText(mFriendId);
        mFriend.setUnReadMsgNum(0);
        UserList.addToFriendList(mFriend);//modify unread num to zero

        DataBaseManager.initChattingTable(mFriendId);

    /*  NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);
        NotificationUtils.deleteUnReadNum(mFriendId);
*/
        msgList.addAll(UserList.getChattingList(mFriendId));

        //   msgList = DataBaseManager.readChattingTable(mFriendId);
        //  msgList = UserList.getChattingList(mFriendId);

        Log.d("MainActivity","onCreate(),msgList.size(): "+msgList.size());
        adapter = new MsgAdatpter(MainActivity.this, R.layout.msg_item, msgList);
  //      usernameEdit = (EditText) findViewById(R.id.username);
      //  ipEdit = (EditText) findViewById(R.id.ip)
        inputText = (EditText) findViewById(R.id.input_text);
        inputText.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int editStart;
            private int editEnd;
            private final int charMaxNum = 18;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                temp = s;
                Log.d("MainActivity","beforeTextChanged():temp: "+temp);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("MainActivity","onTextChanged()");
                Toast.makeText(MainActivity.this,"you can still input "+(charMaxNum-s.length())+" characters",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("MainActivity","afterTextChanged()");
                editEnd = inputText.getSelectionEnd();
                editStart = inputText.getSelectionStart();
                if(s.length()>0){
                    send.setText("Send");
                }
                else
                {
                    send.setText("Choose");
                }
            }
        });
/*
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("MainActivity", "(width,height---1)=" + "(" + layout_chatting.getWidth()
                        + "," + layout_chatting.getHeight() + ")");

            }
        }, 10,2000);
*/
      //  inputText.setOnClickListener(listener);
        inputText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("MainActivity", "(on touch input root layout height---1)=" + "(" + layout_chatting.getHeight() + ")");
                Log.d("MainActivity","input text is touch");
                inputText.requestFocus();

                send_layout.setVisibility(View.GONE);
                Log.d("MainActivity", "(on touch input root layout height----2)=" + "(" + layout_chatting.getHeight() + ")");
                keyBoardPopFlag = 1;//set the keyboard pop up flag
                return false;
            }
        });
      //  inputText.clearFocus();
        send = (Button) findViewById(R.id.send);
        send.setOnFocusChangeListener(this);
   //     loginButton = (Button) findViewById(R.id.login_button);
  //      leaveButton = (Button) findViewById(R.id.leave_button);


        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        msgListView.setSelection(msgList.size());
        send.setOnClickListener(listener);
        images.setOnClickListener(listener);
        sights.setOnClickListener(listener);
        location.setOnClickListener(listener);
        openBroadCastReceiverFromLogin();
        MsgUtils.SetCurrBroadCast(mFriendId);
        Log.d("ChatsActivity","MainActivity,set broadcast:"+MsgUtils.GetCurrBroadCast());
   //     loginButton.setOnClickListener(listener);
    //    leaveButton.setOnClickListener(listener);
    }
    public void openBroadCastReceiverFromLogin(){
        receiver = new MessageFromLoginThreadBroadcastReceiver();
        IntentFilter filter = new IntentFilter();

        filter.addAction(Constants.Actions.CHATTING_PREFIX + mFriendId);
        registerReceiver(receiver, filter);
        Log.d("MainActivity","register broadcast successed");
    }
    //receive message from server
    public class MessageFromLoginThreadBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
         //   String textMessage = intent.getStringExtra("TextMessage");

            Bundle extras = intent.getExtras();
            ChatData.MSG msg = (ChatData.MSG)extras.getSerializable(Constants.Flags.MSG);

            String textMessage = msg.getMsg();
            Msg msg1 = UserList.getLastChattingMapHash().get(msg.getFromId());
            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0,500,500,500},-1);
        //    msg1.setHasRead(true);//has read
       //     UserList.getLastChattingMapHash().remove(msg.getFromId());
      //      UserList.getLastChattingMapHash().put(msg.getFromId(), msg1);

            Log.d("MainActivity", "onReceive(),msgList.size()--1: " + msgList.size());
            Log.d("MainActivity", "onReceive(),UserList.getChattingList(mFriendId).size()--1: " + UserList.getChattingList(mFriendId).size());
            Log.d("MainActivity","onReceive text message: "+textMessage);
            Log.d("MainActivity", "onReceive from id: " + msg.getFromId());
            Log.d("MainActivity", "onReceive to id: " + msg.getToId());
//            msgList.remove(msgList.size()-1);
          //  msg.setHasRead(true);
            seqNumImage++;
            Msg message;
            if(msg.getChatType()==ChatData.Type.CHARACTER){
                //message is character
                message = new Msg(textMessage, Msg.TYPE_RECEIVED,msg.getFromId(),msg.getToId(),MsgUtils.GetFormatTime(),false);



            }else{
                //message is image
                message = new Msg(textMessage, Msg.TYPE_RECEIVED_IMAGE,msg.getFromId(),msg.getToId(),MsgUtils.GetFormatTime(),false);
                message.setSeqNumImage(msg.getSeqNum());
            }


            msgList.add(message);
        //    msgList = UserList.getChattingList(mFriendId);
            Log.d("MainActivity", "onReceive msgList.size()--2: " + (msgList.size()));
     //       msgListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            msgListView.setSelection(msgList.size());
        }
    }
    private void navigateTo(Location location){
        if(isFirstLocate){
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            Log.d("MainActivity","send button get focus");
            send_layout.setVisibility(View.VISIBLE);
        }else{
            Log.d("MainActivity","send button lost focus  ");
            send_layout.setVisibility(View.GONE);
        }
    }
    private int chooseButtonPushFlag = 0; //1:push the choose button
    private int keyBoardPopFlag = 0;//0:disappear 1:pop up
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            /*
                case R.id.input_text:
                    Log.d("MainActivity", "click the input text");
                    if(!inputText.isFocused()){
                        inputText.requestFocus();

                        Log.d("MainActivity", " input text request focus");

                    }

                    send_layout.setVisibility(View.GONE);
                    break;*/
                case R.id.send:
                    Log.d("MainActivity", "click the send button");
                    LinearLayout.LayoutParams params = null;
                    int widthRoot = 0;
                    int heightRoot = 0;
                    int sendLayoutWidth = 0;
                    int sendLayoutHeight = 0;
               //     Log.d("MainActivity", "(sendlayout.width,sendlayoutheight)=-----1"+
                 //           "("+send_layout.getWidth()+","+send_layout.getHeight()+")");
                    if(send.getText().toString().equals("Send")){
                        //input text has character to send

                    String sendMessage =  inputText.getText().toString();
                    String UserId = CacheUtils.GetUserId();
                //    Msg msg1 = new Msg( sendMessage, Msg.TYPE_SENT,UserId,mFriendId,MsgUtils.GetFormatTime(),false);
                //    msgList.add(msg1);

               //     Log.d("MainActivity", " send Messaage msgList.size()--2: " + (msgList.size()));
                    Log.d("MainActivity", " send Messaage msgList.size()--2: " + (msgList.size()));
                   final ChatData.MSG msg =new ChatData.MSG();
                    msg.setTime(MsgUtils.GetFormatTime());
                    msg.setType(ChatData.Type.CHATTING);
                    msg.setFromId(CacheUtils.GetUserId());
                    msg.setToId(mFriendId);

                    msg.setMsg(sendMessage);
                    msg.setPhoto(" ");
                    msg.setName(CacheUtils.GetUserId());
                    msg.setGender(CacheUtils.GetGender());

                        Log.d("MainActivity", "send message chat type: " + msg.getChatType()+"");
                    Log.d("MainActivity", "send message: " + msg.getMsg());
                    Log.d("MainActivity","send message from: " +msg.getFromId());
                    Log.d("MainActivity","send message to: " +msg.getToId());

                    Msg message = new Msg( sendMessage, Msg.TYPE_SENT,UserId,mFriendId,MsgUtils.GetFormatTime(),false);
                    List<Msg> chattingList = UserList.getChattingList(mFriendId);
                    chattingList.add(message);
                    UserList.addToChattingMapHash(mFriendId, chattingList);
                    UserList.getLastChattingMapHash().remove(mFriendId);
                    UserList.addToLastChattingMapHash(mFriendId, message);
                    msgList.add(message);
                    Log.d("MainActivity", "send message msgList size: " + msgList.size());
              //      msgListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                MsgUtils.SendMsg(msg);
                            }
                        });
                        seqNumImage++;

                //    Log.d("MainActivity","send to server ,the message is: "+inputText.getText().toString());
                        inputText.setText(" ");
                        send.setText("Choose");


                    }else{
                        //no character to send, choose layout should be showed
                        if(inputText.isFocused()){



                            //     widthRoot = layout_chatting.getWidth();
                            //       heightRoot = layout_chatting.getHeight();
                            //       Log.d("MainActivity", "(sendlayout.width,sendlayoutheight)=----2"+
                            //               "("+send_layout.getWidth()+","+send_layout.getHeight()+")");
                            Log.d("MainActivity", "(rootlayout_width,rootLayout_height)=----1"+"("+layout_chatting.getWidth()+
                                    ","+layout_chatting.getHeight()+")");
                            //     keyBoardPopFlag = 0;//set the flag to indicate the keyboard disappear
                            inputText.clearFocus();
                            //set choose layout flag to indicate the choose layout params setting will be done at the time
                            // when the event keyboard disappear is listened by layoutchange listener
                            chooseButtonPushFlag = 1;
                            hideKeyBoard();

                        }
                        else{
                            Log.d("MainActivity", "send button ,input not focused,keyboardheight: "+keyBoardHeight);
                            sendLayoutHeight = keyBoardHeight>0?keyBoardHeight:rootLayoutHeight/2;
                            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,sendLayoutHeight);
                            send_layout.setVisibility(View.VISIBLE);
                            send_layout.setLayoutParams(params);
                        }
                        Log.d("MainActivity", "(rootlayout_width,rootLayout_height)=----2"+"("+layout_chatting.getWidth()+
                                ","+layout_chatting.getHeight()+")");
                    }


              //      Log.d("MainActivity", "(sendlayout.width,sendlayoutheight)=---3"+
           //                 "("+send_layout.getWidth()+","+send_layout.getHeight()+")");
                    //get whole window size include status bar
             //       int widthWindow = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
              //      int heightWindow = ((WindowManager)mContext.getSysteon"("+widthWindow+","+heightWindow+")");
               //     Log.d("MainActivity", "keyBoardHeight: "+keyBoardHeight);
              //      Log.d("MainActivity", "rootLayoutHeight: "+ rootLayoutHeight);
                    /*
                    if(keyBoardHeight>0){
                       // sendLayoutWidth = widthWindow-widthRoot;
                    //    layout_chatting.removeOnLayoutChangeListener(MainActivity.this);
                        sendLayoutHeight = keyBoardHeight;
                    }
                    else{
                        //sendLayoutWidth = widthWindow/2;
                        sendLayoutHeight = rootLayoutHeight/2;
                    }
                    */
                 //   sendLayoutHeight = 910;
              //      params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,sendLayoutHeight);
              //    inputText.setClickable(true);
              //    inputText.setFocusable(true);
              //    inputText.requestFocus();

               //     chooseButtonPushFlag = 1;
               //     if(keyBoardPopFlag==1){
                 //       params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,sendLayoutHeight);
                   // }
             //       send_layout.setVisibility(View.VISIBLE);
               //     send_layout.setLayoutParams(params);
                    /*
                    if(inputText.isFocused()){



                        widthRoot = layout_chatting.getWidth();
                        heightRoot = layout_chatting.getHeight();
                        Log.d("MainActivity", "(sendlayout.width,sendlayoutheight)=----2"+
                                "("+send_layout.getWidth()+","+send_layout.getHeight()+")");
                        Log.d("MainActivity", "(width,height)="+"("+widthRoot+","+heightRoot+")");
                        inputText.clearFocus();
                        hideKeyBoard();

                    }*/

                    Log.d("MainActivity", "(sendlayout.width,sendlayoutheight)=----4" + "" +
                            "(" + send_layout.getWidth() + "," + send_layout.getHeight() + ")");

                    break;

                case R.id.choose_images:
                    /*
                    //solution 1
                    File outputImage = new File(Environment.getExternalStorageDirectory(),"image_send.jpg");
                    try{
                        if(outputImage.exists()) {
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    }catch(IOException e){
                        e.printStackTrace();

                    }
                    imageUri = Uri.fromFile(outputImage);
                     Intent intent1 = new Intent("android.intent.action.GET_CONTENT",null);
                    intent1.setType("image/*");

                     intent1.putExtra("crop", true);
                    intent1.putExtra("scale", true);
                    intent1.putExtra("aspectX",2);
                    intent1.putExtra("outputX",600);
                    intent1.putExtra("outputY",300);
                     intent1.putExtra("return-data",false);
                     intent1.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    intent1.putExtra("noFaceDetection",true);

*/
                // solution 2:
                     Intent intent1 = new Intent(Intent.ACTION_PICK,
                      MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent1,CROP_PHOTO);


                    break;

                case R.id.choose_sights:
                    break;
                case R.id.choose_location:
                    Msg message = new Msg("", Msg.TYPE_SENT_MAP,CacheUtils.GetUserId(),mFriendId,MsgUtils.GetFormatTime(),false);

                    List<Msg> chattingList = UserList.getChattingList(mFriendId);
                    chattingList.add(message);
                    UserList.addToChattingMapHash(mFriendId, chattingList);
                    UserList.getLastChattingMapHash().remove(mFriendId);
                    UserList.addToLastChattingMapHash(mFriendId, message);
                    msgList.add(message);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                break;
            case CROP_PHOTO:
                if(resultCode==RESULT_OK){
                    /*
                    //bitmap handling solution 1
                    Bitmap bigBitmap;
                    Bitmap smallBitmap;

                        bigBitmap = PhotoUtils.decodeUriToBitmap(imageUri,mContext);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        int smallBitmapWidth = bigBitmap.getWidth()/4;
                        int smallBitmapHeight = bigBitmap.getHeight()/4;
                        Log.d(TAG,"smallBitmapWidth should be: "+smallBitmapWidth);
                        Log.d(TAG,"smallBitmapHeight should be: "+smallBitmapHeight);
                        smallBitmap = Bitmap.createScaledBitmap(bigBitmap,smallBitmapWidth,smallBitmapHeight,false);
                        Log.d(TAG,"smallBitmapWidth result be: "+smallBitmap.getWidth());
                        Log.d(TAG,"smallBitmapHeight result be: "+smallBitmap.getHeight());
                */


                   /*
                    Log.d("MainActivity", "choose picture uri: " + selectedImageUri.toString());
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath =  cursor.getString(columnIndex);
                    cursor.close();
                    */

                    //bitmap handling solution 2
                    Uri selectedImageUri = data.getData();
                    String picturePath = PhotoUtils.uriToPath(selectedImageUri, mContext);
                    Log.d("MainActivity", "choose picture Uri: " + selectedImageUri.toString());
                    Log.d("MainActivity", "choose picture path: " + picturePath);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                     BitmapFactory.decodeFile(picturePath, options);

                    Log.d("MainActivity", "send path width: " + options.outWidth);
                    Log.d("MainActivity", "send path height: " + options.outHeight);
                    Log.d("MainActivity", "send path prefer config: " + options.inPreferredConfig);
                    Log.d("MainActivity", "send path ARGB_4444: " + Bitmap.Config.ARGB_4444);
                    options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                    if(options.outWidth>1080)
                        options.inSampleSize = 8;
                    else
                    options.inSampleSize = 4;
                    options.inJustDecodeBounds = false;

                    Bitmap smallBitmap =BitmapFactory.decodeFile(picturePath,options);
                    Bitmap bigBitmap = BitmapFactory.decodeFile(picturePath);

                    Log.d("MainActivity", "send path width--1: " + options.outWidth);
                    Log.d("MainActivity", "send path height--2: " + options.outHeight);
                    Log.d("MainActivity", "send path bitmap width--1: " + smallBitmap.getWidth());
                    Log.d("MainActivity", "send path bitmap height--2: " + smallBitmap.getHeight());

                   final ChatData.MSG msg =new ChatData.MSG();
                    msg.setTime(MsgUtils.GetFormatTime());
                    msg.setType(ChatData.Type.CHATTING);
                    msg.setChatType(ChatData.Type.IMAGE);
                    msg.setFromId(CacheUtils.GetUserId());
                    msg.setToId(mFriendId);
                    msg.setMsg(bitmapToString(smallBitmap, 100));

                    msg.setBigBitmap(bitmapToBytes(bigBitmap, 100));
                    msg.setSeqNum(seqNumImage++);
                    Log.d("MainActivity", "seqnum:" + msg.getSeqNum());
//                    msg.setPhoto(" ");
                    msg.setName(CacheUtils.GetUserId());
                    msg.setGender(CacheUtils.GetGender());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MsgUtils.SendMsg(msg);
                        }
                    });
                   //send message to server
                    Log.d("MainActivity", "send image---------------: "+msg.getMsg());
                    Log.d("MainActivity","send image from: " +msg.getFromId());
                    Log.d("MainActivity", "send image to: " + msg.getToId());

                    Msg message = new Msg(bitmapToString(smallBitmap,100), Msg.TYPE_SENT_IMAGE,CacheUtils.GetUserId(),mFriendId,MsgUtils.GetFormatTime(),false);
                    message.setPicUri(picturePath);
                    message.setSeqNumImage(msg.getSeqNum());
                    List<Msg> chattingList = UserList.getChattingList(mFriendId);
                    chattingList.add(message);
                    UserList.addToChattingMapHash(mFriendId, chattingList);
                    UserList.getLastChattingMapHash().remove(mFriendId);
                    UserList.addToLastChattingMapHash(mFriendId, message);
                    msgList.add(message);
                    Log.d("MainActivity", "send image msgList size: " + msgList.size());
                    //      msgListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());




                    /*
                    try{
                         bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        if(bitmap==null){
                            Log.d("MainActivity","bitmap==null!!!!!!!");
                        }

                        ChatData.MSG msg =new ChatData.MSG();
                        msg.setTime(MsgUtils.GetFormatTime());
                        msg.setType(ChatData.Type.CHATTING);
                        msg.setChatType(ChatData.Type.IMAGE);
                        msg.setFromId(CacheUtils.GetUserId());
                        msg.setToId(mFriendId);
                        msg.setMsg(bitmapToString(bitmap, 100));
                      //  msg.setBitmap(bitmap);

                        msg.setPhoto(" ");
                        msg.setName(CacheUtils.GetUserId());
                        msg.setGender(CacheUtils.GetGender());

                        Log.d("MainActivity", "send image: " + msg.getMsg());
                        Log.d("MainActivity","send image from: " +msg.getFromId());
                        Log.d("MainActivity","send image to: " +msg.getToId());

                        Msg message = new Msg(bitmapToString(bitmap,100), Msg.TYPE_SENT_IMAGE,CacheUtils.GetUserId(),mFriendId,MsgUtils.GetFormatTime(),false);
                        message.setBitmap(bitmap);
                        List<Msg> chattingList = UserList.getChattingList(mFriendId);
                        chattingList.add(message);
                        UserList.addToChattingMapHash(mFriendId, chattingList);
                        UserList.getLastChattingMapHash().remove(mFriendId);
                        UserList.addToLastChattingMapHash(mFriendId, message);
                        msgList.add(message);
                        Log.d("MainActivity","send image msgList size: " +msgList.size());
                        //      msgListView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        msgListView.setSelection(msgList.size());

                        MsgUtils.SendMsg(msg);

                     //   Toast.makeText(MainActivity.this,"you send picture",Toast.LENGTH_SHORT).show();
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    */
                }
        }
    }
    public byte[] bitmapToBytes(Bitmap bitmap,int bitmapQuality){
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,bitmapQuality,bStream);
        byte[] bytes = bStream.toByteArray();
        //string = Base64.encodeToString(bytes, Base64.DEFAULT);

        return bytes;
    }
    public String bitmapToString(Bitmap bitmap,int bitmapQuality){
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,bitmapQuality,bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);

        return string;
    }

@Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity","onStart()");
    }
    private int keyBoardHeight = 0;
    private int rootLayoutHeight = 0;

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        Log.d("MainActivity","bottom:"+bottom);
        Log.d("MainActivity", "oldBottom:" + oldBottom);
        if(oldBottom>0&&bottom>0&&oldBottom==bottom){
            Log.d("MainActivity","the root view has fully show");
            if(rootLayoutHeight==0) {
                //get the value when root view fully show at first time,
                // //the value is the max height of root layout
                rootLayoutHeight = bottom;
            }
            if(bottom==rootLayoutHeight){
                //before push choose button,there is no keyboard pop up and disappear
                if(chooseButtonPushFlag==1){
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,rootLayoutHeight/2);
                    send_layout.setVisibility(View.VISIBLE);
                    Log.d("MainActivity", "the root view has fully show----1");
                    send_layout.setLayoutParams(params);
                    chooseButtonPushFlag=0;
                }
            }
        }else if(oldBottom>0&&bottom>0&&oldBottom>bottom){
            Log.d("MainActivity","the keyboard has pop up");
            if(keyBoardHeight==0) {
                keyBoardHeight = oldBottom - bottom;
            }
        }else if(oldBottom>0&&bottom>0&&oldBottom<bottom){
            Log.d("MainActivity","soft keyboard disappear");
            //10/1 bug 1,clear the input text focus when keyboard disappear,so when push the choose button,it will show the choose layout
            inputText.clearFocus();
            if(chooseButtonPushFlag==1){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,keyBoardHeight);
                send_layout.setVisibility(View.VISIBLE);
                Log.d("MainActivity", "soft keyboard disappear--2,keyBoardHeight: "+keyBoardHeight);
                send_layout.setLayoutParams(params);
                chooseButtonPushFlag=0;
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
     //   Log.d("MainActivity","onBackPressed()");
     //   if(send_layout.getVisibility()==View.VISIBLE){
            //if send/choose layout is visible,then set it gone
    //        Log.d("MainActivity","onBackPressed(),set send layout gone");
   //         send_layout.setVisibility(View.GONE);
    //    }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("MainActivity", "onKeyDown()");
        if(send_layout.getVisibility()==View.VISIBLE){
            //if send/choose layout is visible,then set it gone,and MainActivity will not quit.
            Log.d("MainActivity","onKeyDown(),set send layout gone");
            send_layout.setVisibility(View.GONE);
            return false;
        }
        else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  onStopFlag = 0;
        NotificationUtils.setResumeFlagForChattingActivity(1);
        layout_chatting.addOnLayoutChangeListener(this);
        MsgUtils.SetCurrBroadCast(mFriendId);
       // msgList.addAll(UserList.getChattingList(mFriendId));
        //adapter.notifyDataSetChanged();
       // msgListView.setSelection(msgList.size());
        seqNumImage = msgList.size();
        NotificationCancel(1, mFriendId);
        Log.d("TabHostActivity", "MainActivity onResume()  " );
        Log.d("MainActivity", "onResume(),curr broadcast: " + MsgUtils.GetCurrBroadCast());
    }
   private void NotificationCancel(int notificationId,String friendId){
       NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
       manager.cancel(notificationId);
       NotificationUtils.deleteUnReadNum(friendId);
   }
    @Override
    protected void onPause() {
        super.onPause();
        NotificationUtils.setResumeFlagForChattingActivity(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataBaseManager.updateChattingTable(mFriendId, msgList);
                List<Msg> list = DataBaseManager.readChattingTable(mFriendId);
                Log.d("MainActivity","onResume(),read chatting table,list size: "+list.size());
            }
        }).start();


        //set user unreadNum to zero
        User friend = UserList.getFriendByUserId(mFriendId);
        friend.setUnReadMsgNum(0);

        Log.d("MainActivity", "onPause msgList.size(): " + (msgList.size()));

    }
 private int onStopFlag = 0;//1:stop state, 0:back to resume state
    @Override
    protected void onStop() {
        super.onStop();
        onStopFlag = 1;
        Log.d("MainActivity", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        MsgUtils.SetCurrBroadCast("");
        ActivityCollector.removeActivity(this);
        Log.d("MainActivity", "onDestroy()");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("MainActivity", "onRestart()");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("MainActivity", "protected onSaveInstanceState()");

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d("MainActivity", "public onSaveInstanceState()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
    private void hideKeyBoard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }
    class LruMemoryCache extends LruCache<String,Bitmap> {
        public LruMemoryCache(int maxSize){
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount()/1024;

        }
        public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
            if (getBitmapFromMemCache(key) == null) {
                put(key, bitmap);
            }
        }

        public Bitmap getBitmapFromMemCache(String key) {
            return get(key);
        }

    }


    /**
     * Created by Administrator on 8/25/2016.
     */
    private class MsgAdatpter extends ArrayAdapter<Msg> {
        private int resourceId;
        private List<Msg> list;
        private String lastTime;
        private Context mContext;
        public MsgAdatpter(Context context,int textViewResurceId,List<Msg> objects){
            super(context,textViewResurceId,objects);
            resourceId =  textViewResurceId;
            list = objects;
            lastTime = " ";
            mContext = context;
        }
        public View getView(int position,View convertView,ViewGroup parent){
            //Msg msg = getItem(position);
            String lastTime  = " ";
            if(position==0){
                lastTime = " ";
            }
            else{
                lastTime = list.get(position-1).getTimeStamp();
            }


            final Msg msg = list.get(position);
            String currentTime  = msg.getTimeStamp();

            View view;
            ViewHolder viewHolder;
            if(convertView==null){
                view = LayoutInflater.from(getContext()).inflate(resourceId,null);
                viewHolder = new ViewHolder();
                viewHolder.centerLayout = (LinearLayout)view.findViewById(R.id.center_layout);
                viewHolder.leftLayout = (RelativeLayout)view.findViewById(R.id.left_layout);
                viewHolder.rightLayout = (RelativeLayout)view.findViewById(R.id.right_layout);
                viewHolder.timeStamp = (TextView)view.findViewById(R.id.center_timeStamp);
                viewHolder.leftMsg = (TextView)view.findViewById(R.id.left_msg);
                viewHolder.rightMsg = (TextView)view.findViewById(R.id.right_msg);
                viewHolder.leftImage = (ImageView)view.findViewById(R.id.left_image);
            //    viewHolder.rightMap = (MapView)view.findViewById(R.id.right_map_view);
            //    viewHolder.leftMap = (MapView)view.findViewById(R.id.left_map_view);
            //    viewHolder.baiduMap = viewHolder.rightMap.getMap();
            /*
            viewHolder.rightImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                }
            });*/
                viewHolder.rightImage = (ImageView)view.findViewById(R.id.right_image);

                Log.d("MainActivity", "view =null last msg time:" + MsgUtils.GetFormatTime());
                view.setTag(viewHolder);
            }else {

                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
                Log.d("MainActivity", "set tag for view,viewHolder. ");
            }
            if(msg.getType()==Msg.TYPE_RECEIVED){
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.leftLayout.setBackgroundResource(R.drawable.chatting_msg_receive);
                viewHolder.leftMsg.setVisibility(View.VISIBLE);
                viewHolder.leftImage.setVisibility(View.GONE);
                viewHolder.leftMsg.setText(msg.getContent());
            }else if(msg.getType()==Msg.TYPE_SENT){
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setBackgroundResource(R.drawable.chatting_msg_send);
                viewHolder.rightImage.setVisibility(View.GONE);
                viewHolder.rightMsg.setVisibility(View.VISIBLE);
                viewHolder.rightMsg.setText(msg.getContent());
            }else if(msg.getType()==Msg.TYPE_SENT_IMAGE){
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.rightMsg.setVisibility(View.GONE);
                viewHolder.rightImage.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setBackgroundResource(0);

                int imageSeqNum = msg.getSeqNumImage();
                Log.d("MsgAdapter","right image seq num: "+imageSeqNum);
                //String
                Bitmap bm = CacheUtils.getBitmapFromLruCache(msg.getAccountFrom()+"_"+imageSeqNum);
                if(bm==null){
                    Log.d("MsgAdapter","load cache right null: "+imageSeqNum);
                    bm = PhotoUtils.StringToBitmap(msg.getContent());
                    CacheUtils.AddBitmapToLruCache(msg.getAccountFrom()+"_"+imageSeqNum,bm);
                }
                viewHolder.rightImage.setImageBitmap(bm);
                //  viewHolder.rightImage.setImageBitmap(StringToBitmap(msg.getContent()));
                final ImageView imageView= viewHolder.rightImage;
                viewHolder.rightImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext,PictureViewActivity.class);
                        Log.d("MainActivity","msgAdapter,pic Uri:"+msg.getPicUri().toString());
                        intent.putExtra(Constants.ExtraData.SENT_PHOTO_URI,msg);
                        mContext.startActivity(intent);

                    }
                });

                Log.d("MainActivity","msg adapter set right image");
            }else if(msg.getType()==Msg.TYPE_RECEIVED_IMAGE){
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.leftMsg.setVisibility(View.GONE);
                viewHolder.leftImage.setVisibility(View.VISIBLE);
                viewHolder.leftLayout.setBackgroundResource(0);
                int imageSeqNum = msg.getSeqNumImage();
                Log.d("MsgAdapter","left image seq num: "+imageSeqNum);
                //String
                Bitmap bm = CacheUtils.getBitmapFromLruCache(msg.getAccountFrom()+"_"+imageSeqNum);
                if(bm==null){
                    Log.d("MsgAdapter","load cache left null: "+imageSeqNum);
                    bm = PhotoUtils.StringToBitmap(msg.getContent());
                    CacheUtils.AddBitmapToLruCache(msg.getAccountFrom()+"_"+imageSeqNum,bm);
                }
                viewHolder.leftImage.setImageBitmap(bm);
                //   viewHolder.leftImage.setImageBitmap(StringToBitmap(msg.getContent()));
                viewHolder.leftImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext,PictureViewActivity.class);
                        intent.putExtra(Constants.ExtraData.SENT_PHOTO_URI,msg);
                        mContext.startActivity(intent);
                    /*
                    Bitmap bm = CacheUtils.getBitmapFromLruCache(msg.getAccountFrom()+"#"+msg.getSeqNumImage());
                    if(bm!=null){
                        Intent intent = new Intent(mContext,PictureViewActivity.class);
                        intent.putExtra(Constants.ExtraData.SENT_PHOTO_URI,msg);
                        mContext.startActivity(intent);
                    }else{
                        Intent intent = new Intent(mContext,PictureViewActivity.class);
                      //  intent.putExtra(Constants.ExtraData.SENT_PHOTO_URI,msg);
                        mContext.startActivity(intent);

                        ChatData.MSG msg1 =new ChatData.MSG();
                        msg1.setTime(msg.getTimeStamp());
                        msg1.setType(ChatData.Type.GET_BIGIMAGE);
                        msg1.setSeqNum(msg.getSeqNumImage());
                        msg1.setFromId(CacheUtils.GetUserId());
                        msg1.setToId(msg.getAccountFrom());

                        msg1.setMsg("this is get bitmap message");
                        msg1.setPhoto(" ");
                        msg1.setName(CacheUtils.GetUserId());
                        msg1.setGender(CacheUtils.GetGender());
                        //  Log.d("AddFriend", "add friend,user_Id: " + user_Id);
                        Log.d("PictureViewActivity", "get big image from: " + msg1.getFromId());
                        Log.d("PictureViewActivity", "get big image to: " + msg1.getToId());
                        Log.d("PictureViewActivity", "get big image seqnum: " + msg1.getSeqNum());
                        MsgUtils.SendMsg(msg1);
                    }


*/

                    }
                });
                Log.d("MainActivity", "msg adapter set left image");
            }/*
            else if(msg.getType()==Msg.TYPE_SENT_MAP){
                viewHolder.baiduMap.setMyLocationEnabled(true);
                Location location = locationManager.getLastKnownLocation(provider);
                if(location!=null){
                    navigateTo(location);
                }
            }else if(msg.getType()==Msg.TYPE_RECEIVED_IMAGE){

            }*/
            Log.d("MainActivity","curr msg time:"+msg.getTimeStamp());
            Log.d("MainActivity","last msg time:"+lastTime);
            if (!(msg.getTimeStamp().equals(lastTime))){
                //first msg or time pass 60s since last msg


                viewHolder.centerLayout.setVisibility(View.VISIBLE);
                viewHolder.timeStamp.setText(msg.getTimeStamp());
                Log.d("MainActivity", "the[" + position + "] message," + msg.getContent() + "at: " + msg.getTimeStamp());
                lastTime = msg.getTimeStamp();
            }
            else{
                viewHolder.centerLayout.setVisibility(View.GONE);
                Log.d("MainActivity", "time gap small than 60s,the[" + position + "] message," + msg.getContent() + "at: " + msg.getTimeStamp());
                //   viewHolder.timeStamp.setText(msg.getTimeStamp());
            }
            return view;
        }
        class ViewHolder{
            LinearLayout centerLayout;
            RelativeLayout leftLayout;
            RelativeLayout rightLayout;
            TextView timeStamp;
            TextView leftMsg;
            TextView rightMsg;
            ImageView rightImage;
            ImageView leftImage;
       //     MapView leftMap;
       //     MapView rightMap;
       //     BaiduMap baiduMap;

        }
        public Bitmap StringToBitmap(String string){
            Bitmap bitmap = null;
            try{
                byte[] bitmapArray;
                bitmapArray = Base64.decode(string, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);


            }catch(Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }
    }

}

