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
public class SettingPriceActivity extends ImmersiveActivity implements View.OnClickListener{
    private ImageView ivSetPriceBack;
    private EditText etPriceNumber;
    private TextView tvSetPriceSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_price);
        setview();
    }
    private void setview() {
        ivSetPriceBack=(ImageView)findViewById(R.id.ivSetPriceBack);
        etPriceNumber=(EditText)findViewById(R.id.etSetPriceNumber);
        tvSetPriceSave=(TextView)findViewById(R.id.tvSetPriceSave);
        tvSetPriceSave.setOnClickListener(this);
        ivSetPriceBack.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent i=new Intent();
        switch (v.getId()){
            case R.id.ivSetPriceBack:
                i.putExtra("price",0.0);
                setResult(15,i);
                finish();
                break;
            case R.id.tvSetPriceSave:
                String met_activity_price= etPriceNumber.getText().toString();
                if(met_activity_price.equals("")|met_activity_price==null|met_activity_price.equals("0")|met_activity_price.equals("0.0")|met_activity_price.equals("0.00")){
                    i.putExtra("price",0.0);
                }else {
                    double price= Double.parseDouble(met_activity_price);
                    i.putExtra("price",price);
                }
                setResult(15,i);
                finish();
                break;
    }
    }
}
