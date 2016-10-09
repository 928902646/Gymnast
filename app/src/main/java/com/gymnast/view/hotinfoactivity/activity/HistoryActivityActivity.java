package com.gymnast.view.hotinfoactivity.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.gymnast.R;
import com.gymnast.data.hotinfo.RecentActivityDetail;
import com.gymnast.data.net.API;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.utils.TimeUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.hotinfoactivity.adapter.HistoryActivityAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by yf928 on 2016/8/3.
 */
public class HistoryActivityActivity extends ImmersiveActivity implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView rvItemHistory;
    private ImageView ivBack;
    private EditText etSearch;
    SwipeRefreshLayout reflesh;
    HistoryActivityAdapter adapter;
    List<RecentActivityDetail> activityList=new ArrayList<>();
    public static final int HANDLE_DATA=1;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLE_DATA:
                    adapter=new HistoryActivityAdapter(HistoryActivityActivity.this,activityList);
                    adapter.setFriends(activityList);
                    rvItemHistory.setAdapter(adapter);
                    adapter.getFilter().filter(etSearch.getText().toString());
                    adapter.notifyDataSetChanged();
                    reflesh.setRefreshing(false);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        rvItemHistory=(RecyclerView) findViewById(R.id.rvItemHistory);
        rvItemHistory.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvItemHistory.setLayoutManager(layoutManager);
        ivBack=(ImageView) findViewById(R.id.personal_back);
        etSearch=(EditText) findViewById(R.id.etSearch);
        reflesh= (SwipeRefreshLayout) findViewById(R.id.srlReflesh);
        RefreshUtil.refresh(reflesh, this);
        setData();
        setListener();
    }
    private void setListener() {
        reflesh.setOnRefreshListener(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryActivityActivity.this.onBackPressed();
            }
        });
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
    }
    private void setData() {
        SharedPreferences share=getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        final String userId=share.getString("UserId","");
        if (activityList.size()!=0){
            activityList.clear();
        }
        new Thread(){
            @Override
            public void run() {
                try{
                    String uri= API.BASE_URL+"/v1/activity/history";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("pageNumber",100+"");
                    params.put("page",1+"");
                    if (userId!=null&&!userId.equals("")){
                        params.put("userId",userId);
                    }
                    String result= PostUtil.sendPostMessage(uri,params);
                    JSONObject object=new JSONObject(result);
                    if (object.getInt("state")==200){
                        JSONArray array=object.getJSONArray("data");
                        for (int i=0;i<array.length();i++){
                            JSONObject data=array.getJSONObject(i);
                            RecentActivityDetail detail = new RecentActivityDetail();
                            String imageURL=data.getString("imgUrls");
                            String descContent=data.getString("descContent");
                            String address=data.getString("address");
                            long startTime=data.getLong("startTime");
                            long endTime=data.getLong("endTime");
                            String time= TimeUtil.getMonthDayTime(startTime)+"~"+TimeUtil.getMonthDayTime(endTime);
                            int price=data.getInt("price");
                            String type=(price==0)?"免费":"￥ "+price;
                            int memberCount=data.getInt("collection");
                            int collection=data.getInt("isCollection");
                            int id=data.getInt("id");
                            detail.setModelID(id);
                            detail.setIsCollected(collection==1?true:false);
                            detail.setMaxpeople(data.getInt("maxPeople")+"");
                            detail.setStartTime(data.getLong("startTime"));
                            detail.setEndTime(data.getLong("endTime"));
                            detail.setPhone(data.getString("phone"));
                            detail.setPrice(data.getInt("price"));
                            detail.setTitle(data.getString("title"));
                            detail.setPictureURL(imageURL);
                            detail.setDetail(descContent);
                            detail.setAddress(address);
                            detail.setTime(time);
                            detail.setType(type);
                            detail.setLoverNum(memberCount);
                            activityList.add(detail);
                        }
                    }else if (object.getInt("state")==10002){
                        activityList=new ArrayList<RecentActivityDetail>();
                    }
                handler.sendEmptyMessage(HANDLE_DATA);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Override
    public void onRefresh() {
        setData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                reflesh.setRefreshing(false);
            }
        }, 1000);
    }
}
