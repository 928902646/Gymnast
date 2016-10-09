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
 * Created by Cymbi on 2016/8/18.
 */
public class PersonalConsigneeActivity extends ImmersiveActivity  {
    private EditText et_activity_name;
    private TextView save;
    private String nickname;
    private String token,id;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_consignee);
        SharedPreferences share = getSharedPreferences("UserInfo",MODE_PRIVATE);
        token= share.getString("Token","");
        id= share.getString("UserId","");
        setView();
        setListeners();
    }
    private void setView() {
        et_activity_name=(EditText)findViewById(R.id.et_activity_name);
        save=(TextView) findViewById(R.id.save);
        back=(ImageView) findViewById(R.id.personal_back);
    }
    private void setListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });
    }
    private void setData (){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String uri= API.BASE_URL+"/v1/account/info/edit";
                    Map<String, String> params=new HashMap<>();
                    params.put("token",token);
                    params.put("account_id",id);
                    nickname = et_activity_name.getText().toString();
                    params.put("nickname",nickname);
                    String result= PostUtil.sendPostMessage(uri,params);
                    JSONObject json =  new JSONObject(result);
                    if(json.getInt("state")==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                Toast.makeText(PersonalConsigneeActivity.this,"昵称修改成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if(json.getInt("state")==301){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalConsigneeActivity.this,"请填写昵称",Toast.LENGTH_SHORT).show();
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
