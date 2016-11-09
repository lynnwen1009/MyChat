package com.lynn.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by Administrator on 2016/11/2.
 */
public class MyButton extends Button {
    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("Lynn", "Button dispatchTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("Lynn","Button dispatchTouchEvent ACTION_UP");
                break;
            default:
                break;
        }
        // return false;
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("Lynn", "Button onTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("Lynn","Button onTouchEvent ACTION_UP");
                break;
            default:
                break;
        }
       // return true;
        return   super.onTouchEvent(event);
      //return   false;
    }
}
