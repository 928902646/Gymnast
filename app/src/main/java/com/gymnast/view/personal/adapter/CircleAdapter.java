package com.gymnast.view.personal.adapter;

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
import com.gymnast.data.personal.CircleData;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.home.view.HomeSearchResultAcitivity;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by yf928 on 2016/7/20.
 */
public class CircleAdapter extends RecyclerView.Adapter implements Filterable {
    private final List<CircleData> mValues ;
    List<CircleData>  mCopyInviteMessages;
    List<CircleData>  inviteMessages;
    private Activity activity;
    private static final int VIEW_TYPE = -1;
    private OnItemClickListener onItemClickListener;
    public CircleAdapter( Activity activity,List<CircleData> mValues) {
        this.activity=activity;
        if(mValues.size()==0){
            this.mValues = new ArrayList<>();
        }else {
            this.mValues = mValues;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(VIEW_TYPE==viewType){
            view=inflater.inflate(R.layout.empty_view,parent,false);
            view.setLayoutParams(lp);
            return new Myempty(view);
        }
        view=inflater.inflate(R.layout.circle_item,parent,false);
        view.setLayoutParams(lp);
        return new ItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder =  (ItemViewHolder)holder;
            CircleData newcirclerdata =  mValues.get(position);
            PicassoUtil.handlePic(activity, StringUtil.isNullImage(newcirclerdata.getHeadImgUrl()),viewHolder.circle_head,320,320);
            viewHolder.circle_name.setText(newcirclerdata.getTitle());
            int CircleItem=  newcirclerdata.getCircleItemCount();
            viewHolder.number.setText("("+CircleItem+")");
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
    public void setFriends(List<CircleData> data) {
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
                    final ArrayList<CircleData> newValues = new ArrayList<CircleData>();
                    for (int i = 0; i < count; i++) {
                        final CircleData value = inviteMessages.get(i);
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
                CircleAdapter.this.inviteMessages.clear();//清除原始数据
                CircleAdapter.this.inviteMessages.addAll((List<CircleData>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    CircleAdapter.this.notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        CircleAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    CircleAdapter.this.setFriends(mCopyInviteMessages);
                }
            }
        };
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.circle_head)
        ImageView circle_head;
        @BindView(R.id.circle_name)
        TextView circle_name;
        @BindView(R.id.number)
        TextView number;
        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    public class Myempty extends RecyclerView.ViewHolder{
        private TextView textempty;
        public Myempty(View itemView) {
            super(itemView);
            textempty = (TextView) itemView.findViewById(R.id.text_empty);
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void OnItemClickListener(View view, int position);
    }
}
