package com.gymnast.view.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
public class CustomViewPager extends ViewPager {
  private boolean isSlide = true; // 是否滑动，true:允许滑动，false:禁止滑动
  public CustomViewPager(Context context) {
    super(context);
  }
  public CustomViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }
  /**
   * 设置是否滑动
   * @param isSlide true:允许滑动，false:禁止滑动
   */
  public void setSlide(boolean isSlide) {
    this.isSlide = isSlide;
  }
  @Override public boolean onTouchEvent(MotionEvent event) {
    return this.isSlide && super.onTouchEvent(event);
  }
  @Override public boolean onInterceptTouchEvent(MotionEvent event) {
    return this.isSlide && super.onInterceptTouchEvent(event);
  }
  /*解决页面切换，页面会闪烁的问题*/
  @Override public void setCurrentItem(int item, boolean smoothScroll) {
      super.setCurrentItem(item, smoothScroll);
  }
  @Override public void setCurrentItem(int item) {
    super.setCurrentItem(item, false);
  }
    /*end*/
}
