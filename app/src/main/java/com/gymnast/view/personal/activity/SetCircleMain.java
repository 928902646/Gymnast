package com.gymnast.view.personal.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
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
import com.gymnast.utils.PostUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.personal.adapter.CircleMainAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**CESHIFD
 * Created by Cymbi on 2016/9/21.
 */
public class SetCircleMain extends ImmersiveActivity {
    private List<CircleMainData> list=new ArrayList<>();
    private RecyclerView recyclerview;
    private String id,token,master_id;
    private int CircleId;
    private CircleMainAdapter adapter;
    private int circleMasterId;
    public static final int HANDLER_DATA=1;
    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_DATA:
                    adapter= new CircleMainAdapter(SetCircleMain.this,list);
                    recyclerview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setcircle_main);
        getInfo();
        setView();
        setData();
    }
    private void setData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri= API.BASE_URL+"/v1/cern/findParams";
                HashMap<String,String> params=new HashMap<String, String>();
                params.put("token",token);
                params.put("concernId",CircleId+"");
                params.put("concernType",4+"");
                String result= PostUtil.sendPostMessage(uri,params);
                try {
                    JSONObject object=new JSONObject(result);
                    JSONArray data= object.getJSONArray("data");
                    for(int i=0;i<data.length();i++){
                        CircleMainData circleMainData= new CircleMainData();
                        JSONObject obj= data.getJSONObject(i);
                        String nickName= obj.getString("nickName");
                        int userId= obj.getInt("userId");
                        String avatar= obj.getString("avatar");
                        circleMainData.setAvatar(avatar);
                        circleMainData.setNickname(nickName);
                        circleMainData.setUserId(userId);
                        circleMainData.setCircleId(CircleId);
                        circleMainData.setCircleMasterId(circleMasterId);
                        list.add(circleMainData);
                    }
                    handler.sendEmptyMessage(HANDLER_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void getInfo() {
        SharedPreferences share = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        id = share.getString("UserId", "");
        token = share.getString("Token", "");
        CircleId=getIntent().getIntExtra("CircleId",0);
        String str=getIntent().getStringExtra("circleMasterId");
        circleMasterId=Integer.parseInt(str);
    }
    private void setView() {
        ImageView  back= (ImageView) findViewById(R.id.ivSettingBack);
        TextView tvTitle=(TextView)findViewById(R.id.tvTitle);
        tvTitle.setText("设置圈主");
        recyclerview= (RecyclerView) findViewById(R.id.recyclerview);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(list.size()!=0){
            list.clear();
            setData();
        }else {}

    }
}
