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
    private List<String> imageUrlList=new ArrayList<>();
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
                    swipeRefresh.setRefreshing(false);
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_pack_auslese,container,false);
        getInfo();
        setview();
        getcircle();
        getposts();
        getBanner();
        return view;
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
        ivCircleMore=(ImageView)view.findViewById(R.id.ivCircleMore);
        ivCircleMore.setOnClickListener(this);
        swipeRefresh=(SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        RefreshUtil.refresh(swipeRefresh,getActivity());
        swipeRefresh.setOnRefreshListener(this);
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
            list.clear();
            listdata.clear();
            getcircle();
            getposts();
        }else {
        }
    }
}