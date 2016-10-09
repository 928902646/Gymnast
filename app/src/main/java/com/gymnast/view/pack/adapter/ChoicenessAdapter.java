package com.gymnast.view.pack.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.pack.ChoicenessData;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.personal.activity.PersonalCircleActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cymbi on 2016/9/6.
 */
public class ChoicenessAdapter extends RecyclerView.Adapter {
    Context context;
    private final List<ChoicenessData> mValues;
    public ChoicenessAdapter( Context context,List<ChoicenessData> mValues) {
        this.context = context;
        if (mValues.size()==0){
            this.mValues=new ArrayList<>();
        }else {
            this.mValues=mValues;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater= LayoutInflater.from(parent.getContext());
        view= mInflater.inflate(R.layout.pack_auslese_item,null);
        return new Holder(view);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Holder viewHolder = (Holder)holder;
        final ChoicenessData choicenessData    =  mValues.get(position);
        PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(choicenessData.getHeadImgUrl()), 320, 320),viewHolder.auslese_head,320,320);
        viewHolder.tv_title.setText(choicenessData.getTitle());
        viewHolder.tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, PersonalCircleActivity.class);
                i.putExtra("CircleId",choicenessData.getId());
                context.startActivity(i);
            }
        });
        viewHolder.tv_number.setText("共计"+choicenessData.getCircleItemCount()+"个帖子");
        viewHolder.concern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                                        String token=share.getString("Token","");
                                        String id = share.getString("UserId","");
                                        String url= API.BASE_URL+"/v1/cern/add";
                                        Map<String,String> parmas2=new HashMap<String, String>();
                                        parmas2.put("token",token);
                                        parmas2.put("concernId",choicenessData.getId()+"");
                                        parmas2.put("accountId",id);
                                        parmas2.put("concernType",4+"");
                                        String result2= PostUtil.sendPostMessage(url,parmas2);
                                        try {
                                            JSONObject object=new JSONObject(result2);
                                            if(object.getInt("state")==30028){
                                                Activity activity=(Activity)context;
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(context,"已关注过了",Toast.LENGTH_SHORT).show();
                                                        viewHolder.concern.setText("已关注");
                                                        viewHolder.concern.setBackgroundColor(context.getResources().getColor(R.color.background));
                                                    }
                                                });
                                            }
                                            if(object.getInt("state")==200){
                                                Activity activity=(Activity)context;
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        viewHolder.concern.setText("已关注");
                                                        viewHolder.concern.setBackgroundColor(context.getResources().getColor(R.color.background));
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
    @Override
    public int getItemCount() {
        return mValues.size();
    }
    class Holder extends RecyclerView.ViewHolder{
        private final ImageView auslese_head;
        private final TextView tv_title;
        private final TextView tv_number;
        private final TextView concern;
        private final RelativeLayout re_layout;
        public Holder(View itemView) {
            super(itemView);
            auslese_head=(ImageView) itemView.findViewById(R.id.auslese_head);
            tv_title=(TextView) itemView.findViewById(R.id.tv_title);
            tv_number=(TextView) itemView.findViewById(R.id.tv_number);
            concern=(TextView) itemView.findViewById(R.id.concern);
            re_layout=(RelativeLayout) itemView.findViewById(R.id.re_layout);
        }
    }
}
