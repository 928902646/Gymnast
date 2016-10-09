package com.gymnast.view.personal.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.pack.view.MyConcernFragment;
import com.gymnast.view.pack.view.ChoicenessCircleFragment;
import com.gymnast.view.pack.view.StarFragment;
import com.gymnast.view.personal.adapter.PersonalAdapter;
import com.gymnast.view.personal.fragment.AllFragment;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by yf928 on 2016/7/20.
 */
public class OrdersActivity extends ImmersiveActivity {
    private List<Fragment> mFragment=new ArrayList<Fragment>();
    private List<String> mTitle = new ArrayList<String>();
    private TabLayout tab;
    private ViewPager vp;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        initview();
        PersonalAdapter adapterd = new PersonalAdapter(getSupportFragmentManager(),mFragment,mTitle);
        vp.setAdapter(adapterd);
        //tablayout和viewpager关联
        tab.setupWithViewPager(vp);
        //为tablayout设置适配器
        tab.setTabsFromPagerAdapter(adapterd);
        tab.setTabGravity(TabLayout.GRAVITY_FILL);
        tab.setTabMode(TabLayout.MODE_FIXED);
    }
    private void initview() {
        tab=(TabLayout) findViewById(R.id.personal_orders_tab);
        vp=(ViewPager)findViewById(R.id.personal_orders_vp);
        back=(ImageView)findViewById(R.id.back);
        mTitle.add("全部");
        mTitle.add("待付款");
        mTitle.add("待发货");
        mTitle.add("待收货");
        mTitle.add("待评价");
        mFragment.add(new AllFragment());
        mFragment.add(new MyConcernFragment());
        mFragment.add(new StarFragment());
        mFragment.add(new ChoicenessCircleFragment());
        mFragment.add(new ChoicenessCircleFragment());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
