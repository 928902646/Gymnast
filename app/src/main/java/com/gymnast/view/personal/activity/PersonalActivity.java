package com.gymnast.view.personal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.DialogUtil;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.pack.view.MyConcernFragment;
import com.gymnast.view.personal.adapter.PersonalAdapter;
import com.gymnast.view.personal.contact.PersonFenSiActivity;
import com.gymnast.view.personal.contact.PersonalActivityContact;
import com.gymnast.view.personal.fragment.CircleListFragment;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
/**
 * Created by yf928 on 2016/7/18.
 */
public class PersonalActivity extends ImmersiveActivity {
    private TextView line1,line2,mNickname,title,my_dynamic,me_fans,tvConcern;
    private TabLayout tab;
    private ViewPager vp;
    private List<String> mTitle=new ArrayList<String>();
    private List<Fragment> mFragment = new ArrayList<Fragment>();
    private Toolbar tb;
    private AppBarLayout appbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private State state;
    private Button order;
    private ImageView back,me_msg,setting;
    private SharedPreferences share;
    private String token,id,name,return_signature;
    private com.makeramen.roundedimageview.RoundedImageView me_head;
    private Button sign;
    private SimpleDateFormat sdf =new SimpleDateFormat("dd");
    private TextView authentication;
    private int return_authenticate;
    private String return_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token","");
        Log.e("token",token);
        id = share.getString("UserId","");
        setview();
        initview();
        setdata();
        PersonalAdapter adapter = new PersonalAdapter(getSupportFragmentManager(),mFragment,mTitle);
        vp.setAdapter(adapter);
        //tablayout和viewpager关联
        tab.setupWithViewPager(vp);
        //viewpager加载2个页面，保证下面的fragment不会被销毁导致重复加载
        vp.setOffscreenPageLimit(2);
        //为tablayout设置适配器
        tab.setTabsFromPagerAdapter(adapter);
        tab.setTabGravity(TabLayout.GRAVITY_FILL);
        tab.setTabMode(TabLayout.MODE_FIXED);
        setSupportActionBar(tb);
        //下面三个是主页点击任务跳转到个人中心钱包界面来；
        Intent intent = getIntent();
        int page=intent.getIntExtra("page",0);
        vp.setCurrentItem(page);
        tab.setVisibility(View.GONE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setdata();
    }
    private void setdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri2= API.BASE_URL+"/v1/account/querySign";
                HashMap<String,String> params2=new HashMap<String, String>();
                params2.put("token", token);
                params2.put("accountId", id);
                String result2 = GetUtil.sendGetMessage(uri2,params2);
                try {
                    JSONObject  obj=  new JSONObject(result2);
                    JSONObject data=obj.getJSONObject("data");
                    long markTime=data.getLong("markTime");
                    String mark= sdf.format(new Date(markTime));
                    String now= sdf.format(new Date());
                    final int marktime=Integer.valueOf(mark );
                    final int nowtime=Integer.valueOf(now);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(marktime<nowtime){
                                sign.setText("签到");
                            }else {
                                sign.setText("已签到");
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String uri = API.BASE_URL + "/v1/account/center_info";
                HashMap<String, String> params = new HashMap<>();
                params.put("token", token);
                Log.e("token",token);
                params.put("account_id", id);
                String result = GetUtil.sendGetMessage(uri, params);
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getInt("state") == 401) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtil.goBackToLogin(PersonalActivity.this, "是否重新登陆？", "账号在其他设备登陆,您已下线！！！");
                            }
                        });
                    }
                    JSONObject data = obj.getJSONObject("data");
                    name = data.getString("nickname");
                    return_avatar = data.getString("avatar");
                    return_authenticate=data.getInt("authenticate");
                    final int Concern = data.getInt("gz");
                    final int fans= data.getInt("fs");
                    final int dynamic= data.getInt("dt");
                    //final Bitmap bitmap= PicUtil.getImageBitmap(API.IMAGE_URL + return_avatar);
                    if (obj.getInt("state") == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mNickname.setText(name);
                              //  me_head.setImageBitmap(bitmap);
                                PicassoUtil.handlePic(PersonalActivity.this, PicUtil.getImageUrlDetail(PersonalActivity.this, StringUtil.isNullAvatar(return_avatar), 320, 320),me_head,320,320);
                                tvConcern.setText(Concern+"关注");
                                me_fans.setText(fans+"粉丝");
                                my_dynamic.setText(dynamic+"动态");
                                if(return_authenticate==0){
                                    authentication.setText("亲太懒了，还没有认证呢");
                                }
                                else {
                                    authentication.setText("认证进行中，亲不要着急哦");
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void setview() {
        line1=(TextView)findViewById(R.id.line1);
        line2=(TextView)findViewById(R.id.line2);
        authentication=(TextView)findViewById(R.id.authentication);
        tab=(TabLayout)findViewById(R.id.personal_tab);
        vp=(ViewPager) findViewById(R.id.personal_vp);
        tb=(Toolbar)findViewById(R.id.toolbar);
        mNickname=(TextView)findViewById(R.id.nickname);
        title=(TextView)findViewById(R.id.personal_name_title);
        me_fans=(TextView)findViewById(R.id.me_fans);
        my_dynamic=(TextView)findViewById(R.id.my_dynamic);
        tvConcern=(TextView)findViewById(R.id.tvConcern);
        appbar=(AppBarLayout)findViewById(R.id.personal_appbar);
        order=(Button) findViewById(R.id.order);
        sign=(Button) findViewById(R.id.sign);
        back=(ImageView)findViewById(R.id.me_back);
        me_msg=(ImageView)findViewById(R.id.me_msg);
        setting=(ImageView) findViewById(R.id.me_setting);
        me_head=(com.makeramen.roundedimageview.RoundedImageView) findViewById(R.id.me_head);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collp_toolbar_layout);
    }
    //枚举出Toolbar的三种状态
    private enum State{
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }
    private void initview() {
        mTitle.add("我的圈子");
     //   mTitle.add("我的收藏");
    //  mTitle.add("我的钱包");
        mFragment.add(new CircleListFragment());
      //  mFragment.add(new MyConcernFragment());
       // mFragment.add(new WalletFragmengt());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        me_fans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PersonalActivity.this,PersonFenSiActivity.class);
                startActivity(i);
            }
        });
        my_dynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PersonalActivity.this,PersonalDynamicActivity.class);
                startActivity(i);
            }
        });
        tvConcern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PersonalActivity.this,PersonalActivityContact.class);
                startActivity(i);
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PersonalActivity.this,OrdersActivity.class);
                startActivity(i);
            }
        });
        authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent i=new Intent(PersonalActivity.this,PersonalAuthenticationActivity.class);
                    startActivity(i);
            }
        });
        /**
         * 通过枚举修改AppBarLyout状态
         */
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != State.EXPANDED) {
                        state = State.EXPANDED;//修改状态标记为展开
                        title.setVisibility(View.GONE);
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != State.COLLAPSED) {
                        title.setVisibility(View.VISIBLE);//显示title控件
                        state = State.COLLAPSED;//修改状态标记为折叠
                        title.setText(name);//设置title为用户名
                    }
                } else {
                    if (state != State.INTERNEDIATE) {
                        if (state == State.COLLAPSED) {
                            title.setVisibility(View.GONE);//由折叠变为中间状态时隐藏播放按钮
                        }
                        state = State.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PersonalActivity.this,PersonSettingActivity.class);
                startActivity(i);
            }
        });
        me_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PersonalActivity.this,PersonalAllMsgActivity.class);
                startActivity(i);
            }
        });
        me_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(PersonalActivity.this,ImageActivity.class);
                i.putExtra("IMAGE",API.IMAGE_URL+return_avatar);
                startActivity(i);
            }
        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String uri= API.BASE_URL+"/v1/account/sign";
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put("token", token);
                        params.put("accountId", id);
                        String result = GetUtil.sendGetMessage(uri,params);
                        try {
                            JSONObject  obj=  new JSONObject(result);
                            if (obj.getInt("state")==200){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PersonalActivity.this,"签到完成",Toast.LENGTH_SHORT).show();
                                        sign.setText("已签到");
                                    }
                                });
                            }else if(obj.getInt("state")==50200){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sign.setText("已签到");
                                        Toast.makeText(PersonalActivity.this,"签到失败！",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else if(obj.getInt("state")==50201){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sign.setText("已签到");
                                        Toast.makeText(PersonalActivity.this,"今天已经签到过了哟~请明天再签到",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if(obj.getInt("state")==50203){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PersonalActivity.this,"签到失败！服务器异常",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PersonalActivity.this,"还未登录哟~",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}
