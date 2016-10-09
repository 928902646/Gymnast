package com.gymnast.view.pack.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.CircleData;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.adapter.SearchCircleAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zzqybyb19860112 on 2016/9/8.
 */
public class MoreCircleActivity extends ImmersiveActivity {
    private EditText etSearch;
    private RecyclerView recyclerView;
    ImageView ivMoreBack;
    List<CircleData> activityList=new ArrayList<>();;
    SearchCircleAdapter adapter;
    public static final int HANFLE_DATA_UPDATE=1;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANFLE_DATA_UPDATE:
                    if (activityList.size()!=0){
                        adapter = new SearchCircleAdapter(MoreCircleActivity.this,activityList);
                        adapter.setFriends(activityList);
                        recyclerView.setAdapter(adapter);
                        adapter.getFilter().filter(etSearch.getText().toString());
                        adapter.notifyDataSetChanged();
                    }
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
        tvTabTitle.setText("更多圈子");
        etSearch= (EditText) findViewById(R.id.etSearch);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
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
                String uri= API.BASE_URL+"/v1/search/model";
                HashMap<String,String> params=new HashMap<String, String>();
                params.put("model","2");
                params.put("pageNumber","100");
                params.put("pages","1");
                String result= PostUtil.sendPostMessage(uri, params);
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray data=jsonObject.getJSONArray("data");
                    if(jsonObject.getInt("state")==200){
                        for(int i=0;i<data.length();i++){
                            JSONObject object = data.getJSONObject(i);
                            CircleData circleData= new CircleData();
                            int id= object.getInt("id");
                            String title= object.getString("title");
                            circleData.setConcerned(object.getBoolean("atten"));
                            circleData.setTitle(title);
                            circleData.setId(id);
                            String accountTemp= StringUtil.isNullAuth(object.getString("account"));
                            String headImgUrl="";
                            int circleItemCount=0;
                            if (!accountTemp.equals("")){
                                JSONObject account=new JSONObject(accountTemp);
                                circleItemCount= account.getInt("authenticate");
                                String authTemp= StringUtil.isNullAuth(account.getString("auth"));
                                headImgUrl= StringUtil.isNullAvatar(account.getString("avatar"));
                                if (!authTemp.equals("")&&!authTemp.equals("null")){
                                    JSONObject auth=  new JSONObject(authTemp);
                                    circleData.setDetails(auth.getString("authinfo"));
                                }else {
                                    circleData.setDetails("");
                                }
                            }else {
                                headImgUrl=StringUtil.isNullAvatar("");
                                circleData.setDetails("");
                            }
                            circleData.setHeadImgUrl(headImgUrl);
                            circleData.setCircleItemCount(circleItemCount);
                            activityList.add(circleData);
                        }
                    }else {
                        activityList=new ArrayList<CircleData>();
                    }
                    handler.sendEmptyMessage(HANFLE_DATA_UPDATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
