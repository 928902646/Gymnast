package com.gymnast.view.home.view;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.home.HomeActivity;
import com.gymnast.view.home.adapter.FragmentAdapter;
import com.gymnast.utils.InputWatcherUtil;
import java.util.ArrayList;
import java.util.List;
public class HomeSearchResultAcitivity extends NewBaseActivity implements View.OnClickListener{
   public static EditText etSearch;
    TextView tvConfirm,tvCancel;
    Button btnCancel;
    LinearLayout llUser,llCircle,llLive,llActive,llDynamic,llTieZi;
    TextView tvUserA,tvUserB,tvCircleA,tvCircleB,tvLiveA,tvLiveB,tvActiveA,tvActiveB,tvDynamicA,tvDynamicB,tvTieZiA,tvTieZiB;
    ViewPager viewPager;
    List<Fragment> mFragmentList=new ArrayList<Fragment>();
    SearchActiveFragment activeFragment;//活动
    SearchCircleFragment circleFragment;//圈子
    SearchTieZiFragment itemFragment;//帖子
    SearchDynamicFragment dynamicFragment;//动态
    SearchUserFragment userFragment;//用户
    SearchLiveFragment liveFragment;//直播
    private FragmentAdapter mFragmentAdapter;
     LinearLayout [] llContainer;
    private static String searchText;
    static int type;
   static String []searchHot=new String[]{"NBA","CBA","WTA","WNBA","李娜","球队","赛事","帖子",};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search_result);
        searchText=getIntent().getStringExtra("searchText");
        type=getIntent().getIntExtra("type", 9);
        setViews();
        setListeners();
    }
    public static String getSearchText(){
        if (!(type==9)){
            return searchHot[type];
        }
        return searchText;
    }
    private void setListeners() {
        etSearch.setText((type==9)?searchText:searchHot[type]);
        btnCancel.setVisibility(View.VISIBLE);
        etSearch.addTextChangedListener(new InputWatcherUtil(btnCancel, etSearch, tvConfirm, tvCancel));
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userFragment.adapter.getFilter().filter(charSequence);
                circleFragment.adapter.getFilter().filter(charSequence);
                dynamicFragment.adapter.getFilter().filter(charSequence);
                activeFragment.adapter.getFilter().filter(charSequence);
                liveFragment.adapter.getFilter().filter(charSequence);
                itemFragment.adapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        btnCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        llContainer=new LinearLayout[]{llUser,llCircle,llActive,llLive,llDynamic,llTieZi};
        final TextView[] tvTexts=new TextView[]{tvUserA,tvCircleA,tvActiveA,tvLiveA,tvDynamicA,tvTieZiA};
        final TextView[] tvBgs=new TextView[]{tvUserB,tvCircleB,tvActiveB,tvLiveB,tvDynamicB,tvTieZiB};
        for (int i=0;i<llContainer.length;i++){
            final int finalI = i;
            llContainer[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvTexts[finalI].setTextColor(Color.RED);
                    tvBgs[finalI].setBackgroundResource(R.color.text_red);
                    viewPager.setCurrentItem(finalI);
                    int j=finalI;
                    for (int k=0;k<tvBgs.length;k++){
                        if (k!=j){
                            tvTexts[k].setTextColor(Color.parseColor("#333333"));
                            tvBgs[k].setBackgroundResource(R.color.text_white);
                        }
                    }
                }
            });
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                tvTexts[position].setTextColor(Color.RED);
                tvBgs[position].setBackgroundResource(R.color.text_red);
                for (int k = 0; k < tvBgs.length; k++) {
                    if (k != position) {
                        tvTexts[k].setTextColor(Color.parseColor("#333333"));
                        tvBgs[k].setBackgroundResource(R.color.text_white);
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tvCancel.setOnClickListener(this);
    }
    private void setViews() {
        etSearch= (EditText) findViewById(R.id.etSearch);
        btnCancel= (Button) findViewById(R.id.btnCancel);
        tvConfirm= (TextView) findViewById(R.id.tvConfirm);
        tvCancel= (TextView) findViewById(R.id.tvCancel);
        llUser= (LinearLayout) findViewById(R.id.llUser);
        llCircle= (LinearLayout) findViewById(R.id.llCircle);
        llLive= (LinearLayout) findViewById(R.id.llLive);
        llActive= (LinearLayout) findViewById(R.id.llActive);
        llDynamic= (LinearLayout) findViewById(R.id.llDynamic);
        llTieZi= (LinearLayout) findViewById(R.id.llTieZi);
        tvUserA= (TextView) findViewById(R.id.tvUserA);
        tvUserB= (TextView) findViewById(R.id.tvUserB);
        tvCircleA= (TextView) findViewById(R.id.tvCircleA);
        tvCircleB= (TextView) findViewById(R.id.tvCircleB);
        tvLiveA= (TextView) findViewById(R.id.tvLiveA);
        tvLiveB= (TextView) findViewById(R.id.tvLiveB);
        tvActiveA= (TextView) findViewById(R.id.tvActiveA);
        tvActiveB= (TextView) findViewById(R.id.tvActiveB);
        tvDynamicA= (TextView) findViewById(R.id.tvDynamicA);
        tvDynamicB= (TextView) findViewById(R.id.tvDynamicB);
        tvTieZiA= (TextView) findViewById(R.id.tvTieZiA);
        tvTieZiB= (TextView) findViewById(R.id.tvTieZiB);
        viewPager= (ViewPager) findViewById(R.id.viewPager);
        userFragment= new SearchUserFragment();
        circleFragment= new SearchCircleFragment();
        activeFragment=new SearchActiveFragment();
        liveFragment= new SearchLiveFragment();
        dynamicFragment= new SearchDynamicFragment();
        itemFragment= new SearchTieZiFragment();
        mFragmentList.add(userFragment);
        mFragmentList.add(circleFragment);
        mFragmentList.add(activeFragment);
        mFragmentList.add(liveFragment);
        mFragmentList.add(dynamicFragment);
        mFragmentList.add(itemFragment);
        mFragmentAdapter =new FragmentAdapter(this.getSupportFragmentManager(),mFragmentList);
        viewPager.setAdapter(mFragmentAdapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setCurrentItem(0);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCancel:
                etSearch.setText("");
                btnCancel.setVisibility(View.GONE);
                tvConfirm.setVisibility(View.GONE);
                tvCancel.setVisibility(View.VISIBLE);
                break;
            case R.id.tvConfirm:
                finish();
                break;
            case R.id.tvCancel:
                Intent intent1=new Intent(HomeSearchResultAcitivity.this,HomeActivity.class);
                HomeSearchResultAcitivity.this.startActivity(intent1);
                break;
        }
    }
}
