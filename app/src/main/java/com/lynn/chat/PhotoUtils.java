package com.lynn.chat;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/8.
 */
public class PhotoUtils {
    private static Map<Uri,Bitmap> photoCache = new HashMap<Uri,Bitmap>();

    public static void saveToPhotoCache(Uri uri,Bitmap bitmap){
        photoCache.put(uri,bitmap);
    }
    public static Bitmap getBitmapFromPhotoCache(Uri uri){
        return photoCache.get(uri);
    }
    public static String bitmapToString(Bitmap bitmap,int bitmapQuality){
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,bitmapQuality,bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);

        return string;
    }
    public static String uriToPath(Uri uri,Context mContext){


     //   String[] filePathColumn = {MediaStore.Images.Media.DATA};
        String[] filePathColumn = {MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }
    public static Bitmap decodeUriToBitmap(Uri uri,Context mContext){
        Bitmap bitmap = null;
        try{
            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));

        }catch(FileNotFoundException e){
            Log.d("PhotoUtils","select photo not found,uri: "+uri.toString());
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
    public static Drawable StringToDrawable(String string){
        Drawable drawable = null;

        try{
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            drawable = new BitmapDrawable(bitmap);


        }catch(Exception e){
            e.printStackTrace();
        }
        return drawable;
    }

    public static String drawableToString(Drawable drawable){
        if(drawable!=null){
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),
                    drawable.getOpacity()!= PixelFormat.OPAQUE?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            int size  = bitmap.getWidth()*bitmap.getHeight();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
            byte[] imagedata = baos.toByteArray();
            String icon = Base64.encodeToString(imagedata,Base64.DEFAULT);
            return icon;

        }
        return null;
    }
    public static byte[] bitmapToBytes(Bitmap bitmap,int bitmapQuality){
        String string = null;

        try{
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,bitmapQuality,bStream);
            byte[] bytes = bStream.toByteArray();
        //string = Base64.encodeToString(bytes, Base64.DEFAULT);
            return bytes;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static Bitmap StringToBitmap(String string){
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
