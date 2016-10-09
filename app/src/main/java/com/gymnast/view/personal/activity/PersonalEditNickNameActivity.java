package com.gymnast.view.personal.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.PostUtil;
import com.gymnast.view.ImmersiveActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by Cymbi on 2016/8/16.
 */
public class PersonalEditNickNameActivity extends ImmersiveActivity {
    private EditText etEditName;
    private TextView tvEditNicknameSave;
    private String token,id,nickname;
    private ImageView ivEditNickNameBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nickname);
        SharedPreferences share = getSharedPreferences("UserInfo",MODE_PRIVATE);
        token= share.getString("Token","");
        id= share.getString("UserId","");
        setView();
        setListeners();
    }
    private void setView() {
        etEditName=(EditText)findViewById(R.id.etEditName);
        tvEditNicknameSave=(TextView) findViewById(R.id.tvEditNicknameSave);
        ivEditNickNameBack=(ImageView) findViewById(R.id.ivEditNickNameBack);
    }
    private void setListeners() {
        ivEditNickNameBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvEditNicknameSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdata();
            }
        });
    }
    private void setdata (){
        new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        String uri= API.BASE_URL+"/v1/account/info/edit";
                        Map<String, String> params=new HashMap<>();
                        params.put("token",token);
                        params.put("account_id",id);
                        nickname = etEditName.getText().toString();
                        params.put("nickname",nickname);
                        String result= PostUtil.sendPostMessage(uri,params);
                        JSONObject json =  new JSONObject(result);
                        if(json.getInt("state")==200){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                    Toast.makeText(PersonalEditNickNameActivity.this,"昵称修改成功",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if(json.getInt("state")==301){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PersonalEditNickNameActivity.this,"请填写昵称",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        }).start();
    }
}
