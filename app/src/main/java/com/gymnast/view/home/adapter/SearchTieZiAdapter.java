package com.gymnast.view.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.data.personal.PostsData;
import com.gymnast.utils.GlobalInfoUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.home.view.HomeSearchResultAcitivity;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;
import com.gymnast.view.personal.activity.PersonalCircleActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zzqybyb19860112 on 2016/9/6.
 */
public class SearchTieZiAdapter extends RecyclerView.Adapter implements Filterable {
    private Context context;
    private static final int VIEW_TYPE = -1;
    private  final List<PostsData> mValues;
    List<PostsData> mCopyInviteMessages;
    List<PostsData> inviteMessages;
    private OnItemClickListener onItemClickListener;
    private SimpleDateFormat sdf =new SimpleDateFormat("MM月-dd日 HH:mm");
    public SearchTieZiAdapter(Context context,List<PostsData> mValues) {
        this.context = context;
        if (mValues.size() == 0) {
            this.mValues=new ArrayList<>();
        } else {
            this.mValues = mValues;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.item_circle_dynamic, null);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemview.setLayoutParams(lp1);
        if(VIEW_TYPE==viewType){
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            View view=LayoutInflater.from(context).inflate(R.layout.empty_view, parent, false);
            view.setLayoutParams(lp2);
            return new empty(view);
        }
        return new SearchTieZiHolder(itemview);
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SearchTieZiHolder){
            SearchTieZiHolder viewHolder = (SearchTieZiHolder) holder;
            final PostsData postsData = mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(postsData.getAvatar()), 320, 320), viewHolder.rivSearchPic, 320, 320);
            viewHolder.tvSearchName.setText(postsData.getNickname()+"");
            viewHolder.tvSearchText.setText(Html.fromHtml(GlobalInfoUtil.delHTMLTag(postsData.getContent()))+"");
            viewHolder.tvSearchTitle.setText(postsData.getTitle()+"");
            viewHolder.tvSearchFrom.setText(postsData.getCircleTitle()+"");
            viewHolder.tvSearchLook.setText(postsData.getMeetCount() + "人浏览");
            viewHolder.tvSearchPrise.setText(postsData.getZanCount() + "");
            viewHolder.tvSearchTime.setText(sdf.format(new Date(postsData.getCreateTime()))+"");
            viewHolder.tvSearchMSG.setText(postsData.getMsgCount()+"");
            viewHolder.rivSearchPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mValues.size()!=0) {
                        Intent i = new Intent(context, PersonalOtherHomeActivity.class);
                        int userID = postsData.getCreateId();
                        i.putExtra("UserID", userID);
                        context.startActivity(i);
                    }
                }
            });
            viewHolder.tvSearchFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mValues.size()!=0) {
                        Intent i = new Intent(context, PersonalCircleActivity.class);
                        int  circle_id= postsData.getCircleId();
                        i.putExtra("CircleId",circle_id);
                        context.startActivity(i);
                    }
                }
            });
        }
        if (onItemClickListener != null) {
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
        return mValues.size() > 0 ? mValues.size() : 1;
    }
    public void setFriends(List<PostsData> data) {
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
                    final ArrayList<PostsData> newValues = new ArrayList<PostsData>();
                    for (int i = 0; i < count; i++) {
                        final PostsData value = inviteMessages.get(i);
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
                SearchTieZiAdapter.this.inviteMessages.clear();//清除原始数据
                SearchTieZiAdapter.this.inviteMessages.addAll((List<PostsData>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    SearchTieZiAdapter.this.notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        SearchTieZiAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    SearchTieZiAdapter.this.setFriends(mCopyInviteMessages);
                }
            }
        };
    }
    public int getItemViewType(int position) {
        if (mValues.size() <= 0) {
            return VIEW_TYPE;
        }
        return super.getItemViewType(position);
    }
    class empty extends RecyclerView.ViewHolder{
        public empty(View itemView) {
            super(itemView);
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void OnItemClickListener(View view, int position);
    }
    class SearchTieZiHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.circle_head)  RoundedImageView rivSearchPic;
        @BindView(R.id.circle_name)  TextView tvSearchName;
        @BindView(R.id.circle_time)  TextView tvSearchTime;
        @BindView(R.id.circle_Title)  TextView tvSearchTitle;
        @BindView(R.id.circle_content)  TextView tvSearchText;
        @BindView(R.id.circle_title)  TextView tvSearchFrom;
        @BindView(R.id.circle_look)  TextView tvSearchLook;
        @BindView(R.id.circle_zan)  TextView tvSearchPrise;
        @BindView(R.id.circle_reply)  TextView tvSearchMSG;
        public SearchTieZiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
