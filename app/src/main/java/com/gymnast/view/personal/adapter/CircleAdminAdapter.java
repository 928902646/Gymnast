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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cymbi on 2016/9/22.
 */
public class CircleAdminAdapter extends RecyclerView.Adapter {
    Context context;
    List<CircleMainData> mValue;
    private String id,token;
    int tag=-1;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
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
                        tag=position;
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
                                String adminIds;
                                @Override
                                public void run() {
                                    try {
                                        int circle_id= circleMainData.getCircleId();
                                        String admin=String.valueOf(circleMainData.getUserId());
                                        String uri= API.BASE_URL+"/v1/circle/setAdminIds";
                                        HashMap<String,String> params=new HashMap<>();
                                        params.put("token",token);
                                        params.put("accountId",id);
                                        List<String> AdminIds=circleMainData.getAdminIds();
                                        Log.e("AdminIds",AdminIds+"");
                                        if(AdminIds!=null&&AdminIds.size()!=0){
                                            String str= StringUtil.listToString(AdminIds);
                                            adminIds=str+","+admin;
                                            params.put("adminIds",adminIds);
                                        }else {
                                            params.put("adminIds",admin);
                                        }
                                        params.put("circleId",circle_id+"");
                                        String result=PostUtil.sendPostMessage(uri,params);

                                        JSONObject obj=new JSONObject(result);
                                        if(obj.getInt("state")==200){
                                            String url=API.BASE_URL+"/v1/circle/getOne/";
                                            HashMap<String,String> par=new HashMap<String, String>();
                                            par.put("circleId",circle_id+"");
                                            par.put("accountId",id+"");
                                            String res= GetUtil.sendGetMessage(url,par);
                                            JSONObject object=new JSONObject(res);
                                            JSONObject data = object.getJSONObject("data");
                                            JSONObject circle=data.getJSONObject("circle");
                                            String circleAdminIds=circle.getString("adminIds");
                                            List<String>  adminlist = Arrays.asList(circleAdminIds.split(","));
                                            circleMainData.setAdminIds(adminlist);

                                            Activity activity=(Activity)context;
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }){}.start();
                            dialog.dismiss();
                        }
                    });
                    notifyDataSetChanged();
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
