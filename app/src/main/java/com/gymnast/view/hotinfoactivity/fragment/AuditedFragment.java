package com.gymnast.view.hotinfoactivity.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gymnast.R;
import com.gymnast.data.hotinfo.AuditData;
import com.gymnast.data.net.API;
import com.gymnast.utils.PostUtil;
import com.gymnast.view.hotinfoactivity.adapter.AuditMainAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Cymbi on 2016/9/12.
 */
public class AuditedFragment extends Fragment {
    private View view;
    private ImageView ivAvatar;
    private TextView tvUserName,tvPass,tvUser,tvPhone;
    private RecyclerView rvAudit;
    private RecyclerView mainRecyclerview;
    private int activityId;
    private SharedPreferences share;
    private String token,key,value,realname="",sex="",age="",phone="",address="",id="";
    private List<AuditData> MainList=new ArrayList<>();
    public static final  int HANDLER_UPDATA=1;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_UPDATA:
                    AuditMainAdapter adapter =  new AuditMainAdapter(getActivity(),MainList);
                    mainRecyclerview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.audit_recycleview,container,false);
        getinfo();
        setview();
        getdata();
        return  view;
    }

    private void getdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri= API.BASE_URL+"/v1/activity/memberPeople";
                Map<String,String> params=new HashMap<>();
                params.put("token",token);
                params.put("activityId",activityId+"");
                Log.i("tag","token====="+token);
                Log.i("tag","activityId====="+activityId);
                String result= PostUtil.sendPostMessage(uri,params);
                try {
                    Log.i("tag","result-----"+result);
                    JSONObject object = new JSONObject(result);
                    JSONArray data = object.getJSONArray("data");
                    for(int i=0;i<data.length();i++){
                        AuditData audit= new AuditData();
                        JSONObject obj = data.getJSONObject(i);
                        String userNick= obj.getString("userNick");
                        String avatar= obj.getString("avatar");
                        int examineType = obj.getInt("examineType");
                        String memberInfo = obj.getString("memberInfo");
                        JSONObject json=new JSONObject(memberInfo);
                        Iterator<String> iter = json.keys();
                        while (iter.hasNext()) {
                            key = iter.next();
                            value = json.getString(key);
                            if (key.equals("手机号码")||key.equals("手机号")) {
                                phone = value;
                                continue;
                            }else
                            if (key.equals("姓名")) {
                                realname = value;
                                continue;
                            }else
                            if (key.equals("性别")) {
                                sex = value;
                                continue;
                            }
                            else
                            if (key.equals("年龄")) {
                                age = value;
                                continue;
                            }
                            else
                            if (key.equals("地址")) {
                                address = value;
                                continue;
                            }
                            else
                            if (key.equals("身份证")) {
                                id = value;
                                continue;
                            }else {

                            }
                        }
                        audit.setPhone(phone);
                        audit.setRealname(realname);
                        audit.setSex(sex);
                        audit.setAge(age);
                        audit.setAddress(address);
                        audit.setId(id);
                        audit.setAvatar(avatar);
                        audit.setUsername(userNick);
                        if(examineType==1){
                            MainList.add(audit);
                        }else {}
                    }
                    handler.sendEmptyMessage(HANDLER_UPDATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setview() {
        mainRecyclerview=(RecyclerView)view.findViewById(R.id.recyclerview);
    }
    private void getinfo() {
        activityId=getActivity().getIntent().getIntExtra("activityId",0);
        share= getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token","");
    }
}
