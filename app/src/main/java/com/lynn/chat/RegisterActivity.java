package com.lynn.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lynn.chattest2.R;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class RegisterActivity extends Activity implements HttpCallBackListener {
    private EditText registerName;
    private ImageView registerPhoto;
    private RadioGroup genderGroup;
    private RadioButton radioMale;
    private  RadioButton radioFemale;
    private EditText registerId;
    private EditText registerPwd;
    LinearLayout layout_register_photo;
    private Button registerButton;
    private Context mContext;
    String gender;
    int photoWidth;
    String photoBitmapString;
    private final static int SET_PHOTO = 1;
    private final static String TAG = "RegisterActivity";


    private String register_Name;
    private String register_Id;
    private String register_Pwd;
    private String register_Photo;
    private String register_gender;
    private Bitmap register_photo_bitmap;

    private Handler UIHandler;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActivityCollector.addActivity(this);
        mContext = this;

        UIHandler = new Handler();


        initViews();



    }
    private void initViews(){

        layout_register_photo = (LinearLayout)findViewById(R.id.layout_register_photo);
        registerName = (EditText)findViewById(R.id.register_user_name);
        registerPhoto = (ImageView)findViewById(R.id.register_photo);
        registerPhoto.setImageResource(R.drawable.default_photo);
        genderGroup = (RadioGroup)findViewById(R.id.gender_radioGroup);
        radioMale = (RadioButton)findViewById(R.id.radioMale);
        radioFemale = (RadioButton)findViewById(R.id.radiofemale);
        registerId = (EditText)findViewById(R.id.register_id);
        registerPwd = (EditText)findViewById(R.id.register_password);
        registerButton = (Button)findViewById(R.id.register_button);

        //set photo size according to the phone size,1/5  window size
    //    photoWidth = CacheUtils.getPhotoDefaultSize();
        photoWidth = MyApplication.getPhotoDefaultSize();
        layout_register_photo.setLayoutParams(new LinearLayout.LayoutParams(photoWidth ,photoWidth));
        Log.d(TAG, "initViews(),widthWindow:" + photoWidth);

        final String name = registerName.getText().toString();
        final String Id = registerId.getText().toString();
        final String pwd = registerPwd.getText().toString();
        Log.d("RegisterActivity", "send register request,gender: " + gender);
        Log.d("RegisterActivity", "send register request,Id: " + Id);
        Log.d("RegisterActivity", "send register request,name: " + name);
        if(genderGroup.getCheckedRadioButtonId()==R.id.radiofemale){
            gender ="female";
        }
        else
        {
            gender = "male";
        }

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                if (radioButtonId == R.id.radiofemale) {
                    gender = "female";
                } else {
                    gender = "male";
                  }

        }
    });
        registerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //solution 1
                File outputImage = new File(Environment.getExternalStorageDirectory(),"user_photo.jpg");
                try{
                    if(outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch(IOException e){
                    e.printStackTrace();

                }
                imageUri = Uri.fromFile(outputImage);
                Intent intent1 = new Intent("android.intent.action.GET_CONTENT");
                intent1.setType("image/*");

                 intent1.putExtra("crop", true);
                 intent1.putExtra("scale", true);
              //  intent1.putExtra("aspectX", 2);
              //  intent1.putExtra("aspectY",1);
          //      intent1.putExtra("outputX",100);
          //      intent1.putExtra("outputY",100);
                intent1.putExtra("return-data",true);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                 intent1.putExtra("noFaceDetection", true);
                startActivityForResult(intent1, SET_PHOTO);
HashMap map;

                /*
                //solution 2
                Intent intent1 = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent1,SET_PHOTO);
                */

            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = registerName.getText().toString();
                final String Id = registerId.getText().toString();
                final String pwd = registerPwd.getText().toString();



                //final String photo = bitmapToString(((BitmapDrawable) registerPhoto.getDrawable()).getBitmap(), 100);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) registerPhoto.getDrawable();

                final String photo = PhotoUtils.drawableToString(bitmapDrawable);
                userPhotoBitmap = bitmapDrawable.getBitmap();

                if(userPhotoBitmap==null){
                    Log.d(TAG,"default photo bitmap null");
                }
                if(photo==null){
                    Log.d("RegisterActivity", "send register request,photo is empty");
                }
                Log.d("RegisterActivity", "send register request,gener: " + gender);
                Log.d("RegisterActivity", "send register request,Id: " + Id);
                Log.d("RegisterActivity", "send register request,name: " + name);

                if (name.isEmpty() || Id.isEmpty() || pwd.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("error");
                    builder.setMessage("register information not complete");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return;
                }

                register_Name = name;
                register_Id  =Id;
                register_Pwd  =pwd;
                register_gender = gender;
                register_Photo = photo;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject reqJson = new JSONObject();
                        try {
                            //user id
                            reqJson.put(Constants.Register.RequestParams.USER_ID, Id);
                            //name
                            reqJson.put(Constants.Register.RequestParams.NAME, name);
                            //gender
                            reqJson.put(Constants.Register.RequestParams.GENDER, gender);
                            reqJson.put(Constants.Register.RequestParams.PASSWORD, pwd);
                            reqJson.put(Constants.Register.RequestParams.PHOTO,photo);
                            if(bigPhoto!=null)
                                reqJson.put(Constants.Register.RequestParams.BIG_PHOTO,bigPhoto);
                         /*   String data = EncryptUtils.GetRsaEncrypt(reqJson
                                    .toString());*/
                            String data = reqJson.toString();


                            HttpUtils.sendRequest(Constants.ID.REGISTER, data, RegisterActivity.this);


                        } catch (ConnectTimeoutException e) {


                        } catch (SocketTimeoutException e) {

                        } catch (Exception e) {

                        }
                    }
                }).start();

            }
        });
    }
        Bitmap userPhotoBitmap;
    Bitmap smallBitmap;
    Bitmap originalBitmap;
    String bigPhoto;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode==SET_PHOTO){
           //solution 1
            Bitmap bm = data.getParcelableExtra("return-data");
           if(bm==null){
               Log.d(TAG,"return data==null");
           }

         //   final Intent data1 = data;



           Log.d(TAG, "selected photo Uri: " + imageUri);


           //     String path = PhotoUtils.uriToPath(imageUri,mContext);
         //
       //    final Bitmap bigBitmap =data1.getParcelableExtra("data");
         //   userPhotoBitmap = bigBitmap;
         //   bigBitmap = PhotoUtils.decodeUriToBitmap(imageUri,mContext);
           new Thread(new Runnable() {
               @Override
               public void run() {
                   try{


                               //  userPhoto = data1.data1.getParcelableExtra("data");

                      // BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    //   String path = PhotoUtils.uriToPath(imageUri,mContext);
                    //   Log.d(TAG,"selected photo file path: "+path);
                       //        BitmapFactory.Options options = new  BitmapFactory.Options();
                       //         options.inSampleSize = 4;
                       //        options.inJustDecodeBounds = false;
                       //        smallBitmap = BitmapFactory.decodeFile(path, options);\
                       final InputStream stream = getContentResolver().openInputStream(imageUri);
                       UIHandler.post(new Runnable() {
                           @Override
                           public void run() {
                               originalBitmap = BitmapFactory.decodeStream(stream);
                               bigPhoto = PhotoUtils.bitmapToString(originalBitmap,100);
                               BitmapFactory.Options options = new BitmapFactory.Options();
                               options.inSampleSize = 2;
                               options.inJustDecodeBounds = false;
                               smallBitmap = BitmapFactory.decodeStream(stream,null,options);
                          //     smallBitmap = BitmapFactory.decodeStream(stream,null,options);
                               Log.d(TAG,"user photo original height: "+originalBitmap.getHeight());
                               Log.d(TAG, "user photo original width: " + originalBitmap.getWidth());
                               Log.d(TAG,"user photo small height: "+smallBitmap.getHeight());
                               Log.d(TAG, "user photo small width: " + smallBitmap.getWidth());
                               // photoBitmapString = bitmapToString(bitmap,100);
                               registerPhoto.setImageBitmap(smallBitmap);
                             //  register_Photo = PhotoUtils.bitmapToString(originalBitmap,100);

                          //    Bitmap bitmap2 =  ((BitmapDrawable)registerPhoto.getDrawable()).getBitmap();
                          //     Log.d(TAG, "user photo view height: " + bitmap2.getHeight());
                          //     Log.d(TAG, "user photo  view width: " + bitmap2.getWidth());
                           }
                       });
                   }catch(Exception e){
                       e.printStackTrace();
                       return;
                   }
               }
           }).start();



       //    bigBitmap = BitmapFactory.decodeFile(path);
       //    int smallBitmapWidth = bigBitmap.getWidth()/4;
       //    int smallBitmapHeight = bigBitmap.getHeight()/4;
       //    Log.d(TAG,"smallBitmapWidth should be: "+smallBitmapWidth);
       //    Log.d(TAG,"smallBitmapHeight should be: "+smallBitmapHeight);
        //   smallBitmap = Bitmap.createScaledBitmap(bigBitmap,smallBitmapWidth,smallBitmapHeight,false);

        //   Log.d(TAG,"smallBitmapHeight result be: "+smallBitmap.getHeight());
          /*
           //solution 2
           Uri selectedImage = data.getData();

           String picturePath = PhotoUtils.uriToPath(selectedImage,mContext);


            Log.d("RegisterActivity", "choose picture path: " + picturePath);

           BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
             options.inPreferredConfig = Bitmap.Config.ARGB_4444;

           options.outWidth = photoWidth;
            options.outHeight = photoWidth;
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, photoWidth, photoWidth, true);
           */




       }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void httpCallBack(int id, JSONObject resp){
        if(id==Constants.ID.REGISTER)
        {
            String resCode = resp.optString(Constants.ResponseParams.RES_CODE);
            if(resCode.equals(Constants.ResponseInfo.CODE_SUCCESS)){
                Log.d(TAG,"register success,write to sharedPrefereces,name: "+register_Name);
                Log.d(TAG,"register success,write to sharedPrefereces,id: "+register_Id);
                Log.d(TAG, "register success,write to sharedPrefereces,photo len: " + register_Photo.length());
                Log.d(TAG, "register success,write to sharedPrefereces, pwd: " + register_Pwd);
                Log.d(TAG, "register success,write to sharedPrefereces, gender: " + register_gender);
                SharedPreferences pref = CacheUtils.getAccountSharedPreferences(mContext);

                Log.d(TAG,"register success sharedPrefereces,id before : "+pref.getString(Constants.AutoLogin.ID,""));


            //    SharedPreferences.Editor editor= getSharedPreferences("data",MODE_PRIVATE).edit();
           //     SharedPreferences pref = CacheUtils.getAccountSharedPreferences(mContext);
                SharedPreferences.Editor editor= CacheUtils.getEditor(mContext);
                //remove the original data
                editor.clear();
                editor.commit();
                Log.d(TAG, "register success sharedPrefereces,id middle : " + pref.getString(Constants.AutoLogin.ID, ""));
                editor.putString(Constants.AutoLogin.NAME, register_Name);
                editor.putString(Constants.AutoLogin.ID, register_Id);
                editor.putString(Constants.AutoLogin.PHOTO, register_Photo);
                editor.putString(Constants.AutoLogin.GENDER, register_gender);
            //    editor.putString(Constants.AutoLogin.PWD, register_Pwd);
                editor.commit();
                Log.d(TAG, "register success sharedPrefereces,id after : " + pref.getString(Constants.AutoLogin.ID, ""));

                //add original and small bitmap to LruCache
                CacheUtils.AddBitmapToLruCache(register_Id, userPhotoBitmap);
                CacheUtils.AddBitmapToLruCache(register_Id + "_big", originalBitmap);
                if(!smallBitmap.isRecycled()){
                    smallBitmap.recycle();//recycle the bitmap,it is equal to userPhotoBitmap
                }
                UIHandler.post(new Runnable() {  //update UI view in UI thread
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Info");
                        builder.setMessage("the id: " + register_Id + " register success, you can login or cancel");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //    Intent intent = new Intent(mContext, LoginActivity.class);
                                finish();//exit from register Activity,and go back to login Activity
                                //    startActivity(intent);
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

                Log.d("RegisterActivity", "register succeed ,username go to login activity");
                finish();//exit from register Activity,and go back to login Activity
            //    Intent intent = new Intent(mContext,LoginActivity.class);
             //   startActivity(intent);
            }else if(resCode.equals(Constants.Register.ErrorInfo.CODE_ID_EXISTED)){
                Log.d(TAG, " have registered already,please login");

                UIHandler.post(new Runnable() {  //update UI view in UI thread
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Info");
                        builder.setMessage("the id: " + register_Id + " has already registered, you can login or cancel");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            //    Intent intent = new Intent(mContext, LoginActivity.class);
                                finish();//exit from register Activity,and go back to login Activity
                            //    startActivity(intent);
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
}
