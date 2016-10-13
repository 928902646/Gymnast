package com.gymnast.view.personal.activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.CircleMainData;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.pack.view.MyConcernFragment;
import com.gymnast.view.personal.adapter.PersonalAdapter;
import com.gymnast.view.personal.fragment.CircleItemFragment;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yf928 on 2016/8/3.
 */
public class PersonalCircleActivity extends ImmersiveActivity implements View.OnClickListener{
    private List<String> mTitle=new ArrayList<String>();
    private List<Fragment> mFragment = new ArrayList<Fragment>();
    private ImageView back,write,menu,circle_head;
    private ViewPager vp;
    private TabLayout tab;
    private PopupWindow mPopupwindow;
    private boolean isShow;
    private TextView delete_circle,setting_circle,setting_admin,Title;
    private SharedPreferences share;
    private String id,token;
    private TextView circle_concernCount,circle_title,circle_ItemCount,isConcern,sign;
    private Toolbar tb;
    private int createId,concernType=4;
    private boolean isconcern;
    private  int CircleId;
    private FragmentManager fragmentManager;
    private int isMeet;
    private String circleMasterId;
    private String adminIds;
    private View contenview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_main);
        getInfo();
        setView();
        initView();
        PersonalAdapter adapter = new PersonalAdapter(getSupportFragmentManager(),mFragment,mTitle);
        vp.setAdapter(adapter);
        //tablayout和viewpager关联
        tab.setupWithViewPager(vp);
        //为tablayout设置适配器
        tab.setTabsFromPagerAdapter(adapter);
        tab.setTabGravity(TabLayout.GRAVITY_FILL);
        tab.setTabMode(TabLayout.MODE_FIXED);
        setSupportActionBar(tb);
        tab.setVisibility(View.GONE);
        getData();
    }
    public void getInfo() {
        share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token=share.getString("Token","");
        id = share.getString("UserId","");
        CircleId=getIntent().getIntExtra("CircleId",0);
        createId=getIntent().getIntExtra("createId",0);
    }
    private void setView() {
        back=(ImageView)findViewById(R.id.personal_back);
        circle_head=(ImageView)findViewById(R.id.circle_head);
        circle_title=(TextView)findViewById(R.id.circle_title);
        circle_concernCount=(TextView)findViewById(R.id.circle_concernCount);
        circle_ItemCount=(TextView)findViewById(R.id.circle_ItemCount);
        isConcern=  (TextView)findViewById(R.id.isConcern);
        sign=  (TextView)findViewById(R.id.sign);
        Title=  (TextView)findViewById(R.id.title);
        vp=(ViewPager) findViewById(R.id.pack_vp);
        tab=(TabLayout) findViewById(R.id.pack_tab);
        write=(ImageView) findViewById(R.id.personal_write);
        menu=(ImageView) findViewById(R.id.personal_menu);
        menu.setVisibility(createId==Integer.parseInt(id)?View.VISIBLE:View.GONE);
        tb=(Toolbar)findViewById(R.id.toolbar);
    }
    private void initView() {
        CircleItemFragment circleItemFragment =  new CircleItemFragment();
        MyConcernFragment myConcernFragment =  new MyConcernFragment();
        //要给Fragement传参数，必须是在创建fragment之后和绑定之前
        Bundle bundle = new Bundle();
        bundle.putInt("CircleId",CircleId);
        circleItemFragment.setArguments(bundle);
        mTitle.add("话题");
    //    mTitle.add("聊天室");
        mFragment.add(circleItemFragment);
     //   mFragment.add(myConcernFragment);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i= new Intent(PersonalCircleActivity.this,PersonalCirclePublishActivity.class);
                i.putExtra("circle_id",CircleId);
                startActivity(i);
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShow){
                    mPopupwindow.dismiss();
                }else{
                    showpopupwindow();
                }
                isShow=!isShow;
            }
        });
        isConcern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConcern.getText().toString().equals("关注")){
                    Concern();
                }else if(isConcern.getText().toString().equals("已关注")){
                    cancelConcern();
                }
            }
        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMeet==0){
                    sign.setText("签到");
                    sign.setBackgroundColor(getResources().getColor(R.color.green));
                    if(isconcern){
                        Sign();
                    }else {
                        Toast.makeText(PersonalCircleActivity.this,"还未关注无法签到哦",Toast.LENGTH_SHORT).show();
                    }
                }else if(isMeet==1){
                    Toast.makeText(PersonalCircleActivity.this,"你已经签到过了,请明天再来签到吧",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void Sign() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String uri=API.BASE_URL+"/v1/circle/meetting";
                    Map<String,String> params=new HashMap<String, String>();
                    params.put("token",token);
                    params.put("circleId",CircleId+"");
                    params.put("accountId",id);
                    String result=PostUtil.sendPostMessage(uri,params);
                    JSONObject obj=new JSONObject(result);
                    if (obj.getInt("state")==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalCircleActivity.this,"签到完成",Toast.LENGTH_SHORT).show();
                                sign.setText("已签到");
                                sign.setBackgroundColor(getResources().getColor(R.color.background));
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PersonalCircleActivity.this,"你已经签到过了,请明天再来签到吧",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }
    private void showpopupwindow(){
        contenview =  LayoutInflater.from(PersonalCircleActivity.this).inflate(R.layout.activity_popup_circle_menu,null);
        mPopupwindow= new PopupWindow(contenview);
        mPopupwindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupwindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //是否响应touch事件
        mPopupwindow.setTouchable(true);
        //设置可以获得焦点
        mPopupwindow.setFocusable(true);
        //设置弹窗内可点击
        mPopupwindow.setTouchable(true);
        //设置弹窗外可点击
        mPopupwindow.setOutsideTouchable(true);
        //外部是否可以点击
        mPopupwindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupwindow.showAsDropDown(menu);
        setting_admin = (TextView) contenview.findViewById(R.id.setting_admin);
        setting_circle = (TextView) contenview.findViewById(R.id.setting_circle);
        delete_circle = (TextView) contenview.findViewById(R.id.delete_circle);
        delete_circle.setOnClickListener(this);
        setting_circle.setOnClickListener(this);
        setting_admin.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete_circle:
                Toast.makeText(this,"删除圈子",Toast.LENGTH_SHORT).show();
                mPopupwindow.dismiss();
                break;
            case R.id.setting_circle:
                Intent i=new Intent(PersonalCircleActivity.this,SetCircleMain.class);
                i.putExtra("CircleId",CircleId);
                i.putExtra("circleMasterId",circleMasterId);
                startActivity(i);
                mPopupwindow.dismiss();
                break;
            case R.id.setting_admin:
                Intent intent=new Intent(PersonalCircleActivity.this,setCircleAdmin.class);
                intent.putExtra("CircleId",CircleId);
                intent.putExtra("adminIds",adminIds);
                startActivity(intent);
                mPopupwindow.dismiss();
                break;
        }
    }
    private void Concern() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url=API.BASE_URL+"/v1/cern/add";
                Map<String,String> parmas2=new HashMap<String, String>();
                parmas2.put("token",token);
                parmas2.put("concernId",CircleId+"");
                parmas2.put("accountId",id);
                parmas2.put("concernType",concernType+"");
                String result2= PostUtil.sendPostMessage(url,parmas2);
                try {
                    JSONObject object=new JSONObject(result2);
                    if(object.getInt("state")==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalCircleActivity.this,"关注成功",Toast.LENGTH_SHORT).show();
                                isConcern.setText("已关注");
                                isConcern.setBackgroundColor(getResources().getColor(R.color.background));
                                getData();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void cancelConcern() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url=API.BASE_URL+"/v1/cern/add";
                Map<String,String> parmas2=new HashMap<String, String>();
            }
        }).start();
    }
    public void  getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri= API.BASE_URL+"/v1/circle/getOne/";
                HashMap<String,String> params=new HashMap<String, String>();
                params.put("circleId",CircleId+"");
                params.put("accountId",id+"");
                String result= GetUtil.sendGetMessage(uri,params);
                try {
                    JSONObject obj=new JSONObject(result);
                    JSONObject data = obj.getJSONObject("data");
                    final JSONObject circle=data.getJSONObject("circle");
                    final int circleItemCount = data.getInt("circleItemCount");
                    final  int concernCount = data.getInt("concernCount");
                    isconcern =   data.getBoolean("isConcern");
                    isMeet = data.getInt("isMeet");
                    final String title=circle.getString("title");
                    circleMasterId=circle.getString("circleMasterId");
                    adminIds=circle.getString("adminIds");
                    String return_headImgUrl= StringUtil.isNullImage(circle.getString("headImgUrl"));
                    final Bitmap bitmap= PicUtil.getImageBitmap(return_headImgUrl);
                    if(obj.getInt("state")==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                circle_title.setText(title);
                                Title.setText(title);
                                circle_concernCount.setText(concernCount+"");
                                circle_ItemCount.setText(circleItemCount+"");
                                circle_head.setImageBitmap(bitmap);
                                if(isconcern){
                                    isConcern.setText("已关注");
                                    isConcern.setBackgroundColor(getResources().getColor(R.color.background));
                                }else {
                                    isConcern.setText("关注");
                                    isConcern.setBackgroundColor(getResources().getColor(R.color.green));
                                }
                                if(isMeet==0){
                                    sign.setText("签到");
                                    sign.setBackgroundColor(getResources().getColor(R.color.green));
                                }else if(isMeet==1){
                                    sign.setText("已签到");
                                    sign.setBackgroundColor(getResources().getColor(R.color.background));
                                }
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalCircleActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        getData();
        if(isconcern){
            isConcern.setText("关注");
            isConcern.setBackgroundColor(getResources().getColor(R.color.green));
        }else {
            isConcern.setText("已关注");
            isConcern.setBackgroundColor(getResources().getColor(R.color.background));
        }
        if(isMeet==0){
            sign.setText("签到");
            sign.setBackgroundColor(getResources().getColor(R.color.green));
        }else {
            sign.setText("已签到");
            sign.setBackgroundColor(getResources().getColor(R.color.background));
        }
    }
}
