package com.gymnast.view.pack.adapter;

import java.util.ArrayList;
import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> views;
    private List<String> titles;
    public MyFragmentPagerAdapter(FragmentManager fm, List<String> titles, List<Fragment> views) {
        super(fm);
        if (views.size() == 0) {
            this.views = new ArrayList<>();
        } else {
            this.views = views;
        }
        if (titles.size() == 0) {
            this.titles = new ArrayList<>();
        }
        this.titles = titles;
    }
    @Override
    public int getCount() {
        return views.size();
    }
    @Override
    public Fragment getItem(int arg0) {
        return views.get(arg0);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
