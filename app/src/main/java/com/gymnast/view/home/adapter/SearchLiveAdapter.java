package com.gymnast.view.home.adapter;

import android.app.Activity;
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
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.utils.TimeUtil;
import com.gymnast.view.home.view.HomeSearchResultAcitivity;
import com.gymnast.view.live.entity.LiveItem;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zzqybyb19860112 on 2016/9/4.
 */
public class SearchLiveAdapter extends RecyclerView.Adapter implements Filterable {
    private  List<LiveItem> mValues;
     List<LiveItem> mCopyInviteMessages;
     List<LiveItem> inviteMessages;
    private static final int VIEW_TYPE = -1;
    private Activity activity;
    static LiveItem liveItem;
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void OnItemPhotoClick(View view, LiveItem liveItem);
    }
    public SearchLiveAdapter(Activity activity, List<LiveItem> mValues) {
        this.activity = activity;
        if (mValues.size() == 0) {
            this.mValues=new ArrayList<>();
        } else {
            this.mValues = mValues;
        }
    }
    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(activity).inflate(R.layout.item_search_live, null);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemview.setLayoutParams(lp1);
        if(VIEW_TYPE==viewType){
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            View view=LayoutInflater.from(activity).inflate(R.layout.empty_view, parent, false);
            view.setLayoutParams(lp2);
            return new empty(view);
        }
        return new SearchLiveViewHolder(itemview);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if(viewHolder instanceof SearchLiveViewHolder) {
            final SearchLiveViewHolder holder = (SearchLiveViewHolder) viewHolder;
            liveItem= mValues.get(position);
            PicassoUtil.handlePic(activity, PicUtil.getImageUrlDetail(activity, StringUtil.isNullAvatar(liveItem.getBigPictureUrl()), 494, 368), holder.ivSearchBig, 494, 368);
            PicassoUtil.handlePic(activity, PicUtil.getImageUrlDetail(activity, StringUtil.isNullAvatar(liveItem.getMainPhotoUrl()), 72, 72), holder.rivSearchPhoto, 72, 72);
            holder.tvSearchTitle.setText("     " + liveItem.getTitle());
            holder.tvSearchJustfy.setText(liveItem.getAuthInfo());
            holder.tvSearchName.setText(liveItem.getNickName());
            holder.tvSearchNumber.setText(liveItem.getCurrentNum()+"");
            holder.tvSearchTime.setText(TimeUtil.getNumberTime(liveItem.getStartTime()));
            if(onItemClickListener!=null){
                holder.rivSearchPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //注意，这里的position不要用上面参数中的position，会出现位置错乱\
                        onItemClickListener.OnItemPhotoClick(holder.rivSearchPhoto, mValues.get(position));
                    }
                });
                holder.ivSearchBig.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //注意，这里的position不要用上面参数中的position，会出现位置错乱\
                        onItemClickListener.OnItemPhotoClick(holder.ivSearchBig, mValues.get(position));
                    }
                });
            }
        }
    }
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
    public void setFriends(List<LiveItem> data) {
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
                    final ArrayList<LiveItem> newValues = new ArrayList<LiveItem>();
                    for (int i = 0; i < count; i++) {
                        final LiveItem value = inviteMessages.get(i);
                        String username = value.title;
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
                SearchLiveAdapter.this.inviteMessages.clear();//清除原始数据
                SearchLiveAdapter.this.inviteMessages.addAll((List<LiveItem>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    SearchLiveAdapter.this.notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        SearchLiveAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    SearchLiveAdapter.this.setFriends(mCopyInviteMessages);
                }
            }
        };
    }
    class empty extends RecyclerView.ViewHolder{
        public empty(View itemView) {
            super(itemView);
        }
    }
    public class SearchLiveViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rivSearchPhoto) ImageView rivSearchPhoto;//播主头像
        @BindView(R.id.ivSearchBig) ImageView ivSearchBig;//直播大图
        @BindView(R.id.tvSearchTitle)  TextView tvSearchTitle;//直播标题
        @BindView(R.id.tvSearchName) TextView tvSearchName;//播主昵称
        @BindView(R.id.tvSearchTime) TextView tvSearchTime;//直播开始时间
        @BindView(R.id.tvSearchNumber) TextView tvSearchNumber;//直播在线人数
        @BindView(R.id.tvSearchJustfy) TextView tvSearchJustfy;//认证信息
        public SearchLiveViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
