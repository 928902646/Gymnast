package com.gymnast.view.live.customview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import com.gymnast.R;
/**
 * Created by Administrator on 2016/8/3.
 */
public class MyTextView extends TextView {
    public  boolean isOut=false;
    public  boolean isSelect=false;
    public  boolean isToday=false;
    public  int posInCalendar=0;
    Context context;
    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.MyTextView);
        isOut=array.getBoolean(R.styleable.MyTextView_isOut,false);
        isSelect=array.getBoolean(R.styleable.MyTextView_isSelect,false);
        isToday=array.getBoolean(R.styleable.MyTextView_isToday,false);
        posInCalendar=array.getInt(R.styleable.MyTextView_myPosition,0);
        this.setIsOut(isOut);
        this.setIsToday(isToday);
        this.setIsSelect(isSelect);
        this.setPosInCalendar(posInCalendar);
        array.recycle();
    }
    public int getPosInCalendar() {
        return posInCalendar;
    }
    public void setPosInCalendar(int posInCalendar) {
        this.posInCalendar = posInCalendar;
    }
    public  boolean isToday() {
        return isToday;
    }
    public  void setIsToday(boolean isToday) {
        this.isToday = isToday;
    }
    public  boolean isSelect() {
        return isSelect;
    }
    public  void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
    public  boolean isOut() {
        return isOut;
    }
    public  void setIsOut(boolean isOut) {
        this.isOut = isOut;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isOut){
            this.setTextColor(getResources().getColor(R.color.half_black));
        }else {
            this.setTextColor(getResources().getColor(R.color.hot_info_circle_content_color));
        }
        if (isSelect) {
            this.setTextColor(getResources().getColor(R.color.white));
            this.setBackgroundResource(R.mipmap.bg_circle_red);
        }else {
            this.setBackgroundResource(0);
        }
        if (isToday){
            Drawable smallRedPoint=context.getResources().getDrawable(R.drawable.border_radius_circle_red_point);
            smallRedPoint.setBounds(0, 0,  8, 8);
            this.setCompoundDrawables(null, null,null,smallRedPoint);
            this.setCompoundDrawablePadding(-12);
        }else {
            this.setCompoundDrawables(null, null,null,null);
        }
        this.setGravity(Gravity.CENTER);
        Rect rect = new Rect();
        ((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int x = rect.width();
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX,27*x/750);
    }
}
