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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.home.view.HomeSearchResultAcitivity;
import com.gymnast.data.user.SearchUserEntity;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by 永不言败 on 2016/8/9.
 */
public class SearchUserAdapter extends RecyclerView.Adapter implements Filterable {
    Context context;
    List<SearchUserEntity> entities;
    List<SearchUserEntity> mCopyInviteMessages;
    List<SearchUserEntity> inviteMessages;
    private static final int VIEW_TYPE = -1;
    public SearchUserAdapter(Context context, List<SearchUserEntity> entities) {
        this.context = context;
        if (entities.size()!=0){
            this.entities = entities;
        }else {
            this.entities =new ArrayList<>();
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.item_found_rv, null);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemview.setLayoutParams(lp1);
        if(VIEW_TYPE==viewType){
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            View view=LayoutInflater.from(context).inflate(R.layout.empty_view, parent, false);
            view.setLayoutParams(lp2);
            return new empty(view);
        }
        return new FoundViewHolder(itemview);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof FoundViewHolder) {
            final SharedPreferences share= context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            final String UserId = share.getString("UserId","");
            final String token = share.getString("Token","");
            final FoundViewHolder viewHolder = (FoundViewHolder) holder;
            final SearchUserEntity entity = entities.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(entity.getPhotoUrl()), 72, 72),viewHolder.rivPhoto,72,72);
            viewHolder.tvLiveName.setText(entity.getName());
            String Authinfo=entity.getAuthinfo();
            if(Authinfo!=null&&!Authinfo.equals("null")){
                viewHolder.tvType.setText(entity.getAuthinfo());
            }else {viewHolder.tvType.setText("");}
            viewHolder.rivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (entities.size() != 0) {
                        Intent intent = new Intent(context, PersonalOtherHomeActivity.class);
                        intent.putExtra("UserID", entity.getId());
                        context.startActivity(intent);
                    }
                }
            });
          final int toId=entity.getId();
            viewHolder.tvConcern.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String uri= API.BASE_URL+"/v1/attention/user";
                            HashMap<String,String> params=new HashMap<String, String>();
                            params.put("token",token);
                            params.put("fromId",UserId);
                            params.put("toId",toId+"");
                            String result= GetUtil.sendGetMessage(uri,params);
                            try {
                                JSONObject object=new JSONObject(result);
                                if (object.getInt("state")==200){
                                    final Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            viewHolder.tvConcern.setText("已关注");
                                            viewHolder.tvConcern.setBackgroundResource(R.drawable.border_radius_concern);
                                            viewHolder.tvConcern.setTextColor(context.getResources().getColor(R.color.hot_info_circle_content_color));
                                            Toast.makeText(context,"点击成功",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context,"已关注过了，请勿重复关注",Toast.LENGTH_SHORT).show();
                                        }
                                    });}
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Activity activity=(Activity)context;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"已关注过了，请勿重复关注",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return entities.size() > 0 ? entities.size() : 1;
    }
    public void setFriends(List<SearchUserEntity> data) {
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
                    entities.clear();
                    entities.addAll(mCopyInviteMessages);
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
                    final ArrayList<SearchUserEntity> newValues = new ArrayList<SearchUserEntity>();
                    for (int i = 0; i < count; i++) {
                        final SearchUserEntity value = inviteMessages.get(i);
                        String username = value.getName();
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
                SearchUserAdapter.this.inviteMessages.clear();//清除原始数据
                SearchUserAdapter.this.inviteMessages.addAll((List<SearchUserEntity>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    SearchUserAdapter.this.notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        SearchUserAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    SearchUserAdapter.this.setFriends(mCopyInviteMessages);
                }
            }
        };
    }
    @Override
    public int getItemViewType(int position) {
        if (entities.size() <= 0) {
            return VIEW_TYPE;
        }
        return super.getItemViewType(position);
    }
    class empty extends RecyclerView.ViewHolder{
        public empty(View itemView) {
            super(itemView);
        }
    }
    class FoundViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView rivPhoto;
        TextView tvLiveName,tvType,tvConcern;
        public FoundViewHolder(View itemView) {
            super(itemView);
            rivPhoto= (RoundedImageView) itemView.findViewById(R.id.rivPhoto);
            tvLiveName= (TextView) itemView.findViewById(R.id.tvLiveName);
            tvType= (TextView) itemView.findViewById(R.id.tvType);
            tvConcern= (TextView) itemView.findViewById(R.id.tvConcern);
        }
    }
}
