package com.gymnast.view.home.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.HomeActivity;

/**
 * Created by Cymbi on 2016/9/7.
 */
public class WelcomeActivity extends ImmersiveActivity {
    private boolean isFirstIn = false;
    private static final int TIME = 2000;
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
        init();
    }
    private void init(){
        SharedPreferences perPreferences = getSharedPreferences("Welcome", MODE_PRIVATE);
        isFirstIn = perPreferences.getBoolean("isFirstIn", true);
        if (!isFirstIn) {
            mHandler.sendEmptyMessageDelayed(GO_HOME, TIME);
        }else{
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, TIME);
            SharedPreferences.Editor editor = perPreferences.edit();
            editor.putBoolean("isFirstIn", false);
            editor.commit();
        }
    }
    private void goHome(){
        Intent i = new Intent(WelcomeActivity.this,HomeActivity.class);
        startActivity(i);
        finish();
    }
    private void goGuide(){
        Intent i = new Intent(WelcomeActivity.this,GuideActivity.class);
        startActivity(i);
        finish();
    }
}
