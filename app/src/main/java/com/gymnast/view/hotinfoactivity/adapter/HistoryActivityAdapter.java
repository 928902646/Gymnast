package com.gymnast.view.hotinfoactivity.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gymnast.App;
import com.gymnast.R;
import com.gymnast.data.hotinfo.RecentActivityDetail;
import com.gymnast.data.net.API;
import com.gymnast.utils.CollectUtil;
import com.gymnast.utils.GlobalInfoUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.utils.TimeUtil;
import com.gymnast.view.home.view.HomeSearchResultAcitivity;
import com.gymnast.view.hotinfoactivity.activity.ActivityDetailsActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/27.
 */
public class HistoryActivityAdapter extends RecyclerView.Adapter implements Filterable {
    private List<RecentActivityDetail> mValues;
    List<RecentActivityDetail> mCopyInviteMessages;
    List<RecentActivityDetail> inviteMessages;
    private static final int VIEW_TYPE = -1;
    private Context context;
    public HistoryActivityAdapter(Context context, List<RecentActivityDetail> mValues){
        this.context=context;
        if (mValues.size()==0){
            this.mValues=new ArrayList<>();
        }else{
            this.mValues=mValues;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(VIEW_TYPE==viewType){
            View   view=inflater.inflate(R.layout.empty_view, viewGroup, false);
            view.setLayoutParams(lp);
            return new empty(view);
        }
        View viewItem =inflater.inflate(R.layout.item_recyleview_history_activity, null);
        viewItem.setLayoutParams(lp);
           return new PersonViewHolder(viewItem);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PersonViewHolder){
            final PersonViewHolder viewHolder = (PersonViewHolder)holder;
            final RecentActivityDetail recentActivityDetail=mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(recentActivityDetail.getPictureURL()), 1080, 720),viewHolder.ivTitle,1080,720);
            viewHolder.tvDetail.setText(Html.fromHtml(GlobalInfoUtil.delHTMLTag(recentActivityDetail.getDetail() + "")));
            viewHolder.tvAddress.setText(recentActivityDetail.getAddress());
            viewHolder.tvTime.setText(recentActivityDetail.getTime());
            String favNumber= TimeUtil.setLoveNum(recentActivityDetail.getLoverNum());
            viewHolder.tvLoveNum.setText(favNumber+"人收藏");
            if (recentActivityDetail.getType().equals("免费")){
                viewHolder.tvType.setBackground(context.getResources().getDrawable(R.drawable.border_radius_cornner_green));
                viewHolder.tvType.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                viewHolder.tvType.setBackground(context.getResources().getDrawable(R.drawable.border_radius_cornner_red));
                viewHolder.tvType.setTextColor(context.getResources().getColor(R.color.login_btn_normal_color));
            }
            viewHolder.tvType.setText(recentActivityDetail.getType());
            viewHolder.ivTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, ActivityDetailsActivity.class);
                    intent.putExtra("RecentActivityDetail",recentActivityDetail);
                    context.startActivity(intent);
                }
            });
            final boolean isCollected=recentActivityDetail.isCollected();
            if (isCollected){
                Drawable collectedPic=context.getResources().getDrawable(R.mipmap.icon_collection_click);
                collectedPic.setBounds(0,0,collectedPic.getMinimumWidth(), collectedPic.getMinimumHeight());
                viewHolder.tvLoveNum.setCompoundDrawables(collectedPic,null,null,null);
            }else {
                Drawable collectedPic=context.getResources().getDrawable(R.mipmap.icon_collection);
                collectedPic.setBounds(0,0,collectedPic.getMinimumWidth(), collectedPic.getMinimumHeight());
                viewHolder.tvLoveNum.setCompoundDrawables(collectedPic,null,null,null);
            }
            viewHolder.tvLoveNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CollectUtil.toCollect(context,handler, recentActivityDetail.getModelID(), viewHolder.tvLoveNum);
                }
            });
        }
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CollectUtil.ERROR:
                    Toast.makeText(context,"您已经收藏过了！",Toast.LENGTH_SHORT).show();
                    break;
                case CollectUtil.TO_COLLECT:
                    TextView tvLove= (TextView) msg.obj;
                    Drawable collectedPic1=context.getResources().getDrawable(R.mipmap.icon_collection_click);
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
                    Toast.makeText(context,"已收藏！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    public int getItemCount() {
        return mValues.size() > 0 ? mValues.size() : 1;
    }
    public int getItemViewType(int position) {
        if (mValues.size() <= 0) {
            return VIEW_TYPE;
        }
        return super.getItemViewType(position);
    }
    public void setFriends(List<RecentActivityDetail> data) {
        //复制数据
        mCopyInviteMessages = new ArrayList<>();
        this.mCopyInviteMessages.addAll(data);
        this.inviteMessages = data;
        this.notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //初始化过滤结果对象
                FilterResults results = new FilterResults();
                //假如搜索为空的时候，将复制的数据添加到原始数据，用于继续过滤操作
                if (results.values == null) {
                    mValues.clear();
                    mValues.addAll(mCopyInviteMessages);
                }
                //关键字为空的时候，搜索结果为复制的结果
                if (constraint == null || constraint.length() == 0) {
                    results.values = mCopyInviteMessages;
                    results.count = mCopyInviteMessages.size();
                } else {
                    String searchText= HomeSearchResultAcitivity.getSearchText();
                    String prefixString;
                    if (searchText.equals("")){
                        prefixString=searchText.toString();
                    }else {
                        prefixString= constraint.toString();
                    }
                    final int count = inviteMessages.size();
                    //用于存放暂时的过滤结果
                    final ArrayList<RecentActivityDetail> newValues = new ArrayList<RecentActivityDetail>();
                    for (int i = 0; i < count; i++) {
                        final RecentActivityDetail value = inviteMessages.get(i);
                        String username = value.getDetail();
                        // First match against the whole ,non-splitted value，假如含有关键字的时候，添加
                        if (username.contains(prefixString)) {
                            newValues.add(value);
                        } else {
                            //过来空字符开头
                            final String[] words = username.split(" ");
                            final int wordCount = words.length;
                            // Start at index 0, in case valueText starts with space(s)
                            for (int k = 0; k < wordCount; k++) {
                                if (words[k].contains(prefixString)) {
                                    newValues.add(value);
                                    break;
                                }
                            }
                        }
                    }
                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;//过滤结果
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                HistoryActivityAdapter.this.inviteMessages.clear();//清除原始数据
                HistoryActivityAdapter.this.inviteMessages.addAll((List<RecentActivityDetail>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    HistoryActivityAdapter.this.notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        HistoryActivityAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    HistoryActivityAdapter.this.setFriends(mCopyInviteMessages);
                }
            }
        };
    }
    class empty extends RecyclerView.ViewHolder{
        private final TextView text_empty;
        public empty(View itemView) {
            super(itemView);
            text_empty=(TextView) itemView.findViewById(R.id.text_empty);
        }
    }
    class PersonViewHolder extends RecyclerView.ViewHolder{
        ImageView ivTitle;
        TextView tvDetail,tvAddress,tvTime,tvType,tvLoveNum;
        public PersonViewHolder(View itemView) {
            super(itemView);
            ivTitle= (ImageView) itemView.findViewById(R.id.ivTitle);
            tvDetail= (TextView) itemView.findViewById(R.id.tvDetail);
            tvAddress= (TextView) itemView.findViewById(R.id.tvAddress);
            tvTime= (TextView) itemView.findViewById(R.id.tvTime);
            tvType= (TextView) itemView.findViewById(R.id.tvType);
            tvLoveNum= (TextView) itemView.findViewById(R.id.tvLoveNum);
        }
    }
}
