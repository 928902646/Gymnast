package com.gymnast.view.hotinfoactivity.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.hotinfoactivity.customView.MyCMDTextView;
import com.gymnast.view.hotinfoactivity.fragment.AuditedFragment;
import com.gymnast.view.hotinfoactivity.fragment.NotAuditFragment;
import com.gymnast.view.personal.adapter.PersonalAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cymbi on 2016/9/12.
 */
public class AuditActivity extends ImmersiveActivity implements View.OnClickListener{

    private TextView tvAuditNumber;
    public static MyCMDTextView tvAudit;
    private TabLayout tbAuditTab;
    private ViewPager vpAudit;
    private List<Fragment> mFragment = new ArrayList<Fragment>();
    private List<String> mTitle=new ArrayList<String>();
    private CheckBox cbSelect;
   public static boolean ischeck;
    private boolean isShow;
    public Handler handler=new Handler();
    private RelativeLayout re_layout;
    private CheckBoxShowCMD cmd;
    public void setOnItemClickListener(CheckBoxShowCMD cmd) {
        this.cmd = cmd;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit);
        Message msg= new Message();
        initview();
        setListener();
        String str= tvAudit.getText().toString();
        msg.obj=str;
        msg.what=2;
        handler.sendMessage(msg);
        PersonalAdapter adapter = new PersonalAdapter(getSupportFragmentManager(),mFragment,mTitle);
        vpAudit.setAdapter(adapter);
        //tablayout和viewpager关联
        tbAuditTab.setupWithViewPager(vpAudit);
        //为tablayout设置适配器
       // tbAuditTab.setTabsFromPagerAdapter(adapter);
         tbAuditTab.setTabGravity(TabLayout.GRAVITY_FILL);
        tbAuditTab.setTabMode(TabLayout.MODE_FIXED);
    }

    private void setListener() {
        mTitle.add("未审核");
        mTitle.add("已审核");
        mFragment.add(new NotAuditFragment());
        mFragment.add(new AuditedFragment());
    }


    private void initview() {
        ImageView audit_back = (ImageView) findViewById(R.id.audit_back);
        tvAuditNumber = (TextView) findViewById(R.id.tvAuditNumber);
        tvAudit = (MyCMDTextView) findViewById(R.id.tvAudit);
        tvAudit.setCancel(false);
        vpAudit = (ViewPager) findViewById(R.id.vpAudit);
        tbAuditTab = (TabLayout) findViewById(R.id.tbAuditTab);
        re_layout = (RelativeLayout) findViewById(R.id.re);
        audit_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvAudit.setOnClickListener(this);
         vpAudit.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
             @Override
             public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
             }
             @Override
             public void onPageSelected(int position) {
                 if(position==0){
                     tvAudit.setVisibility(View.VISIBLE);
                 }else {
                     tvAudit.setVisibility(View.GONE);
                 }
             }
             @Override
             public void onPageScrollStateChanged(int state) {
             }
         });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvAudit:
                if (cmd!=null){
                    cmd.showOrHide(ischeck);
                    if(ischeck){
                        tvAudit.setText("审核");
                    }else {
                        tvAudit.setText("取消");
                    }
                    ischeck=!ischeck;
                    tvAudit.setCancel(ischeck);
                }

                if (isShow){
                    re_layout.setVisibility(View.GONE);
                }else {
                    re_layout.setVisibility(View.VISIBLE);
                }
                isShow=!isShow;
                break;
        }
    }

    public interface CheckBoxShowCMD{
        void showOrHide(boolean isShow);
    }
}
