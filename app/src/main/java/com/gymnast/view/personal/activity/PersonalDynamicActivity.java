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
import com.gymnast.data.personal.DynamicData;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.personal.adapter.DynamicAdapter;
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
    List<DynamicData> activityList=new ArrayList<>();
    public static final int HANFLE_DATA_UPDATE=1;
    private DynamicAdapter adapter;
    private SharedPreferences share;
    private String token,id;
    private ImageView back;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANFLE_DATA_UPDATE:
                    adapter = new DynamicAdapter(PersonalDynamicActivity.this,activityList);
                    mRecyclerview.setAdapter(adapter);
                    adapter.setOnItemClickListener(new DynamicAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClickListener(View view, int position) {
                            DynamicData item= activityList.get(position);
                            if(activityList.size()!=0){
                                Intent i=new Intent(PersonalDynamicActivity.this,PersonalDynamicDetailActivity.class);
                                i.putExtra("CirleID",item.getId());
                                startActivity(i);}
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
                        String tempAuth= StringUtil.isNullAuth(object.getString("userAuthVo"));
                        if(!tempAuth.equals("")){
                            JSONObject accountAuth=new JSONObject(tempAuth);
                            authInfo=accountAuth.getString("authInfo");
                        }
                        String nickName=userVo.getString("nickName");
                        String avatar=userVo.getString("avatar");
                        int authenticate=userVo.getInt("authenticate");
                        int id=object.getInt("id");
                        long createTime= object.getLong("createTime");
                        int type=object.getInt("type");
                        String topicTitle= object.getString("topicTitle");
                        String topicContent=object.getString("topicContent");
                        int zanCounts=object.getInt("zanCounts");
                        int commentCounts=object.getInt("commentCounts");
                        String videoUrl=object.getString("videoUrl");
                        int state=object.getInt("state");
                        String urls= object.getString("imgUrl");
                        if (urls==null|urls.equals("null")|urls.equals("")){
                        }else {
                            String [] imageUrls=urls.split(",");
                            for (int j=0;j<imageUrls.length;j++){
                                    imageURL.add(API.IMAGE_URL+imageUrls[j]);
                            }
                        }
                        DynamicData data1=new DynamicData();
                        data1.setId(id);
                        data1.setType(type);
                        data1.setImgUrl(imageURL);
                        data1.setNickName(nickName);
                        data1.setTopicContent(topicContent);
                        data1.setTopicTitle(topicTitle);
                        data1.setAuthenticate(authenticate);
                        data1.setAvatar(avatar);
                        data1.setCommentCounts(commentCounts);
                        data1.setZanCounts(zanCounts);
                        data1.setCreateTime(createTime);
                        if(!tempAuth.equals("")){
                            data1.setAuthInfo(authInfo);
                        }
                        data1.setState(state);
                        data1.setAuthInfo("");
                        //data1.setTopicVisible(return_topicVisible);
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
