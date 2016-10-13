package com.gymnast.view.personal.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.CollectionData;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.hotinfoactivity.activity.ActivityDetailsActivity;
import com.gymnast.view.live.activity.LiveActivity;
import com.gymnast.view.personal.activity.PersonalDynamicDetailActivity;
import com.gymnast.view.personal.activity.PersonalPostsDetailActivity;
import com.gymnast.view.personal.adapter.CollectionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cymbi on 2016/10/11.
 */
public class MyCollectionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private View view;
    private String token,userId;
    private RecyclerView rvMyConcern;
    private LinearLayout llMyConcernLogin;
    private TextView tvMyConcernLogin;
    private SwipeRefreshLayout swipeRefresh;
    private String nickname,avatar,authInfo,title,imgUrls,descContent,circleTitle,topicTitle,topicContent;
    private long createTime,startTime;
    private int model,modelId,pageViews,id,commentCount,zanCount,createId,authenticate;
    public static final int HANDEL_DATA=1;
    private List<CollectionData> list=new ArrayList<>();
    private CollectionAdapter adapter;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDEL_DATA:
                    rvMyConcern.setVisibility(View.VISIBLE);
                    adapter=new CollectionAdapter(getActivity(),list);
                    rvMyConcern.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                    adapter.setOnItemClickListener(new CollectionAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClickListener(View view, int position) {
                            CollectionData data = list.get(position);
                            int model=data.getModel();
                            if(list.size()!=0){
                                if(model==1){
                                    int ActiveID =data.getModelId();
                                    Intent i = new Intent(getActivity(), ActivityDetailsActivity.class);
                                    i.putExtra("ActiveID", ActiveID);
                                    startActivity(i);
                                }
                                if(model==2){
                                    Intent i = new Intent(getActivity(), LiveActivity.class);
                                    i.putExtra("item", data);
                                    startActivity(i);
                                }
                                if(model==4){
                                    int tieZiID =data.getModelId();
                                    Intent i = new Intent(getActivity(), PersonalPostsDetailActivity.class);
                                    i.putExtra("TieZiID", tieZiID);
                                    startActivity(i);
                                }
                                if(model==5){
                                    Intent i=new Intent(getActivity(),PersonalDynamicDetailActivity.class);
                                    i.putExtra("CirleID",data.getModelId());
                                    startActivity(i);
                                }

                            }
                        }
                    });
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_recyclerview,container,false);
        SharedPreferences share = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token",null);
        userId = share.getString("UserId",null);
        setview();
        getdata();
        return view;
    }

    private void setview() {
        rvMyConcern = (RecyclerView) view.findViewById(R.id.rvMyConcern);
        swipeRefresh=(SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        RefreshUtil.refresh(swipeRefresh,getActivity());
        swipeRefresh.setOnRefreshListener(this);
    }

    /**
     * model：1活动，2直播，3圈子，4帖子，5动态
     */
    private void getdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> imageURL=new ArrayList<String>();
                String uri= API.BASE_URL+"/v1/my/collection/list";
                HashMap<String,String>params=new HashMap<>();
                params.put("token",token);
                params.put("accountId",userId);
                String result= GetUtil.sendGetMessage(uri,params);
                try {
                    JSONObject object=new JSONObject(result);
                    JSONArray data=object.getJSONArray("data");
                    for(int i=0;i<data.length();i++){
                        JSONObject obj= data.getJSONObject(i);
                        CollectionData collectionData= new CollectionData();
                        createTime=obj.getLong("createTime");
                        model= obj.getInt("model");
                        modelId=obj.getInt("modelId");
                        pageViews=obj.getInt("pageViews");
                        if(model==1){
                            String str=obj.getString("activityInfoVo");
                            if(str!=null&&!str.equals("")&&!str.equals("null")){
                                JSONObject activityInfoVo= obj.getJSONObject("activityInfoVo");
                                JSONObject user=activityInfoVo.getJSONObject("user");
                                nickname=user.getString("nickname");
                                avatar=user.getString("avatar");
                                authInfo=user.getString("authInfo");
                                id=user.getInt("id");
                                title=activityInfoVo.getString("title");
                                imgUrls=activityInfoVo.getString("imgUrls");
                                descContent=activityInfoVo.getString("descContent");
                                startTime=activityInfoVo.getLong("startTime");
                                commentCount=activityInfoVo.getInt("commentCount");
                                zanCount=activityInfoVo.getInt("zanCount");
                                collectionData.setCreateTime(createTime);
                                collectionData.setModel(model);
                                collectionData.setModelId(modelId);
                                collectionData.setPageViews(pageViews);
                                collectionData.setNickname(nickname);
                                collectionData.setAvatar(avatar);
                                collectionData.setAuthInfo(authInfo);
                                collectionData.setUserid(id);
                                collectionData.setTitle(title);
                                collectionData.setImgUrls(imgUrls);
                                collectionData.setStartTime(startTime);
                                collectionData.setDescContent(descContent);
                                collectionData.setCommentCount(commentCount);
                                collectionData.setZanCount(zanCount);
                            }
                            else {}

                        }else if(model==2){

                        }else if(model==4){
                            JSONObject circleItemVo=obj.getJSONObject("circleItemVo");
                            createId=circleItemVo.getInt("createId");
                            title=circleItemVo.getString("title");
                            String urls=circleItemVo.getString("imgUrl");
                            if (urls==null|urls.equals("null")|urls.equals("")){
                            }else {
                                String [] imageUrls=urls.split(",");
                                for (int j=0;j<imageUrls.length;j++){
                                    imageURL.add(API.IMAGE_URL+imageUrls[j]);
                                }
                            }
                            descContent=circleItemVo.getString("baseContent");
                            nickname=circleItemVo.getString("nickname");
                            avatar=circleItemVo.getString("avatar");
                            zanCount=circleItemVo.getInt("zanCount");
                            circleTitle=circleItemVo.getString("circleTitle");
                            authInfo=circleItemVo.getString("authInfo");
                            collectionData.setCreateTime(createTime);
                            collectionData.setModel(model);
                            collectionData.setModelId(modelId);
                            collectionData.setUserid(createId);
                            collectionData.setPageViews(pageViews);
                            collectionData.setNickname(nickname);
                            collectionData.setAvatar(avatar);
                            collectionData.setAuthInfo(authInfo);
                            collectionData.setTitle(title);
                            collectionData.setDescContent(descContent);
                            collectionData.setZanCount(zanCount);
                            collectionData.setCircleTitle(circleTitle);
                            collectionData.setImgUrl(imageURL);

                        }else if(model==5){
                            JSONObject myFollowVo = obj.getJSONObject("myFollowVo");
                            createId=myFollowVo.getInt("userId");
                            topicTitle=myFollowVo.getString("topicTitle");
                            topicContent=myFollowVo.getString("topicContent");
                            String urls=myFollowVo.getString("imgUrl");
                            if (urls==null|urls.equals("null")|urls.equals("")){
                            }else {
                                String [] imageUrls=urls.split(",");
                                for (int j=0;j<imageUrls.length;j++){
                                    imageURL.add(API.IMAGE_URL+imageUrls[j]);
                                }
                            }
                            JSONObject userVo = myFollowVo.getJSONObject("userVo");
                            nickname=userVo.getString("nickName");
                            avatar=userVo.getString("avatar");
                            authenticate=userVo.getInt("authenticate");
                            zanCount=myFollowVo.getInt("zanCounts");
                            commentCount=myFollowVo.getInt("commentCounts");
                            if(authenticate==1){
                                JSONObject userAuthVo = myFollowVo.getJSONObject("userAuthVo");
                                authInfo=userAuthVo.getString("authInfo");
                            }else {}
                            collectionData.setCreateTime(createTime);
                            collectionData.setModel(model);
                            collectionData.setModelId(modelId);
                            collectionData.setUserid(createId);
                            collectionData.setPageViews(pageViews);
                            collectionData.setNickname(nickname);
                            collectionData.setAvatar(avatar);
                            collectionData.setAuthInfo(authInfo);
                            collectionData.setTitle(topicTitle);
                            collectionData.setDescContent(topicContent);
                            collectionData.setZanCount(zanCount);
                            collectionData.setImgUrl(imageURL);
                            collectionData.setCommentCount(commentCount);
                        }
                        list.add(collectionData);
                    }
                    handler.sendEmptyMessage(HANDEL_DATA);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onRefresh() {
        if(list.size()!=0){
            list.clear();
            getdata();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 停止刷新
                    swipeRefresh.setRefreshing(false);
                }
            }, 1000);
        }else {
        }
    }
}
