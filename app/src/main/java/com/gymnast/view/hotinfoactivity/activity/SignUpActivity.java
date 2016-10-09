package com.gymnast.view.hotinfoactivity.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.hotinfoactivity.adapter.SingnUpAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cymbi on 2016/9/10.
 */
public class SignUpActivity extends ImmersiveActivity implements View.OnClickListener{
    private ImageView ivSignUpBack,ivSignUpHead;
    private TextView tvSignUpTitle,tvSignUpMoney,tvSignUpReport,tvSignUpAllMoney,tvSignUpSubmit;
    private RecyclerView rvSignUp;
    private SharedPreferences share;
    private int id,createId,activityId,price;
    private String userId,template,nickName,phoneNew,token,imageUrl,title;
    ArrayList<String> answer=new ArrayList<>();
    private List<String>list=new ArrayList<>();
    SingnUpAdapter adapter;
    public  static final int HANDLE_DATA=1;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLE_DATA:
                    rvSignUp.setVisibility(View.VISIBLE);
                    tvSignUpReport.setVisibility(View.VISIBLE);
                    adapter=new SingnUpAdapter(SignUpActivity.this,list);
                    rvSignUp.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setView();
        getInfo();
        getTitleData();
        queryMemberTemplate();
    }

    private void getTitleData() {
        PicassoUtil.handlePic(SignUpActivity.this, PicUtil.getImageUrlDetail(SignUpActivity.this, StringUtil.isNullAvatar(imageUrl),320,320),ivSignUpHead,320,320);
        tvSignUpTitle.setText(title);
        if (price==0){
            tvSignUpMoney.setText("免费");
            tvSignUpAllMoney.setTextSize(15);
            tvSignUpAllMoney.setText("免费");
        }else {
            tvSignUpMoney.setText(price+"");
            tvSignUpAllMoney.setText(price+"");
        }
    }
    private void queryMemberTemplate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String uri=API.BASE_URL+"/v1/activity/memberTemplate";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("token",token);
                    params.put("id",id+"");
                    String result= PostUtil.sendPostMessage(uri,params);
                    JSONObject object=new JSONObject(result);
                    if(object.getInt("state")==200) {
                        JSONObject data = object.getJSONObject("data");
                        activityId = data.getInt("id");
                        createId = data.getInt("userId");
                        template = data.getString("template");
                        if(template!=null&template!="null"&!template.equals("")){
                            String[] s = template.split(",");
                            for (int i = 0; i < s.length; i++) {
                                list.add(s[i]);
                            }
                            handler.sendEmptyMessage(HANDLE_DATA);
                        }else {}
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void setView() {
        rvSignUp=(RecyclerView) findViewById(R.id.rvSignUp);
        ivSignUpBack=(ImageView) findViewById(R.id.ivSignUpBack);
        ivSignUpHead=(ImageView) findViewById(R.id.ivSignUpHead);
        tvSignUpTitle=(TextView) findViewById(R.id.tvSignUpTitle);
        tvSignUpMoney=(TextView) findViewById(R.id.tvSignUpMoney);
        tvSignUpAllMoney=(TextView) findViewById(R.id.tvSignUpAllMoney);
        tvSignUpReport=(TextView) findViewById(R.id.tvSignUpReport);
        tvSignUpSubmit=(TextView) findViewById(R.id.tvSignUpSubmit);
        ivSignUpBack.setOnClickListener(this);
        tvSignUpSubmit.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivSignUpBack:
                finish();
                break;
            case R.id.tvSignUpSubmit:
                Sign();
                break;
        }
    }
    public void Sign() {
        String answerEnd = null;
        if(list.size()!=0){
            boolean isAllAnswered=allAnswered();
            if (isAllAnswered==false){
                return;
            }
            answerEnd=getAllAnswer();
        }
        final String finalAnswerEnd = answerEnd;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri= API.BASE_URL+"/v1/activity/signup";
                HashMap<String,String> params=new HashMap<String, String>();
                HashMap<String,String> map=new HashMap<String, String>();
                params.put("token",token);
                params.put("activityId",activityId+"");
                params.put("userId",userId);
                params.put("userNick",nickName);
                params.put("phone",phoneNew);
                if(list.size()!=0){
                    params.put("memberInfo", finalAnswerEnd);
                }else {}
                String result=PostUtil.sendPostMessage(uri,params);
                try{
                    JSONObject object=new JSONObject(result);
                    int state=object.getInt("state");
                    if (state==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUpActivity.this,"报名成功",Toast.LENGTH_SHORT).show();
                                Intent i=new Intent();
                                setResult(10,i);
                                finish();
                            }
                        });
                    }else if(state==10005){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUpActivity.this,"不能重复报名！",Toast.LENGTH_SHORT).show();
                                SignUpActivity.this.finish();
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private String getAllAnswer(){
        String myData;
        StringBuffer sb=new StringBuffer("{");
        for (int i=0;i<answer.size();i++){
            sb.append("\"");
            sb.append(list.get(i));
            sb.append("\":\"");
            sb.append(answer.get(i));
            sb.append("\",");

        }
        sb.substring(0,sb.length());
        String src=sb.toString();
        String s= src.substring(0,src.length()-1)+"}";
        myData = s.replaceAll("\\\\","");
        return myData;
    }
    private boolean allAnswered() {
        boolean isAll=false;
        if (answer.size()!=0){
            answer.clear();
        }
        for (int i=0;i<list.size();i++){
            ArrayList<View> viewList = rvSignUp.getChildAt(i).getTouchables();
            EditText etOne= (EditText) viewList.get(0);
            String myAnswer=etOne.getText().toString();
            if (myAnswer!=null&&!myAnswer.equals("")){
                answer.add(myAnswer);
            }else {
                continue;
            }
        }
        if (answer.size()==list.size()){
            isAll=true ;
        }else {
            Toast.makeText(SignUpActivity.this,"信息填写不完整，请检查！",Toast.LENGTH_SHORT).show();
        }
        return isAll;
    }
    public void getInfo() {
        share = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token", "");
        userId = share.getString("UserId","");
        nickName = share.getString("NickName","");
        phoneNew = share.getString("Phone","");
        Intent i=getIntent();
        id= i.getIntExtra("activityId",0);
        price= i.getIntExtra("price",0);
        imageUrl= i.getStringExtra("imageUrl");
        title= i.getStringExtra("title");
    }
}
























