package com.gymnast.view.hotinfoactivity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
/**
 * Created by yf928 on 2016/8/9.
 */
public class SettingSiteActivity extends ImmersiveActivity implements View.OnClickListener{
    private ImageView ivSetSiteBack;
    private EditText etSetSiteAddress;
    private TextView tvSetSiteSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_site);
        setview();
    }
    private void setview() {
        ivSetSiteBack=(ImageView)findViewById(R.id.ivSetSiteBack);
        tvSetSiteSave=(TextView)findViewById(R.id.tvSetSiteSave);
        etSetSiteAddress=(EditText) findViewById(R.id.etSetSiteAddress);
        ivSetSiteBack.setOnClickListener(this);
        tvSetSiteSave.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivSetSiteBack:
                finish();
                break;
            case R.id.tvSetSiteSave:
                String   maddress= etSetSiteAddress.getText().toString();
                Intent i=new Intent();
                i.putExtra("address",maddress);
                setResult(14,i);
                finish();
        }
    }
}
