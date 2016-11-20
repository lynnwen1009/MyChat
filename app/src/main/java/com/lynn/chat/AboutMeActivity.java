package com.lynn.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lynn.chattest2.R;

public class AboutMeActivity extends Activity {
    ImageView photo;
    TextView userId;
    TextView name;
    TextView loginOut;
    LinearLayout profilelayout ;
    LinearLayout layout_photo;
    private Context mContext;
    private static final String TAG = "AboutMeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        ActivityCollector.addActivity(this);
        mContext = this;
        photo = (ImageView)findViewById(R.id.me_photo);
        userId = (TextView)findViewById(R.id.me_userId);
        name = (TextView)findViewById(R.id.me_name);
        userId.setText(CacheUtils.GetUserId());
        name.setText(CacheUtils.GetUserName());
        loginOut = (TextView)findViewById(R.id.logout);
        loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setMessage("Logout will not delete any data.you can still login with this account");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.d("AboutActivity", "log out now");
                        final ChatData.MSG msg =new ChatData.MSG();
                        msg.setTime(MsgUtils.GetFormatTime());
                        msg.setType(ChatData.Type.LOGOUT);
                        msg.setFromId(CacheUtils.GetUserId());

                      //  msg.setToId(toId);
                    //    msg.setMsg(verifyMsg);
                    //    String pt = CacheUtils.getUserPhoto();
                    //      msg.setPhoto(pt);
                   //     msg.setName(CacheUtils.GetUserId());
                   //     msg.setGender(CacheUtils.GetGender());

                      //send logout message to server
                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               MsgUtils.SendMsg(msg);
                           }
                       }).start();
                        //finish all the activies
                      ActivityCollector.finishAll();

                        //delete password in sharedPreferences
                        Log.d("AboutMeActivity", "delete sharedPreferences");
                        SharedPreferences.Editor editor = CacheUtils.getEditor(mContext);
                        editor.remove(Constants.AutoLogin.PWD);
                        editor.commit();

                        // go to welcome UI
                      Intent intent = new Intent(mContext,WelcomeActivity.class);
                            startActivity(intent);
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

        layout_photo = (LinearLayout)findViewById(R.id.layout_profile_photo);
   //     int  photoWidth =  CacheUtils.getPhotoDefaultSize();
        int photoWidth = MyApplication.getPhotoDefaultSize();
        layout_photo.setLayoutParams(new LinearLayout.LayoutParams(photoWidth, photoWidth));
        Bitmap bitmap = CacheUtils.getBitmapFromLruCache(CacheUtils.GetUserId());
        if(bitmap!=null){
            Log.d(TAG,"load buitmap success");
            photo.setImageBitmap(CacheUtils.getBitmapFromLruCache(CacheUtils.GetUserId()));
        }else{
         //   photo.setImageDrawable(CacheUtils.GetUserDrawable());
            String photoSmall = CacheUtils.getUserPhoto();
            if(photoSmall!=""){
                Bitmap bitmap1= PhotoUtils.StringToBitmap(photoSmall);
                CacheUtils.AddBitmapToLruCache(CacheUtils.GetUserId(),bitmap1);
                photo.setImageBitmap(bitmap1);
            }else{
                photo.setImageResource(R.drawable.default_photo);
            }
        }

      //
        profilelayout = (LinearLayout)findViewById(R.id.profile_layout);
        profilelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutMeActivity.this,MeInfoActivity.class);
                startActivity(intent);
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
        getMenuInflater().inflate(R.menu.menu_about_me, menu);
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
}
