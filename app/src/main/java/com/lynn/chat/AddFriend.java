package com.lynn.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lynn.chattest2.R;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;


public class AddFriend extends Activity implements HttpCallBackListener {
    private EditText addFriendusername;
    private  Context mContext;
    private TextView userId;
    private LinearLayout userInfoLayout;
    //private Te
    private TextView username;
    private TextView gender;
    private ImageView image;
    private Button addfriend;
    private String toId;
    private String friendName;
    Handler handler;          // work handler,do time-consuming actions
    Handler UIHandler;       //UI handler,update UI views
    private final static String TAG = "AddFriend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_friend);
        ActivityCollector.addActivity(this);
        addFriendusername = (EditText)findViewById(R.id.search);
        userInfoLayout = (LinearLayout)findViewById(R.id.userInfoLayout);
        userId = (TextView)findViewById(R.id.userid);
        username = (TextView)findViewById(R.id.username);
        gender = (TextView)findViewById(R.id.gender);
        image = (ImageView)findViewById(R.id.image);
        addfriend = (Button)findViewById(R.id.add_friend);


        handler = HandlerUtils.getAddfriendHandler();
        UIHandler =new Handler();
       // userInfoLayout.setVisibility(View.GONE);

        addfriend.setOnClickListener(new View.OnClickListener() {
            final String user_Id = userId.getText().toString();
            final String verifyMsg = "i am lynn";
            final String name = username.getText().toString();


            @Override
            public void onClick(View v) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("AddFriend", "send add friend msg");

                        ChatData.MSG msg =new ChatData.MSG();
                        msg.setTime(MsgUtils.GetFormatTime());
                        msg.setType(ChatData.Type.ADD_FRIEND);
                        msg.setFromId(CacheUtils.GetUserId());
                        msg.setToId(toId);

                        msg.setMsg(verifyMsg);
                        String pt = CacheUtils.getUserPhoto();
                        if(pt==null){
                            Log.d("AddFriend", "add friend,photo null ");
                        }else if(pt.isEmpty()){
                            Log.d("AddFriend", "add friend,photo empty ");
                        }
                        Log.d("AddFriend", "add friend, "+CacheUtils.GetUserId() +"'s photo len: "+pt.length());
                        msg.setPhoto(pt);

                        msg.setName(CacheUtils.GetUserId());
                        msg.setGender(CacheUtils.GetGender());
                        //  Log.d("AddFriend", "add friend,user_Id: " + user_Id);
                        Log.d("AddFriend", "add friend,verify msg: " + msg.getMsg());
                        Log.d("AddFriend", "add friend msg.getToId(): " + msg.getToId());
                        MsgUtils.SendMsg(msg);
                        //save to new friends list ,set type
                        User user = new User();
                        user.setType(Constants.UserType.USER_SEND_ADD_FRIEND);
                        user.setUserId(toId);
                        user.setName(friendName);
                        UserList.addToNewFriendsList(user);
                        Log.d("NewFriendsActivity","AddFriend,send add friend,name:"+friendName);
                        UserList.showNewfriendList();
                    }
                });
                /*
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("AddFriend", "send add friend msg");

                        ChatData.MSG msg =new ChatData.MSG();
                        msg.setTime(MsgUtils.GetFormatTime());
                        msg.setType(ChatData.Type.ADD_FRIEND);
                        msg.setFromId(CacheUtils.GetUserId());
                        msg.setToId(toId);

                        msg.setMsg(verifyMsg);
                        msg.setPhoto(" ");
                        msg.setName(CacheUtils.GetUserId());
                        msg.setGender(CacheUtils.GetGender());
                      //  Log.d("AddFriend", "add friend,user_Id: " + user_Id);
                        Log.d("AddFriend", "add friend,verify msg: " + msg.getMsg());
                        Log.d("AddFriend", "add friend msg.getToId(): " + msg.getToId());
                        MsgUtils.SendMsg(msg);
                        //save to new friends list ,set type
                        User user = new User();
                        user.setType(Constants.UserType.USER_SEND_ADD_FRIEND);
                        user.setUserId(toId);
                        user.setName(friendName);
                        UserList.addToNewFriendsList(user);
                        Log.d("NewFriendsActivity","AddFriend,send add friend,name:"+friendName);
                        UserList.showNewfriendList();

                   //   Msg.setPhoto(ViewUtils.DrawableToString(CacheUtils.GetUserPhoto()));
                   //     Msg.setName(CacheUtils.GetUserName());
                   //     Msg.setGender(CacheUtils.GetGender());




                    }
                }).start();*/
            }
        });
        addFriendusername.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // send search request to server
                    final String searchId = addFriendusername.getText().toString();
                    if (searchId == null) {
                        Toast.makeText(mContext, "empty username", Toast.LENGTH_SHORT).show();
                        //     return;
                    }
                    hideKeyBoard();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject reqJson = new JSONObject();
                            try {
                                reqJson.put(Constants.AddFriend.RequestParams.USER_ID, searchId);
                                //  String data = EncryptUtils.GetRsaEncrypt(reqJson
                                //          .toString());
                                String data = reqJson.toString();
                                HttpUtils.sendRequest(Constants.ID.SEARCH_USER, data,
                                        AddFriend.this);
                            } catch (ConnectTimeoutException e) {

                            }catch(SocketTimeoutException e){

                            }catch(Exception e){

                            }
                        }
                    });

                 //   return true;
                    /*
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject reqJson = new JSONObject();
                            try {
                                reqJson.put(Constants.AddFriend.RequestParams.USER_ID, searchId);
                              //  String data = EncryptUtils.GetRsaEncrypt(reqJson
                              //          .toString());
                                String data = reqJson.toString();
                                HttpUtils.sendRequest(Constants.ID.SEARCH_USER, data,
                                        AddFriend.this);
                            } catch (ConnectTimeoutException e) {

                            }catch(SocketTimeoutException e){

                            }catch(Exception e){

                            }
                        }
                    }).start();*/

                }
                return true;
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_friend, menu);
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

    @Override
    public void httpCallBack(int id, JSONObject resp) {
        switch(id){
            case Constants.ID.SEARCH_USER:
                Log.d("AddFriend", " httpcallback search id");
                String resCode = resp.optString(Constants.ResponseParams.RES_CODE);
                final JSONObject resp1 = resp;
                if("0".equals(resCode)){

                    UIHandler.post(new Runnable() {
                        //update UI views in UI thread
                        @Override
                        public void run() {
                            image.setImageDrawable(PhotoUtils.StringToDrawable(resp1.optString(Constants.AddFriend.ResponseParams.PHOTO)));

                            userId.setText(resp1.optString(Constants.AddFriend.ResponseParams.USER_ID));
                            username.setText(resp1.optString(Constants.AddFriend.ResponseParams.NAME));
                            gender.setText(resp1.optString(Constants.AddFriend.ResponseParams.GENDER));
                        }
                    });

                    toId = resp.optString(Constants.AddFriend.ResponseParams.USER_ID);
                    friendName =resp.optString(Constants.AddFriend.ResponseParams.NAME);
                    Log.d("AddFriend", " httpcallback search success user "+toId+"'s photo size:"+
                            resp.optString(Constants.AddFriend.ResponseParams.PHOTO).length());

              //      userInfoLayout.setVisibility(View.VISIBLE);


                }
                else{
                    //search friend failed
                }
        }

    }
}
