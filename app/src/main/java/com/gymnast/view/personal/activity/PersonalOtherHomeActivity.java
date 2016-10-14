package com.gymnast.view.personal.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.hotinfo.CirleDevas;
import com.gymnast.data.hotinfo.UserDevas;
import com.gymnast.data.net.API;
import com.gymnast.data.pack.ConcernData;
import com.gymnast.data.personal.DynamicData;
import com.gymnast.data.personal.PostsData;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.hotinfoactivity.activity.ActivityDetailsActivity;
import com.gymnast.view.live.activity.LiveActivity;
import com.gymnast.view.pack.adapter.ConcernAdapter;
import com.gymnast.view.personal.adapter.DynamicAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cymbi on 2016/9/4.
 */
public class PersonalOtherHomeActivity extends ImmersiveActivity {
    List<ConcernData> activityList=new ArrayList<>();
    private RecyclerView recyclerview;
    private ImageView back,mHead;
    private TextView mContent,title,mFans,tvConcern,mNickname,tvauthInfo;
    private Button mSign;
    private State state;
    private SharedPreferences share;
    private String Godtoken="tiyujia2016";
    public static final int HANFLE_DATA_UPDATE=1;
    private ConcernAdapter adapter;
    private int userID;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout appbar;
    private String token,UserId,returnAccountAuth,name,avatar;
    private Toolbar tb;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANFLE_DATA_UPDATE:
                    adapter = new ConcernAdapter(PersonalOtherHomeActivity.this,activityList);
                    recyclerview.setAdapter(adapter);
                    adapter.setOnItemClickListener(new ConcernAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClickListener(View view, int position) {
                            ConcernData item= activityList.get(position);
                            String fromtype=item.getFromType();
                            if (activityList.size() != 0) {
                                if (fromtype.equals("1")) {
                                    Intent i = new Intent(PersonalOtherHomeActivity.this, LiveActivity.class);
                                    i.putExtra("item", item);
                                    startActivity(i);
                                } else if (fromtype.equals("2")) {
                                    int ActiveID = Integer.parseInt(item.getFromId());
                                    Intent i = new Intent(PersonalOtherHomeActivity.this, ActivityDetailsActivity.class);
                                    i.putExtra("ActiveID",ActiveID);
                                    startActivity(i);
                                } else if (fromtype.equals("3")) {
                                    int tieZiID = Integer.parseInt(item.getFromId());
                                    Intent i = new Intent(PersonalOtherHomeActivity.this, PersonalPostsDetailActivity.class);
                                    i.putExtra("TieZiID", tieZiID);
                                    startActivity(i);
                                } else if (fromtype.equals("null")) {
                                    Intent i = new Intent(PersonalOtherHomeActivity.this, PersonalDynamicDetailActivity.class);
                                    i.putExtra("CirleID", item.getId());
                                    startActivity(i);
                                }
                            }
                        }
                    });
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_home);
        getInfo();
        setView();
        getUser();
        getSign();
        getData();
        setListeners();
        setSupportActionBar(tb);
    }
    private void getSign() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri=API.BASE_URL+"/v1/attention/user_check";
                HashMap<String,String> params=new HashMap<>();
                params.put("token",token);
                params.put("fromId",UserId);
                params.put("toId",userID+"");
                String result=GetUtil.sendGetMessage(uri,params);
                try {
                    JSONObject object=new JSONObject(result);
                    final int data=object.getInt("data");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (data==0){
                                mSign.setText("关注");
                            }else {
                                mSign.setText("已关注");
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void getInfo() {
        share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        UserId = share.getString("UserId","");
        token = share.getString("Token","");
        try {
            userID = getIntent().getIntExtra("UserID", 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        mSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String uri=API.BASE_URL+"/v1/attention/user";
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put("token",token);
                        params.put("fromId",UserId);
                        params.put("toId",userID+"");
                        String result=GetUtil.sendGetMessage(uri,params);
                        try {
                            JSONObject object=new JSONObject(result);
                            int data=object.getInt("data");
                            if (object.getInt("state")==200){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSign.setText("已关注");
                                        Toast.makeText(PersonalOtherHomeActivity.this,"关注成功",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        mHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(PersonalOtherHomeActivity.this,ImageActivity.class);
                i.putExtra("IMAGE",API.IMAGE_URL+avatar);
                startActivity(i);
            }
        });
    }
    private void setView() {
        recyclerview=(RecyclerView)findViewById(R.id.recyclerview);
        back= (ImageView)findViewById(R.id.me_back);
        mHead= (ImageView)findViewById(R.id.me_head);
        mNickname= (TextView)findViewById(R.id.nickname);
        mContent= (TextView)findViewById(R.id.content);
        tvConcern= (TextView)findViewById(R.id.tvConcern);
        mFans= (TextView)findViewById(R.id.me_fans);
        mSign= (Button)findViewById(R.id.sign);
        title=(TextView)findViewById(R.id.personal_name_title);
        tvauthInfo=(TextView)findViewById(R.id.authInfo);
        tb=(Toolbar)findViewById(R.id.toolbar);
        appbar=(AppBarLayout)findViewById(R.id.personal_appbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collp_toolbar_layout);
    }
    public void getUser() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri= API.BASE_URL+"/v1/account/center_info";
                HashMap<String, String> params = new HashMap<>();
                params.put("token", Godtoken);
                params.put("account_id", userID+"");
                String result = GetUtil.sendGetMessage(uri, params);
                try {
                    JSONObject  obj = new JSONObject(result);
                    JSONObject data = obj.getJSONObject("data");
                    //判断用户类型是否为空，不为空再获取他的JsonObject
                    final String tempAuth=StringUtil.isNullAuth(data.getString("accountAuth"));
                    if(!tempAuth.equals("")){
                        JSONObject accountAuth=new JSONObject(tempAuth);
                        returnAccountAuth=accountAuth.getString("authinfo");
                    }
                    name = data.getString("nickname");
                    avatar = data.getString("avatar");
                    final String signature=data.getString("signature");
                    final int Concern = data.getInt("gz");
                    final int fans= data.getInt("fs");
                    if (obj.getInt("state") == 200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PicassoUtil.handlePic(PersonalOtherHomeActivity.this, PicUtil.getImageUrlDetail(PersonalOtherHomeActivity.this, StringUtil.isNullAvatar(avatar), 320, 320),mHead,320,320);
                                mNickname.setText(name);
                                if(signature=="null"){
                                    mContent.setText("这个人很懒，什么也没有留下");
                                }else {
                                    mContent.setText(signature);
                                }
                                tvConcern.setText(Concern+"关注");
                                mFans.setText(fans+"粉丝");
                                if(!tempAuth.equals("")){
                                    tvauthInfo.setText("（"+returnAccountAuth+"）");
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PersonalOtherHomeActivity.this,"服务器故障，数据解析出错",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
    public void getData() {
        new Thread(new Runnable() {
            public String returnfromId;
            public String returnType;
            public String authInfo;
            @Override
            public void run() {
                try {
                    String uri= API.BASE_URL+"/v1/my/concern/list";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("token",Godtoken);
                    params.put("accountId", userID+"");
                    String result= GetUtil.sendGetMessage(uri,params);
                    JSONObject json=new JSONObject(result);
                    JSONArray data = json.getJSONArray("data");
                    for (int i=0;i<data.length();i++){
                        ArrayList<String> imageURL=new ArrayList<String>();
                        JSONObject  object=  data.getJSONObject(i);
                        JSONObject  userVo= object.getJSONObject("userVo");
                        JSONObject  pageViews= object.getJSONObject("pageViews");
                        int pageviews=pageViews.getInt("pageviews");
                        String tempAuth= StringUtil.isNullAuth(object.getString("userAuthVo"));
                        if(!tempAuth.equals("")){
                            JSONObject accountAuth=new JSONObject(tempAuth);
                            authInfo=accountAuth.getString("authInfo");
                        }
                        String  returnNickName=userVo.getString("nickName");
                        String returnAvatar=userVo.getString("avatar");
                        int returnAuthenticate=userVo.getInt("authenticate");
                        int returnId=object.getInt("id");
                        int returnUserId=object.getInt("userId");
                        long returnCreateTime= object.getLong("createTime");
                        if(object.getString("fromType")!=null){
                            returnType=object.getString("fromType");
                        }else {}
                        if(object.getString("fromId")!=null){
                            returnfromId=object.getString("fromId");
                        }else {}
                        String  returnTopicTitle= object.getString("topicTitle");
                        String returnTopicContent=object.getString("topicContent");
                        int  returnZanCounts=object.getInt("zanCounts");
                        int returnCommentCounts=object.getInt("commentCounts");
                        String returnVideoUrl=object.getString("videoUrl");
                        int  returnState=object.getInt("state");
                        String urls= object.getString("imgUrl");
                        if (urls==null|urls.equals("null")|urls.equals("")){
                        }else {
                            String [] imageUrls=urls.split(",");
                            for (int j=0;j<imageUrls.length;j++){
                                imageURL.add(API.IMAGE_URL+imageUrls[j]);
                            }
                        }
                        ConcernData data1=new ConcernData();
                        data1.setId(returnId);
                        data1.setUserId(returnUserId);
                        data1.setFromType(returnType);
                        data1.setFromId(returnfromId);
                        data1.setImgUrl(imageURL);
                        data1.setPageviews(pageviews);
                        data1.setNickName(returnNickName);
                        data1.setTopicContent(returnTopicContent);
                        data1.setTopicTitle(returnTopicTitle);
                        data1.setAuthenticate(returnAuthenticate);
                        data1.setAvatar(returnAvatar);
                        data1.setCommentCounts(returnCommentCounts);
                        data1.setZanCounts(returnZanCounts);
                        data1.setCreateTime(returnCreateTime);
                        if(!tempAuth.equals("")){
                            data1.setAuthInfo(authInfo);
                        }
                        data1.setState(returnState);
                        data1.setAuthInfo("");
                        activityList.add(data1);
                    }
                    handler.sendEmptyMessage(HANFLE_DATA_UPDATE);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }
    //枚举出Toolbar的三种状态
    private enum State{
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }
    @Override
    protected void onResume() {
        super.onResume();
        getUser();
        getSign();
    }
}