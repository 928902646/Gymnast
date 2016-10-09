package com.gymnast.view.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.data.personal.DynamicData;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.view.home.view.HomeSearchResultAcitivity;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;
import com.gymnast.view.personal.adapter.GridAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zzqybyb19860112 on 2016/9/6.
 */
public class SearchDynamicAdapter extends RecyclerView.Adapter implements Filterable {
    private Context context;
    private final List<DynamicData> mValues;
    List<DynamicData> mCopyInviteMessages;
    List<DynamicData> inviteMessages;
    private static final int VIEW_TYPE = -1;
    private OnItemClickListener onItemClickListener;
    private SimpleDateFormat sdf =new SimpleDateFormat("MM月dd日 HH:mm");
    public SearchDynamicAdapter(Context context, List<DynamicData> mValues) {
        this.context = context;
        if (mValues.size()==0){
            this.mValues=new ArrayList<>();
        }else {
            this.mValues=mValues;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.item_user_dynamic, null);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemview.setLayoutParams(lp1);
        if(VIEW_TYPE==viewType){
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            View view=LayoutInflater.from(context).inflate(R.layout.empty_view, parent, false);
            view.setLayoutParams(lp2);
            return new empty(view);
        }
        return new UserViewHolder(itemview);
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder){
            UserViewHolder viewHolder = (UserViewHolder)holder;
            final DynamicData dynamicData=  mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, dynamicData.getAvatar(), 320, 320), viewHolder.user_head, 320, 320);
            viewHolder.user_name.setText(dynamicData.getNickName());
            viewHolder.user_time.setText(sdf.format(new Date(dynamicData.getCreateTime()))+"");
            viewHolder.user_content.setText("     "+dynamicData.getTopicContent());
            viewHolder.user_look.setText(+dynamicData.getPageviews()+"次浏览");
            viewHolder.user_zan.setText(dynamicData.getZanCounts()+ "");
            viewHolder.user_msg.setText(dynamicData.getCommentCounts()+ "");
            ArrayList<String> urls=dynamicData.getImgUrl();
            GridAdapter adapter=new GridAdapter(context,urls);
            viewHolder.gridview.setAdapter(adapter);
            viewHolder.gridview.invalidate();
            viewHolder.user_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mValues.size()!=0){
                        Intent i=new Intent(context, PersonalOtherHomeActivity.class);
                        i.putExtra("UserID",dynamicData.getUserId());
                        context.startActivity(i);
                    }
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
        return mValues.size() > 0 ? mValues.size() : 1;
    }
    @Override
    public int getItemViewType(int position) {
        if (mValues.size() <= 0) {
            return VIEW_TYPE;
        }
    return super.getItemViewType(position);
    }
    public void setFriends(List<DynamicData> data) {
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
                    final ArrayList<DynamicData> newValues = new ArrayList<DynamicData>();
                    for (int i = 0; i < count; i++) {
                        final DynamicData value = inviteMessages.get(i);
                        String username = value.getTopicContent();
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
                SearchDynamicAdapter.this.inviteMessages.clear();//清除原始数据
                SearchDynamicAdapter.this.inviteMessages.addAll((List<DynamicData>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    SearchDynamicAdapter.this.notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        SearchDynamicAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    SearchDynamicAdapter.this.setFriends(mCopyInviteMessages);
                }
            }
        };
    }
    class empty extends RecyclerView.ViewHolder{
        public empty(View itemView) {
            super(itemView);
        }
    }
    class UserViewHolder extends RecyclerView.ViewHolder{
        private final ImageView user_head;
        private final TextView user_name,user_time,user_content,user_look,user_zan,user_msg;
        private final GridView gridview;
        public UserViewHolder(View itemView) {
            super(itemView);
            user_head= (ImageView) itemView.findViewById(R.id.user_head);
            user_name= (TextView) itemView.findViewById(R.id.user_name);
            user_time= (TextView) itemView.findViewById(R.id.user_time);
            user_content= (TextView) itemView.findViewById(R.id.user_content);//详细介绍
            user_look= (TextView) itemView.findViewById(R.id.user_look);//多少人浏览
            user_zan= (TextView) itemView.findViewById(R.id.user_zan);//点赞
            user_msg= (TextView) itemView.findViewById(R.id.user_msg);//回复
            gridview= (GridView) itemView.findViewById(R.id.gridview);
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void OnItemClickListener(View view, int position);
    }
}
