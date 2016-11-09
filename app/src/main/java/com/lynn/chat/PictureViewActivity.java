package com.lynn.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.lynn.chattest2.R;

import java.util.List;

public class PictureViewActivity extends Activity implements GestureDetector.OnGestureListener {
    private ImageView pictureView;
    private static final int SET_IMAGE_FROM_SERVER = 1;
    private static final int SET_IMAGE_FROM_PHONE = 2;
    private static final int SEND_GET_IMAGE_FROM_SERVER = 3;

    private Bitmap bitmap1;
    private Bitmap bitmap;
    GetBigImageReceiver receiver;
    private Context mContext;

    private Handler UIhandler = new Handler(){
        @Override

        public void handleMessage(Message msg) {
            switch(msg.what){
                case SET_IMAGE_FROM_SERVER:
                    pictureView.setImageBitmap(bitmap1);
                    Log.d("PictureViewActivity","handleMessage bitmap from server: "+((bitmap1!=null)?("get bitmap"):("bitmap1 null")));
                    break;
                case SET_IMAGE_FROM_PHONE:
                    pictureView.setImageBitmap(bitmap);

                    Log.d("PictureViewActivity", "handleMessage bitmap from phone: " + ((bitmap != null) ? ("get bitmap") : ("bitmap null")));
                    break;
                case SEND_GET_IMAGE_FROM_SERVER:
                    //send get bigmap msg to server



                   final ChatData.MSG msg1 =new ChatData.MSG();
                    msg1.setTime(message.getTimeStamp());
                    msg1.setType(ChatData.Type.GET_BIGIMAGE);
                    msg1.setSeqNum(message.getSeqNumImage());
                    msg1.setFromId(CacheUtils.GetUserId());

                    msg1.setToId(message.getAccountTo());



                    msg1.setMsg("this is get bitmap message");
                    msg1.setPhoto(" ");
                    msg1.setName(CacheUtils.GetUserId());
                    msg1.setGender(CacheUtils.GetGender());
                    //  Log.d("AddFriend", "add friend,user_Id: " + user_Id);
                    Log.d("PictureViewActivity", "get big image from: " + msg1.getFromId());
                    Log.d("PictureViewActivity", "get big image to: " + msg1.getToId());
                    Log.d("PictureViewActivity", "get big image seqnum: " + msg1.getSeqNum());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MsgUtils.SendMsg(msg1);
                        }
                    });



            }
        }
    };
private final static String TAG = "PictureViewActivity";
    Handler handler; //work handler
    Msg message;
    String path;
    String Id;
    int seqNum;
    List<Msg> list ;
    private GestureDetector gestureDetector = null;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_picture_view);
        mContext = this;
        ActivityCollector.addActivity(this);
        pictureView = (ImageView)findViewById(R.id.picture_view);
        pictureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        handler = HandlerUtils.getPictureViewHandler();
        Log.d(TAG, "onCreate() ");
        IntentFilter filter = new IntentFilter(Constants.Actions.GET_BIG_IMAGE);
        receiver = new GetBigImageReceiver();
        registerReceiver(receiver, filter);
        gestureDetector = new GestureDetector(this);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String friendId;
           message = (Msg)extras.getSerializable(Constants.ExtraData.SENT_PHOTO_URI);
        if(message.getType()==Msg.TYPE_SENT_IMAGE){
            friendId = message.getAccountTo();
        }
        else {
            friendId = message.getAccountFrom();
        }

        Log.d(TAG, "onCreate(),get friend id :" +friendId);

        list = UserList.getChattingList(friendId);

        {
        //    position = list.indexOf(message);

              path = message.getPicUri();
            Id = message.getAccountFrom();
            seqNum = message.getSeqNumImage();
             for(int i=0;i<list.size();i++){
                 if((seqNum==list.get(i).getSeqNumImage())&&(Id.equals(list.get(i).getAccountFrom()))){
                     position = i;
                     break;
                 }
             }
            Log.d(TAG,"onCreate(),get intent extra,position :"+position);
            Log.d(TAG,"onCreate(),get intent extra,list.size() :"+list.size());
            final Msg msg = message;
            Log.d(TAG,"onCreate(),get intent extra,uri:"+path);
            Log.d(TAG,"onCreate(),get intent extra,Id:"+Id);
            Log.d(TAG,"onCreate(),get intent extra,seqNum:"+seqNum+"");

            Bitmap bm = CacheUtils.getBitmapFromLruCache(Id+"#"+seqNum); //original photo,big
            if(bm!=null){
                Log.d(TAG,"onCreate(),get bitmap from cache success");
                pictureView.setImageBitmap(bm);
            }
            else{
                if(msg.getType()==Msg.TYPE_SENT_IMAGE)//the right message,sent image
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                         //   bitmap = PhotoUtils.decodeUriToBitmap(uri,mContext); //decode big image from phone
                            bitmap = BitmapFactory.decodeFile(path);
                            //PhotoUtils.saveToPhotoCache(uri,bitmap);
                            if(bitmap!=null){
                                Log.d(TAG, "onCreate(),get photo from phone success");
                                CacheUtils.AddBitmapToLruCache(Id+"#"+seqNum,bitmap);
                                Message message1 = new Message();
                                message1.what = SET_IMAGE_FROM_PHONE;
                                UIhandler.sendMessage(message1);
                            }
                            else{//get photo from server
                                Log.d(TAG, "onCreate(),get photo from phone failed,need get from server");

                                Message message1 = new Message();
                             //   message1.obj = message;
                                message1.what = SEND_GET_IMAGE_FROM_SERVER;
                                UIhandler.sendMessage(message1);

                            }

                        }
                    });
                }else{
                    Message message1 = new Message();
                    //   message1.obj = message;
                    message1.what = SEND_GET_IMAGE_FROM_SERVER;
                    UIhandler.sendMessage(message1);
                }
                //decode bitmap and add to LruCache

            }

        }
    //    final Uri picUri = intent.getStringExtra("picturePath");
   //     final Uri picUri = (Uri)intent.getParcelableExtra(Constants.ExtraData.SENT_PHOTO_URI);
     //   if(picUri!=null){
    //        Log.d("MainActivity","onCreate() right picture view picUri: "+picUri.toString());
     //   }
        //read from cache,do not decode bitmap from uri every time
        /*
        bitmap = PhotoUtils.getBitmapFromPhotoCache(picUri);

        if(bitmap!=null){
            pictureView.setImageBitmap(bitmap);

            Log.d("PictureViewActivity", "onCreate() set image bitmap ");
        }
        else{
            if(picUri!=null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bitmap = PhotoUtils.decodeUriToBitmap(picUri,mContext);
                        PhotoUtils.saveToPhotoCache(picUri,bitmap);
                        Message message1 = new Message();
                        message1.what = SET_IMAGE_FROM_PHONE;
                        handler.sendMessage(message1);
                    }
                }).start();

//            Log.d("PictureViewActivity", "pic width: " + bitmap.getWidth());
                //          Log.d("PictureViewActivity", "pic height: " + bitmap.getHeight());
                //   pictureView.setImageBitmap(bitmap);
            }
        }
*/
      //  BitmapFactory.Options options = new BitmapFactory.Options();
    //    options.inSampleSize = 1;
     //   options.inJustDecodeBounds = false;
     //   Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);


     //   Log.d("MainActivity", "PictureViewActivity onCreate(),pic Uri: " + picUri.toString());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    //    Log.d(TAG,"onTouchEvent");
        return gestureDetector.onTouchEvent(event);

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    int k;
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG,"onFling,position: "+position);
        boolean flag = false;
        if((e2.getX()-e1.getX())>120){
            Log.d(TAG,"onFling,left to right ");
            int i = position-1;

           while(i>0){
               flag = show(i);
               if(flag==true){

                   break;
               }
               i--;
           }
        }else {
            Log.d(TAG,"onFling,right to left ");
            int j = position+1;
            while(j<list.size()){
               flag = show(j);
               if(flag==true){
                   break;
               }
                j++;
            }
        }
        return flag;
    }

    public boolean show(int i)
    {

        String id;
        int seqNum;
        Bitmap bm;
        Log.d(TAG,"show pic i: "+i);
        {
            final Msg msg = list.get(i);

            final String pathUri = msg.getPicUri();
            if(msg.getType()==Msg.TYPE_RECEIVED_IMAGE){

                id = msg.getAccountFrom();
                seqNum = msg.getSeqNumImage();
                Log.d(TAG,"show pic TYPE_RECEIVED_IMAGE id: "+id);
                Log.d(TAG,"show pic TYPE_RECEIVED_IMAGE seqNum :"+seqNum);
                bm = CacheUtils.getBitmapFromLruCache(id+"#"+seqNum);
                if(bm==null) {
                    Log.d(TAG,"show pic TYPE_RECEIVED_IMAGE bm==null ");
                    Message message1 = new Message();
                    //   message1.obj = message;
                    message1.what = SEND_GET_IMAGE_FROM_SERVER;
                    UIhandler.sendMessage(message1);
                    Id = msg.getAccountFrom();
                    message = msg;
                }else{
                    Log.d(TAG,"show pic TYPE_RECEIVED_IMAGE set bm ");
                    pictureView.setImageBitmap(bm);
                }
                position = i;
                 return true;

            }
            else if( msg.getType()==Msg.TYPE_SENT_IMAGE){

                position = i;
                id = msg.getAccountFrom();
                seqNum = msg.getSeqNumImage();
                Log.d(TAG,"show pic TYPE_SENT_IMAGE id: "+id);
                Log.d(TAG,"show pic TYPE_SENT_IMAGE,seqNum: "+seqNum);
                bm = CacheUtils.getBitmapFromLruCache(id+"#"+seqNum);
                final int seq = seqNum;
                final String userId = id;
                if(bm==null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //   bitmap = PhotoUtils.decodeUriToBitmap(uri,mContext); //decode big image from phone
                            bitmap = BitmapFactory.decodeFile(pathUri);
                            //PhotoUtils.saveToPhotoCache(uri,bitmap);
                            if(bitmap!=null){
                                Log.d(TAG, "show pic TYPE_SENT_IMAGE get from phone success");
                                CacheUtils.AddBitmapToLruCache(userId+"#"+seq,bitmap);
                                Message message1 = new Message();
                                message1.what = SET_IMAGE_FROM_PHONE;
                                UIhandler.sendMessage(message1);
                            }
                            else{//get photo from server
                                Log.d(TAG, "show pic TYPE_SENT_IMAGE get from phone failed,get from server");

                                Message message1 = new Message();
                                //   message1.obj = message;
                                message1.what = SEND_GET_IMAGE_FROM_SERVER;
                                UIhandler.sendMessage(message1);
                                Id = CacheUtils.GetUserId();
                                message = msg;
                            }

                        }
                    });
                }else{
                    Log.d(TAG, "fling(),TYPE_SENT_IMAGE get photo from cache success");
                    pictureView.setImageBitmap(bm);
                }
                return true;
            }

        }
        return false;
    }

    public class GetBigImageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, final Intent intent) {

            int seq = intent.getIntExtra("seqNum",-1);
            Log.d("PictureViewActivity", "onReceive(),Id: "+Id);
            Log.d("PictureViewActivity", "onReceive(),seqNum: "+seq);
             Bitmap bitmap = CacheUtils.getBitmapFromLruCache(Id+"#"+seq);
            pictureView.setImageBitmap(bitmap);
        //    final Bundle extras = intent.getExtras();

         //  final ChatData.MSG msg = (ChatData.MSG)extras.getSerializable(Constants.Flags.MSG);
        //    Log.d("PictureViewActivity", "onReceive from "+msg.getFromId());
        //    Log.d("PictureViewActivity", "onReceive to "+msg.getToId());
        //    Log.d("PictureViewActivity", "onReceive time " + msg.getTime());
            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //get msg from intent is time-consuming action should do in work thread
                    Bundle extras = intent.getExtras();
                     ChatData.MSG msg = (ChatData.MSG)extras.getSerializable(Constants.Flags.MSG);
                    bitmap1 = BytesToBitmap(msg.getBigBitmap());
                    CacheUtils.AddBitmapToLruCache(msg.getFromId()+"#"+msg.getSeqNum(),bitmap1);
                    Log.d("PictureViewActivity", "onReceive ,decode from bytes ");
                    if (bitmap1 == null) {
                        Log.d("PictureViewActivity", "onReceive ,decode from bytes failed");
                    }
                    Message message = new Message();
                    message.what = SET_IMAGE_FROM_SERVER;
                    UIhandler.sendMessage(message);
                }
            }).start();
            //pictureView.setImageBitmap(StringToBitmap(msg.getBigBitmap()));
*/

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picture_view, menu);
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
    public Bitmap BytesToBitmap(byte[] bitmapArray){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        if(receiver!=null)
         unregisterReceiver(receiver);
    }
}
