package com.gymnast.view.personal.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
 * Created by Cymbi on 2016/9/22.
 */
public class CircleAdminAdapter extends RecyclerView.Adapter {
    Context context;
    List<CircleMainData> mValue;
    private String id,token;
    private Integer userid;

    public CircleAdminAdapter(Context context, List<CircleMainData> mValue) {
        this.context = context;
        if(mValue.size()==0){
            this.mValue=new ArrayList<>();
        }else {
            this.mValue = mValue;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.item_circle_main,parent,false);
        SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        id = share.getString("UserId", "");
        token = share.getString("Token", "");
        return new AdminHolder(view);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof AdminHolder){
            AdminHolder viewholder=(AdminHolder) holder;
            final CircleMainData circleMainData = mValue.get(position);
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(circleMainData.getAvatar()), 320, 320),viewholder.me_head,320,320);
            viewholder.tvNickname.setText(circleMainData.getNickname());
            List<String> AdminIds=circleMainData.getAdminIds();
            viewholder.circle_admin.setVisibility(View.GONE);
            for(int i=0;i<AdminIds.size();i++){
                String str = AdminIds.get(i);
                try{ int admin=Integer.parseInt(str);
                    int userId=circleMainData.getUserId();
                    if(admin == userId){
                        viewholder.circle_admin.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){e.printStackTrace();}

            }
            viewholder.llSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    LayoutInflater inflater= LayoutInflater.from(context);
                    View v=inflater.inflate(R.layout.setmaster_dialog, null);
                    final Dialog dialog = new Dialog(context,R.style.Dialog_Fullscreen);
                    LinearLayout llSetMaster = (LinearLayout) v.findViewById(R.id.llSetMaster);
                    TextView tvText = (TextView) v.findViewById(R.id.tvText);
                    tvText.setText("设置为管理员");
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
                                    int circle_id= circleMainData.getCircleId();
                                    String adminIds=String.valueOf(userid);
                                    String uri= API.BASE_URL+"/v1/circle/setAdminIds";
                                    HashMap<String,String> params=new HashMap<>();
                                    params.put("token",token);
                                    params.put("accountId",id);
                                    //   params.put("adminIds",AdminIds+","+adminIds);
                                    // params.put("adminIds",userId+"");
                                    params.put("circleId",circle_id+"");
                                    String result= PostUtil.sendPostMessage(uri,params);
                                    Activity activity=(Activity)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context,"管理员设置成功",Toast.LENGTH_SHORT).show();
                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                            }){}.start();
                            dialog.dismiss();
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
        return mValue.size();
    }

    class AdminHolder extends RecyclerView.ViewHolder{
        private final ImageView me_head;
        private final TextView tvNickname;
        private final TextView circle_admin;
        private final LinearLayout llSelect;
        public AdminHolder(View itemView) {
            super(itemView);
            me_head=(ImageView)itemView.findViewById(R.id.me_head);
            tvNickname=(TextView)itemView.findViewById(R.id.tvNickname);
            circle_admin=(TextView)itemView.findViewById(R.id.circle_admin);
            // circle_main=(TextView)itemView.findViewById(R.id.circle_main);
            llSelect=(LinearLayout)itemView.findViewById(R.id.llSelect);
        }
    }
}
