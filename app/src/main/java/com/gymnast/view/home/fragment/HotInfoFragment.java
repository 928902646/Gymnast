package com.gymnast.view.home.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.gymnast.App;
import com.gymnast.R;
import com.gymnast.data.DataManager;
import com.gymnast.data.hotinfo.ActivtyDevas;
import com.gymnast.data.hotinfo.CirleDevas;
import com.gymnast.data.hotinfo.ConcerDevas;
import com.gymnast.data.hotinfo.HotInfoData;
import com.gymnast.data.hotinfo.HotInfoService;
import com.gymnast.data.hotinfo.LiveDevas;
import com.gymnast.data.hotinfo.UserDevas;
import com.gymnast.data.net.Result;
import com.gymnast.utils.DialogUtil;
import com.gymnast.utils.LiveUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.BaseFragment;
import com.gymnast.view.home.adapter.HotInfoActivityUserRecyclerViewAdapter;
import com.gymnast.view.home.adapter.HotInfoCircleRecyclerViewAdapter;
import com.gymnast.view.home.adapter.HotInfoDynamicRecyclerViewAdapter;
import com.gymnast.view.home.adapter.HotInfoLiveRecyclerViewAdapter;
import com.gymnast.view.home.view.MorePostsActivity;
import com.gymnast.view.pack.view.MoreCircleActivity;
import com.gymnast.view.home.view.MoreDynamicActivity;
import com.gymnast.view.home.view.MoreUserActivity;
import com.gymnast.view.hotinfoactivity.activity.ActivityDetailsActivity;
import com.gymnast.view.hotinfoactivity.activity.CalendarActivityActivity;
import com.gymnast.view.hotinfoactivity.activity.HistoryActivityActivity;
import com.gymnast.view.hotinfoactivity.activity.NewActiveActivity;
import com.gymnast.view.live.activity.LiveActivity;
import com.gymnast.view.live.activity.MoreLiveActivity;
import com.gymnast.view.live.entity.LiveItem;
import com.gymnast.view.user.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
public class HotInfoFragment extends BaseFragment    implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
  HotInfoService hotInfoService;
  @BindView(R.id.hot_info_swipe_container) SwipeRefreshLayout swipeContainer;
  @BindView(R.id.home_hot_info_banner) ConvenientBanner banner;
  @BindView(R.id.home_hot_info_banner_title) TextView banner_title;
  @BindView(R.id.ivLiveMore) ImageView ivLiveMore;
  @BindView(R.id.ivDynamicMore) ImageView ivDynamicMore;
  @BindView(R.id.ivCircleMore) ImageView ivCircleMore;
  @BindView(R.id.ivUserMore) ImageView ivUserMore;
  @BindView(R.id.hot_info_live_list) RecyclerView liveList;
  @BindView(R.id.hot_info_dynamic_list) RecyclerView dynamicList;
  @BindView(R.id.hot_info_circle_list) RecyclerView circleList;
  @BindView(R.id.hot_info_activity_user_list) RecyclerView activityUserList;
  @BindView(R.id.hot_info_recent)  Button btnHotActivity;
  @BindView(R.id.hot_info_history)  Button btnHisActivity;
  @BindView(R.id.hot_info_activity)  Button btnActivityCalendar;
  private List<ActivtyDevas> items = new ArrayList<>();
  private List<ActivtyDevas> itemsTemp = new ArrayList<>();
  private List<LiveDevas> liveItems = new ArrayList<>();
  private HotInfoLiveRecyclerViewAdapter liveAdapter;
  private List<ConcerDevas> dynamicItems = new ArrayList<>();
  private HotInfoDynamicRecyclerViewAdapter dynamicAdapter;
  private List<UserDevas> activityUserItems = new ArrayList<>();
  private HotInfoActivityUserRecyclerViewAdapter activityUserAdapter;
  private List<CirleDevas> circleItems = new ArrayList<>();
  private HotInfoCircleRecyclerViewAdapter circleAdapter;
  LiveItem liveItem;
  public static final int UPDATE_STATE_OK=1;
  public static final int MAINUSER_IN_OK=2;
  public static final int MAINUSER_IN_ERROR=3;
  public static final int OTHERUSER_IN_OK=4;
  public static final int OTHERUSER_IN_ERROR=5;
  Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what){
        case UPDATE_STATE_OK:
          Toast.makeText(getActivity(),"您开启了直播",Toast.LENGTH_SHORT).show();
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
      }
    }
  };
  public HotInfoFragment() {
  }
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    hotInfoService = DataManager.getService(HotInfoService.class);
  }
  @Override public void onActivityCreated( Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }
  @Override public void onResume() {
    super.onResume();
    banner.startTurning(2500);
  }
  @Override public void onPause() {
    super.onPause();
    banner.stopTurning();
  }
  @Override protected int getLayout() {
    return R.layout.fragment_hot_info;
  }
  @Override protected void initViews(Bundle savedInstanceState) {
    //banner
    banner.setPages(new CBViewHolderCreator<ImageHolderView>() {
      @Override public ImageHolderView createHolder() {
        return new ImageHolderView();
      }
    }, items)
        .setPageIndicator(
            new int[] { R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused })
        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
    //最新活动
    btnHotActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getActivity().startActivity(new Intent(getActivity(), NewActiveActivity.class));
      }
    });
    btnHisActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getActivity().startActivity(new Intent(getActivity(), HistoryActivityActivity.class));
      }
    });
    btnActivityCalendar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getActivity().startActivity(new Intent(getActivity(), CalendarActivityActivity.class));
      }
    });
    //直播
    LinearLayoutManager liveLayout =new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
    liveAdapter = new HotInfoLiveRecyclerViewAdapter(getActivity(), liveItems);
    liveList.setLayoutManager(liveLayout);
    liveList.setAdapter(liveAdapter);
    liveAdapter.setOnItemClickListener(new HotInfoLiveRecyclerViewAdapter.OnItemClickListener() {
      @Override
      public void OnBigPhotoClick(View view, LiveDevas liveDevas) {
        if (view.getId()==R.id.hot_info_live_picture) {
          liveItem = new LiveItem();
          SharedPreferences share = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
          String userId = share.getString("UserId", "");
          String token = share.getString("Token", "");
          if (!token.equals("") && App.isStateOK) {
            int userType = 0;
            if (liveDevas.userId == Integer.parseInt(userId)) {
              userType = LiveActivity.USER_MAIN;
            } else {
              userType = LiveActivity.USER_OTHER;
            }
            liveItem.setUserType(userType);
            liveItem.setStartTime(liveDevas.startTime);
            liveItem.setLiveState(liveDevas.state);
            liveItem.setLiveId(liveDevas.id);
            String bigUrl = liveDevas.bgmUrl == null ? StringUtil.isNullAvatar(liveDevas.imageUrl) : StringUtil.isNullAvatar(liveDevas.bgmUrl);
            liveItem.setBigPictureUrl(bigUrl);
            liveItem.setTitle(liveDevas.title);
            liveItem.setGroupId(liveDevas.groupId);
            liveItem.setMainPhotoUrl(StringUtil.isNullAvatar(liveDevas.avatar));//
            liveItem.setCurrentNum(liveDevas.watchNumber);
            liveItem.setLiveOwnerId(liveDevas.userId + "");
            LiveUtil.doIntoLive(getActivity(), handler, liveItem);
          }else {
            Toast.makeText(getActivity(), "亲，您还没有登陆呢！", Toast.LENGTH_SHORT).show();
            getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
          }
        }
      }
    });
    liveList.setNestedScrollingEnabled(false);
    //动态
    GridLayoutManager dynamicLayout = new GridLayoutManager(getActivity(),3);
    dynamicAdapter = new HotInfoDynamicRecyclerViewAdapter(getActivity(), dynamicItems);
    dynamicList.setLayoutManager(dynamicLayout);
    dynamicList.setAdapter(dynamicAdapter);
    dynamicList.setNestedScrollingEnabled(false);
    ////圈子
    LinearLayoutManager circleLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    circleAdapter = new HotInfoCircleRecyclerViewAdapter(getActivity(), circleItems);
    circleList.setLayoutManager(circleLayout);
    circleList.setAdapter(circleAdapter);
    circleList.setNestedScrollingEnabled(false);
    //用户
    LinearLayoutManager activityUserLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
    activityUserAdapter =new HotInfoActivityUserRecyclerViewAdapter(getActivity(), activityUserItems);
    activityUserList.setLayoutManager(activityUserLayout);
    activityUserList.setAdapter(activityUserAdapter);
    activityUserList.setNestedScrollingEnabled(false);
  }
  @Override protected void initListeners() {
    swipeContainer.setOnRefreshListener(this);
    ivLiveMore.setOnClickListener(this);
    ivDynamicMore.setOnClickListener(this);
    ivCircleMore.setOnClickListener(this);
    ivUserMore.setOnClickListener(this);
  }
  @Override protected void initData() {
    Subscription hotInfo = hotInfoService.getAllHotInfo().subscribeOn(Schedulers.io())//
        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Result<HotInfoData>>() {
          @Override public void onCompleted() {
          }
          @Override public void onError(Throwable e) {
            Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
            Log.i("tag","error----------"+e.toString());
          }
          @Override public void onNext(Result<HotInfoData> result) {
            if (result.state == 200) {
              refresh(result.data);
            } else {
              Toast.makeText(getActivity(), "诶~网络好像有问题啊", Toast.LENGTH_SHORT).show();
            }
          }
        });
    mCompositeSubscription.add(hotInfo);
  }
  @Override public void onClick(View v) {
    switch (v.getId()){
      case R.id.ivLiveMore:
        Intent intent1=new Intent(getActivity(), MoreLiveActivity.class);
        getActivity().startActivity(intent1);
        break;
      case R.id.ivDynamicMore:
        Intent intent2=new Intent(getActivity(), MoreDynamicActivity.class);
        getActivity().startActivity(intent2);
        break;
      case R.id.ivCircleMore:
        Intent intent3=new Intent(getActivity(), MorePostsActivity.class);
        getActivity().startActivity(intent3);
        break;
      case R.id.ivUserMore:
        Intent intent4=new Intent(getActivity(), MoreUserActivity.class);
        getActivity().startActivity(intent4);
        break;
    }
  }
  @Override public void onRefresh() {
    updateData();
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        // 停止刷新
        swipeContainer.setRefreshing(false);
      }
    }, 500); // 5秒后发送消息，停止刷新
  }
  public void updateData() {
    Subscription hotInfo = hotInfoService.getAllHotInfo().subscribeOn(Schedulers.io())//
        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Result<HotInfoData>>() {
          @Override public void onCompleted() {
            swipeContainer.setRefreshing(false);
          }
          @Override public void onError(Throwable e) {
            swipeContainer.setRefreshing(false);
            Toast.makeText(getActivity(), "刷新内容失败", Toast.LENGTH_SHORT).show();
          }
          @Override public void onNext(Result<HotInfoData> result) {
            swipeContainer.setRefreshing(false);
            if (result.state == 200) {
              refresh(result.data);
            } else {
              Toast.makeText(getActivity(), "当前网络状态不佳，请检查您的网络设置！", Toast.LENGTH_SHORT).show();
            }
          }
        });
    mCompositeSubscription.add(hotInfo);
  }
  //banner set
  public class ImageHolderView implements Holder<ActivtyDevas> {
    private ImageView iv;
    int pos=0;
    @Override public View createView(Context context) {
      iv = new ImageView(context);
      iv.setScaleType(ImageView.ScaleType.FIT_XY);
      iv.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          ActivtyDevas devas = itemsTemp.get(pos);
          int id = devas.id;
          int userId = devas.userId;
          Intent intent = new Intent(getActivity(), ActivityDetailsActivity.class);
          intent.putExtra("ActiveID", id);
          intent.putExtra("UserId", userId);
          getActivity().startActivity(intent);
        }
      });
      return iv;
    }
    @Override public void UpdateUI(Context context, final int position, ActivtyDevas data) {
      pos=position;
      Rect rect = new Rect();
      ((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
      int x = rect.width();
      if (itemsTemp.get(position).getImageUrl()!=null && !itemsTemp.get(position).getImageUrl().equals("")){
        PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, itemsTemp.get(position).getImageUrl(), x, 1920), iv, x, 720);
      }else {
        PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, itemsTemp.get(position).getBgmUrl(), x, 1920), iv, x, 720);
      }
      int newPos=position-1==-1?itemsTemp.size()-1:position-1;
      banner_title.setText(itemsTemp.get(newPos).title);
    }
  }
  /**
   * 刷新方法
   * @param data
     */
  private void refresh(HotInfoData data) {
    //banner
    if (data.activityDevas != null && data.activityDevas.size() > 0) {
      items.clear();
      itemsTemp.clear();
      items.addAll(data.activityDevas);
      itemsTemp.addAll(items);
      banner.notifyDataSetChanged();
    }
    //live
    if (data.liveDevas != null && data.liveDevas.size() > 0) {
      liveItems.clear();
      liveItems.addAll(data.liveDevas);
      liveAdapter.notifyDataSetChanged();
    }
    //dynamic
    if (data.concerDevas != null && data.concerDevas.size() > 0) {
      dynamicItems.clear();
      dynamicItems.addAll(data.concerDevas);
      dynamicAdapter.notifyDataSetChanged();
    }
    //circle
    if (data.cirleItemDevas != null && data.cirleItemDevas.size() > 0) {
      circleItems.clear();
      circleItems.addAll(data.cirleItemDevas);
      circleAdapter.notifyDataSetChanged();
    }
    //user
    if (data.userDevas != null && data.userDevas.size() > 0) {
      activityUserItems.clear();
      activityUserItems.addAll(data.userDevas);
      activityUserAdapter.notifyDataSetChanged();
    }
  }
}
