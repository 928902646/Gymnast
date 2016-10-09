package com.gymnast.view.hotinfoactivity.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import com.gymnast.R;
import com.gymnast.data.hotinfo.NewActivityItemDevas;
import com.gymnast.data.hotinfo.RecentBigActivity;
import com.gymnast.data.net.API;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.HomeActivity;
import com.gymnast.view.hotinfoactivity.adapter.NewActivityAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
public class NewActiveActivity extends ImmersiveActivity implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView rvNewActive;
    NewActivityAdapter adapter;
    List<NewActivityItemDevas> activityList=new ArrayList<>();
    RecentBigActivity recentBigActivity=new RecentBigActivity();
    private SharedPreferences share;
    private String token;
    String userId;
    private int pageNumber=60,page=1;
    SwipeRefreshLayout swipeLayout;
    private LinearLayoutManager layoutManager;
    private ImageView ivNewActiveBack;
    public static final int HANDLE_DATA_UPDATE=1;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLE_DATA_UPDATE:
                    adapter = new NewActivityAdapter(NewActiveActivity.this,activityList);
                    rvNewActive.setAdapter(adapter);
                    adapter.setOnItemClickListener(new NewActivityAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClickListener(View view, int position) {
//                            if (position!=2){
                                NewActivityItemDevas item= activityList.get(position);
                                Intent i=new Intent(NewActiveActivity.this,ActivityDetailsActivity.class);
                                i.putExtra("ActiveID",item.getActiveId());
                                i.putExtra("UserId",item.getUserID());
                                startActivity(i);
//                            }
                        }
                    });
                    adapter.notifyDataSetChanged();
                    swipeLayout.setRefreshing(false);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
        setView();
        share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token", "");
        userId=share.getString("UserId","");
        initData();
    }
    private void setView() {
        rvNewActive= (RecyclerView) findViewById(R.id.rvNewActive);
        ivNewActiveBack=(ImageView)findViewById(R.id.ivNewActiveBack);
        rvNewActive.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvNewActive.setLayoutManager(layoutManager);
        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.new_activity_swipe_container);
        RefreshUtil.refresh(swipeLayout,this);
        swipeLayout.setRefreshing(true);
        swipeLayout.setOnRefreshListener(this);
        ivNewActiveBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    protected void initData() {
        new Thread(){
            @Override
            public void run() {
                try {
//                    int []pics={R.mipmap.demo_01,R.mipmap.demo_02,R.mipmap.demo_03,R.mipmap.demo_04,R.mipmap.demo_05,R.mipmap.demo_06,R.mipmap.demo_07,R.mipmap.demo_08,R.mipmap.demo_09,R.mipmap.demo_10};
//                    String []types={"免费","￥128","免费","￥250","免费","￥99","免费","免费","￥88","免费"};
//                    recentBigActivity.setBigPic(R.mipmap.demo_07);
//                    recentBigActivity.setBigTitle("世界杯预选赛活动（热）");
//                    recentBigActivity.setBigNum(66);
//                    recentBigActivity.setPictureA(pics[new Random().nextInt(10)]);
//                    recentBigActivity.setPictureB(pics[new Random().nextInt(10)]);
//                    recentBigActivity.setPictureC(pics[new Random().nextInt(10)]);
//                    recentBigActivity.setPictureD(pics[new Random().nextInt(10)]);
//                    recentBigActivity.setBigTitleA("中国-日本门票特价");
//                    recentBigActivity.setBigTitleB("中国-美国门票特价");
//                    recentBigActivity.setBigTitleC("中国-意大利门票特价");
//                    recentBigActivity.setBigTitleD("中国-韩国门票特价");
//                    recentBigActivity.setBigTypeA(types[new Random().nextInt(10)]);
//                    recentBigActivity.setBigTypeB(types[new Random().nextInt(10)]);
//                    recentBigActivity.setBigTypeC(types[new Random().nextInt(10)]);
//                    recentBigActivity.setBigTypeD(types[new Random().nextInt(10)]);
//                    recentBigActivity.setBigLoveA(new Random().nextInt(100)+"");
//                    recentBigActivity.setBigLoveB(new Random().nextInt(100)+"");
//                    recentBigActivity.setBigLoveC(new Random().nextInt(100)+"");
//                    recentBigActivity.setBigLoveD(new Random().nextInt(100)+"");
                    if (activityList.size()!=0){
                        activityList.clear();
                    }
                    String uri = API.BASE_URL + "/v1/activity/query";
                    Map<String, String> params = new HashMap<>();
                    params.put("token", token);
                    if (userId!=null&&!userId.equals("")){
                        params.put("createId",userId);
                    }
                    params.put("pageNumber", pageNumber + "");
                    params.put("page", page + "");
                    String result = PostUtil.sendPostMessage(uri, params);
                    JSONObject json = new JSONObject(result);
                    JSONArray data = json.getJSONArray("data");
                    for (int i=0;i<data.length();i++){
                        JSONObject object=data.getJSONObject(i);
                        JSONObject objectUser=object.getJSONObject("user");
                        String  nickName=objectUser.getString("nickname");
                        int  userId=objectUser.getInt("id");
                        //收藏人数
                        int returnCollection = object.getInt("collection");
                        String returnTitle= object.getString("title");
                        String  returnDescContent= object.getString("descContent");
                        String  returnAddress= object.getString("address");
                        String  returnMaxPeople= object.getString("maxPeople");
                        String  returnPhone= object.getString("phone");
                        String returnTargetUrl= object.getString("targetUrl");
                        String returnMemberTemplate= object.getString("memberTemplate");
                        int returnPrice= object.getInt("price");
                        int activityId= object.getInt("id");
                        int isCollected=object.getInt("isCollection");
                        int  returnVisible= object.getInt("visible");
                        int returnType= object.getInt("type");
                        int returnMemberCount= object.getInt("memberCount");
                        int  returnExamine= object.getInt("examine");
                        long returnStartTime= object.getLong("startTime");
                        long returnEndTime= object.getLong("endTime");
                        long  returnLastTime= object.getLong("lastTime");
                        String imgUrls= StringUtil.isNullAvatar(object.getString("imgUrls"));
                        NewActivityItemDevas  itemDevas=new NewActivityItemDevas();
                        itemDevas.setIsCollected(isCollected==1?true:false);
                        itemDevas.setNickname(nickName);
                        itemDevas.setCollection(returnCollection);
                        itemDevas.setTitle(returnTitle);
                        itemDevas.setAddress(returnAddress);
                        itemDevas.setPrice(returnPrice);
                        itemDevas.setStartTime(returnStartTime);
                        itemDevas.setImgUrls(imgUrls);
                        itemDevas.setTargetUrl(returnTargetUrl);
                        itemDevas.setMemberTemplate(returnMemberTemplate);
                        itemDevas.setEndTime(returnEndTime);
                        itemDevas.setLastTime(returnLastTime);
                        itemDevas.setActiveId(activityId);
                        itemDevas.setUserID(userId);
                        itemDevas.setMaxPeople(returnMaxPeople);
                        itemDevas.setDescContent(returnDescContent);
                        itemDevas.setPhone(returnPhone);
                        itemDevas.setVisible(returnVisible);
                        itemDevas.setType(returnType);
                        itemDevas.setMemberCount(returnMemberCount);
                        itemDevas.setExamine(returnExamine);
                        activityList.add(itemDevas);
                    }
                    handler.sendEmptyMessage(HANDLE_DATA_UPDATE);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Override
    public void onRefresh() {
        initData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }
}
