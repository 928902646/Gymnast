package com.gymnast.view.live.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
import com.gymnast.R;
/**
 * Created by 永不言败 on 2016/8/8.
 */
public class BarrageView extends TextView {
    Bitmap photo=null;
    String text=null;
    String name=null;
    public BarrageView(Context context, AttributeSet attrs,Bitmap photo,String text,String name){
        super(context,attrs);
        this.photo=photo;
        this.text=text;
        this.name=name;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        this.setBackgroundResource(R.drawable.border_radius_cornner_all_black);
        MyDrawable drawable =new MyDrawable(photo);
        drawable.setBounds(300, 300, 300, 300);
        this.setCompoundDrawables(drawable, null, null, null);
        this.setCompoundDrawablePadding(64);
        this.setTextColor(getResources().getColor(R.color.white));
        this.setText(text);
        this.setMaxEms(20);
        this.setEllipsize(TextUtils.TruncateAt.END);
        this.setSingleLine(true);
        this.setHorizontallyScrolling(false);
        this.setMaxWidth(200);
        this.setGravity(Gravity.CENTER_VERTICAL);
        this.setTextSize(12);
        super.onDraw(canvas);
    }
}