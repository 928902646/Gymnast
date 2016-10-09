package com.gymnast.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;
/**
 * Created by yf928 on 2016/8/9.
 */
public class ScrollviewEditUtil extends ScrollView {
    private static final String TAG = "ScrollviewEdit";
    private ScrollView parent_scrollview;
    public ScrollView getParent_scrollview() {
        return parent_scrollview;
    }
    public void setParent_scrollview(ScrollView parent_scrollview) {
        this.parent_scrollview = parent_scrollview;
    }
    public ScrollviewEditUtil(Context context) {
        super(context);
    }
    public ScrollviewEditUtil(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    int currentY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (parent_scrollview == null) {
            return super.onInterceptTouchEvent(ev);
        } else {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                // 将父scrollview的滚动事件拦截
                currentY = (int) ev.getY();
                setParentScrollAble(false);
                return super.onInterceptTouchEvent(ev);
            } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                // 把滚动事件恢复给父Scrollview
                setParentScrollAble(true);
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
    /**
     * 是否把滚动事件交给父scrollview
     * @param flag
     */
    private void setParentScrollAble(boolean flag) {
        parent_scrollview.requestDisallowInterceptTouchEvent(!flag);
    }
}
