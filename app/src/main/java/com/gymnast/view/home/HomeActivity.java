package com.gymnast.view.home;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import android.view.KeyEvent;

import com.gymnast.App;
import com.gymnast.MyReceiver;
import com.gymnast.R;
import com.gymnast.data.user.User;
import com.gymnast.event.Event;
import com.gymnast.event.EventBus;
import com.gymnast.view.BaseToolbarActivity;
import com.gymnast.view.dialog.QuickOptionDialog;
import com.gymnast.view.home.view.HomeSearchActivity;
import com.gymnast.view.home.fragment.HotInfoFragment;
import com.gymnast.view.home.fragment.MinePackFragment;
import com.gymnast.view.home.fragment.StandFragment;
import com.gymnast.view.personal.activity.PersonalActivity;
import com.gymnast.view.user.LoginActivity;
import com.gymnast.view.widget.BadgeView;
import com.gymnast.view.widget.CustomViewPager;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
public class HomeActivity extends BaseToolbarActivity implements View.OnClickListener {
  private final static int INFO = 0;
  private final static int STAND = 1;
  private final static int PACK = 2;
  @BindView(R.id.home_viewpager) CustomViewPager pager;
  @BindView(R.id.tab_info_btn) Button tabInfoBtn;
  @BindView(R.id.tab_stand_btn) Button tabStandBtn;
  @BindView(R.id.tab_play_btn) Button tabPlayBtn;
  @BindView(R.id.tab_pack_btn) Button tabPackBtn;
  @BindView(R.id.ivSearch) ImageView ivSearch;
  @BindView(R.id.toolbar_save)TextView save;
  @BindView(R.id.tab_quick_option_btn) ImageButton tabQuickOptionBtn;
  private int currentTabIndex = 0; // 当前tab下标
  List<Fragment> fragmentList;
  HotInfoFragment infoFragment = null;
  StandFragment standFragment = null;
  //PlayGroundFragment playFragment = null;
  MinePackFragment packFragment = null;
  private String token,id;
  private com.makeramen.roundedimageview.RoundedImageView toolbar_head;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      fragmentList = getSupportFragmentManager().getFragments();
      if (fragmentList != null && fragmentList.size() > 0) {
        boolean showFlag = false;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = fragmentList.size() - 1; i >= 0; i--) {
          Fragment fragment = fragmentList.get(i);
          if (fragment != null) {
            if (!showFlag) {
              ft.show(fragmentList.get(i));
              showFlag = true;
            } else {
              ft.hide(fragmentList.get(i));
            }
          }
        }
        ft.commit();
      }
    }
    toolbar_head=(com.makeramen.roundedimageview.RoundedImageView)findViewById(R.id.toolbar_head);
    SharedPreferences share = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
    token = share.getString("Token",null);
    id = share.getString("UserId","");
    save.setVisibility(View.GONE);
  }
  @Override protected void onResume() {
    super.onResume();
  }
  @Override protected void onPause() {
    super.onPause();
  }
  @Override protected void onDestroy() {
    super.onDestroy();
  }
  /**
   * 连按两次返回
   * @param keyCode
   * @param event
   * @return
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if(keyCode== KeyEvent.KEYCODE_BACK){
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("确认退出")
                .setIcon(R.mipmap.timg)
                .setMessage("请您选择是否退出系统？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    HomeActivity.this.finish();
                  }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                  }
                }).create();
        dialog.show();
    }
    return super.onKeyDown(keyCode, event);
  }
  @Override
    public void onConfigurationChanged(Configuration newConfig){
      super.onConfigurationChanged(newConfig);
           if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
         //land
             } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
          //port
             }
      }
  @Override protected int getLayout() {
    return R.layout.activity_home;
  }
  @Override protected void initViews(Bundle savedInstanceState) {
    //toolbarHead.setVisibility(View.GONE);
    fragmentList = new ArrayList<Fragment>();
    infoFragment = new HotInfoFragment();
    standFragment = new StandFragment();
    //playFragment = new PlayGroundFragment();
    packFragment = new MinePackFragment();
    fragmentList.add(infoFragment);
    fragmentList.add(standFragment);
    fragmentList.add(packFragment);
    FragmentPagerAdapter fragmentPagerAdapter = new HomeFragmentPagerAdapter(this.getSupportFragmentManager(), fragmentList);
    pager.setAdapter(fragmentPagerAdapter);
    pager.setOffscreenPageLimit(fragmentList.size());
    pager.setSlide(false);
    this.mActionBarHelper.setDisplayHomeAsUpEnabled(false);
    setTitle("居中");
  }
  @Override protected void initListeners() {
    tabInfoBtn.setOnClickListener(this);
    tabStandBtn.setOnClickListener(this);
    tabPlayBtn.setOnClickListener(this);
    tabPackBtn.setOnClickListener(this);
    tabQuickOptionBtn.setOnClickListener(this);
//    toolbarHead.setOnClickListener(this);
    pager.addOnPageChangeListener(new HomeViewPagerListener());
    // 登录监听
    mSubscription = EventBus.getInstance()
        .toObservable(Event.class)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Event>() {
          @Override public void call(Event objectsEvent) {
            if (objectsEvent.t != null) {
              if (objectsEvent.t.getClass().isAssignableFrom(User.class)) {
                Toast.makeText(HomeActivity.this, "登录刷新:" + ((User) objectsEvent.t).nickname,
                    Toast.LENGTH_SHORT).show();
              }
            }
          }
        });
    ivSearch.setOnClickListener(this);
  }
  public void initData() {
    setTabSelection(INFO);// 设置默认选中的tab页
  }
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.tab_info_btn:
          setTabSelection(INFO);
          break;
        case R.id.tab_stand_btn:
          setTabSelection(STAND);
          break;
        case R.id.tab_play_btn:
          //setTabSelection(PLAY);
          Intent i = new Intent(this, PersonalActivity.class);
          startActivity(i);
          break;
        case R.id.tab_pack_btn:
          setTabSelection(PACK);
          break;
        case R.id.tab_quick_option_btn:
          showQuickOption();
          break;
        case R.id.toolbar_head:
          if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
          } else {
            Intent inten = new Intent(HomeActivity.this, PersonalActivity.class);
            inten.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(inten);
          }
          break;
        case R.id.ivSearch:
          if (TextUtils.isEmpty(token)||!App.isStateOK) {
            Toast.makeText(this, "亲你还没有登录哟~", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
          }else {
            Intent intent = new Intent(HomeActivity.this, HomeSearchActivity.class);
            HomeActivity.this.startActivity(intent);
          }
          break;
        default:
          setTabSelection(INFO);
          break;
      }
    }
    // 显示快速操作界面
    private void showQuickOption() {
      final QuickOptionDialog dialog = new QuickOptionDialog(HomeActivity.this);
      if (TextUtils.isEmpty(token)||!App.isStateOK) {
        Toast.makeText(this, "亲你还没有登录哟~", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
      } else {
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
      }
    }
    /**
     * 根据传入的index参数来设置选中的tab页
     */
    private void setTabSelection(int index) {
      // 重置状态
      resetState();
      switch (index) {
        case INFO: {  // 信息
          tabInfoBtn.setTextColor(this.getResources().getColor(R.color.home_tab_pressed_color));
          tabInfoBtn.setSelected(true);
          break;
        }
        case STAND: { // 看台
          tabStandBtn.setTextColor(this.getResources().getColor(R.color.home_tab_pressed_color));
          tabStandBtn.setSelected(true);
          break;
        }
        case PACK: {  //操场
          tabPackBtn.setTextColor(this.getResources().getColor(R.color.home_tab_pressed_color));
          tabPackBtn.setSelected(true);
          break;
        }
      /*case PLAY: {  // 背包
        tabPlayBtn.setTextColor(this.getResources().getColor(R.color.home_tab_pressed_color));
        tabPlayBtn.setSelected(true);
        break;
      }*/
      }
      pager.setCurrentItem(index, false);
      currentTabIndex = index;
    }
    /**
     * 重置状态
     */
    private void resetState() {
      tabInfoBtn.setTextColor(this.getResources().getColor(R.color.home_tab_nor_color));
      tabInfoBtn.setSelected(false);
      tabStandBtn.setTextColor(this.getResources().getColor(R.color.home_tab_nor_color));
      tabStandBtn.setSelected(false);
      tabPackBtn.setTextColor(this.getResources().getColor(R.color.home_tab_nor_color));
      tabPackBtn.setSelected(false);
      tabPlayBtn.setTextColor(this.getResources().getColor(R.color.home_tab_nor_color));
      tabPlayBtn.setSelected(false);
    }
    /**
     * 设置tab标记
     */
    private void setBadgeView(BadgeView badgeView, int num) {
      if (num > 0) {
        badgeView.setText(String.valueOf(num));
        badgeView.show();
      } else {
        badgeView.hide();
      }
    }
    @Override
    public void onBackPressed() {//back to home
      Intent intent = new Intent(Intent.ACTION_MAIN, null);
      intent.addCategory(Intent.CATEGORY_HOME);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
      startActivity(intent);
    }
    private class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
      private List<Fragment> fragmentList;
      public HomeFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
      }
      @Override
      public Fragment getItem(int position) {
        return fragmentList.get(position);
      }
      @Override
      public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
      }
    }
    private class HomeViewPagerListener implements ViewPager.OnPageChangeListener {
      @Override
      public void onPageScrollStateChanged(int position) {
      }
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }
      @Override
      public void onPageSelected(int position) {
        setTabSelection(position);
      }
    }
    //设置主界面头像，现在先不要
  /*private void setdata() {
    new Thread(new Runnable() {
      public String return_avatar;
      @Override
      public void run() {
        String uri = API.BASE_URL + "/v1/account/center_info";
        HashMap<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("account_id", id);
        String result = GetUtil.sendGetMessage(uri, params);
        if (TextUtils.isEmpty(token)) {
        } else {
          try {
            JSONObject obj = new JSONObject(result);
            JSONObject data = obj.getJSONObject("data");
            return_avatar = data.getString("avatar");
            final Bitmap bitmap= PicUtil.getImageBitmap(API.IMAGE_URL + return_avatar);
            if (obj.getInt("state") == 200) {
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  toolbar_head.setImageBitmap(bitmap);
                }
              });
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }*/
  }