package com.gymnast.view.live.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.utils.PicUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.HomeActivity;
import com.gymnast.view.live.entity.EndLiveEntity;
import com.hyphenate.chat.EMClient;

import java.util.Random;
import de.hdodenhof.circleimageview.CircleImageView;

public class CloseLiveActivity extends ImmersiveActivity {
    FrameLayout rlMain;
    CircleImageView civPhoto;
    TextView tvMainName,tvTotalTime,tvPeopleNumber,tvShareNumber,tvPriseNumber,tvMoney;
    ImageView ivClose;
    String mainPhotoUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_live);
        Intent intent=getIntent();
        rlMain= (FrameLayout) findViewById(R.id.rlMain);
        rlMain.getBackground().setAlpha(50);
        civPhoto= (CircleImageView) findViewById(R.id.civPhoto);
        tvMainName= (TextView) findViewById(R.id.tvMainName);
        tvTotalTime= (TextView) findViewById(R.id.tvTotalTime);
        tvPeopleNumber= (TextView) findViewById(R.id.tvPeopleNumber);
        tvShareNumber= (TextView) findViewById(R.id.tvShareNumber);
        tvPriseNumber= (TextView) findViewById(R.id.tvPriseNumber);
        tvMoney= (TextView) findViewById(R.id.tvMoney);
        ivClose= (ImageView) findViewById(R.id.ivClose);
        EndLiveEntity entity= (EndLiveEntity) intent.getSerializableExtra("EndLiveEntity");
        mainPhotoUrl=entity.getBitmapSmallPhotoUrl();
        tvMainName.setText(entity.getNickName());
        tvTotalTime.setText(entity.getTotalTime());
        tvPeopleNumber.setText(entity.getPeopleNumber() + "");
        tvShareNumber.setText(entity.getShareNumber() + "");
        tvPriseNumber.setText(entity.getPriseNumber() + "");
        tvMoney.setText("直播收益："+new Random().nextInt(100)+"金币");
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CloseLiveActivity.this, HomeActivity.class));
                finish();
            }
        });
       final String groupId=intent.getStringExtra("groupID");
       new Thread(){
            @Override
            public void run() {
                try{
                    final Bitmap bitmap=PicUtil.getImageBitmap(mainPhotoUrl);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            civPhoto.setImageBitmap(bitmap);
                        }
                    });
                    EMClient.getInstance().groupManager().destroyGroup(groupId);//需异步处理
                    Log.i("tag", "send_end_ok---" + groupId);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("tag", "send_end_error---" + groupId);
                    Log.i("tag","1"+e.toString());
                }

            }
        }.start();
    }
}
