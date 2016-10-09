package com.gymnast.view.hotinfoactivity.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Cymbi on 2016/9/17.
 */
public class MyCMDTextView extends TextView {
    boolean isCancel;
    public MyCMDTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyCMDTextView(Context context, AttributeSet attrs,boolean isCancel) {
        super(context, attrs);
        this.isCancel=isCancel;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }
}
