package com.lynn.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.lynn.chattest2.R;

/**
 * Created by Administrator on 9/13/2016.
 */
public class TitleLayout extends LinearLayout {
   // private  Context mContext;
   public TitleLayout(Context context,AttributeSet attrs ){
       super(context, attrs);
    //   mContext = context;
       Log.d("TabHostActivity", "load title layout!!!!!!!!!");
       LayoutInflater.from(context).inflate(R.layout.title,this);

       final Button titlePlus = (Button)findViewById(R.id.title_plus);
       TextView chattitle =  (TextView)findViewById(R.id.chattitle);
       MyButton myButton = (MyButton)findViewById(R.id.my_button);
       myButton.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.d("Lynn","myButton onclick");
           }
       });
       myButton.setOnTouchListener(new OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               switch (event.getAction()){
                   case MotionEvent.ACTION_DOWN:
                       Log.d("Lynn","Button onTouch ACTION_DOWN ");
                       break;
                   case MotionEvent.ACTION_UP:
                       Log.d("Lynn","Button OnTouch ACTION_UP ");
                       break;
                   default:
                       break;
               }
               return false;
           }
       });
       titlePlus.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               Context mContext = getContext();
           //    Log.d("Lynn","TitleLayout,button onClick: ");
               showPopUpMenu(titlePlus, mContext);
           }
       });
       titlePlus.setOnTouchListener(new OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {


               return false;
           }
       });


   }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("Lynn","TitleLayout,dispatchTouchEvent ACTION_DOWN ");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("Lynn","TitleLayout,dispatchTouchEvent ACTION_UP ");
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);


    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("Lynn","TitleLayout,onInterceptTouchEvent ACTION_DOWN ");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("Lynn","TitleLayout,onInterceptTouchEvent ACTION_UP ");
                break;
            default:
                break;
        }
        //   return true;

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("Lynn","TitleLayout,onTouchEvent ACTION_DOWN ");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("Lynn","TitleLayout,onTouchEvent ACTION_UP ");
                break;
            default:
                break;
        }
         return super.onTouchEvent(event);
       // return false;
    }




    private void showPopUpMenu(View view,final Context context){
        PopupMenu menu = new PopupMenu(context,view);

        menu.getMenuInflater().inflate(R.menu.titleplus,menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_friend:
                        Intent intent = new Intent(((Activity) context), AddFriend.class);

                        ((Activity) context).startActivity(intent);
                        break;
                    case R.id.group_chat:
                        Intent intent1 = new Intent(((Activity) context), GroupChat.class);

                        ((Activity) context).startActivity(intent1);
                        break;

                    default:
                }
                return false;
            }
        });
        menu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });
        menu.show();

    }
}
