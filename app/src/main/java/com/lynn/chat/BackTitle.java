package com.lynn.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.lynn.chattest2.R;

/**
 * Created by Administrator on 9/23/2016.
 */

/**
 * Created by Administrator on 9/13/2016.
 */
public class BackTitle extends LinearLayout {
    // private  Context mContext;
    public BackTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        //   mContext = context;
        LayoutInflater.from(context).inflate(R.layout.back_title, this);
        final Button backButton = (Button) findViewById(R.id.back_button);
        TextView chattitle = (TextView) findViewById(R.id.chattitle);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext = getContext();
                ((Activity) mContext).finish();
            }
        });


    }
}