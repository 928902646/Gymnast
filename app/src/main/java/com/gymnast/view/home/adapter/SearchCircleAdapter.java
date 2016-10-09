package com.gymnast.view.home.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
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
import com.gymnast.data.personal.CircleData;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.view.home.view.HomeSearchResultAcitivity;
import com.gymnast.view.personal.activity.PersonalCircleActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zzqybyb19860112 on 2016/9/5.
 */
public class SearchCircleAdapter extends RecyclerView.Adapter implements Filterable {
    private final List<CircleData> mValues ;
    List<CircleData>  mCopyInviteMessages;
    List<CircleData>  inviteMessages;
    private Context context;
    private static final int VIEW_TYPE = -1;
    public SearchCircleAdapter( Context context,List<CircleData> mValues) {
        this.context=context;
        if(mValues.size()==0){
            this.mValues = new ArrayList<>();
        }else {
            this.mValues = mValues;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.item_circle_rv, null);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemview.setLayoutParams(lp1);
        if(VIEW_TYPE==viewType){
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            View view=LayoutInflater.from(context).inflate(R.layout.empty_view, parent, false);
            view.setLayoutParams(lp2);
            return new empty(view);
        }
        return new CircleItemViewHolder(itemview);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SharedPreferences share= context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        final String UserId = share.getString("UserId", "");
        final String token = share.getString("Token","");
        if (viewHolder instanceof CircleItemViewHolder) {
            final CircleItemViewHolder holder= (CircleItemViewHolder) viewHolder;
            final CircleData data =  mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, data.getHeadImgUrl(), 320, 320), holder.ivCirclePhoto, 320, 320);
            holder.tvCircleName.setText(data.getTitle());
            holder.tvCircleName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mValues.size()!=0) {
                        Intent i = new Intent(context, PersonalCircleActivity.class);
                        i.putExtra("CircleId", data.getId());
                        context.startActivity(i);
                    }
                }
            });
            holder.tvCircleType.setText(data.getDetails());
            holder.tvCircleConcern.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String uri=API.BASE_URL+"/v1/cern/add";
                            Map<String,String> params=new HashMap<String, String>();
                            params.put("token",token);
                            params.put("concernId",data.getId()+"");
                            params.put("accountId",UserId);
                            params.put("concernType",4+"");
                            String result= PostUtil.sendPostMessage(uri,params);
                            try {
                                JSONObject object=new JSONObject(result);
                                if(object.getInt("state")==200){
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context,"关注成功",Toast.LENGTH_SHORT).show();
                                            holder.tvCircleConcern.setText("已关注");
                                            holder.tvCircleConcern.setBackgroundColor(context.getResources().getColor(R.color.background));
                                        }
                                    });
                                }else {
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context,"已关注过了，请勿重复关注",Toast.LENGTH_SHORT).show();
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
                SearchCircleAdapter.this.inviteMessages.clear();//清除原始数据
                SearchCircleAdapter.this.inviteMessages.addAll((List<CircleData>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    SearchCircleAdapter.this.notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        SearchCircleAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    SearchCircleAdapter.this.setFriends(mCopyInviteMessages);
                }
            }
        };
    }
    class empty extends RecyclerView.ViewHolder{
        public empty(View itemView) {
            super(itemView);
        }
    }
    public class CircleItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivCirclePhoto)  ImageView ivCirclePhoto;
        @BindView(R.id.tvCircleName)  TextView tvCircleName;
        @BindView(R.id.tvCircleType)  TextView tvCircleType;
        @BindView(R.id.tvCircleConcern)  TextView tvCircleConcern;
        public CircleItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
