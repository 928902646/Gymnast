package com.gymnast.view.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.HomeActivity;
import com.gymnast.view.home.adapter.ViewPagerAdapter;
import java.util.ArrayList;

/**
 * Created by Cymbi on 2016/9/7.
 */
public class GuideActivity extends ImmersiveActivity implements ViewPager.OnPageChangeListener {
    private ViewPager vp;
    private TextView boot_in;
    private ArrayList<View> views;
    private ViewPagerAdapter vpAdapter;
    private ImageView[] dots;
    private int[] ids = { R.id.iv1, R.id.iv2, R.id.iv3 };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        initViews();
        initDots();
    }
    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.activity_boot_a, null));
        views.add(inflater.inflate(R.layout.activity_boot_b, null));
        views.add(inflater.inflate(R.layout.activity_boot_c, null));
        vpAdapter = new ViewPagerAdapter(views, this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        boot_in = (TextView) views.get(2).findViewById(R.id.boot_in);
        boot_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(GuideActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
        vp.setOnPageChangeListener(this);
    }
    private void initDots() {
        dots = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) findViewById(ids[i]);
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }
    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < ids.length; i++) {
            if (position == i) {
                dots[i].setImageResource(R.mipmap.login_point_selected);
            } else {
                dots[i].setImageResource(R.mipmap.login_point);
            }
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
