package com.gymnast.view.personal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.gymnast.data.net.API;
import com.gymnast.data.personal.DynamicData;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.home.view.HomeSearchResultAcitivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cymbi on 2016/8/31.
 */
public class DynamicAdapter extends RecyclerView.Adapter implements Filterable {
    private Context context;
    private final List<DynamicData> mValues;
    List<DynamicData> mCopyInviteMessages;
    List<DynamicData> inviteMessages;
    private int USER=1;
    private int ACTIVITY=2;
    private int STAR=3;
    private int CIRCLE=4;
    private static final int VIEW_TYPE = -1;
    private OnItemClickListener onItemClickListener;
    private SimpleDateFormat sdf =new SimpleDateFormat("MM月-dd日 HH:mm");

    public DynamicAdapter(Context context, List<DynamicData> mValues) {
        this.context = context;
        if (mValues.size()==0){
            this.mValues=new ArrayList<>();
        }else {
            this.mValues=mValues;
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (mValues.size() <= 0) {
            return VIEW_TYPE;
        }
        DynamicData data=mValues.get(position);
        int type=data.getType();
        if(type==1){return USER;}
        else if (type==2){return ACTIVITY;}
        else if (type==3){return STAR;}
        return CIRCLE;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater=  LayoutInflater.from(parent.getContext());
        View view;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(USER==viewType){
            view= mInflater.inflate(R.layout.item_user_dynamic,null);
            view.setLayoutParams(lp);
            return new UserViewHolder(view);
        }else if(ACTIVITY==viewType){
            view= mInflater.inflate(R.layout.item_activity_dynamic,null);
            view.setLayoutParams(lp);
            return new ActivityViewHolder(view);
        }else if(STAR==viewType){
            view= mInflater.inflate(R.layout.item_star_dynamic,null);
            view.setLayoutParams(lp);
            return new StarViewHolder(view);
        }else if(CIRCLE==viewType){
            view= mInflater.inflate(R.layout.item_circle_dynamic,null);
            view.setLayoutParams(lp);
            return new CircleViewHolder(view);
        }
        view=mInflater.inflate(R.layout.empty_view,parent,false);
        view.setLayoutParams(lp);
        return new empty(view);
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        final String token=share.getString("Token","");
        if (holder instanceof UserViewHolder){
            final UserViewHolder viewHolder = (UserViewHolder)holder;
            final DynamicData dynamicData=  mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(dynamicData.getAvatar()), 320, 320),viewHolder.user_head,320,320);
            viewHolder.user_content.setText("     "+dynamicData.getTopicContent());
            viewHolder.user_zan.setText(dynamicData.getZanCounts()+"");
            viewHolder.user_name.setText(dynamicData.getNickName());
            viewHolder.user_msg.setText(dynamicData.getCommentCounts()+"");
            viewHolder.user_time.setText(sdf.format(new Date(dynamicData.getCreateTime()))+"");
            final int bodyType=dynamicData.getType();
            final int bodyId=dynamicData.getId();
            final int accountId=dynamicData.getUserId();
            ArrayList<String> urls=dynamicData.getImgUrl();
            GridAdapter adapter=new GridAdapter(context,urls);
            viewHolder.gridview.setAdapter(adapter);
            viewHolder.gridview.invalidate();
            viewHolder.user_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String uri= API.BASE_URL+"/v1/zan/add";
                            HashMap<String,String>params=new HashMap<String, String>();
                            params.put("token",token);
                            params.put("bodyId",bodyId+"");
                            params.put("bodyType",bodyType+"");
                            params.put("accountId",accountId+"");
                            String result= PostUtil.sendPostMessage(uri,params);
                            try {
                                JSONObject object=new JSONObject(result);
                                if(object.getInt("state")==200){
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            viewHolder.user_zan.setText(dynamicData.getZanCounts()+"");
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
        }else if (holder instanceof ActivityViewHolder){
            final ActivityViewHolder viewHolder = (ActivityViewHolder)holder;
            final DynamicData dynamicData=  mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(dynamicData.getImgUrl().get(0)), 320, 320),viewHolder.activity_imgUrl,320,320);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(dynamicData.getAvatar()), 320, 320),viewHolder.activity_head,320,320);
            viewHolder.activity_name.setText(dynamicData.getNickName());
            viewHolder.activity_title.setText(dynamicData.getTopicTitle());
            viewHolder.activity_content.setText(dynamicData.getTopicContent());
            viewHolder.activity_time.setText(sdf.format(new Date(dynamicData.getCreateTime()))+"");
            viewHolder.activity_comment.setText(dynamicData.getCommentCounts()+"");
            viewHolder.activity_zan.setText(dynamicData.getZanCounts()+"");
            final int bodyType=dynamicData.getType();
            final int bodyId=dynamicData.getId();
            final int accountId=dynamicData.getUserId();
            viewHolder.activity_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String uri= API.BASE_URL+"/v1/zan/add";
                            HashMap<String,String>params=new HashMap<String, String>();
                            params.put("token",token);
                            params.put("bodyId",bodyId+"");
                            params.put("bodyType",bodyType+"");
                            params.put("accountId",accountId+"");
                            String result= PostUtil.sendPostMessage(uri,params);
                            try {
                                JSONObject object=new JSONObject(result);
                                if(object.getInt("state")==200){
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            viewHolder.activity_zan.setText(dynamicData.getZanCounts()+"");
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
        }else if (holder instanceof StarViewHolder){
            final StarViewHolder viewHolder = (StarViewHolder)holder;
            final DynamicData dynamicData=  mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(dynamicData.getAvatar()), 320, 320),viewHolder.star_head,320,320);
            viewHolder.star_content.setText("     "+dynamicData.getTopicContent());
            viewHolder.star_look.setText(dynamicData.getPageviews()+"人浏览");
            viewHolder.star_name.setText(dynamicData.getNickName());
            viewHolder.star_type.setText(dynamicData.getAuthInfo());
            viewHolder.star_msg.setText(dynamicData.getCommentCounts()+"");
            viewHolder.star_time.setText(sdf.format(new Date(dynamicData.getCreateTime()))+"");
            viewHolder.star_zan.setText(dynamicData.getZanCounts()+"");
            ArrayList<String> urls=dynamicData.getImgUrl();
            GridAdapter adapter=new GridAdapter(context,urls);
            viewHolder.gridview.setAdapter(adapter);
            viewHolder.gridview.invalidate();
            final int bodyType=dynamicData.getType();
            final int bodyId=dynamicData.getId();
            final int accountId=dynamicData.getUserId();
            viewHolder.star_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String uri= API.BASE_URL+"/v1/zan/add";
                            HashMap<String,String>params=new HashMap<String, String>();
                            params.put("token",token);
                            params.put("bodyId",bodyId+"");
                            params.put("bodyType",bodyType+"");
                            params.put("accountId",accountId+"");
                            String result= PostUtil.sendPostMessage(uri,params);
                            try {
                                JSONObject object=new JSONObject(result);
                                if(object.getInt("state")==200){
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            viewHolder.star_zan.setText(dynamicData.getZanCounts()+"");
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
        }else if (holder instanceof CircleViewHolder){
            final CircleViewHolder viewHolder = (CircleViewHolder)holder;
            final DynamicData dynamicData=  mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(dynamicData.getAvatar()), 320, 320),viewHolder.circle_head,320,320);
            viewHolder.circle_content.setText(dynamicData.getTopicContent());
            viewHolder.circle_zan.setText(dynamicData.getZanCounts()+"");
            viewHolder.circle_name.setText(dynamicData.getNickName());
            viewHolder.circle_comment.setText(dynamicData.getCommentCounts()+"");
            viewHolder.circle_time.setText(sdf.format(new Date(dynamicData.getCreateTime()))+"");
            viewHolder.circle_title.setText(dynamicData.getTopicTitle());
            final int bodyType=dynamicData.getType();
            final int bodyId=dynamicData.getId();
            final int accountId=dynamicData.getUserId();
            viewHolder.circle_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String uri= API.BASE_URL+"/v1/zan/add";
                            HashMap<String,String>params=new HashMap<String, String>();
                            params.put("token",token);
                            params.put("bodyId",bodyId+"");
                            params.put("bodyType",bodyType+"");
                            params.put("accountId",accountId+"");
                            String result= PostUtil.sendPostMessage(uri,params);
                            try {
                                JSONObject object=new JSONObject(result);
                                if(object.getInt("state")==200){
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            viewHolder.circle_zan.setText(dynamicData.getZanCounts()+"");
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
        return mValues.size() > 0 ? mValues.size() : 1;
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
                        String username = value.getTopicTitle();
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
                DynamicAdapter.this.inviteMessages.clear();//清除原始数据
                DynamicAdapter.this.inviteMessages.addAll((List<DynamicData>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    DynamicAdapter.this.notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        DynamicAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    DynamicAdapter.this.setFriends(mCopyInviteMessages);
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
    class UserViewHolder extends RecyclerView.ViewHolder{
        private final ImageView user_head;
        private final TextView user_name,user_time,user_content,user_look,user_zan,user_msg,video_number;
        private final GridView gridview;
        public UserViewHolder(View itemView) {
            super(itemView);
            user_head= (ImageView) itemView.findViewById(R.id.user_head);
            user_name= (TextView) itemView.findViewById(R.id.user_name);
            user_content= (TextView) itemView.findViewById(R.id.user_content);
            user_time= (TextView) itemView.findViewById(R.id.user_time);
            user_look= (TextView) itemView.findViewById(R.id.user_look);
            user_zan= (TextView) itemView.findViewById(R.id.user_zan);
            user_msg= (TextView) itemView.findViewById(R.id.user_msg);
            video_number= (TextView) itemView.findViewById(R.id.video_number);
            gridview= (GridView) itemView.findViewById(R.id.gridview);

        }
    }
    class CircleViewHolder extends RecyclerView.ViewHolder{
        private final ImageView circle_head;
        private final TextView circle_name,circle_content,circle_time,circle_look,circle_zan,circle_comment,circle_title;
        public CircleViewHolder(View itemView) {
            super(itemView);
            circle_head=(ImageView)itemView.findViewById(R.id.circle_head);
            circle_name= (TextView) itemView.findViewById(R.id.circle_name);
            circle_title= (TextView) itemView.findViewById(R.id.circle_title);
            circle_content= (TextView) itemView.findViewById(R.id.circle_content);
            circle_time= (TextView) itemView.findViewById(R.id.circle_time);
            circle_look= (TextView) itemView.findViewById(R.id.circle_look);
            circle_zan= (TextView) itemView.findViewById(R.id.circle_zan);
            circle_comment= (TextView) itemView.findViewById(R.id.circle_reply);
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
    class StarViewHolder extends RecyclerView.ViewHolder{
        private final ImageView star_head;
        private final TextView star_name,star_content,star_time,star_look,star_zan,star_msg,video_number,star_type;
        private final GridView gridview;
        public StarViewHolder(View itemView) {
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
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void OnItemClickListener(View view, int position);
    }
}

