package com.lynn.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.lynn.chattest2.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatsActivity extends Activity {
    ChatsListAdatpter adapter;
    ArrayList<HashMap<String,Object>> listItem;
    ListView chatsListView;
    int i  = 1;
    String Clients[] = {"","","","","","","","","","","","","","","","","","","","","","","","",""};
    String YourName;
//    FriendListReceiver receiver;
//    Button addFriends;
    HashMap<String ,Object> map;
    private Context mContext;
    private MessageBroadcastReceiver receiver;
    LinearLayout layout_chats_photo;
    List<User> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        ActivityCollector.addActivity(this);
        chatsListView = (ListView)findViewById(R.id.chats_listView);
        LinearLayout layout_title = (LinearLayout)findViewById(R.id.title_layout);
        layout_title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Lynn", "titleLayout onTouch,ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Lynn", "TitleLayout onTouch ACTION_UP");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        layout_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Lynn", "TitleLayout onClick ");
            }
        });

        mContext = this;

        LinearLayout layout_chats =(LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.chats_item,null);
        layout_chats_photo = (LinearLayout)layout_chats.findViewById(R.id.layout_chats_photo);

        int photoSize = CacheUtils.getPhotoDefaultSize();
        layout_chats_photo.setLayoutParams(new LinearLayout.LayoutParams(photoSize,photoSize));


        list = new ArrayList<User>();
        List<User> friendList = UserList.getFriendsList();
       //      List<User> friendList = DataBaseManager.readFriendsTable();
        for(int i=0;i<friendList.size();i++){
            User user = friendList.get(i);
            String lastMsg = user.getLastMsg();
            if((lastMsg!=null)&&(!lastMsg.isEmpty())){
                list.add(user);
            }
        }
        Log.d("ChatsActivity","onCreate(),list size: "+list.size());
        adapter = new ChatsListAdatpter(ChatsActivity.this,R.layout.chats_item,list);


        chatsListView.setAdapter(adapter);
        chatsListView.setOnItemClickListener(new ListViewListener());
 
        MsgUtils.SetCurrBroadCast(Constants.Actions.CHAT_LIST);
        openBroadCastReceiver();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("Lynn", "ChatsActivity,dispatchTouchEvent,ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("Lynn", "ChatsActivity,dispatchTouchEvent,ACTION_UP");
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("Lynn", "ChatsActivity,onTouchEvent,ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("Lynn", "ChatsActivity,onTouchEvent,ACTION_UP");
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }



    public void openBroadCastReceiver (){
        receiver = new MessageBroadcastReceiver();
        IntentFilter filter = new IntentFilter( );
     //   List<User> list = UserList.getFriendsList();
     //   int size = list.size();
     //   User friend = new User();


     //   filter.addAction(Constants.Actions.CHAT_LIST);
        filter.addAction(Constants.Flags.CHATTING_MSG);
         filter.addAction(Constants.Flags.ADD_FRIEND_AGREE);
        registerReceiver(receiver, filter);
        Log.d("ChatsActivity","register broadcast successed");
    }
    //receive message from server
    public class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ChatsActivity","onReceive()");

            //   String textMessage = intent.getStringExtra("TextMessage");
             Bundle extras = intent.getExtras();
            ChatData.MSG msg = (ChatData.MSG)extras.getSerializable(Constants.Flags.MSG);
            List<User> friendList = UserList.getFriendsList();
            list.clear();
            for(int i=0;i<friendList.size();i++){
                User user = friendList.get(i);
                String lastMsg = user.getLastMsg();
                if((lastMsg!=null)&&(!lastMsg.isEmpty())){
                    list.add(user);
                }
            }
            for(int i=0;i<list.size();i++){
                User user = list.get(i);
                if(user!=null){
                    Log.d("ChatsActivity","onCreate(),user id: "+user.getUserId());
                    Log.d("ChatsActivity","onCreate(),user last msg: "+user.getLastMsg());
                    Log.d("ChatsActivity","onCreate(),user last msg time: "+user.getLastMsgTimeStamp());
                //    Log.d("ChatsActivity","onCreate(),user photo len: "+user.getPhoto().length());
                }
            }
            Log.d("ChatsActivity","onReceive(),list size: "+list.size());
            adapter.notifyDataSetChanged();
            chatsListView.setAdapter(adapter);




        }
    }
    class ListViewListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            /*
            List<User> list = UserList.getFriendsList();
            int size = list.size();
            User friend = new User();

            for(int i=0;i<size;i++){
                if(friendName.equals(list.get(i).getName())){
                    friend = (User)list.get(i);
                    break;
                }
            }*/
            User friend = list.get(position);
            Intent intent = new Intent();

            intent.putExtra(Constants.Flags.CHATTING_FRIEND, friend);


            intent.setClass(ChatsActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ChatsActivity","onStart()");
    }



    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
       List<User> friendList = UserList.getFriendsList();
        for(int i=0;i<friendList.size();i++){
            User user = friendList.get(i);
            String lastMsg = user.getLastMsg();
            if((lastMsg!=null)&&(!lastMsg.isEmpty())){
                list.add(user);
            }
        }
        for(int i=0;i<list.size();i++){
            User user = list.get(i);
            if(user!=null){
                Log.d("ChatsActivity","onResume(),user id: "+user.getUserId());
                Log.d("ChatsActivity","onResume(),user last msg: "+user.getLastMsg());
                Log.d("ChatsActivity","onResume(),user unread num "+user.getUnReadMsgNum());
                Log.d("ChatsActivity","onResume(),user last msg time: "+user.getLastMsgTimeStamp());
                Log.d("ChatsActivity", "onResume(),user photo len: " + user.getPhoto().length());
            }
        }
        Log.d("ChatsActivity", "onResume(), list size: "+list.size());

        adapter.notifyDataSetChanged();

    //     friendList.setAdapter(simpleAdapter);
   //     friendList.setOnItemClickListener(new ListViewListener());
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread(new Runnable() {
            @Override
            public void run() {

           DataBaseManager.storeChattingTable(list);

            }
        }).start();
        Log.d("ChatsActivity", "onPause() ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ChatsActivity", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
      //  unregisterReceiver(receiver);
        Log.d("ChatsActivity", "onDestroy()");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("ChatsActivity", "onRestart()");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chats, menu);
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
    public class ChatsListAdatpter extends ArrayAdapter<User> {
        private int resourceId;
        private List<User> list;
        private String lastTime;
        private Context mContext;
        public ChatsListAdatpter(Context context,int textViewResurceId,List<User> objects){
            super(context,textViewResurceId,objects);
            resourceId =  textViewResurceId;
            list = objects;

            mContext = context;
        }
        public View getView(int position,View convertView,ViewGroup parent){
            User user = getItem(position);



            View view;
            ViewHolder viewHolder;
            if(convertView==null){
                view = LayoutInflater.from(getContext()).inflate(resourceId,null);
                viewHolder = new ViewHolder();
                viewHolder.layout_chats_photo = (LinearLayout)view.findViewById(R.id.layout_chats_photo);
                int photoSize = CacheUtils.getPhotoDefaultSize();
                viewHolder.layout_chats_photo.setLayoutParams(new LinearLayout.LayoutParams(photoSize,photoSize));
                viewHolder.chats_photo = (ImageView)view.findViewById(R.id.chats_photo);
                viewHolder.chats_unread_num = (TextView)view.findViewById(R.id.chat_unread_num);
                viewHolder.chats_last_msg = (TextView)view.findViewById(R.id.chats_last_message);
                viewHolder.chats_name = (TextView)view.findViewById(R.id.chats_name);
                viewHolder.chats_time = (TextView)view.findViewById(R.id.chats_time);
                Log.d("FriendsList", "get view set tag");
                view.setTag(viewHolder);
            }else {

                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
                Log.d("FriendsList", "get tag for view,viewHolder. ");
            }
            Log.d("ChatsActivity", "getView ,user id:" + user.getUserId());
            Log.d("ChatsActivity", "getView ,user photo len:" + user.getPhoto().length());
            Log.d("ChatsActivity", "getView ,user last msg:" + user.getLastMsg());
            Log.d("ChatsActivity", "getView ,user time:" + user.getLastMsgTimeStamp());
           //  viewHolder.chats_photo.setImageDrawable(user.getDrawable());
            String userId = user.getUserId();
            Bitmap bitmap = CacheUtils.getBitmapFromLruCache(userId);
            if(bitmap==null){
               bitmap = PhotoUtils.StringToBitmap(user.getPhoto());
                CacheUtils.AddBitmapToLruCache(userId, bitmap);

            }
            viewHolder.chats_photo.setImageBitmap(bitmap);
             int unReadNum = user.getUnReadMsgNum();
            if(unReadNum>0){
                viewHolder.chats_unread_num.setText(user.getUnReadMsgNum()+"");
            }else{
                viewHolder.chats_unread_num.setText("");
            }

            viewHolder.chats_name.setText(user.getName());
            viewHolder.chats_last_msg.setText(user.getLastMsg());
            viewHolder.chats_time.setText(user.getLastMsgTimeStamp());
            return view;
        }
        class ViewHolder{
            LinearLayout layout_chats_photo;
            ImageView chats_photo;
            TextView chats_unread_num;
            TextView chats_name;
            TextView chats_last_msg;
            TextView chats_time;

        }

    }



}
