package com.gymnast.view.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.data.hotinfo.NewActivityItemDevas;
import com.gymnast.utils.NumberUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.home.view.HomeSearchResultAcitivity;
import com.gymnast.view.hotinfoactivity.activity.ActivityDetailsActivity;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zzqybyb19860112 on 2016/9/6.
 */
public class SearchActiveAdapter extends RecyclerView.Adapter implements Filterable {
    private final List<NewActivityItemDevas> mValues ;
    List<NewActivityItemDevas> mCopyInviteMessages;
    List<NewActivityItemDevas> inviteMessages;
    Context context;
    private static final int VIEW_TYPE = -1;
    public SearchActiveAdapter( Context context, List<NewActivityItemDevas> mValues) {
        this.context=context;
        if(mValues.size()==0){
            this.mValues=new ArrayList<>();
        }else {
            this.mValues=mValues;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_active, null);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemview.setLayoutParams(lp1);
        if(VIEW_TYPE==viewType){
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            View view=LayoutInflater.from(context).inflate(R.layout.empty_view, parent, false);
            view.setLayoutParams(lp2);
            return new empty(view);
        }
        return new SearchActiveHolder(itemview);
    }
    public static String setTime(long time){
        SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日");
        String timeTmp=sdf.format(new Date(time));
        String month=timeTmp.substring(0, 2);
        String day=timeTmp.substring(3,5);
        month= NumberUtil.handleNumber(month);
        day= NumberUtil.handleNumber(day);
        return month+"月"+day+"日";
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchActiveHolder) {
            SearchActiveHolder viewHolder = (SearchActiveHolder) holder;
            final NewActivityItemDevas data=mValues.get(position);
            viewHolder.ivActiveItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mValues.size()!=0) {
                        Intent intent = new Intent(context, ActivityDetailsActivity.class);
                        intent.putExtra("ActiveID", data.getActiveId());
                        intent.putExtra("UserId", data.getUserID());
                        context.startActivity(intent);
                    }
                }
            });
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(data.getImgUrls()), 320, 320), viewHolder.ivActiveItem, 320, 320);
            viewHolder.tvActiveTitle.setText(data.getTitle());
            viewHolder.tvActiveStart.setText(setTime(data.getStartTime())+"开始");
            viewHolder.tvActiveFrom.setText("来自："+data.getNickname());
            viewHolder.tvActiveFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mValues.size()!=0) {
                        Intent intent = new Intent(context, PersonalOtherHomeActivity.class);
                        intent.putExtra("UserID", data.getUserID());
                        context.startActivity(intent);
                    }
                }
            });
            viewHolder.tvActiveSee.setText(data.getPageViews()+"次浏览");
            viewHolder.tvActiveZan.setText(data.getZanCount()+"");
            viewHolder.tvActiveMSG.setText(data.getMsgCount() + "");
        }
    }
    @Override
    public int getItemCount() {
         return mValues.size() > 0 ? mValues.size() : 1;
    }
    @Override
    public int getItemViewType(int position) {
        if (mValues.size() <= 0) {
            return VIEW_TYPE;
        }
        return 0;
    }
    public void setFriends(List<NewActivityItemDevas> data) {
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
                    final ArrayList<NewActivityItemDevas> newValues = new ArrayList<NewActivityItemDevas>();
                    for (int i = 0; i < count; i++) {
                        final NewActivityItemDevas value = inviteMessages.get(i);
                        String username = value.getTitle();
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
                SearchActiveAdapter.this.inviteMessages.clear();//清除原始数据
                SearchActiveAdapter.this.inviteMessages.addAll((List<NewActivityItemDevas>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    SearchActiveAdapter.this.notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        SearchActiveAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    SearchActiveAdapter.this.setFriends(mCopyInviteMessages);
                }
            }
        };
    }
    class empty extends RecyclerView.ViewHolder{
        public empty(View itemView) {
            super(itemView);
        }
    }
    class SearchActiveHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.llActiveItem)  LinearLayout llActiveItem;
        @BindView(R.id.ivActiveItem)  ImageView ivActiveItem;
        @BindView(R.id.tvActiveTitle)  TextView tvActiveTitle;
        @BindView(R.id.tvActiveStart)  TextView tvActiveStart;
        @BindView(R.id.tvActiveFrom)  TextView tvActiveFrom;
        @BindView(R.id.tvActiveSee)  TextView tvActiveSee;
        @BindView(R.id.tvActiveZan)  TextView tvActiveZan;
        @BindView(R.id.tvActiveMSG)  TextView tvActiveMSG;
        public SearchActiveHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
