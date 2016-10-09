package com.gymnast.view.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzqybyb19860112 on 2016/9/4.
 */
public class FragmentAdapter extends FragmentPagerAdapter {
  List<Fragment> fragmentList = new ArrayList<Fragment>();
  public FragmentAdapter(FragmentManager fm,List<Fragment> fragmentList) {
         super(fm);
         this.fragmentList = fragmentList;
         }
         @Override
        public Fragment getItem(int position) {
          return fragmentList.get(position);
         }
         @Override
         public int getCount() {
         return fragmentList.size();
         }
}
