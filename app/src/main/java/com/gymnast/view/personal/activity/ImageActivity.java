package com.gymnast.view.personal.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.gymnast.R;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.personal.customview.TouchImageView;

/**
 * Created by Cymbi on 2016/9/21.
 */
public class ImageActivity extends ImmersiveActivity {
    private TouchImageView iv_dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog);
        iv_dialog=(TouchImageView) findViewById(R.id.iv_dialog);
        String image=getIntent().getStringExtra("IMAGE");
        Log.i("tag","imageUrl------------"+ image);
        if (image.contains("easemob")||image.contains("__")){
            PicassoUtil.handlePic(this, image, iv_dialog, 720, 720);
        }else {
            PicassoUtil.handlePic(this, PicUtil.getImageUrlDetail(this, image, 720, 720), iv_dialog, 720, 720);
        }
        iv_dialog.setMaxZoom(33);
        iv_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
