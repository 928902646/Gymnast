package com.gymnast.view.personal.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gymnast.App;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.CustomUtil;
import com.gymnast.utils.DataCleanManagerUtil;
import com.gymnast.utils.GetUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.HomeActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by yf928 on 2016/7/25.
 */
public class PersonSettingActivity extends ImmersiveActivity   {
    private RelativeLayout data,address,feedback,about,clean,authentication;
    private TextView cache,code;
    private AlertDialog builder;
    private ImageView back;
    private TextView personal_exit;
    private SharedPreferences share;
    private String token;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        setView();
        setListeners();
    }
    private void setView() {
        data=(RelativeLayout)findViewById(R.id.data);
        address=(RelativeLayout)findViewById(R.id.address);
        feedback=(RelativeLayout)findViewById(R.id.feedback);
        about=(RelativeLayout)findViewById(R.id.about);
        clean=(RelativeLayout)findViewById(R.id.clean);
        authentication=(RelativeLayout)findViewById(R.id.authentication);
        cache=(TextView)findViewById(R.id.cache);
        code=(TextView)findViewById(R.id.code);
        personal_exit=(TextView)findViewById(R.id.personal_exit);
        back= (ImageView)findViewById(R.id.personal_back);
        share= getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        token = share.getString("Token","");
    }
    private void setListeners() {
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.isStateOK){
                    Intent i = new Intent(PersonSettingActivity.this, PersonalEditDataActivity.class);
                    startActivity(i);
                }else {
                    Toast.makeText(PersonSettingActivity.this,"您还没有登陆呢，亲！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PersonSettingActivity.this, PersonalAddressActivity.class);
                startActivity(i);
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PersonSettingActivity.this, PersonalFeedbackActivity.class);
                startActivity(i);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PersonSettingActivity.this, PersonalAboutActivity.class);
                startActivity(i);
            }
        });
        authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PersonSettingActivity.this, PersonalAuthenticationActivity.class);
                startActivity(i);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        code.setText(CustomUtil.getVersionName(this));

        clean.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(PersonSettingActivity.this).create();
                builder.setView(getLayoutInflater().inflate(R.layout.activity_personal_dialog, null));
                builder.show();
                //去掉dialog四边的黑角
                builder.getWindow().setBackgroundDrawable(new BitmapDrawable());
                builder.getWindow().findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
                builder.getWindow().findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataCleanManagerUtil.cleanInternalCache(getApplicationContext());
                        Toast.makeText(getApplicationContext(), "清理完成", Toast.LENGTH_LONG).show();
                        builder.dismiss();
                    }
                });
            }
        });
        personal_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.isStateOK){
                    showDialogs();
                }else {
                    Toast.makeText(PersonSettingActivity.this,"您还没有登陆，不能注销账号！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showDialogs() {
        View view=getLayoutInflater().inflate(R.layout.log_out_dialog, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        Button cancel=(Button)view.findViewById(R.id.btnCancle);
        Button sure=(Button)view.findViewById(R.id.btnSure);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EMClient.getInstance().logout(true, new EMCallBack() {
                                        @Override
                                public void onSuccess() {
                                            Log.i("tag","环信注销成功");
                                    }
                                        @Override
                                public void onProgress(int progress, String status) {
                                            Log.i("tag","环信注销中。。。");
                                    }
                                     @Override
                                public void onError(int code, String message) {
                                         Log.i("tag","环信注销失败");
                                    }
                            });
                            String uri= API.BASE_URL+"/v1/account/signout";
                            HashMap<String, String> params=new HashMap<>();
                            params.put("token",token);
                            String result= GetUtil.sendGetMessage(uri,params);
                            JSONObject obj;
                            try{
                            obj=new JSONObject(result);
                                if(obj.getInt("state")==200){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(PersonSettingActivity.this,"注销成功",Toast.LENGTH_SHORT).show();
                                            Log.e("注销token=",token);
                                            share.edit().clear().commit();
                                            Intent i=new Intent(PersonSettingActivity.this, HomeActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                            App.isStateOK=false;
                                            finish();
                                        }
                                    });
                                }
                            }catch (Exception e){
                            }
                        }
                    }).start();
            }
        });
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.log_out_dialog);
       // WindowManager.LayoutParams wl = window.getAttributes();
        //设置dialog显示在屏幕的位置
        //wl.x = 0;
        //wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        //wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        //dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
