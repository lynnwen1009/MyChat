package com.lynn.chat;

/**
 * Created by Administrator on 8/29/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lynn.chattest2.R;

import java.util.List;

/**
 * Created by Administrator on 8/25/2016.
 */
public class MsgAdatpter extends ArrayAdapter<Msg> {
    private int resourceId;
    private List<Msg> list;
    private String lastTime;
    private Context mContext;
    public MsgAdatpter(Context context,int textViewResurceId,List<Msg> objects){
        super(context,textViewResurceId,objects);
        resourceId =  textViewResurceId;
        list = objects;
        lastTime = " ";
        mContext = context;
    }
    public View getView(int position,View convertView,ViewGroup parent){
        //Msg msg = getItem(position);
       String lastTime  = " ";
        if(position==0){
            lastTime = " ";
        }
        else{
            lastTime = list.get(position-1).getTimeStamp();
        }


       final Msg msg = list.get(position);
        String currentTime  = msg.getTimeStamp();

         View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.centerLayout = (LinearLayout)view.findViewById(R.id.center_layout);
            viewHolder.leftLayout = (RelativeLayout)view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (RelativeLayout)view.findViewById(R.id.right_layout);
            viewHolder.timeStamp = (TextView)view.findViewById(R.id.center_timeStamp);
            viewHolder.leftMsg = (TextView)view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView)view.findViewById(R.id.right_msg);
            viewHolder.leftImage = (ImageView)view.findViewById(R.id.left_image);
            /*
            viewHolder.rightImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                }
            });*/
            viewHolder.rightImage = (ImageView)view.findViewById(R.id.right_image);

            Log.d("MainActivity", "view =null last msg time:" + MsgUtils.GetFormatTime());
            view.setTag(viewHolder);
        }else {

            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
            Log.d("MainActivity", "set tag for view,viewHolder. ");
        }
        if(msg.getType()==Msg.TYPE_RECEIVED){
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setBackgroundResource(R.drawable.chatting_msg_receive);
            viewHolder.leftMsg.setVisibility(View.VISIBLE);
            viewHolder.leftImage.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getContent());
        }else if(msg.getType()==Msg.TYPE_SENT){
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setBackgroundResource(R.drawable.chatting_msg_send);
            viewHolder.rightImage.setVisibility(View.GONE);
            viewHolder.rightMsg.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setText(msg.getContent());
        }else if(msg.getType()==Msg.TYPE_SENT_IMAGE){
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setVisibility(View.GONE);
            viewHolder.rightImage.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setBackgroundResource(0);

            int imageSeqNum = msg.getSeqNumImage();
            Log.d("MsgAdapter","right image seq num: "+imageSeqNum);
            //String
            Bitmap bm = CacheUtils.getBitmapFromLruCache(msg.getAccountFrom()+"_"+imageSeqNum);
            if(bm==null){
                Log.d("MsgAdapter","load cache right null: "+imageSeqNum);
                bm = PhotoUtils.StringToBitmap(msg.getContent());
                CacheUtils.AddBitmapToLruCache(msg.getAccountFrom()+"_"+imageSeqNum,bm);
            }
            viewHolder.rightImage.setImageBitmap(bm);
          //  viewHolder.rightImage.setImageBitmap(StringToBitmap(msg.getContent()));
            final ImageView imageView= viewHolder.rightImage;
            viewHolder.rightImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,PictureViewActivity.class);
                    Log.d("MainActivity","msgAdapter,pic Uri:"+msg.getPicUri().toString());
                    intent.putExtra(Constants.ExtraData.SENT_PHOTO_URI,msg);
                    mContext.startActivity(intent);

                }
            });

            Log.d("MainActivity","msg adapter set right image");
        }else if(msg.getType()==Msg.TYPE_RECEIVED_IMAGE){
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.leftMsg.setVisibility(View.GONE);
            viewHolder.leftImage.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setBackgroundResource(0);
            int imageSeqNum = msg.getSeqNumImage();
            Log.d("MsgAdapter","left image seq num: "+imageSeqNum);
            //String
            Bitmap bm = CacheUtils.getBitmapFromLruCache(msg.getAccountFrom()+"_"+imageSeqNum);
            if(bm==null){
                Log.d("MsgAdapter","load cache left null: "+imageSeqNum);
                bm = PhotoUtils.StringToBitmap(msg.getContent());
                CacheUtils.AddBitmapToLruCache(msg.getAccountFrom()+"_"+imageSeqNum,bm);
            }
            viewHolder.leftImage.setImageBitmap(bm);
         //   viewHolder.leftImage.setImageBitmap(StringToBitmap(msg.getContent()));
            viewHolder.leftImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,PictureViewActivity.class);
                    intent.putExtra(Constants.ExtraData.SENT_PHOTO_URI,msg);
                    mContext.startActivity(intent);
                    /*
                    Bitmap bm = CacheUtils.getBitmapFromLruCache(msg.getAccountFrom()+"#"+msg.getSeqNumImage());
                    if(bm!=null){
                        Intent intent = new Intent(mContext,PictureViewActivity.class);
                        intent.putExtra(Constants.ExtraData.SENT_PHOTO_URI,msg);
                        mContext.startActivity(intent);
                    }else{
                        Intent intent = new Intent(mContext,PictureViewActivity.class);
                      //  intent.putExtra(Constants.ExtraData.SENT_PHOTO_URI,msg);
                        mContext.startActivity(intent);

                        ChatData.MSG msg1 =new ChatData.MSG();
                        msg1.setTime(msg.getTimeStamp());
                        msg1.setType(ChatData.Type.GET_BIGIMAGE);
                        msg1.setSeqNum(msg.getSeqNumImage());
                        msg1.setFromId(CacheUtils.GetUserId());
                        msg1.setToId(msg.getAccountFrom());

                        msg1.setMsg("this is get bitmap message");
                        msg1.setPhoto(" ");
                        msg1.setName(CacheUtils.GetUserId());
                        msg1.setGender(CacheUtils.GetGender());
                        //  Log.d("AddFriend", "add friend,user_Id: " + user_Id);
                        Log.d("PictureViewActivity", "get big image from: " + msg1.getFromId());
                        Log.d("PictureViewActivity", "get big image to: " + msg1.getToId());
                        Log.d("PictureViewActivity", "get big image seqnum: " + msg1.getSeqNum());
                        MsgUtils.SendMsg(msg1);
                    }


*/

                }
            });
            Log.d("MainActivity", "msg adapter set left image");
        }
        Log.d("MainActivity","curr msg time:"+msg.getTimeStamp());
        Log.d("MainActivity","last msg time:"+lastTime);
        if (!(msg.getTimeStamp().equals(lastTime))){
            //first msg or time pass 60s since last msg


            viewHolder.centerLayout.setVisibility(View.VISIBLE);
            viewHolder.timeStamp.setText(msg.getTimeStamp());
             Log.d("MainActivity", "the[" + position + "] message," + msg.getContent() + "at: " + msg.getTimeStamp());
            lastTime = msg.getTimeStamp();
        }
        else{
            viewHolder.centerLayout.setVisibility(View.GONE);
            Log.d("MainActivity", "time gap small than 60s,the[" + position + "] message," + msg.getContent() + "at: " + msg.getTimeStamp());
         //   viewHolder.timeStamp.setText(msg.getTimeStamp());
        }
        return view;
    }
    class ViewHolder{
        LinearLayout centerLayout;
        RelativeLayout leftLayout;
        RelativeLayout rightLayout;
        TextView timeStamp;
        TextView leftMsg;
        TextView rightMsg;
        ImageView rightImage;
        ImageView leftImage;


    }
    public Bitmap StringToBitmap(String string){
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
