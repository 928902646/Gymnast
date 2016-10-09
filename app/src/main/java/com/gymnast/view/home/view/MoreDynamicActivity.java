package com.gymnast.view.home.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.DynamicData;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.adapter.SearchDynamicAdapter;
import com.gymnast.view.personal.activity.PersonalDynamicDetailActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoreDynamicActivity extends ImmersiveActivity {
    private EditText etSearch;
    private ImageView ivMoreBack;
    private RecyclerView recyclerView;
    List<DynamicData> activityList=new ArrayList<>();
    public static final int HANFLE_DATA_UPDATE=1;
    SearchDynamicAdapter adapter;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANFLE_DATA_UPDATE:
                    adapter = new SearchDynamicAdapter(MoreDynamicActivity.this,activityList);
                    adapter.setFriends(activityList);
                    recyclerView.setAdapter(adapter);
                    adapter.getFilter().filter(etSearch.getText().toString());
                    adapter.setOnItemClickListener(new SearchDynamicAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClickListener(View view, int position) {
                            DynamicData item= activityList.get(position);
                            if(activityList.size()!=0){
                                Intent i=new Intent(MoreDynamicActivity.this,PersonalDynamicDetailActivity.class);
                                i.putExtra("CirleID",item.getId());
                                MoreDynamicActivity.this.startActivity(i);
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
        setContentView(R.layout.search_more_common);
        ivMoreBack= (ImageView) findViewById(R.id.ivMoreBack);
        TextView tvTabTitle= (TextView) findViewById(R.id.tvTabTitle);
        tvTabTitle.setText("更多动态");
        etSearch= (EditText) findViewById(R.id.etSearch);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        initData();
        setListener();
    }
    private void setListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        ivMoreBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String uri= API.BASE_URL+"/v1/search/model";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("model","5");
                    params.put("pageNumber","100");
                    params.put("pages","1");
                    String result= PostUtil.sendPostMessage(uri, params);
                    Log.i("tag", "Dynamic" + result);
                    JSONObject json=new JSONObject(result);
                    if (json.getInt("state")==200) {
                        JSONArray data = json.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            ArrayList<String> imageURL = new ArrayList<String>();
                            JSONObject object = data.getJSONObject(i);
                            int dynamicId = object.getInt("id");//动态的id
                            String topicContent = object.getString("topicContent");
                            int zanCount = object.getInt("zanCount");
                            int msgCount = object.getInt("msgCount");
                            long createTime = object.getLong("createTime");
                            String urls = object.getString("imageUrl");
                            if (urls == null | urls.equals("null") | urls.equals("")) {
                            } else {
                                if (urls.contains(",")) {
                                    String[] imageUrls = urls.split(",");
                                    for (int j = 0; j < imageUrls.length; j++) {
                                        imageURL.add(API.IMAGE_URL + imageUrls[j]);
                                    }
                                } else {
                                    imageURL.add(API.IMAGE_URL + urls);
                                }
                            }
                            JSONObject account = object.getJSONObject("account");
                            int userid = account.getInt("id");
                            String nickName = account.getString("nickName");
                            String avatar = StringUtil.isNullAvatar(account.getString("avatar"));
                            int authenticate = account.getInt("authenticate");
                            String authTemp = StringUtil.isNullAuth(account.getString("auth"));
                            String authinfo = "";
                            if (!authTemp.equals("")) {
                                JSONObject auth = new JSONObject(authTemp);
                                authinfo = auth.getString("authinfo");
                            }
                            DynamicData data1 = new DynamicData();
                            data1.setId(dynamicId);
                            data1.setAuthenticate(authenticate);
                            data1.setImgUrl(imageURL);
                            data1.setNickName(nickName);
                            data1.setTopicContent(topicContent);
                            data1.setAvatar(avatar);
                            data1.setCommentCounts(msgCount);
                            data1.setZanCounts(zanCount);
                            data1.setCreateTime(createTime);
                            data1.setUserId(userid);
                            data1.setAuthInfo(authinfo);
                            data1.setPageviews(0);
                            activityList.add(data1);
                        }
                    }else {
                        activityList=new ArrayList<DynamicData>();
                    }
                    handler.sendEmptyMessage(HANFLE_DATA_UPDATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
