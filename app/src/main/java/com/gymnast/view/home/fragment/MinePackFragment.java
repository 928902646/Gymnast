package com.gymnast.view.home.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gymnast.R;
import com.gymnast.view.pack.adapter.MyFragmentPagerAdapter;
import com.gymnast.view.pack.view.MyConcernFragment;
import com.gymnast.view.pack.view.ChoicenessCircleFragment;
import com.gymnast.view.pack.view.StarFragment;
import java.util.ArrayList;
import java.util.List;
public class MinePackFragment extends Fragment {
    private List<String> mTitle=new ArrayList<String>();
    private List<Fragment> mFragment = new ArrayList<Fragment>();
    private TabLayout tab;
    private ViewPager vp;
    private View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_mine_pack, null);
        setview();
        initView();
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getChildFragmentManager(), mTitle, mFragment);
        vp.setAdapter(adapter);
        //tablayout和viewpager关联
        tab.setupWithViewPager(vp);
        //为tablayout设置适配器
        tab.setTabGravity(TabLayout.GRAVITY_FILL);
        tab.setTabMode(TabLayout.MODE_FIXED);
        vp.setOffscreenPageLimit(2);
        return  view;
    }
    private void setview() {
        tab =(TabLayout) view.findViewById(R.id.pack_tab);
        vp =(ViewPager) view.findViewById(R.id.pack_vp);
    }
    private void initView() {
        mTitle.add("好友动态");
        mTitle.add("大咖动态");
        mTitle.add("精选圈子");
        mFragment.add(new MyConcernFragment());
        mFragment.add(new StarFragment());
        mFragment.add(new ChoicenessCircleFragment());
    }
}
