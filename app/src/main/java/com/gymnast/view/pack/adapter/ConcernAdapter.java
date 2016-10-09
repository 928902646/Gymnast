package com.gymnast.view.pack.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.pack.ConcernData;
import com.gymnast.data.personal.DynamicData;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;
import com.gymnast.view.personal.adapter.GridAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cymbi on 2016/9/28.
 */
public class ConcernAdapter extends RecyclerView.Adapter{
    Context context;
    List<ConcernData> mValues;
    private OnItemClickListener onItemClickListener;
    private static final int VIEW_TYPE = -1;
    private int LIVE=1;
    private int ACTIVITY=2;
    private int CIRCLE=3;
    private int Dynamic=4;
    private SimpleDateFormat sdf =new SimpleDateFormat("MM月-dd日 HH:mm");


    public ConcernAdapter(Context context, List<ConcernData> mValues) {
        this.context = context;
        if(mValues.size()==0){
            this.mValues = new ArrayList<>();
        }else {
            this.mValues = mValues;
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (mValues.size() <= 0) {
            return VIEW_TYPE;
        }
        ConcernData data=mValues.get(position);
        String FromType=data.getFromType();
        try{int type=Integer.parseInt(FromType);
            if(type==1){return LIVE;}
            else if (type==2){return ACTIVITY;}
            else if (type==3){return CIRCLE;}
        }catch (Exception e){e.printStackTrace();}
        return Dynamic;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View view;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(viewType==ACTIVITY){
            view= mInflater.inflate(R.layout.item_activity_dynamic,null);
            view.setLayoutParams(lp);
            return new ActivityViewHolder(view);
        }
        else if(viewType==CIRCLE){
            view= mInflater.inflate(R.layout.item_circle_dynamic,null);
            view.setLayoutParams(lp);
            return new CircleViewHolder(view);
        }
        else if(viewType==Dynamic){
            view= mInflater.inflate(R.layout.item_star_dynamic,null);
            view.setLayoutParams(lp);
            return new DynamicViewHolder(view);
        }
        else if(viewType==LIVE){
            view= mInflater.inflate(R.layout.item_search_live,null);
            view.setLayoutParams(lp);
            return new LiveViewHolder(view);
        }
        view=mInflater.inflate(R.layout.empty_view,parent,false);
        view.setLayoutParams(lp);
        return new empty(view);
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        final String token=share.getString("Token","");
        if(holder instanceof ActivityViewHolder){
            final ActivityViewHolder viewHolder=(ActivityViewHolder)holder;
            final ConcernData data=mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(data.getImgUrl().get(0)), 320, 320),viewHolder.activity_imgUrl,320,320);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(data.getAvatar()), 320, 320),viewHolder.activity_head,320,320);
            viewHolder.activity_content.setText("     "+data.getTopicContent());
            viewHolder.activity_zan.setText(data.getZanCounts()+"");
            viewHolder.activity_comment.setText(data.getCommentCounts()+"");
            viewHolder.activity_name.setText(data.getNickName());
            viewHolder.activity_title.setText(data.getTopicTitle());
            viewHolder.activity_look.setText(data.getPageviews()+"人浏览");
            viewHolder.activity_time.setText(sdf.format(new Date(data.getCreateTime()))+"");
            viewHolder.activity_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,PersonalOtherHomeActivity.class);
                    i.putExtra("UserID",data.getUserId());
                    context.startActivity(i);
                }
            });
            viewHolder.activity_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                            String token=share.getString("Token","");
                            String accountId=share.getString("UserId","");
                            String uri= API.BASE_URL+"/v1/zan/add";
                            HashMap<String,String> params=new HashMap<String, String>();
                            params.put("token",token);
                            params.put("bodyId",data.getFromId()+"");
                            params.put("bodyType",3+"");
                            params.put("accountId",accountId+"");
                            try {
                                String result= PostUtil.sendPostMessage(uri,params);
                                JSONObject jsonObject=new JSONObject(result);
                                if(jsonObject.getString("successmsg").equals("添加成功")){
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String a=viewHolder.activity_zan.getText().toString();
                                            int zan=Integer.parseInt(a);
                                            int b=zan+1;
                                            viewHolder.activity_zan.setText(b+"");
                                            Drawable drawable = ContextCompat.getDrawable(context,R.mipmap.like_pressed);
                                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                            viewHolder.activity_zan.setCompoundDrawables(drawable,null,null,null);
                                        }
                                    });
                                }else {
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String a=viewHolder.activity_zan.getText().toString();
                                            int zan=Integer.parseInt(a);
                                            int b=zan-1;
                                            viewHolder.activity_zan.setText(b+"");
                                            Drawable drawable =ContextCompat.getDrawable(context,R.mipmap.like_normal);
                                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                            viewHolder.activity_zan.setCompoundDrawables(drawable,null,null,null);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        }
        if(holder instanceof CircleViewHolder){
            final CircleViewHolder viewHolder = (CircleViewHolder)holder;
            final ConcernData data=  mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(data.getAvatar()), 320, 320),viewHolder.circle_head,320,320);
            viewHolder.circle_content.setText(data.getTopicContent());
            viewHolder.circle_zan.setText(data.getZanCounts()+"");
            viewHolder.circle_name.setText(data.getNickName());
            viewHolder.circle_comment.setText(data.getCommentCounts()+"");
            viewHolder.circle_time.setText(sdf.format(new Date(data.getCreateTime()))+"");
            viewHolder.circle_Title.setText(data.getTopicTitle());
            viewHolder.circle_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,PersonalOtherHomeActivity.class);
                    i.putExtra("UserID",data.getUserId());
                    context.startActivity(i);
                }
            });
            viewHolder.circle_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                            String token=share.getString("Token","");
                            String accountId=share.getString("UserId","");
                            String uri= API.BASE_URL+"/v1/zan/add";
                            HashMap<String,String> params=new HashMap<String, String>();
                            params.put("token",token);
                            params.put("bodyId",data.getFromId()+"");
                            params.put("bodyType",1+"");
                            params.put("accountId",accountId+"");
                            try {
                                String result= PostUtil.sendPostMessage(uri,params);
                                JSONObject jsonObject=new JSONObject(result);
                                if(jsonObject.getString("successmsg").equals("添加成功")){
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String a=viewHolder.circle_zan.getText().toString();
                                            int zan=Integer.parseInt(a);
                                            int b=zan+1;
                                            viewHolder.circle_zan.setText(b+"");
                                            Drawable drawable = ContextCompat.getDrawable(context,R.mipmap.like_pressed);
                                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                            viewHolder.circle_zan.setCompoundDrawables(drawable,null,null,null);
                                        }
                                    });
                                }else {
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String a=viewHolder.circle_zan.getText().toString();
                                            int zan=Integer.parseInt(a);
                                            int b=zan-1;
                                            viewHolder.circle_zan.setText(b+"");
                                            Drawable drawable =ContextCompat.getDrawable(context,R.mipmap.like_normal);
                                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                            viewHolder.circle_zan.setCompoundDrawables(drawable,null,null,null);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        }
        if(holder instanceof DynamicViewHolder){
            final DynamicViewHolder viewHolder = (DynamicViewHolder)holder;
            final ConcernData data=  mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(data.getAvatar()), 320, 320),viewHolder.star_head,320,320);
            viewHolder.star_content.setText("     "+data.getTopicContent());
            viewHolder.star_look.setText(data.getPageviews()+"人浏览");
            viewHolder.star_name.setText(data.getNickName());
            viewHolder.star_type.setText(data.getAuthInfo());
            viewHolder.star_msg.setText(data.getCommentCounts()+"");
            viewHolder.star_time.setText(sdf.format(new Date(data.getCreateTime()))+"");
            viewHolder.star_zan.setText(data.getZanCounts()+"");
            ArrayList<String> urls=data.getImgUrl();
            GridAdapter adapter=new GridAdapter(context,urls);
            viewHolder.gridview.setAdapter(adapter);
            viewHolder.gridview.invalidate();
            viewHolder.star_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,PersonalOtherHomeActivity.class);
                    i.putExtra("UserID",data.getUserId());
                    context.startActivity(i);
                }
            });
            viewHolder.star_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                            String token=share.getString("Token","");
                            String accountId=share.getString("UserId","");
                            String uri= API.BASE_URL+"/v1/zan/add";
                            HashMap<String,String> params=new HashMap<String, String>();
                            params.put("token",token);
                            params.put("bodyId",data.getId()+"");
                            params.put("bodyType",2+"");
                            params.put("accountId",accountId+"");
                            try {
                                String result= PostUtil.sendPostMessage(uri,params);
                                JSONObject jsonObject=new JSONObject(result);
                                if(jsonObject.getString("successmsg").equals("添加成功")){
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String a=viewHolder.star_zan.getText().toString();
                                            int zan=Integer.parseInt(a);
                                            int b=zan+1;
                                            viewHolder.star_zan.setText(b+"");
                                            Drawable drawable = ContextCompat.getDrawable(context,R.mipmap.like_pressed);
                                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                            viewHolder.star_zan.setCompoundDrawables(drawable,null,null,null);
                                        }
                                    });
                                }else {
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String a=viewHolder.star_zan.getText().toString();
                                            int zan=Integer.parseInt(a);
                                            int b=zan-1;
                                            viewHolder.star_zan.setText(b+"");
                                            Drawable drawable =ContextCompat.getDrawable(context,R.mipmap.like_normal);
                                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                            viewHolder.star_zan.setCompoundDrawables(drawable,null,null,null);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        }
        if(holder instanceof LiveViewHolder){
            final LiveViewHolder viewHolder = (LiveViewHolder)holder;
            final ConcernData data=  mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(data.getAvatar()), 320, 320),viewHolder.live_head,320,320);
            viewHolder.live_name.setText(data.getNickName());
            viewHolder.live_time.setText(sdf.format(new Date(data.getCreateTime()))+"");
            viewHolder.live_title.setText(data.getTopicTitle());
            viewHolder.live_look.setText(data.getPageviews()+"人浏览");
            viewHolder.live_zan.setText(data.getZanCounts()+"");
            viewHolder.live_msg.setText(data.getCommentCounts()+"");
            viewHolder.live_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,PersonalOtherHomeActivity.class);
                    i.putExtra("UserID",data.getUserId());
                    context.startActivity(i);
                }
            });
            viewHolder.live_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                            String token=share.getString("Token","");
                            String accountId=share.getString("UserId","");
                            String uri= API.BASE_URL+"/v1/zan/add";
                            HashMap<String,String> params=new HashMap<String, String>();
                            params.put("token",token);
                            params.put("bodyId",data.getFromId()+"");
                            params.put("bodyType",4+"");
                            params.put("accountId",accountId+"");
                            try {
                                String result= PostUtil.sendPostMessage(uri,params);
                                JSONObject jsonObject=new JSONObject(result);
                                if(jsonObject.getString("successmsg").equals("添加成功")){
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String a=viewHolder.live_zan.getText().toString();
                                            int zan=Integer.parseInt(a);
                                            int b=zan+1;
                                            viewHolder.live_zan.setText(b+"");
                                            Drawable drawable = ContextCompat.getDrawable(context,R.mipmap.like_pressed);
                                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                            viewHolder.live_zan.setCompoundDrawables(drawable,null,null,null);
                                        }
                                    });
                                }else {
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String a=viewHolder.live_zan.getText().toString();
                                            int zan=Integer.parseInt(a);
                                            int b=zan-1;
                                            viewHolder.live_zan.setText(b+"");
                                            Drawable drawable =ContextCompat.getDrawable(context,R.mipmap.like_normal);
                                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                            viewHolder.live_zan.setCompoundDrawables(drawable,null,null,null);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        }
        if(onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱\
                    onItemClickListener.OnItemClickListener(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void OnItemClickListener(View view, int position);
    }

    class empty extends RecyclerView.ViewHolder{
        public empty(View itemView) {
            super(itemView);
        }
    }
    class ActivityViewHolder extends RecyclerView.ViewHolder{
        private final ImageView activity_head,activity_imgUrl;
        private final TextView activity_name,activity_title,activity_content,
                activity_time,activity_look,activity_comment,activity_zan,activity_startTime;
        public ActivityViewHolder(View itemView) {
            super(itemView);
            activity_head=(ImageView)itemView.findViewById(R.id.activity_head);
            activity_imgUrl=(ImageView)itemView.findViewById(R.id.activity_imgUrl);
            activity_name= (TextView) itemView.findViewById(R.id.activity_name);
            activity_title= (TextView) itemView.findViewById(R.id.activity_title);
            activity_content= (TextView) itemView.findViewById(R.id.activity_content);
            activity_time= (TextView) itemView.findViewById(R.id.activity_time);
            activity_look= (TextView) itemView.findViewById(R.id.activity_look);
            activity_zan= (TextView) itemView.findViewById(R.id.activity_zan);
            activity_comment= (TextView) itemView.findViewById(R.id.activity_comment);
            activity_startTime= (TextView) itemView.findViewById(R.id.activity_startTime);
        }
    }
    class CircleViewHolder extends RecyclerView.ViewHolder{
        private final ImageView circle_head;
        private final TextView circle_name,circle_content,circle_time,circle_look,circle_zan,circle_comment,circle_Title;
        public CircleViewHolder(View itemView) {
            super(itemView);
            circle_head=(ImageView)itemView.findViewById(R.id.circle_head);
            circle_name= (TextView) itemView.findViewById(R.id.circle_name);
            circle_Title= (TextView) itemView.findViewById(R.id.circle_Title);
            circle_content= (TextView) itemView.findViewById(R.id.circle_content);
            circle_time= (TextView) itemView.findViewById(R.id.circle_time);
            circle_look= (TextView) itemView.findViewById(R.id.circle_look);
            circle_zan= (TextView) itemView.findViewById(R.id.circle_zan);
            circle_comment= (TextView) itemView.findViewById(R.id.circle_reply);
        }
    }
    class DynamicViewHolder extends RecyclerView.ViewHolder{
        private final ImageView star_head;
        private final TextView star_name,star_content,star_time,star_look,star_zan,star_msg,video_number,star_type;
        private final GridView gridview;
        public DynamicViewHolder(View itemView) {
            super(itemView);
            star_head=  (ImageView) itemView.findViewById(R.id.star_head);
            star_name= (TextView) itemView.findViewById(R.id.star_name);
            star_type= (TextView) itemView.findViewById(R.id.star_type);
            star_content= (TextView) itemView.findViewById(R.id.star_content);
            star_time= (TextView) itemView.findViewById(R.id.star_time);
            star_look= (TextView) itemView.findViewById(R.id.star_look);
            star_zan= (TextView) itemView.findViewById(R.id.star_zan);
            star_msg= (TextView) itemView.findViewById(R.id.star_msg);
            video_number= (TextView) itemView.findViewById(R.id.video_number);
            gridview= (GridView) itemView.findViewById(R.id.gridview);
        }
    }
    class LiveViewHolder extends RecyclerView.ViewHolder{
        private final ImageView live_head,live_big;
        private final TextView live_name,live_auth,live_time,live_title,live_number,live_look,live_zan,live_msg;
        public LiveViewHolder(View itemView) {
            super(itemView);
            live_head=(ImageView)itemView.findViewById(R.id.rivSearchPhoto);
            live_big=(ImageView)itemView.findViewById(R.id.ivSearchBig);
            live_name=(TextView)itemView.findViewById(R.id.tvSearchName);
            live_auth=(TextView)itemView.findViewById(R.id.pack_name);
            live_time=(TextView)itemView.findViewById(R.id.tvSearchTime);
            live_title=(TextView)itemView.findViewById(R.id.tvSearchTitle);
            live_number=(TextView)itemView.findViewById(R.id.tvSearchNumber);
            live_look=(TextView)itemView.findViewById(R.id.live_look);
            live_zan=(TextView)itemView.findViewById(R.id.live_zan);
            live_msg=(TextView)itemView.findViewById(R.id.live_msg);

        }
     }
}