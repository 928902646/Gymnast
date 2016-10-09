package com.gymnast.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import com.gymnast.R;
import com.gymnast.view.hotinfoactivity.activity.PromulgateActivityActivity;
import com.gymnast.view.live.activity.CreateLiveActivity;
import com.gymnast.view.live.activity.SearchActivity;
import com.gymnast.view.pack.view.PlayActivityIssueActivity;
import com.gymnast.view.personal.activity.PersonalActivity;
import com.gymnast.view.personal.activity.PersonalCircleCreateActivity;
public class QuickOptionDialog extends Dialog implements View.OnClickListener {
  Context context;
  private ImageView mClose;
  public interface OnQuickOptionformClick {
    void onQuickOptionClick(int id);
  }
  private OnQuickOptionformClick mListener;
  private QuickOptionDialog(Context context, boolean flag, OnCancelListener listener) {
    super(context, flag, listener);
  }
  @SuppressLint("InflateParams") private QuickOptionDialog(Context context, int defStyle) {
    super(context, defStyle);
    this.context=context;
    View contentView = getLayoutInflater().inflate(R.layout.activity_home_tab_quick_option_dialog, null);
    contentView.findViewById(R.id.ll_quick_option_activity).setOnClickListener(this);
    contentView.findViewById(R.id.ll_quick_option_live).setOnClickListener(this);
    contentView.findViewById(R.id.ll_quick_option_dynamic).setOnClickListener(this);
    contentView.findViewById(R.id.ll_quick_option_concern).setOnClickListener(this);
    contentView.findViewById(R.id.ll_quick_option_circle).setOnClickListener(this);
    contentView.findViewById(R.id.ll_quick_option_task).setOnClickListener(this);
    mClose = (ImageView) contentView.findViewById(R.id.iv_close);
    Animation operatingAnim =
        AnimationUtils.loadAnimation(getContext(), R.anim.quick_option_dialog_close);
    LinearInterpolator lin = new LinearInterpolator();
    operatingAnim.setInterpolator(lin);
    mClose.startAnimation(operatingAnim);
    mClose.setOnClickListener(this);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    contentView.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        QuickOptionDialog.this.dismiss();
        return true;
      }
    });
    super.setContentView(contentView);
  }
  public QuickOptionDialog(Context context) {
    this(context, R.style.quick_option_dialog);
  }
  @SuppressWarnings("deprecation") @Override protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    getWindow().setGravity(Gravity.BOTTOM);
    WindowManager m = getWindow().getWindowManager();
    Display d = m.getDefaultDisplay();
    WindowManager.LayoutParams p = getWindow().getAttributes();
    p.width = d.getWidth();
    getWindow().setAttributes(p);
  }
  public void setOnQuickOptionformClickListener(OnQuickOptionformClick lis) {
    mListener = lis;
  }
  @Override public void onClick(View v) {
    final int id = v.getId();
    switch (id) {
      case R.id.iv_close:
        dismiss();
        break;
      case R.id.ll_quick_option_activity:
        context.startActivity(new Intent(context, PromulgateActivityActivity.class));
        dismiss();
        break;
      case R.id.ll_quick_option_live:
       context.startActivity(new Intent(context, CreateLiveActivity.class));
        break;
      case R.id.ll_quick_option_dynamic:
        context.startActivity(new Intent(context, PlayActivityIssueActivity.class));
        break;
      case R.id.ll_quick_option_concern:
        context.startActivity(new Intent(context, SearchActivity.class));
        break;
      case R.id.ll_quick_option_circle: context.startActivity(new Intent(context, PersonalCircleCreateActivity.class));
        break;
      case R.id.ll_quick_option_task:
       Intent i= new Intent(context, PersonalActivity.class);
        i.putExtra("page",2);
        context.startActivity(i);
        break;
      default:
        break;
    }
    if (mListener != null) {
      mListener.onQuickOptionClick(id);
    }
    dismiss();
  }
}