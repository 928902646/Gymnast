package com.gymnast.view.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.CacheUtils;
import com.gymnast.utils.DialogUtil;
import com.gymnast.utils.JSONParseUtil;
import com.gymnast.utils.LiveUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.view.BaseFragment;
import com.gymnast.view.live.activity.MoreLiveActivity;
import com.gymnast.view.live.entity.LiveItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
public class StandFragment extends BaseFragment implements View.OnClickListener{
  @BindView(R.id.stand_tab)  TabLayout tab;
  @BindView(R.id.stand_vp)   ViewPager vp;
  ImageView ivBigPic1,ivBigPic2,ivBigPic3,ivBigPic4;
  TextView tvLiveNumber,tvTitle,stand_more;
  LinearLayout llYuGao;
  ArrayList<String> bitmapList=new ArrayList<>();
  ArrayList<String> titleList=new ArrayList<>();
  ArrayList<String> numberList=new ArrayList<>();
  List<LiveItem> liveItems=new ArrayList<>();
  int k=0;
  LiveItem liveItem=null;
  LiveItem liveItemA=null;
  LiveItem liveItemB=null;
  LiveItem liveItemC=null;
  LiveItem liveItemD=null;
  public static final int HANDLE_PICS=1;
  public static final int DATA_READY_OK=2;
  public static final int UPDATE_STATE_OK=3;
  public static final int MAINUSER_IN_OK=4;
  public static final int MAINUSER_IN_ERROR=5;
  public static final int OTHERUSER_IN_OK=6;
  public static final int OTHERUSER_IN_ERROR=7;
  Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what){
        case UPDATE_STATE_OK:
          Toast.makeText(getActivity(), "您开启了直播", Toast.LENGTH_SHORT).show();
          LiveUtil.doNext(getActivity(), liveItem);
          break;
        case MAINUSER_IN_OK:
          Toast.makeText(getActivity(),"您开启了直播",Toast.LENGTH_SHORT).show();
          LiveUtil.doNext(getActivity(), liveItem);
          break;
        case MAINUSER_IN_ERROR:
          DialogUtil.goBackToLogin(getActivity(), "是否重新登陆？", "账号在其他地方登陆,您被迫下线！！！");
          break;
        case OTHERUSER_IN_OK:
          Toast.makeText(getActivity(),"您已进入直播室",Toast.LENGTH_SHORT).show();
          LiveUtil.doNext(getActivity(), liveItem);
          break;
        case OTHERUSER_IN_ERROR:
          DialogUtil.goBackToLogin(getActivity(), "是否重新登陆？", "账号在其他地方登陆,您被迫下线！！！");
          break;
        case DATA_READY_OK:
          if (timer==null){
            TimerTask task=new TimerTask() {
              @Override
              public void run() {
                k+=1;
                handler.sendEmptyMessage(HANDLE_PICS) ;
              }
            };
            timer=new Timer();
            timer.schedule(task,0, 5000);
          }
          break;
        case HANDLE_PICS:
          int bigIndex=k%4;
          int smallIndexA=0;
          int smallIndexB=0;
          int smallIndexC=0;
          switch (bigIndex){
            case 0:smallIndexA=1;smallIndexB=2;smallIndexC=3;break;
            case 1:smallIndexA=2;smallIndexB=3;smallIndexC=0;break;
            case 2:smallIndexA=3;smallIndexB=0;smallIndexC=1;break;
            case 3:smallIndexA=0;smallIndexB=1;smallIndexC=2;break;
          }
          liveItemA=liveItems.get(bigIndex);
          liveItemB=liveItems.get(smallIndexA);
          liveItemC=liveItems.get(smallIndexB);
          liveItemD=liveItems.get(smallIndexC);
          String  bitmapBig=bitmapList.get(bigIndex);
          String bitmapSmallA=bitmapList.get(smallIndexA);
          String bitmapSmallB=bitmapList.get(smallIndexB);
          String bitmapSmallC=bitmapList.get(smallIndexC);
          PicassoUtil.handlePic(getActivity(), bitmapBig, ivBigPic1, 1280, 720);
          tvLiveNumber.setText(numberList.get(bigIndex));
          tvTitle.setText(titleList.get(bigIndex));
          PicassoUtil.handlePic(getActivity(), bitmapSmallA, ivBigPic2, 1280, 720);
          PicassoUtil.handlePic(getActivity(), bitmapSmallB, ivBigPic3, 1280, 720);
          PicassoUtil.handlePic(getActivity(), bitmapSmallC, ivBigPic4, 1280, 720);
          ivBigPic1.invalidate();
          tvLiveNumber.invalidate();
          tvTitle.invalidate();
          ivBigPic2.invalidate();
          ivBigPic3.invalidate();
          ivBigPic4.invalidate();
          llYuGao.setVisibility(View.GONE);
          llYuGao.invalidate();
          break;
      }
    }
  };
  String[] titles = new String[]{"广场", "大咖", "世界杯", "NBA", "WTA", "冠军杯", "亚冠"};
  private Timer timer;
  public static FragmentStatePagerAdapter pa;
   StandSquareFragment standSquareFragment;
  ACupFragment aCupFragment;
  AsiaCupFragment asiaCupFragment;
  BigCupFragment bigCupFragment;
  NBAFragment nbaFragment;
  WorldCupFragment worldCupFragment;
  WTAFragment wtaFragment;
  FragmentManager manager;
  public static Fragment mCurrentFragment;
  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    tab.setTabGravity(TabLayout.GRAVITY_FILL);
    tab.setTabMode(TabLayout.MODE_SCROLLABLE);
    if(savedInstanceState==null){
      standSquareFragment = new StandSquareFragment();
      bigCupFragment = new BigCupFragment();
      worldCupFragment = new WorldCupFragment();
      nbaFragment = new NBAFragment();
      wtaFragment = new WTAFragment();
      aCupFragment = new ACupFragment();
      asiaCupFragment = new AsiaCupFragment();
      manager= getChildFragmentManager();
      FragmentTransaction transaction=manager.beginTransaction();
      transaction.add(standSquareFragment,""+1);
      transaction.add(bigCupFragment, "" + 2);
      transaction.add(worldCupFragment, "" + 3);
      transaction.add(nbaFragment, "" + 4);
      transaction.add(wtaFragment, "" + 5);
      transaction.add(aCupFragment, "" + 6);
      transaction.add(asiaCupFragment, "" + 7);
      transaction.commit();
    }
     pa = new FragmentStatePagerAdapter(manager) {
      @Override public Fragment getItem(int position) {
          if (position==0){
            return standSquareFragment.newInstance("测试", "" + 1, getActivity());
          }else if (position==1){
            return bigCupFragment.newInstance("测试", "" + 2,getActivity());
          }else if (position==2){
            return worldCupFragment.newInstance("测试", "" + 3,getActivity());
          }else if (position==3){
            return nbaFragment.newInstance("测试", "" + 4,getActivity());
          }else if (position==4){
            return wtaFragment.newInstance("测试", "" + 5,getActivity());
          }else if (position==5){
            return aCupFragment.newInstance("测试", "" + 6,getActivity());
          }else if (position==6){
            return asiaCupFragment.newInstance("测试", "" + 7,getActivity());
          }
        return null;
      }
       @Override
       public void destroyItem(ViewGroup container, int position, Object object) {
       }
       @Override public int getCount() {
        return titles.length;
      }
      @Override public CharSequence getPageTitle(int position) {
        return titles[position];
      }
       @Override
       public void setPrimaryItem(ViewGroup container, int position, Object object) {
          mCurrentFragment = (Fragment)object;
          super.setPrimaryItem(container, position, object);
          }
     };
    vp.setAdapter(pa);
    vp.setOffscreenPageLimit(6);
    tab.setupWithViewPager(vp);
    initData();
  }
  public static Fragment getCurrentFragment() {
    return mCurrentFragment;
  }
  @Override protected int getLayout() {
    return R.layout.fragment_stand;
  }
  @Override protected void initViews(Bundle savedInstanceState) {
    ivBigPic1= (ImageView) getActivity().findViewById(R.id.ivBigPic1);
    ivBigPic2= (ImageView) getActivity().findViewById(R.id.ivBigPic2);
    ivBigPic3= (ImageView) getActivity().findViewById(R.id.ivBigPic3);
    ivBigPic4= (ImageView) getActivity().findViewById(R.id.ivBigPic4);
    tvLiveNumber= (TextView) getActivity().findViewById(R.id.tvLiveNumber);
    stand_more= (TextView) getActivity().findViewById(R.id.stand_more);
    tvTitle= (TextView) getActivity().findViewById(R.id.tvTitle);
    llYuGao= (LinearLayout) getActivity().findViewById(R.id.llYuGao);
  }
  @Override protected void initListeners() {
    ivBigPic1.setOnClickListener(this);
    ivBigPic2.setOnClickListener(this);
    ivBigPic3.setOnClickListener(this);
    ivBigPic4.setOnClickListener(this);
    stand_more.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getActivity().startActivity(new Intent(getActivity(), MoreLiveActivity.class));
      }
    });
  }
   protected void initData() {
     ArrayList<String> cacheData= (ArrayList<String>) CacheUtils.readJson(getActivity(), StandFragment.this.getClass().getName() + ".json");
     if (cacheData==null||cacheData.size()==0) {
       new Thread() {
         @Override
         public void run() {
             String uri = API.BASE_URL + "/v1/deva/get";
             HashMap<String, String> parms = new HashMap<String, String>();
             parms.put("area", "2");
             String result = PostUtil.sendPostMessage(uri, parms);
            JSONParseUtil.parseNetDataStandBanner(getActivity(),result,StandFragment.this.getClass().getName() + ".json",bitmapList,titleList,numberList,liveItems,handler,DATA_READY_OK);
         }
       }.start();
     }else {
       JSONParseUtil.parseLocalDataStandBanner(getActivity(), StandFragment.this.getClass().getName() + ".json", bitmapList, titleList, numberList, liveItems, handler, DATA_READY_OK);
     }
  }
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.ivBigPic1:
        liveItem=liveItemA;
        LiveUtil.doIntoLive(getActivity(), handler, liveItem);
        break;
      case R.id.ivBigPic2:
        liveItem=liveItemB;
        LiveUtil.doIntoLive(getActivity(), handler, liveItem);
        break;
      case R.id.ivBigPic3:
        liveItem=liveItemC;
        LiveUtil.doIntoLive(getActivity(), handler, liveItem);
        break;
      case R.id.ivBigPic4:
        liveItem=liveItemD;
        LiveUtil.doIntoLive(getActivity(), handler, liveItem);
        break;
    }
  }
  @Override
  public void onDestroy() {
    handler.removeMessages(HANDLE_PICS);
    timer.cancel();
    super.onDestroy();
  }
}
