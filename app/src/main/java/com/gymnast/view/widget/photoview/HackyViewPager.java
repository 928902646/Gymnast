package com.gymnast.view.widget.photoview;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * 解决 Android 系统库bug IllegalArgumentException: pointerIndex out of range.
 * @description
 * @author summer
 * @date 2014年5月21日 下午4:11:48
 */
public class HackyViewPager extends ViewPager {
	private boolean isLocked;
	private boolean isIntercept;
    public HackyViewPager(Context context) {
        this(context,null);
    }
    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	if (!isLocked) {
	        try {
	        	return isIntercept ? isIntercept:super.onInterceptTouchEvent(ev);
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	            return false;
	        }
    	}
    	return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
		return !isLocked && super.onTouchEvent(event);
	}
	public void toggleLock() {
		isLocked = !isLocked;
	}
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}
	public boolean isLocked() {
		return isLocked;
	}
	/**
	 * 是否拦截事件
	 * @param isIntercept
	 */
	public void setIntercept(boolean isIntercept) {
		this.isIntercept = isIntercept;
	}
}
