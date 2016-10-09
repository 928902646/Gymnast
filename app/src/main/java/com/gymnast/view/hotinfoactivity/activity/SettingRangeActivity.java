package com.gymnast.view.hotinfoactivity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
/**
 * Created by yf928 on 2016/8/9.
 */
public class SettingRangeActivity extends ImmersiveActivity {
    private ImageView ivSetRangeBack;
    private RadioGroup rgSetRange;
    private TextView tvSetRangeSave;
    private RadioGroup.OnCheckedChangeListener myRgListener;
    private String str;
    private int  radioButtonNumber;
    private RadioButton rbSetRangeAll,rbSetRangeFans,rbSetRangeAttention;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_range);
        setview();
    }
    private void setview() {
        ivSetRangeBack=(ImageView)findViewById(R.id.ivSetRangeBack);
        tvSetRangeSave=(TextView) findViewById(R.id.tvSetRangeSave);
        rgSetRange=(RadioGroup) findViewById(R.id.rgSetRange);
        rbSetRangeAll=(RadioButton) findViewById(R.id.rbSetRangeAll);
        rbSetRangeFans=(RadioButton) findViewById(R.id.rbSetRangeFans);
        rbSetRangeAttention=(RadioButton) findViewById(R.id.rbSetRangeAttention);
        ivSetRangeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myRgListener = new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rbSetRangeAll.getId()==checkedId){
                    str=rbSetRangeAll.getText().toString();
                    radioButtonNumber=0;
                }else if(rbSetRangeFans.getId()==checkedId){
                    str=rbSetRangeFans.getText().toString();
                    radioButtonNumber=1;
                }else if(rbSetRangeAttention.getId()==checkedId){
                    str=rbSetRangeAttention.getText().toString();
                    radioButtonNumber=2;
                }
            }
        };
        rgSetRange.setOnCheckedChangeListener(myRgListener);
        tvSetRangeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();
                i.putExtra("RadioButton",str);
                i.putExtra("RadiobuttonNumber",radioButtonNumber);
                setResult(13,i);
                finish();
            }
        });
    }
}
