package com.gymnast.view.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gymnast.App;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.UnKnownUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.HomeActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ImmersiveActivity {
  private AutoCompleteTextView login_phone;
  private EditText login_password;
  private Button login_btn;
  private TextView loginToRegister;
  private TextView loginToForgetPassword;
  private ImageView loginQQ;
  private ImageView loginWX;
  private ImageView loginWB;
  private String mphone;
  private String mpwd;
  private String token;
  private String id;
  private String nickname;
  private String phone;
  private String avatar;
  private ImageView back;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView( R.layout.activity_login);
    setView();
    setListeners();
  }
  private void setView() {
    login_phone= (AutoCompleteTextView)findViewById(R.id.login_phone);
    login_password= (EditText)findViewById(R.id.login_password);
    login_btn= (Button)findViewById(R.id.login_btn);
    loginToRegister= (TextView)findViewById(R.id.login_to_register);
    loginToForgetPassword= (TextView)findViewById(R.id.login_forget_password);
    loginQQ= (ImageView)findViewById(R.id.login_qq);
    loginWX= (ImageView)findViewById(R.id.login_wx);
    loginWB= (ImageView)findViewById(R.id.login_wb);
    back= (ImageView)findViewById(R.id.personal_back);
  }

  private void setListeners() {
    login_btn.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        attemptLogin();
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(login_btn.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
      }
    });
    loginToRegister.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
      }
    });
    loginToForgetPassword.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
      }
    });
    loginQQ.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        Toast.makeText(LoginActivity.this, "开发中", Toast.LENGTH_SHORT).show();
      }
    });
    loginWX.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        Toast.makeText(LoginActivity.this, "开发中", Toast.LENGTH_SHORT).show();
      }
    });
    loginWB.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        Toast.makeText(LoginActivity.this, "开发中", Toast.LENGTH_SHORT).show();
      }
    });
    back.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
  }
  private void attemptLogin() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        mphone = login_phone.getText().toString();
        mpwd = login_password.getText().toString();
        try {
          String uri= API.BASE_URL+"/v1/account/login";
          Map<String, String> params=new HashMap<>();
          params.put("phone",mphone);
         // params.put("pwd", UnKnownUtil.MD5(mpwd) );
            params.put("pwd", mpwd );
          String result= PostUtil.sendPostMessage(uri,params);
          JSONObject json =  new JSONObject(result);
          JSONObject data = json.getJSONObject("data");
          id= data.getString("id");
          token= data.getString("token");
          Log.i("tag",token);
          Log.i("tag", id);
          nickname= data.getString("nickname");
          phone= data.getString("phone");
          avatar= data.getString("avatar");
          try {
            String username=id;
            String pwd="111111";
            EMClient.getInstance().createAccount(username, pwd);
          } catch (HyphenateException e) {
            e.printStackTrace();
            Log.i("tag","环信注册失败");
          }
          String userName=id;
          String password="111111";
          EMClient.getInstance().login(userName, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
              EMClient.getInstance().groupManager().loadAllGroups();
              EMClient.getInstance().chatManager().loadAllConversations();
              Log.i("tag", "登录环信聊天服务器成功！");
            }
            @Override
            public void onProgress(int progress, String status) {
              Log.i("tag", "正在登录环信聊天服务器。。。");
            }
            @Override
            public void onError(int code, String message) {
              Log.i("tag", "登录环信聊天服务器失败！");
            }
          });
          if(json.getInt("state")==200){
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Long tokenTime =System.currentTimeMillis();
                SharedPreferences share = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                share.edit().clear().apply();
                //获取编辑器，编辑文件
                SharedPreferences.Editor etr = share.edit();
                //通过编辑器添加数据
                etr.putString("Token", token);
                etr.putString("UserId", id);
                etr.putString("NickName", nickname);
                etr.putString("Phone", phone);
                etr.putString("Avatar", avatar);
                etr.apply();
                Intent i=new Intent(LoginActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                App.isStateOK=true;
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                finish();
              }
            });
          }else  if(json.getInt("state")==50001){
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(LoginActivity.this, "用户名和密码错误", Toast.LENGTH_SHORT).show();
              }
            });
          }else  if(json.getInt("state")==50003){
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(LoginActivity.this, "手机号码未注册", Toast.LENGTH_SHORT).show();
              }
            });
          }else  if(json.getInt("state")==500){
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(LoginActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
              }
            });
          }
        }catch (Exception e) {
          e.printStackTrace();
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(LoginActivity.this,"用户名密码错误",Toast.LENGTH_SHORT).show();
            }
          });
        }
      }
    }).start();
  }
}




