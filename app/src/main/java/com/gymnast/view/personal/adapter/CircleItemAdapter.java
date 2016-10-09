package com.gymnast.view.personal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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

import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.PostsData;
import com.gymnast.utils.GlobalInfoUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.personal.activity.PersonalCircleActivity;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;
import com.gymnast.view.home.view.HomeSearchResultAcitivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yf928 on 2016/8/3.
 */
public class CircleItemAdapter extends RecyclerView.Adapter implements Filterable {
    private Activity activity;
    private static final int VIEW_TYPE = -1;
    private  final List<PostsData> mValues;
    List<PostsData> mCopyInviteMessages;
    List<PostsData> inviteMessages;
    private OnItemClickListener onItemClickListener;
    private SimpleDateFormat sdf =new SimpleDateFormat("MM月dd日 HH:mm");
    public CircleItemAdapter(Activity activity,List<PostsData> mValues) {
        this.activity = activity;
        if (mValues.size() == 0) {
            this.mValues=new ArrayList<>();
        } else {
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
            return new empty(view);
        }
        view=inflater.inflate(R.layout.item_circle_dynamic,parent,false);
        view.setLayoutParams(lp);
        return new Holder(view);
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof Holder){
            final Holder viewHolder = (Holder) holder;
            final PostsData postsData = mValues.get(position);
            PicassoUtil.handlePic(activity, PicUtil.getImageUrlDetail(activity, StringUtil.isNullAvatar(postsData.getAvatar()), 320, 320),viewHolder.circle_head,320,320);
            viewHolder.pack_name.setText(postsData.getNickname());
            viewHolder.circle_content.setText(postsData.getContent());
            viewHolder.circle_Title.setText(postsData.getTitle());
            viewHolder.circle_title.setText(postsData.getCircleTitle());
            viewHolder.circle_look.setText(postsData.getPageviews() + "人浏览");
            viewHolder.circle_zan.setText(postsData.getZanCount() + "");
            viewHolder.circle_time.setText(sdf.format(new Date(postsData.getCreateTime()))+"");
            viewHolder.circle_reply.setText(postsData.getMeetCount()+"");
            viewHolder.circle_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(activity, PersonalOtherHomeActivity.class);
                    i.putExtra("UserID",postsData.getCreateId());
                    activity.startActivity(i);
                }
            });
            viewHolder.circle_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(activity,PersonalCircleActivity.class);
                    i.putExtra("CircleId",postsData.getCircleId());
                    activity.startActivity(i);
                }
            });
            viewHolder.circle_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences share = activity.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                            String token=share.getString("Token","");
                            String accountId=share.getString("UserId","");
                            String uri= API.BASE_URL+"/v1/zan/add";
                            HashMap<String,String> params=new HashMap<String, String>();
                            params.put("token",token);
                            params.put("bodyId",postsData.getId()+"");
                            params.put("bodyType",5+"");
                            params.put("accountId",accountId+"");
                            try {
                                String result= PostUtil.sendPostMessage(uri,params);
                                JSONObject jsonObject=new JSONObject(result);
                                if(jsonObject.getString("successmsg").equals("添加成功")){
                                    Activity context=(Activity)activity;
                                    context.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String a=viewHolder.circle_zan.getText().toString();
                                            int zan=Integer.parseInt(a);
                                            int b=zan+1;
                                            viewHolder.circle_zan.setText(b+"");
                                            Drawable drawable =ContextCompat.getDrawable(activity,R.mipmap.like_pressed);
                                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                            viewHolder.circle_zan.setCompoundDrawables(drawable,null,null,null);
                                        }
                                    });
                                }else {
                                    Activity context=(Activity)activity;
                                    context.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String a=viewHolder.circle_zan.getText().toString();
                                            int zan=Integer.parseInt(a);
                                            int b=zan-1;
                                            viewHolder.circle_zan.setText(b+"");
                                            Drawable drawable =ContextCompat.getDrawable(activity,R.mipmap.like_normal);
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
                CircleItemAdapter.this.inviteMessages.clear();//清除原始数据
                CircleItemAdapter.this.inviteMessages.addAll((List<PostsData>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    CircleItemAdapter.this.notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        CircleItemAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    CircleItemAdapter.this.setFriends(mCopyInviteMessages);
                }
            }
        };
    }
    @Override
    public int getItemCount() {
        return mValues.size() > 0 ? mValues.size() : 1;
    }
    /**
     *  获取条目 View填充的类型
     *  默认返回0
     * @param position
     * @return
     */
    public int getItemViewType(int position) {
        if (mValues.size() <= 0) {
            return VIEW_TYPE;
        }
        return super.getItemViewType(position);
    }
    class Holder extends RecyclerView.ViewHolder{
        private final ImageView circle_head;
        private final TextView pack_name;
        private final TextView circle_time;
        private final TextView circle_look;
        private final TextView circle_reply;
        private final TextView circle_zan;
        private final TextView circle_content;
        private final TextView circle_title;
        private final TextView circle_Title;
        public Holder(View itemView) {
            super(itemView);
            circle_head=(ImageView)itemView.findViewById(R.id.circle_head);
            circle_content=(TextView)itemView.findViewById(R.id.circle_content);
            circle_title=(TextView)itemView.findViewById(R.id.circle_title);
            circle_Title=(TextView)itemView.findViewById(R.id.circle_Title);
            pack_name=(TextView)itemView.findViewById(R.id.circle_name);
            circle_time=(TextView)itemView.findViewById(R.id.circle_time);
            circle_look=(TextView)itemView.findViewById(R.id.circle_look);
            circle_zan=(TextView)itemView.findViewById(R.id.circle_zan);
            circle_reply=(TextView)itemView.findViewById(R.id.circle_reply);
        }
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
}
