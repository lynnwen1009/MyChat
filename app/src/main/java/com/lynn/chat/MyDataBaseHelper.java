package com.lynn.chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/10/20.
 */
public class MyDataBaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_CHATS = "create table if not exists chats ("
                                                            +"id integer primary key autoincrement,"
                                                            +"friendId text, "
                                                     //       +"photo text, "
                                                            +"unread text, "
                                                            +"msg text, "
                                                            +"timeStamp text)";

    public static final String CREATE_FRIENDS = "create table if not exists friends("+"id  integer primary key autoincrement,"
            +"friendId text, "
            +"name text, "
        //    +"photo text, "
            +"gender text, "
            +"lastMsg text )";

    public static final String CREATE_NEW_FRIENDS = "create table if not exists new_friends("+"id  integer primary key autoincrement,"
            +"friendId text, "
            +"name text, "
            +"type integer, "
            +"gender text)";


    private Context mContext;
    private int update=0;
    public MyDataBaseHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context, name, factory, version);
        Log.d("TabHostActivity", "new MyDataBaseHelper,name: " + name);
        mContext  = context;
    }
    public int version(){
        return update;
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_CHATS);
        db.execSQL(CREATE_FRIENDS);
        db.execSQL(CREATE_NEW_FRIENDS);
        Log.d("TabHostActivity", "data base onCreate()");

//        Toast.makeText(mContext,"Create database succeeded",Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
       update=1;
        db.execSQL("drop table if exists chats");
        db.execSQL("drop table if exists friends");
        db.execSQL("drop table if exists new_friends");
        Log.d("TabHostActivity", "data base onUpgrade(),old version: " + oldVersion);
        Log.d("TabHostActivity", "data base onUpgrade(),new version: "+newVersion);
  //      switch(oldVersion){
   //         case 3:
    //            db.execSQL("alter table friends drop column photo text");
    //    }
        onCreate(db);
    }
    public void createChattingDataBase(SQLiteDatabase db,String friendId){
        String create_chatting_table = "create table if not exists "+friendId+"("+"id  integer primary key autoincrement,"
                +"friendId text, "
                +"msg text, "
                +"picturePath text, "
                +"seqNum integer, "
                +"type integer, "
                +"timeStamp text)";
        db.execSQL(create_chatting_table);
        Log.d("TabHostActivity", "data base createChattingDataBase()");

    }
    public void updateChattingDataBase(SQLiteDatabase db,String friendId){
        db.execSQL("drop table if exists "+friendId);
        Log.d("TabHostActivity", "data base updateChattingDataBase()");
        createChattingDataBase(db,friendId);
    }


}
