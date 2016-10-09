package com.gymnast.view.personal.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.CircleMainData;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cymbi on 2016/9/21.
 */
public class CircleMainAdapter extends RecyclerView.Adapter {
    Context context;
    List<CircleMainData> mValues;
    private String id,token;

    public CircleMainAdapter(Context context, List<CircleMainData> mValues) {
        this.context = context;
        if(mValues.size()==0){
            this.mValues=new ArrayList<>();
        }else {
            this.mValues = mValues;
        }
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View viewlayout= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_circle_main,parent,false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewlayout.setLayoutParams(lp);
        return new CircleHolder(viewlayout);
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof CircleHolder){
            final CircleHolder viewholder=(CircleHolder)holder;
            final CircleMainData circleMainData= mValues.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(circleMainData.getAvatar()), 320, 320),viewholder.me_head,320,320);
            viewholder.tvNickname.setText(circleMainData.getNickname());
            final Integer MasterId=circleMainData.getCircleMasterId();
            final int userid=circleMainData.getUserId();
            if(MasterId!=null&&MasterId==userid){
                viewholder.circle_main.setVisibility(View.VISIBLE);
            }else {
            }
            viewholder.llSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LayoutInflater inflater= LayoutInflater.from(context);
                    View v=inflater.inflate(R.layout.setmaster_dialog, null);
                    final Dialog dialog = new Dialog(context,R.style.Dialog_Fullscreen);
                    LinearLayout llSetMaster = (LinearLayout) v.findViewById(R.id.llSetMaster);
                    TextView tvText = (TextView) v.findViewById(R.id.tvText);
                    tvText.setText("设置为圈主");
                    TextView cancel = (TextView) v.findViewById(R.id.cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    llSetMaster.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                    SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                                    id = share.getString("UserId", "");
                                    token = share.getString("Token", "");
                                    int circle_id= circleMainData.getCircleId();
                                    int master_id=circleMainData.getUserId();
                                    String uri= API.BASE_URL+"/v1/circle/setMaster";
                                    HashMap<String,String> params=new HashMap<String, String>();
                                    params.put("token",token);
                                    params.put("circle_id",circle_id+"");
                                    params.put("master_id",master_id+"");
                                    params.put("account_id",id);
                                    String result= PostUtil.sendPostMessage(uri,params);
                                        JSONObject jsonObject=new JSONObject(result);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            dialog.dismiss();
                            notifyDataSetChanged();
                        }
                    });
                    dialog.setContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    Window window = dialog.getWindow();
                    // 设置显示动画
                    window.setWindowAnimations(R.style.main_menu_animstyle);
                    Activity activity=(Activity)context;
                    WindowManager.LayoutParams wl = window.getAttributes();
                    wl.x = 0;
                    wl.y = activity.getWindowManager().getDefaultDisplay().getHeight();
                    // 以下这两句是为了保证按钮可以水平满屏
                    wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    // 设置显示位置
                    dialog.onWindowAttributesChanged(wl);
                    // 设置点击外围解散
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
    class CircleHolder extends RecyclerView.ViewHolder{
        private final ImageView me_head;
        private final TextView tvNickname;
        private final TextView circle_admin,circle_main;
        private final LinearLayout llSelect;

        public CircleHolder(View itemView) {
            super(itemView);
            me_head=(ImageView)itemView.findViewById(R.id.me_head);
            tvNickname=(TextView)itemView.findViewById(R.id.tvNickname);
            circle_admin=(TextView)itemView.findViewById(R.id.circle_admin);
            circle_main=(TextView)itemView.findViewById(R.id.circle_main);
            llSelect=(LinearLayout)itemView.findViewById(R.id.llSelect);
        }
    }
}
