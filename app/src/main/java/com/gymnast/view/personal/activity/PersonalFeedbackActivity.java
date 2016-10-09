package com.gymnast.view.personal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
/**
 * Created by yf928 on 2016/7/28.
 */
public class PersonalFeedbackActivity extends ImmersiveActivity {
    private ImageView ivFeedBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_feedback);
        ivFeedBack =(ImageView)findViewById(R.id.ivFeedBack);
        ivFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
