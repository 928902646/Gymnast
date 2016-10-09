package com.gymnast.view.home.view;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by zzqybyb19860112 on 2016/9/30.
 */
public abstract class NewBaseActivity extends FragmentActivity {
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         }
             @Override
     public boolean dispatchTouchEvent(MotionEvent ev) {
         if (ev.getAction() == MotionEvent.ACTION_DOWN) {
             View v = getCurrentFocus();
             if (isShouldHideKeyboard(v, ev)) {
                 hideKeyboard(v.getWindowToken());
                 }
             }
         return super.dispatchTouchEvent(ev);
         }
             private boolean isShouldHideKeyboard(View v, MotionEvent event) {
         if (v != null && (v instanceof EditText)) {
             int[] l = {0, 0};
             v.getLocationInWindow(l);
             int left = l[0],
             top = l[1],
             bottom = top + v.getHeight(),
             right = left + v.getWidth();
             if (event.getX() > left && event.getX() < right
             && event.getY() > top && event.getY() < bottom) {
                 return false;
                 } else {
                 return true;
                 }
             }
         return false;
         }
            private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
             im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
             }
         }
}

