package com.gymnast.view.hotinfoactivity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.utils.VerifyPhoneUtil;
import com.gymnast.view.ImmersiveActivity;
/**
 * Created by yf928 on 2016/8/9.
 */
public class SettingPhoneActivity extends ImmersiveActivity implements View.OnClickListener{
    private ImageView ivSetPhoneBack;
    private TextView tvSetPhoneSave;
    private EditText etSetPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_phone);
        setview();
    }
    private void setview() {
        ivSetPhoneBack=(ImageView)findViewById(R.id.ivSetPhoneBack);
        tvSetPhoneSave=(TextView)findViewById(R.id.tvSetPhoneSave);
        etSetPhoneNumber=(EditText)findViewById(R.id.etSetPhoneNumber);
        tvSetPhoneSave.setOnClickListener(this);
        ivSetPhoneBack.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivSetPhoneBack:
                finish();
                break;
            case R.id.tvSetPhoneSave:
                String met_activity_name= etSetPhoneNumber.getText().toString();
                if(met_activity_name.length()<11){
                  int num= 11-met_activity_name.length();
                  Toast.makeText(this,"电话号码差了"+num+"位，请检查清楚",Toast.LENGTH_SHORT).show();
                }else if(met_activity_name.length()<=11){
                if(VerifyPhoneUtil.isMobileNO(met_activity_name)){
                    Intent i = new Intent();
                    i.putExtra("Phone", met_activity_name);
                    setResult(12, i);
                    finish();
                }else {
                    Toast.makeText(this,"电话号码格式不正确，请检查",Toast.LENGTH_SHORT).show();
                }
            }

                }


        }
    }


