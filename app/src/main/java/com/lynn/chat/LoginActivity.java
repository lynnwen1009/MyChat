package com.lynn.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LoginActivity extends Activity implements HttpCallBackListener {
    private Button loginButton;
    private Button registerButton;
    private EditText login_id;
    private EditText login_pwd;
    ;
    private TextView more;

//    private MsgAdatpter adapter;
    private List<Msg> msgList = new ArrayList<Msg>();
    String chat_content;
    private Context mContext;

    public static final int PORT  = 2056;
    Socket socket;
    Thread thread;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
   // boolean flag = false;
    String user;
    String passwd;
  //  String chat_in,accountFrom;
    PrintWriter output ;
    BufferedReader reader ;

    String gender;

    LinearLayout layout_exist_account;
    LinearLayout layout_login_id;
    ImageView exist_account_photo;
    TextView exist_account_Id;

    String exist_account_id;


  public final static String TAG = "LoginActivity";

    Handler UIHandler;//UI handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCollector.addActivity(this);
        mContext = this;

        UIHandler = new Handler();

        initViews();

    }
  private void initViews(){
      loginButton = (Button)findViewById(R.id.login_button);
      //   registerButton = (Button)findViewById(R.id.register_button);
      login_id = (EditText)findViewById(R.id.login_id);
      login_pwd = (EditText)findViewById(R.id.login_password);
      more = (TextView)findViewById(R.id.more);
       layout_login_id = (LinearLayout)findViewById(R.id.layout_login_id);
      layout_exist_account = (LinearLayout)findViewById(R.id.layout_exist_account);
      exist_account_photo = (ImageView)findViewById(R.id.exist_account_photo);
      exist_account_Id = (TextView)findViewById(R.id.exist_account_id);



      more.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              final String items[] = {"switch accounts", "register"};
              AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
              builder.setItems(items, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      switch (which) {
                          case 0:
                              Log.d("LoginActivity", "more:switch accounts");
                              SharedPreferences.Editor editor = CacheUtils.getEditor(mContext);
                              editor.remove(Constants.AutoLogin.ID);
                              editor.remove(Constants.AutoLogin.PWD);
                              editor.commit();
                              layout_exist_account.setVisibility(View.GONE);
                              exist_account_Id.setVisibility(View.GONE);
                               layout_login_id.setVisibility(View.VISIBLE);
                              exist_account_id = "";

                              login_id.setText("");
                              login_pwd.setText("");
                              break;
                          case 1:
                              Log.d("LoginActivity", "more:register");

                              Intent intent = new Intent(mContext, RegisterActivity.class);

                              startActivity(intent);
                              break;
                      }
                  }
              });
              builder.create().show();
          }
      });

      loginButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              hideKeyBoard();
              new Thread(new Runnable() {
                  @Override
                  public void run() {
                      String id;
                      if (exist_account_id==null||exist_account_id.isEmpty()) {
                          id = login_id.getText().toString();
                      } else {
                          id = exist_account_id;
                      }
                      String pwd = login_pwd.getText().toString();
                      Log.d(TAG, "login now, id: " + id);
                      Log.d(TAG, "login now, pwd: " + pwd);

                      JSONObject reqJson = new JSONObject();
                      try {
                          reqJson.put(Constants.Login.RequestParams.USER_ID, id);
                          reqJson.put(Constants.Login.RequestParams.PASSWORD, pwd);
                          String data = reqJson.toString();
                          Log.d("LoginActivity", "login data:" + data);
                          HttpUtils.sendRequest(Constants.ID.LOGIN, data, LoginActivity.this);


                                           } catch (ConnectTimeoutException e) {

                      } catch (SocketTimeoutException e) {

                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                  }

              }).start();


          }
      });
  }
    @Override
   protected void onResume(){
        super.onResume();
        SharedPreferences pref = CacheUtils.getAccountSharedPreferences(mContext);
        String id = pref.getString(Constants.AutoLogin.ID, "");
        String pwd = pref.getString(Constants.AutoLogin.PWD,"");
        Log.d(TAG, "id: " + id);
        Log.d(TAG,"pwd: "+pwd);
        if(!id.equals("")){
            //have e
            layout_exist_account.setVisibility(View.VISIBLE);

            exist_account_Id.setVisibility(View.VISIBLE);

            int photoWidth = CacheUtils.getPhotoDefaultSize();
            layout_exist_account.setLayoutParams(new LinearLayout.LayoutParams(photoWidth, photoWidth));

            Bitmap bitmap = CacheUtils.getBitmapFromLruCache(id);
            if(bitmap!=null){
                exist_account_photo.setImageBitmap(bitmap);
            }else {
                String photo = pref.getString(Constants.AutoLogin.PHOTO, "");
                if(photo!=""){
                    Bitmap bitmap1= PhotoUtils.StringToBitmap(photo);
                    CacheUtils.AddBitmapToLruCache(id,bitmap1);
                    exist_account_photo.setImageBitmap(bitmap1);
                }else{
                    exist_account_photo.setImageResource(R.drawable.default_photo);
                }
              //  exist_account_photo.setImageDrawable(PhotoUtils.StringToDrawable(pref.getString(Constants.AutoLogin.PHOTO, "")));
            }

            exist_account_id =pref.getString(Constants.AutoLogin.ID,"");
            exist_account_Id.setText( exist_account_id);

            //login id layout should be gone

            layout_login_id.setVisibility(View.GONE);


            //     username.setText(id);
        }
        login_pwd.setText("");
        if(!pwd.equals("")){
        //    login_pwd.setText(pwd);
        }
        Log.d(TAG, "onResume()");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG,"onPause()");
    }
    protected void onStart(){
        super.onStart();
        Log.d(TAG,"onStart()");
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG,"onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
     //   unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    @Override
    public void httpCallBack(int id,JSONObject resp){
        Log.d("LoginActivity","httpCallback");
        switch(id){

            case Constants.ID.LOGIN:

            //    Log.d(TAG, "login succeed");
                String resCode = resp.optString(Constants.ResponseParams.RES_CODE);
                if (Constants.ResponseInfo.CODE_SUCCESS.equals(resCode)) {//login succeed ,go to tab host activity
                    Log.d(TAG, "login succeed");
                    User user = new User();
                    user.setUserId(resp.optString(Constants.Login.ResponseParams.USER_ID));
                    user.setName(resp.optString(Constants.Login.ResponseParams.NAME));
                    user.setGender(resp.optString(Constants.Login.ResponseParams.GENDER));
                    user.setPhoto(resp.optString(Constants.Login.ResponseParams.PHOTO));
         //           Log.d(TAG, "login succeed--2");
         //           List<User> friendsList = (List)(resp.opt(Constants.Login.ResponseParams.FRIENDSLIST));

         //           UserList.setFriendsList(friendsList);
                    SharedPreferences.Editor editor= CacheUtils.getEditor(mContext);
                    editor.putString(Constants.AutoLogin.ID,user.getUserId());
                    editor.putString(Constants.AutoLogin.PWD,login_pwd.getText().toString());
                    editor.putString(Constants.AutoLogin.GENDER,user.getGender());
                    editor.putString(Constants.AutoLogin.PHOTO,user.getPhoto());
                    editor.putString(Constants.AutoLogin.NAME,user.getName());
                    editor.commit();

                    Log.d(TAG, "login succeed--3");
                    CacheUtils.SetUserCache(user);
                    DataBaseManager.init(mContext, CacheUtils.GetUserId());
                    UserList.UpdateFriendList(resp);
                    UserList.initChattingList();
                    Intent intent = new Intent(mContext, TabHostActivity.class);


                    ((Activity) mContext).finish();
                    startActivity(intent);
                }else
                {
                    final String errorMessage;
                    final Intent intent;

                    if(resCode.equals(Constants.Login.ErrorInfo.CODE_PSD_ERROR)){
                        //login failed
                        Log.d(TAG, "login failed,password incorrect ");
                        errorMessage = "incorrect password";
                        intent = null;

                    }else if(resCode.equals(Constants.Login.ErrorInfo.CODE_ID_UNRESGISTER)){
                        Log.d(TAG, "login failed,unregister yet ");
                        intent = new Intent(mContext,RegisterActivity.class);
                     //   startActivity(intent);
                        errorMessage = "unregister yet,please register your account";
                    }else{
                        errorMessage = "unknown error";
                        intent = null;

                    }

                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Login failed");
                            builder.setMessage(errorMessage);
                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (intent != null) {
                                        startActivity(intent);
                                    }
                                }
                            });
                            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                    });

                }



        }
    }
    private void hideKeyBoard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
