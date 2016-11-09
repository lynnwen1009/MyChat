package com.lynn.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Base64;
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
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.lynn.chattest2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FriendsList extends Activity {
    SimpleAdapter simpleAdapter;
    ArrayList<HashMap<String,Object>> listItem;
    ListView friendListView;
    int i  = 1;
    String Clients[] = {"","","","","","","","","","","","","","","","","","","","","","","","",""};
    String YourName;
    FriendListReceiver receiver;
    Button addFriends;
    HashMap<String ,Object> map;
    private Context mContext;
    private FriendsListAdatpter adapter;
    List<User> friendsList;
    LinearLayout firstLayout;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ActivityCollector.addActivity(this);
        addFriends = (Button)findViewById(R.id.add_friends);
     //   firstLayout = (LinearLayout)findViewById(R.id.layout_new_friends);


        friendListView = (ListView)findViewById(R.id.friends_listView);
         YourName = getIntent().getStringExtra("ClientName");
         friendsList = UserList.getFriendsList();

        handler = HandlerUtils.getHandler("FriendsList");

        Log.d("TabHostActivity", "friends list size--2: " + friendsList.size());
   //
        mContext = this;
        /*
          listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<list.size();i++){

            User user = (User)list.get(i);

            map = new HashMap<String,Object>();
            map.put("image",user.getDrawable());
            map.put("userName", user.getUserId());
            listItem.add(map);
        }
            simpleAdapter = new SimpleAdapter(mContext,listItem,R.layout.friend_item,
                    new String[]{"image","userName"},new int[]{R.id.user_image,R.id.friend_name});
           */
           adapter = new FriendsListAdatpter(FriendsList.this, R.layout.friend_item, friendsList);
             friendListView.setAdapter(adapter);
             friendListView.setOnItemClickListener(new ListViewListener());


      //   map = new HashMap<String,Object>();

      //  map.put("name","ross: ");
      //  listItem.add(map);
     /*   simpleAdapter = new SimpleAdapter(this,listItem,R.layout.friend_item,
                new String[]{"image","userName"},new int[]{R.id.user_image,R.id.friend_name});

        friendList.setAdapter(simpleAdapter);
        friendList.setOnItemClickListener(new ListViewListener());*/

        OpenBroadcastToUpdateFriendList();
       // Clients[0]="ross";

        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsList.this,NewFriendsActivity.class);
                startActivity(intent);
            }
        });


    }
    class ListViewListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            User friend  = (User)(UserList.getFriendsList().get(position));

            Intent intent = new Intent();
            intent.putExtra(Constants.Flags.CHATTING_FRIEND, friend);

            intent.setClass(FriendsList.this, MainActivity.class);
            startActivity(intent);
        }
    }
    public void OpenBroadcastToUpdateFriendList(){
        receiver = new FriendListReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Actions.CONTACTS_LIST);
        registerReceiver(receiver, filter);
        Log.d("FriendList","broadcast register succeed");
    }

    public class FriendListReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("FriendsList","onReceive  ");
         /*   if(intent==null)
                Log.d("FriendsList","onReceive intent == null");
            Bundle extras = intent.getExtras();
            if(intent==null)
                Log.d("FriendsList","onReceive extras == null");
            ChatData.MSG msg = (ChatData.MSG)extras.getSerializable(Constants.Flags.NEW_FRIEND_LIST);
        //    ChatData.MSG msg = (ChatData.MSG)(intent.getSerializableExtra("Constants.Flags.NEW_FRIEND_LIST"));
            if(msg==null)
                Log.d("FriendsList","msg = null");
      //      String image = msg.getPhoto();
            String userId = msg.getName();*/
       //     listItem = UserList.getFriendsList();
            List list = UserList.getFriendsList();
            int size = list.size();
            User user = (User)list.get(size-1);
/*
            map = new HashMap<String,Object>();
            map.put("image", user.getDrawable());
            map.put("userName", user.getUserId());
            listItem.add(map);
            simpleAdapter = new SimpleAdapter(mContext,listItem,R.layout.friend_item,
                    new String[]{"image","userName"},new int[]{R.id.user_image,R.id.friend_name});*/

            adapter.notifyDataSetChanged();
            friendListView.setAdapter(adapter);
            friendListView.setOnItemClickListener(new ListViewListener());

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        Log.d("FriendsList","onResume ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
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
    public class FriendsListAdatpter extends ArrayAdapter<User> {
        private int resourceId;
        private List<User> list;
        private String lastTime;
        private Context mContext;
        public FriendsListAdatpter(Context context,int textViewResurceId,List<User> objects){
            super(context,textViewResurceId,objects);
            resourceId =  textViewResurceId;
            list = objects;

            mContext = context;
        }
        public View getView(int position,View convertView,ViewGroup parent){
          final  User user = getItem(position);



            View view;
            ViewHolder viewHolder;
            if(convertView==null){
                view = LayoutInflater.from(getContext()).inflate(resourceId,null);
                viewHolder = new ViewHolder();
                viewHolder.layout_friends_List_photo = (LinearLayout)view.findViewById(R.id.layout_friendsList_photo);
                viewHolder.friend_photo = (ImageView)view.findViewById(R.id.friend_photo);
                viewHolder.friend_name = (TextView)view.findViewById(R.id.friend_name);
                Log.d("FriendsList", "get view set tag");
                view.setTag(viewHolder);
            }else {

                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
                Log.d("FriendsList", "get tag for view,viewHolder. ");
            }
            int photoSize = CacheUtils.getPhotoDefaultSize();
            viewHolder.layout_friends_List_photo.setLayoutParams(new LinearLayout.LayoutParams(photoSize, photoSize));

            Bitmap bitmap = CacheUtils.getBitmapFromLruCache(user.getUserId());
            if(bitmap==null){
            //    handler.post(new Runnable() {
               //     @Override
              //      public void run() {
                         bitmap = PhotoUtils.StringToBitmap(user.getPhoto());
                        CacheUtils.AddBitmapToLruCache(user.getUserId(), bitmap);
             //       }
             //   });


            }else {
                viewHolder.friend_photo.setImageBitmap(bitmap);
            }
           // viewHolder.friend_photo.setImageDrawable(user.getDrawable());
            viewHolder.friend_name.setText(user.getName());
            return view;
        }
        class ViewHolder{
            LinearLayout layout_friends_List_photo;
            ImageView friend_photo;
            TextView friend_name;

        }

    }

}
