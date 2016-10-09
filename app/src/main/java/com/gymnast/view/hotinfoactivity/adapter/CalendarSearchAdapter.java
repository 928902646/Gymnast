package com.gymnast.view.hotinfoactivity.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gymnast.App;
import com.gymnast.R;
import com.gymnast.data.hotinfo.NewActivityItemDevas;
import com.gymnast.utils.CollectUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.TimeUtil;
import com.gymnast.view.hotinfoactivity.activity.ActivityDetailsActivity;
import com.gymnast.view.user.LoginActivity;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zzqybyb19860112 on 2016/9/10.
 */
public class CalendarSearchAdapter extends RecyclerView.Adapter{
   List<NewActivityItemDevas> mValues ;
    private Activity activity;
    private static final int VIEW_TYPE = -1;
     boolean isCollected=false;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CollectUtil.ERROR:
                    Toast.makeText(activity, "您已经收藏过了！", Toast.LENGTH_SHORT).show();
                    break;
                case CollectUtil.TO_COLLECT:
                    TextView tvLove= (TextView) msg.obj;
                    Drawable collectedPic1=activity.getResources().getDrawable(R.mipmap.icon_collection_click);
                    collectedPic1.setBounds(0,0,collectedPic1.getMinimumWidth(), collectedPic1.getMinimumHeight());
                    tvLove.setCompoundDrawables(collectedPic1,null,null,null);
                    String text=tvLove.getText().toString().trim();
                    String tmpText=text.substring(0, text.lastIndexOf("人"));
                    int startNumber=0;
                    if (!tmpText.endsWith("K")&&!tmpText.endsWith("万")){
                        startNumber=Integer.parseInt(tmpText);
                        startNumber++;
                    }
                    tvLove.setText(startNumber + "人收藏");
                    Toast.makeText(activity,"已收藏！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public CalendarSearchAdapter(Activity activity, List<NewActivityItemDevas> mValues) {
        this.activity = activity;
        if (mValues.size() == 0) {
            this.mValues = new ArrayList<>();
        } else {
            this.mValues = mValues;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyleview_recent_activity, null);
        itemview.setLayoutParams(lp);
        if (viewType == VIEW_TYPE) {
            View view=LayoutInflater.from(activity).inflate(R.layout.empty_view, parent, false);
            view.setLayoutParams(lp);
            return new empty(view);
        }
        return  new PersonViewHolder(itemview);
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PersonViewHolder){
            final PersonViewHolder viewHolder = (PersonViewHolder)holder;
            final NewActivityItemDevas newActivityItemDevas=  mValues.get(position);
            Picasso.with(activity).load(PicUtil.getImageUrl(activity, newActivityItemDevas.getImgUrls())).into(viewHolder.ivImage);
            viewHolder.tvaddress.setText(newActivityItemDevas.getAddress());
           final int activeID= newActivityItemDevas.getActiveId();
            isCollected=newActivityItemDevas.isCollected();
            if (isCollected){
                Drawable smallCollect=activity.getResources().getDrawable(R.mipmap.icon_collection_click);
                smallCollect.setBounds(0, 0, smallCollect.getMinimumWidth(), smallCollect.getMinimumHeight());
                viewHolder.tvcollection.setCompoundDrawables(smallCollect,null,null,null);
            }else {
                Drawable smallCollect=activity.getResources().getDrawable(R.mipmap.icon_collection);
                smallCollect.setBounds(0, 0, smallCollect.getMinimumWidth(), smallCollect.getMinimumHeight());
                viewHolder.tvcollection.setCompoundDrawables(smallCollect,null,null,null);
            }
            viewHolder.tvcollection.setText(TimeUtil.setLoveNum(newActivityItemDevas.getCollection())+"人收藏");
            viewHolder.tvnickname.setText(newActivityItemDevas.getNickname());
            viewHolder.tvstartTime.setText(TimeUtil.checkTime(newActivityItemDevas.getStartTime()));
            viewHolder.tvTitle.setText(newActivityItemDevas.getTitle());
            if(newActivityItemDevas.getPrice()==0){
                viewHolder.tvprice.setBackground(activity.getResources().getDrawable(R.drawable.border_radius_cornner_green));
                viewHolder.tvprice.setTextColor(activity.getResources().getColor(R.color.green));
            }else{
                viewHolder.tvprice.setBackground(activity.getResources().getDrawable(R.drawable.border_radius_cornner_red));
                viewHolder.tvprice.setTextColor(activity.getResources().getColor(R.color.login_btn_normal_color));
            }
            if (newActivityItemDevas.getPrice()==0){
                viewHolder.tvprice.setText("免费");
            }else {
                viewHolder.tvprice.setText("￥"+newActivityItemDevas.getPrice());
            }
            SharedPreferences share=activity.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            final String token = share.getString("Token", "");
            viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!token.equals("")&& App.isStateOK){
                        Intent intent=new Intent(activity, ActivityDetailsActivity.class);
                        intent.putExtra("ActiveID",activeID );
                        activity.startActivity(intent);
                    }else {
                        Toast.makeText(activity,"您还没有登陆，请登录！",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                    }
                }
            });
            viewHolder.tvcollection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!token.equals("")&& App.isStateOK){
                        CollectUtil.toCollect(activity, handler, activeID, viewHolder.tvcollection);
                    }else {
                        Toast.makeText(activity,"您还没有登陆，请登录！",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                    }
                }
            });
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (mValues.size() <= 0) {
            return VIEW_TYPE;
        }
        return super.getItemViewType(position);
    }
    @Override
    public int getItemCount() {
        return mValues.size() > 0 ? mValues.size() : 1;
    }
    class empty extends RecyclerView.ViewHolder{
        private final TextView text_empty;
        public empty(View itemView) {
            super(itemView);
            text_empty=(TextView) itemView.findViewById(R.id.text_empty);
        }
    }
    class PersonViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.llitem) LinearLayout llItem;
        @BindView(R.id.ivImage)ImageView ivImage;
        @BindView(R.id.tvTitle)TextView tvTitle;
        @BindView(R.id.tvnickname) TextView tvnickname;
        @BindView(R.id.tvaddress) TextView tvaddress;
        @BindView(R.id.tvstartTime) TextView tvstartTime;
        @BindView(R.id.tvcollection) TextView tvcollection;
        @BindView(R.id.tvprice) TextView tvprice;
        public PersonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
