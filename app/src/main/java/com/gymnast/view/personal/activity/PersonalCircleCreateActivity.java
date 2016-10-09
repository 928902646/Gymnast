package com.gymnast.view.personal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.SelectType;
import com.gymnast.utils.PostUtil;
import com.gymnast.view.ImmersiveActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by yf928 on 2016/8/10.
 */
public class PersonalCircleCreateActivity extends ImmersiveActivity {
    private ImageView back;
    private TextView type;
    private EditText et_title,content;
    private TextView submit;
    private SharedPreferences share;
    private String token,id;
    private int typeid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_circle);
        share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token","");
        id = share.getString("UserId","");
        setview();
        initview();
    }
    private void setview() {
        back= (ImageView) findViewById(R.id.personal_back);
        type = (TextView)findViewById(R.id.type);
        submit = (TextView)findViewById(R.id.submit);
        content=(EditText) findViewById(R.id.circle_content);
        et_title= (EditText)findViewById(R.id.circle_title);
    }
    private void initview() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PersonalCircleCreateActivity.this,PersonalSelectTypeActivity.class);
                startActivityForResult(i,100);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            if(resultCode==10){
                String  name =  data.getStringExtra("typename");
                typeid=data.getIntExtra("typeid",0);
                type.setText(name);
            }
        }
    }
    public void getdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SelectType type =new SelectType();
                    String title= et_title.getText().toString();
                    String details=content.getText().toString();
                    EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                    option.maxUsers = 1000;
                    option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    String []allMembers=new String[]{};
                    EMGroup group = EMClient.getInstance().groupManager().createGroup(id,title,allMembers, null, option);
                    String groupId=group.getGroupId();
                    String uri= API.BASE_URL+"/v1/circle/add";
                    Map<String,String> parmas=new HashMap<String, String>();
                    parmas.put("token",token);
                    parmas.put("createId",id);
                    parmas.put("title",title);
                    parmas.put("details",details);
                    parmas.put("group_id",groupId+"");
                    parmas.put("circleType",typeid+"");
                    String result= PostUtil.sendPostMessage(uri,parmas);
                    JSONObject obj=new JSONObject(result);
                    if(obj.getInt("state")==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalCircleCreateActivity.this,"圈子创建成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalCircleCreateActivity.this,"圈子创建失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
