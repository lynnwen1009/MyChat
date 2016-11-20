package com.lynn.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lynn.chattest2.R;

public class MeInfoActivity extends Activity {
    ImageView profilePhoto;
    TextView name;
    TextView userId;
    TextView gender;
    LinearLayout layout_me_photo;
    private Context mContext;
    private static final String TAG = "MeInfoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_info);
        ActivityCollector.addActivity(this);
        mContext = this;
        initViews();

    }
   private void initViews(){
       name = (TextView)findViewById(R.id.user_name_content);
       userId = (TextView)findViewById(R.id.user_ID_content);
       gender = (TextView)findViewById(R.id.gender_content);
       layout_me_photo = (LinearLayout)findViewById(R.id.layout_me_photo);

   //    int photoSize = CacheUtils.getPhotoDefaultSize();
       int photoSize = MyApplication.getPhotoDefaultSize();

       layout_me_photo.setLayoutParams(new LinearLayout.LayoutParams(photoSize,photoSize));

       layout_me_photo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });

       profilePhoto = (ImageView)findViewById(R.id.profile_photo_content);
       Bitmap bitmap = CacheUtils.getBitmapFromLruCache(CacheUtils.GetUserId());
       if(bitmap!=null){
           Log.d(TAG, "cache bitmap != null");
           profilePhoto.setImageBitmap(bitmap);
       }else{
           String photoSmall = CacheUtils.getUserPhoto();
           if(photoSmall!=""){
               Bitmap bitmap1= PhotoUtils.StringToBitmap(photoSmall);
               CacheUtils.AddBitmapToLruCache(CacheUtils.GetUserId(),bitmap1);
               profilePhoto.setImageBitmap(bitmap1);
           }else{
               profilePhoto.setImageResource(R.drawable.default_photo);
           }
          // profilePhoto.setImageDrawable(CacheUtils.GetUserDrawable());
       }
   //    profilePhoto.setImageDrawable(CacheUtils.GetUserDrawable());
       name.setText(CacheUtils.GetUserName());
       userId.setText(CacheUtils.GetUserId());
       gender.setText(CacheUtils.GetGender());
   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_me_info, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
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
