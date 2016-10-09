package com.gymnast.view.personal.listener;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by zzqybyb19860112 on 2016/9/22.
 */
public class WrapContentLinearLayoutManager extends LinearLayoutManager {
         public WrapContentLinearLayoutManager(Context context) {
         super(context);
         }
         public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
         super(context, orientation, reverseLayout);
         }
         public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
         super(context, attrs, defStyleAttr, defStyleRes); }
         @Override
         public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
             try {
         super.onLayoutChildren(recycler, state);
         } catch (IndexOutOfBoundsException e) {
         e.printStackTrace(); } }
        }
