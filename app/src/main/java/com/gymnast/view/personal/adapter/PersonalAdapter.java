package com.gymnast.view.personal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by yf928 on 2016/7/18.
 */
public class PersonalAdapter extends FragmentPagerAdapter {
    private List<Fragment> views;
    private List<String> titles;
    public PersonalAdapter(FragmentManager fm,List<Fragment> views,List<String>titles) {
        super(fm);
        if(views.size()==0){
            this.views=new ArrayList<>();
        }else {
            this.views=views;
        }
        if(titles.size()==0){
            this.titles=new ArrayList<>();
        }
        this.titles=titles;
    }
    @Override
    public Fragment getItem(int position) {
        return views.get(position);
    }
    @Override
    public int getCount() {
        return views.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
