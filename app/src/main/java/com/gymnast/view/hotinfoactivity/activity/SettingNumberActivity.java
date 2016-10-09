package com.gymnast.view.hotinfoactivity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
/**
 * Created by yf928 on 2016/8/9.
 */
public class SettingNumberActivity extends ImmersiveActivity implements View.OnClickListener{
    private ImageView ivSettingBack;
    private EditText etSettingNumber;
    private TextView tvSettingSave;
    private String settingNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_number);
        setview();
    }
    private void setview() {
        ivSettingBack=(ImageView)findViewById(R.id.ivSettingBack);
        etSettingNumber=(EditText)findViewById(R.id.etSettingNumber);
        tvSettingSave=(TextView)findViewById(R.id.tvSettingSave);
        tvSettingSave.setOnClickListener(this);
        ivSettingBack.setOnClickListener(this);
        etSettingNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etSettingNumber.toString().equals("0") || charSequence.toString().startsWith("0")
                        || etSettingNumber.toString().equals("") || charSequence.toString().trim().length() == 0) {
                    etSettingNumber.setText("1");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivSettingBack:
                finish();
                break;
            case R.id.tvSettingSave:
                settingNumber= etSettingNumber.getText().toString();
                Intent i = new Intent();
                i.putExtra("Number", settingNumber);
                setResult(11, i);
                finish();
        }
    }
}
