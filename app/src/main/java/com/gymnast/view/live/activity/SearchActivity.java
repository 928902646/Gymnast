package com.gymnast.view.live.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.adapter.SearchUserAdapter;
import com.gymnast.data.user.SearchUserEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SearchActivity extends ImmersiveActivity implements View.OnClickListener{
    ImageView ivBack;
    RelativeLayout re_search;
    EditText etSearch;
    RecyclerView rvFound;
    CopyOnWriteArrayList<SearchUserEntity> entities=new CopyOnWriteArrayList<>();
    SearchUserAdapter adapter;
    public static final int HANDLE_DATA=1;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==HANDLE_DATA){
                adapter=new SearchUserAdapter(SearchActivity.this,entities);
                adapter.setFriends(entities);
                rvFound.setAdapter(adapter);
                adapter.getFilter().filter(etSearch.getText().toString());
                adapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ivBack= (ImageView) findViewById(R.id.ivBack);
        re_search= (RelativeLayout) findViewById(R.id.re_search);
        etSearch= (EditText) re_search.findViewById(R.id.etSearch);
        rvFound= (RecyclerView) findViewById(R.id.rvFound);
        rvFound.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvFound.setLayoutManager(layoutManager);
        setData();
        addListeners();
    }
    private void addListeners() {
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
        ivBack.setOnClickListener(this);
    }
    private void setData() {
        new Thread(){
            @Override
            public void run() {
                try {
                    String uri= API.BASE_URL+"/v1/search/model";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("model","1");
                    params.put("pageNumber","100");
                    params.put("pages","1");
                    String result= PostUtil.sendPostMessage(uri, params);
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray data=jsonObject.getJSONArray("data");
                    for (int i=0;i<data.length();i++){
                        JSONObject object=data.getJSONObject(i);
                        SearchUserEntity entity=new SearchUserEntity();
                        entity.setName(object.getString("nickName"));
                        entity.setPhotoUrl(object.getString("avatar"));
                        entity.setAuthenticate(object.getInt("authenticate"));
                        String authTemp= StringUtil.isNullAuth(object.getString("auth"));
                        if (!authTemp.equals("")&&!authTemp.equals("null")){
                            JSONObject auth=  new JSONObject(authTemp);
                            entity.setType(auth.getString("authinfo"));
                        }
                        entities.add(entity);
                    }
                    handler.sendEmptyMessage(HANDLE_DATA);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.ivBack){
            onBackPressed();
        }
    }
}
