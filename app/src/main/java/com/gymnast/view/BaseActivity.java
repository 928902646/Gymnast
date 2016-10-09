package com.gymnast.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
public abstract class BaseActivity extends AppCompatActivity {
  public CompositeSubscription mCompositeSubscription;
  public Subscription mSubscription;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mCompositeSubscription = new CompositeSubscription();
    setContentView(this.getLayout());
    ButterKnife.bind(this);
    initToolbar(savedInstanceState);
    initViews(savedInstanceState);
    initData();
    initListeners();
  }
  @Override protected void onDestroy() {
    super.onDestroy();
    mCompositeSubscription.unsubscribe();
    mCompositeSubscription = null;
    if (mSubscription != null) {
      if (!mSubscription.isUnsubscribed()) {
        mSubscription.unsubscribe();
      }
    }
  }
  protected abstract int getLayout();
  protected abstract void initToolbar(Bundle savedInstanceState);
  protected abstract void initViews(Bundle savedInstanceState);
  protected abstract void initListeners();
  protected abstract void initData();
}
