package com.gymnast.view.hotinfoactivity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;

/**
 * Created by Cymbi on 2016/9/8.
 */
public class AddConditionActivity extends ImmersiveActivity {
    private EditText etAddConditionName;
    private String text;
    private TextView tvAddConditionSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_add);
        setview();
    }
    private void setview() {
        etAddConditionName=(EditText)findViewById(R.id.etAddConditionName);
        tvAddConditionSave=(TextView)findViewById(R.id.tvAddConditionSave);
        findViewById(R.id.ivAddConditionBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddConditionActivity.this.finish();
            }
        });
        tvAddConditionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text=etAddConditionName.getText().toString();
                Intent i=new Intent(AddConditionActivity.this,PromulgateActivityActivity.class);
                i.putExtra("add",text);
                setResult(16,i);
                finish();
            }
        });
    }
}
