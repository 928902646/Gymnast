package com.gymnast.view.personal.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gymnast.R;
import com.gymnast.data.pack.ConcernData;
import com.gymnast.data.personal.CollectionData;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

/**model：1活动，2直播，3圈子，4帖子，5动态
 * Created by Cymbi on 2016/10/12.
 */
public class CollectionAdapter extends RecyclerView.Adapter {
    Context context;
    List<CollectionData> mValues=new ArrayList<>();
    private SimpleDateFormat sdf =new SimpleDateFormat("MM月-dd日 HH:mm");
    private static final int VIEW_TYPE = -1;
    private int ACTIVITY=1;
    private int LIVE=2;
    private int CIRCLE=4;
    private int Dynamic=5;
    public CollectionAdapter(Context context, List<CollectionData> mValues) {
        this.context = context;
        if(mValues.size()==0){
            this.mValues=new ArrayList<>();
        }else {
            this.mValues = mValues;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mValues.size() <= 0) {
            return VIEW_TYPE;
        }
        CollectionData data=mValues.get(position);
        int model=data.getModel();
        if(model==1){return ACTIVITY;}
        else if (model==2){return LIVE;}
        else if (model==4){return CIRCLE;}
        else if(model==5){return Dynamic;}
        return 0;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        final String token=share.getString("Token","");
        if(holder instanceof ActivityViewHolder){
            ActivityViewHolder viewHolder= (ActivityViewHolder)holder;
            final CollectionData data= mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(data.getImgUrl().get(0)), 320, 320),viewHolder.activity_imgUrl,320,320);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(data.getAvatar()), 320, 320),viewHolder.activity_head,320,320);
            viewHolder.activity_content.setText("     "+data.getDescContent());
            viewHolder.activity_zan.setText(data.getZanCount()+"");
            viewHolder.activity_comment.setText(data.getCommentCount()+"");
            viewHolder.activity_name.setText(data.getNickname());
            viewHolder.activity_title.setText(data.getTitle());
            viewHolder.activity_look.setText(data.getPageViews()+"人浏览");
            viewHolder.activity_time.setText(sdf.format(new Date(data.getCreateTime()))+"");
        }
        if(holder instanceof CircleViewHolder){
            CircleViewHolder viewHolder = (CircleViewHolder)holder;
            final CollectionData data= mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(data.getAvatar()), 320, 320),viewHolder.circle_head,320,320);
            viewHolder.circle_content.setText(data.getDescContent());
            viewHolder.circle_zan.setText(data.getZanCount()+"");
            viewHolder.circle_name.setText(data.getNickname());
            viewHolder.circle_comment.setText(data.getCommentCount()+"");
            viewHolder.circle_time.setText(sdf.format(new Date(data.getCreateTime()))+"");
            viewHolder.circle_Title.setText(data.getTitle());
            viewHolder.circle_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,PersonalOtherHomeActivity.class);
                    i.putExtra("UserID",data.getUserid());
                    context.startActivity(i);
                }
            });
        }
        if(holder instanceof DynamicViewHolder){
            final DynamicViewHolder viewHolder = (DynamicViewHolder)holder;
            final CollectionData data= mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(data.getAvatar()), 320, 320),viewHolder.star_head,320,320);
            viewHolder.star_content.setText("     "+data.getDescContent());
            viewHolder.star_look.setText(data.getPageViews()+"人浏览");
            viewHolder.star_name.setText(data.getPageViews());
            viewHolder.star_type.setText(data.getAuthInfo());
            viewHolder.star_msg.setText(data.getCommentCount()+"");
            viewHolder.star_time.setText(sdf.format(new Date(data.getCreateTime()))+"");
            viewHolder.star_zan.setText(data.getZanCount()+"");
            ArrayList<String> urls=data.getImgUrl();
            GridAdapter adapter=new GridAdapter(context,urls);
            viewHolder.gridview.setAdapter(adapter);
            viewHolder.gridview.invalidate();
            viewHolder.star_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,PersonalOtherHomeActivity.class);
                    i.putExtra("UserID",data.getUserid());
                    context.startActivity(i);
                }
            });

        }
        if(holder instanceof LiveViewHolder){
            final LiveViewHolder viewHolder = (LiveViewHolder)holder;
            final CollectionData data= mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(data.getAvatar()), 320, 320),viewHolder.live_head,320,320);
            viewHolder.live_name.setText(data.getNickname());
            viewHolder.live_time.setText(sdf.format(new Date(data.getCreateTime()))+"");
            viewHolder.live_title.setText(data.getTitle());
            viewHolder.live_look.setText(data.getPageViews()+"人浏览");
            viewHolder.live_zan.setText(data.getZanCount()+"");
            viewHolder.live_msg.setText(data.getCommentCount()+"");
            viewHolder.live_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,PersonalOtherHomeActivity.class);
                    i.putExtra("UserID",data.getUserid());
                    context.startActivity(i);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
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
