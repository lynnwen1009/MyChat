package com.lynn.chat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lynn.chattest2.R;

import java.util.ArrayList;
import java.util.List;

public class NewFriendsActivity extends Activity {
    ListView addFriends_listView;

   List<User> newfriendslist ;
    UpdateNewFriendsBroadcastReceiver receiver;
    NewfriendsAdatpter adapter;
    private Context mContext;
    private Handler handler; //work handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends);
        ActivityCollector.addActivity(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Actions.NEW_FRIEND_LIST);
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(2);
        mContext = this;
        handler = HandlerUtils.getAddfriendHandler();
     //   filter.addAction();
        Log.d("NewFriendsActivity", "register broadcast Constants.Actions.NEW_FRIEND_LIST");
        receiver = new UpdateNewFriendsBroadcastReceiver();
        registerReceiver(receiver, filter);

         newfriendslist = new ArrayList<User>();
        newfriendslist.addAll(UserList.getNewFriendsList());
        addFriends_listView =  (ListView) findViewById(R.id.add_friends_listView);
        adapter = new NewfriendsAdatpter(NewFriendsActivity.this,R.layout.new_friend_item,newfriendslist);
        Log.d("NewFriendsActivity", "onCreate NEW_FRIEND_LIST broadcast,newfriendslist size: " + newfriendslist.size());
        addFriends_listView.setAdapter(adapter);
        /*addFriends_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             if( ((Button)view.findViewById(R.id.accept_button)).getTag().equals("change")){

             }

            }
        });

*/
    }
    class UpdateNewFriendsBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
          //  ChatDgetNewFriendsListata.MSG msg = (ChatData.MSG)(intent.getSerializableExtra("Constants.Flags.NEW_FRIEND_LIST"));
            Bundle extras = intent.getExtras();

            ChatData.MSG msg = (ChatData.MSG)extras.getSerializable(Constants.Flags.NEW_FRIEND_LIST);
            //Bundle bundle = intent.getExtras();
            if(intent==null)
                Log.d("NewFriendsActivity","intent==null");
            User user = new User();
            user.setName(msg.getName());
            user.setUserId(msg.getFromId());
            user.setPhoto(msg.getPhoto());
            Log.d("NewFriendsActivity", "NewFriendsActivity onReceive,show new friends list-------------1");
            UserList.showNewfriendList();
            if(msg.getType()==(ChatData.Type.ADD_FRIEND)){
                Log.d("NewFriendsActivity", "NewFriendsActivity onReceive,receive ADD_FRIEND");
                user.setType(Constants.UserType.USER_RECEIVE_ADD_FRIEND);
                UserList.addToNewFriendsList(user);
            }else if((msg.getType()==(ChatData.Type.ADD_AGREE))){
                Log.d("NewFriendsActivity", "NewFriendsActivity onReceive,receive ADD_agree");
                user.setType(Constants.UserType.USER_RECEIVE_ADD_AGREE);
                int i = UserList.findUserInNewFriendsList(msg.getFromId());
                if(i!=-1){
                    UserList.modifyUserInNewFriendsList(i,user);
                }
           //     UserList.addToFriendList(user);
            }
            else {

            }
            Log.d("NewFriendsActivity", "NewFriendsActivity onReceive,show new friends list-------------2");
            UserList.showNewfriendList();
         //   UserList.addToNewFriendsList(user);
                //            newfriendslist.add(msg);
            Log.d("NewFriendsActivity", "to id:"+msg.getToId());
            newfriendslist = UserList.getNewFriendsList();

            adapter = new NewfriendsAdatpter(NewFriendsActivity.this,R.layout.new_friend_item,newfriendslist);
            Log.d("NewFriendsActivity", "received NEW_FRIEND_LIST broadcast,newfriendslist size: "+newfriendslist.size());
            addFriends_listView.setAdapter(adapter);
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receiver);
        ActivityCollector.removeActivity(this);
        Log.d("NewFriendsActivity", "onDestroy()");

    }
    @Override
    protected void onResume(){
        super.onResume();

        Log.d("NewFriendsActivity", "onResume()");

    }
    @Override
    protected void onPause(){
        super.onPause();

        Log.d("NewFriendsActivity", "onPause()");

    }
    @Override
    protected void onRestart(){
        super.onRestart();

        Log.d("NewFriendsActivity", "onRestart()");

    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("NewFriendsActivity", "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("NewFriendsActivity", "onStop()");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_friends, menu);
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




    /**
     * Created by Administrator on 8/25/2016.
     */
    public class NewfriendsAdatpter extends ArrayAdapter<User> {
        private int resourceId;
        public NewfriendsAdatpter(Context context,int textViewResurceId,List<User> objects){
            super(context,textViewResurceId,objects);
            resourceId =  textViewResurceId;
        }

        public View getView(int position,View convertView,ViewGroup parent){
            //    final ChatData.MSG. msg = getItem(position);
      //      newfriendslist = UserList.getNewFriendsList();
            final User user = (User)(newfriendslist.get(position));
            if(user==null){
                Log.d("NewFriendsActivity","msg==null");
                Log.d("NewFriendsActivity","newfriendslist size: "+newfriendslist.size());
                Log.d("NewFriendsActivity", "position: "+position);
            }
            View view;
            ViewHolder viewHolder;
            if(convertView==null){
                view = LayoutInflater.from(getContext()).inflate(R.layout.new_friend_item,null);
                viewHolder = new ViewHolder();

                viewHolder.image = (ImageView)view.findViewById(R.id.user_image);
                viewHolder.UserName = (TextView)view.findViewById(R.id.friend_name);
         //       viewHolder.added = (TextView)view.findViewById(R.id.added);
          //      viewHolder.acceptButton = (Button)view.findViewById(R.id.accept_button);
                viewHolder.layoutType = (LinearLayout)view.findViewById(R.id.layout_new_friend_type);
          //      if(viewHolder.acceptButton==null){
                    Log.d("NewFriendsActivity","accept button null--1");
        //        }
                view.setTag(viewHolder);
            }else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
         //       if(viewHolder.acceptButton==null){
                    Log.d("NewFriendsActivity","accept button null--3");
        //        }
            }
            Log.d("NewFriendsActivity","user:"+user.getUserId()+" type: "+user.getType());
            Bitmap bitmap = CacheUtils.getBitmapFromLruCache(user.getUserId());
            if(bitmap==null){
                bitmap = PhotoUtils.StringToBitmap(user.getPhoto());
                CacheUtils.AddBitmapToLruCache(user.getUserId(), bitmap);

            }
            viewHolder.image.setImageBitmap(bitmap);

          //  viewHolder.image.setImageDrawable(user.getDrawable());
            viewHolder.UserName.setText(user.getName());
               if(user.getType()==Constants.UserType.USER_SEND_ADD_FRIEND) {

                   TextView text = new TextView(mContext);
                    text.setText("wait for verify");
                   viewHolder.layoutType.removeAllViews();
                   viewHolder.layoutType.addView(text);

            //       viewHolder.acceptButton.setVisibility(View.VISIBLE);
            //       viewHolder.acceptButton.setText("wait for verify");
               }else if(user.getType()==Constants.UserType.USER_RECEIVE_ADD_FRIEND){




                    Log.d("NewFriendsActivity","accept button null--2");

                   Button button = new Button(mContext);
                   button.setText("accept");
                   viewHolder.layoutType.removeAllViews();
                   viewHolder.layoutType.addView(button);
                   final ViewHolder holder  = viewHolder;
                   button.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           //send add_agree  msg to server
                          final ChatData.MSG agree_msg = new ChatData.MSG();


                           agree_msg.setType(ChatData.Type.ADD_AGREE);
                           agree_msg.setFromId(CacheUtils.GetUserId());
                           agree_msg.setName(CacheUtils.GetUserName());
                           //???   msg.setPhoto(ViewUtils.DrawableToString(CacheUtils.GetUserPhoto()));
                           Log.d("NewFriendsActivity", "send add friend to: " + user.getUserId());
                           //   Log.d("NewFriendsActivity", "msg.getToId: " + msg.getToId());
                           //    Log.d("NewFriendsActivity","msg.getFromId: "+msg.getFromId());
                           agree_msg.setToId(user.getUserId());
                           Log.d("NewFriendsActivity", "send add friend agree from: " + agree_msg.getFromId() + "to: " + agree_msg.getToId());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                MsgUtils.SendMsg(agree_msg);
                            }
                        });


                           TextView text = new TextView(mContext);
                           text.setText("accepted");
                           holder.layoutType.removeAllViews();
                           holder.layoutType.addView(text);
                           //    holder.layoutType.setText("accepted");

                           //modify newfriendlist
                           int i = UserList.findUserInNewFriendsList(user.getUserId());
                           Log.d("NewFriendsActivity", "NewFriendsActivity adapter,show new friends list-------------1");
                           UserList.showNewfriendList();

                           User modify_user = new User();
                           modify_user.setUserId(user.getUserId());
                           modify_user.setGender(user.getGender());
                           modify_user.setName(user.getName());
                           modify_user.setPhoto(user.getPhoto());
                           Bitmap bitmap = PhotoUtils.StringToBitmap(user.getPhoto());
                           CacheUtils.AddBitmapToLruCache(user.getUserId(), bitmap);

                           modify_user.setType(Constants.UserType.USER_SEND_ADD_AGREE);
                           UserList.modifyUserInNewFriendsList(i, modify_user);
                           UserList.addToFriendList(user);
                           DataBaseManager.updateFriendsTable(user);
                           Log.d("NewFriendsActivity", "NewFriendsActivity adapter,show new friends list-------------2");
                           UserList.showNewfriendList();

                           Intent intent = new Intent(Constants.Actions.CONTACTS_LIST);
                           intent.putExtra(Constants.Flags.NEW_FRIEND_LIST, modify_user);

                           sendBroadcast(intent);
                    }
                });
//                viewHolder.added.setVisibility(View.GONE);
            }else if(user.getType()==Constants.UserType.USER_SEND_ADD_AGREE) {

                   TextView text = new TextView(mContext);
                   text.setText("agreed");
                   viewHolder.layoutType.removeAllViews();
                   viewHolder.layoutType.addView(text);


            }else if(user.getType()==Constants.UserType.USER_RECEIVE_ADD_AGREE) {


                    TextView text = new TextView(mContext);
                   text.setText("added");
                   viewHolder.layoutType.removeAllViews();
                   viewHolder.layoutType.addView(text);

            }
            return view;
        }
        class ViewHolder{
            ImageView image;
            TextView UserName;
            LinearLayout layoutType;

        }
    }

}
