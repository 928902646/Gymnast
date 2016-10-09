package com.gymnast.view.personal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
/**
 * Created by yf928 on 2016/7/28.
 */
public class PersonalAboutActivity extends ImmersiveActivity {
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState  ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_about);
        back =(ImageView)findViewById(R.id.personal_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
