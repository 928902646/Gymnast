package com.gymnast.view.personal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.pack.ConcernData;
import com.gymnast.data.personal.DynamicData;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.hotinfoactivity.activity.ActivityDetailsActivity;
import com.gymnast.view.live.activity.LiveActivity;
import com.gymnast.view.pack.adapter.ConcernAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cymbi on 2016/8/27.
 */
public class PersonalDynamicActivity extends ImmersiveActivity {
    private RecyclerView mRecyclerview;
    List<ConcernData> activityList=new ArrayList<>();
    public static final int HANFLE_DATA_UPDATE=1;
    private ConcernAdapter adapter;
    private SharedPreferences share;
    private String token,id;
    private ImageView back;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANFLE_DATA_UPDATE:
                    adapter = new ConcernAdapter(PersonalDynamicActivity.this,activityList);
                    mRecyclerview.setAdapter(adapter);
                    adapter.setOnItemClickListener(new ConcernAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClickListener(View view, int position) {
                            ConcernData item= activityList.get(position);
                            String fromtype=item.getFromType();
                            if (activityList.size() != 0) {
                                if (fromtype.equals("1")) {
                                    Intent i = new Intent(PersonalDynamicActivity.this, LiveActivity.class);
                                    i.putExtra("item", item);
                                    startActivity(i);
                                } else if (fromtype.equals("2")) {
                                    int ActiveID = Integer.parseInt(item.getFromId());
                                    Intent i = new Intent(PersonalDynamicActivity.this, ActivityDetailsActivity.class);
                                    i.putExtra("ActiveID",ActiveID);
                                    startActivity(i);
                                } else if (fromtype.equals("3")) {
                                    int tieZiID = Integer.parseInt(item.getFromId());
                                    Intent i = new Intent(PersonalDynamicActivity.this, PersonalPostsDetailActivity.class);
                                    i.putExtra("TieZiID", tieZiID);
                                    startActivity(i);
                                } else if (fromtype.equals("null")) {
                                    Intent i = new Intent(PersonalDynamicActivity.this, PersonalDynamicDetailActivity.class);
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
    private String authInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dynamic);
        share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token","");
        id = share.getString("UserId","");
        setView();
        setListeners();
        getData();
    }
    private void setListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void setView() {
        mRecyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        back= (ImageView)findViewById(R.id.personal_back);
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
                    HashMap<String,String> parmas=new HashMap<String, String>();
                    parmas.put("token",token);
                    parmas.put("accountId",id);
                    String result= GetUtil.sendGetMessage(uri,parmas);
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
}
