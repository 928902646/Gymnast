package com.gymnast.view.pack.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.pack.ChoicenessData;
import com.gymnast.data.personal.PostsData;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.pack.adapter.ChoicenessAdapter;
import com.gymnast.view.pack.adapter.ImageAdapter;
import com.gymnast.view.personal.activity.PersonalPostsDetailActivity;
import com.gymnast.view.personal.adapter.CircleItemAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoicenessCircleFragment extends Fragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{
    private View view;
    private String token;
    private String id;
    private ImageView ivCircleMore;
    private RecyclerView rvCircle,rvPosts;
    private ViewPager vpBanner;
    //统计下载了几张图片
    int n=0;
    //统计当前viewpager轮播到第几页
    int p=0;
    //准备好网络图片的地址
    private List<String> imageUrlList=new ArrayList<>();
    ArrayList<ImageView> vpList = new ArrayList<ImageView>();
    //控制图片是否开始轮播的开关,默认关的
    private boolean isStart=false;
    //开始图片轮播的线程
    private BannerStart t;
    //存放代表viewpager播到第几张的小圆点
    private LinearLayout ll_tag;
    //存储小圆点的一维数组
    private ImageView tag[];
    private String imgUrls;
    private List<ChoicenessData> list=new ArrayList<>();
    private List<PostsData> listdata=new ArrayList<>();
    public static final int HANFLE_DATA_UPDATE=1;
    public static final int HANFLE_DATA_VP_START=2;
    public static final int HANFLE_DATA_VP_P=3;
    private ChoicenessAdapter choicenessAdapter;
    private CircleItemAdapter Circleadapter;
    private SwipeRefreshLayout swipeRefresh;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANFLE_DATA_UPDATE:
                    choicenessAdapter = new ChoicenessAdapter(getActivity(),list);
                    Circleadapter = new CircleItemAdapter(getActivity(),listdata);
                    rvCircle.setAdapter(choicenessAdapter);
                    rvPosts.setAdapter(Circleadapter);
                    Circleadapter.setOnItemClickListener(new CircleItemAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClickListener(View view, int position) {
                            PostsData postsData =listdata.get(position);
                            Intent i=new Intent(getActivity(), PersonalPostsDetailActivity.class);
                            int TieZiID= postsData.getId();
                            i.putExtra("TieZiID",TieZiID);
                            getActivity().startActivity(i);
                        }
                    });
                    choicenessAdapter.notifyDataSetChanged();
                    Circleadapter.notifyDataSetChanged();
//                    swipeRefresh.setRefreshing(false);
                    break;
                case HANFLE_DATA_VP_START:
                    n++;
                    Bitmap bitmaps=PicUtil.getImageBitmap(API.IMAGE_URL+imgUrls);
                    ImageView iv=new ImageView(getActivity());
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setImageBitmap(bitmaps);
                    //把图片添加到集合里
                    vpList.add(iv);
                    //当接收到第三张图片的时候，设置适配器,
                    if(n==imageUrlList.size()){
                        vpBanner.setAdapter(new ImageAdapter(getActivity(),vpList));
                        //创建小圆点
                        creatTag();
                        //把开关打开
                        isStart=true;
                        t=new BannerStart();
                        //启动轮播图片线程
                        t.start();
                    }
                    break;
                case HANFLE_DATA_VP_P:
                    //接受到的线程发过来的p数字
                    int page=(Integer) msg.obj;
                    vpBanner.setCurrentItem(page);
                    break;
            }
        }
    };


    protected void creatTag() {
        tag=new ImageView[imageUrlList.size()];
        for(int i=0;i<imageUrlList.size();i++){

            tag[i]=new ImageView(getActivity());
            //第一张图片画的小圆点是白点
            if(i==0){
                tag[i].setBackgroundResource(R.mipmap.ic_page_indicator_focused);
            }else{
                //其它的画灰点
                tag[i].setBackgroundResource(R.mipmap.ic_page_indicator);
            }
            //设置上下左右的间隔
            tag[i].setPadding(10, 20, 10, 20);
            //添加到viewpager底部的线性布局里面
            ll_tag.addView(tag[i]);
        }

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_pack_auslese,container,false);
        getInfo();
        setview();
        getcircle();
        getposts();
        getBanner();
        initView();
        return view;
    }
    private void initView() {
        vpBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //把当前的页数赋值给P
                p=position;
                //得到当前图片的索引,如果图片只有三张，那么只有0，1，2这三种情况
                int currentIndex=(position%imageUrlList.size());
                for(int i=0;i<tag.length;i++){
                    if(i==currentIndex){
                        tag[i].setBackgroundResource(R.mipmap.ic_page_indicator_focused);
                    }else{
                        tag[i].setBackgroundResource(R.mipmap.ic_page_indicator);
                    }
                }

            }
            @Override
            public void onPageSelected(int position) {

            }
            @Override
            public void onPageScrollStateChanged(int state) {
                //这个switch语句我注掉了，我觉得这个语句没有问题啊，可是为什么加上以下语句，当用手拉一次viewpager的时候，轮播就停止了，再也恢复不过来了?有人知道吗
                switch(state){
                //当页面被手指拉动的时候，暂停轮播
                 case ViewPager.SCROLL_STATE_DRAGGING:
                   isStart=false;
                   break;
                //当手指拉完松开或者页面自己翻到下一页静止的时候,开始轮播
                case ViewPager.SCROLL_STATE_IDLE:
                  isStart=true;
                   break;
                 }
            }
        });
    }
    private void getBanner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri=API.BASE_URL+"/v1/deva/get";
                HashMap<String,String> parmas=new HashMap<String, String>();
                parmas.put("area",1+"");
                parmas.put("model",1+"");
                String result=GetUtil.sendGetMessage(uri,parmas);
                try {
                    JSONObject object=new JSONObject(result);
                    JSONArray activityDevas= object.getJSONArray("activityDevas");
                    for (int i=0;i<activityDevas.length();i++){
                        JSONObject json=activityDevas.getJSONObject(i);
                        String title= json.getString("title");
                        imgUrls= json.getString("imgUrls");
                        int ActiveID=json.getInt("id");
                        int UserId=json.getInt("userId");
                        imageUrlList.add(API.IMAGE_URL+imgUrls);
                    }
                    handler.sendEmptyMessage(HANFLE_DATA_VP_START);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void getInfo() {
        SharedPreferences share= getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token", "");
        id = share.getString("UserId", "");
    }
    private void getcircle() {
        new  Thread(new Runnable() {
            public int id;
            public int circleItemCount;
            public String headImgUrl;
            public String title;
            @Override
            public void run() {
                String uri= API.BASE_URL+"/v1/deva/get";
                HashMap<String,String> parmas=new HashMap<String, String>();
                parmas.put("area",3+"");
                parmas.put("model",3+"");
                try {
                    String result= GetUtil.sendGetMessage(uri,parmas);
                    JSONObject obj=new JSONObject(result);
                    JSONArray cirleDevas= obj.getJSONArray("cirleDevas");
                    for(int i=0;i<cirleDevas.length();i++) {
                        JSONObject data = cirleDevas.getJSONObject(i);
                        title = data.getString("title");
                        id = data.getInt("id");
                        headImgUrl = data.getString("bgmUrl");
                        circleItemCount = data.getInt("circleItemCount");
                        ChoicenessData choicenessData = new ChoicenessData();
                        choicenessData.setCircleItemCount(circleItemCount);
                        choicenessData.setHeadImgUrl(headImgUrl);
                        choicenessData.setTitle(title);
                        choicenessData.setId(id);
                        list.add(choicenessData);
                    }
                    handler.sendEmptyMessage(HANFLE_DATA_UPDATE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void setview() {
        rvCircle=(RecyclerView)view.findViewById(R.id.auslese_lv_circle);
        rvPosts=(RecyclerView)view.findViewById(R.id.auslese_lv_posts);
        vpBanner=(ViewPager)view.findViewById(R.id.vpBanner);
        ll_tag=(LinearLayout)view.findViewById(R.id.ll_tag);
        ivCircleMore=(ImageView)view.findViewById(R.id.ivCircleMore);
        ivCircleMore.setOnClickListener(this);
        swipeRefresh=(SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        /*RefreshUtil.refresh(swipeRefresh,getActivity());
        swipeRefresh.setRefreshing(true);
        swipeRefresh.setOnRefreshListener(this);*/
    }
    public void getposts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri2= API.BASE_URL+"/v1/circleItem/list";
                Map<String,String> parmas2=new HashMap<String, String>();
                parmas2.put("start",0+"");
                parmas2.put("pageSize",100+"");
                String result2= PostUtil.sendPostMessage(uri2,parmas2);
                try {
                    JSONObject object=new JSONObject(result2);
                    JSONArray data=object.getJSONArray("data");
                    for(int j=0;j<data.length();j++) {
                        JSONObject datas = data.getJSONObject(j);
                        String title = datas.getString("title");
                        long createTime = datas.getLong("createTime");
                        String content = datas.getString("baseContent");
                        String imgUrl = StringUtil.isNullAvatar(datas.getString("imgUrl"));
                        imgUrl = PicUtil.getImageUrlDetail(getActivity(), imgUrl, 320, 320);
                        String nickname = datas.getString("nickname");
                        String avatar = StringUtil.isNullAvatar(datas.getString("avatar"));
                        String circleTitle = datas.getString("circleTitle");
                        int state = datas.getInt("state");
                        int zanCount = datas.getInt("zanCount");
                        int meetCount = datas.getInt("meetCount");
                        int circleId = datas.getInt("circleId");
                        int createId = datas.getInt("createId");
                        int id = datas.getInt("id");
                        JSONObject pageViews= datas.getJSONObject("pageViews");
                        int pageviews=pageViews.getInt("pageviews");
                        PostsData postsData = new PostsData();
                        postsData.setCreateTime(createTime);
                        postsData.setTitle(title);
                        postsData.setContent(content);
                        postsData.setImgUrl(imgUrl);
                        postsData.setNickname(nickname);
                        postsData.setAvatar(avatar);
                        postsData.setCircleTitle(circleTitle);
                        postsData.setState(state);
                        postsData.setZanCount(zanCount);
                        postsData.setMeetCount(meetCount);
                        postsData.setCircleId(circleId);
                        postsData.setCreateId(createId);
                        postsData.setId(id);
                        postsData.setPageviews(pageviews);
                        listdata.add(postsData);
                    }
                    handler.sendEmptyMessage(HANFLE_DATA_UPDATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivCircleMore:
                Intent i=new Intent(getActivity(), MoreCircleActivity.class);
                getActivity().startActivity(i);
                break;
        }
    }

    @Override
    public void onRefresh() {
        if(list.size()!=0||listdata.size()!=0){
           /* list.clear();
            listdata.clear();
            getcircle();
            getposts();
            getBanner();*/
        }else {
        }
    }

    //控制图片轮播
    class BannerStart extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while(isStart){
                Message message=new Message();
                message.what=1;
                message.obj=p;
                handler.sendMessage(message);
                try {
                    //睡眠3秒,在isStart为真的情况下，一直每隔三秒循环
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                p++;
            }
        }
    }
}