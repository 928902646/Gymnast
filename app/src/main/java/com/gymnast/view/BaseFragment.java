package com.gymnast.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
public abstract class BaseFragment extends Fragment {
  public CompositeSubscription mCompositeSubscription;
  public Subscription mSubscription;
  private Unbinder unbinder;
   @Override
  public View onCreateView(LayoutInflater inflater,  ViewGroup container,Bundle savedInstanceState) {
    View view = inflater.inflate(this.getLayout(), container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }
  @Override public void onActivityCreated( Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mCompositeSubscription = new CompositeSubscription();
    initViews(savedInstanceState);
    initData();
    initListeners();
  }
  @Override public void onDestroyView() {
    unbinder.unbind();
    super.onDestroyView();
    mCompositeSubscription.unsubscribe();
    mCompositeSubscription = null;
    if (mSubscription != null) {
      if (!mSubscription.isUnsubscribed()) {
        mSubscription.unsubscribe();
      }
    }
  }
  protected abstract int getLayout();
  protected abstract void initViews(Bundle savedInstanceState);
  protected abstract void initListeners();
  protected abstract void initData();
}
