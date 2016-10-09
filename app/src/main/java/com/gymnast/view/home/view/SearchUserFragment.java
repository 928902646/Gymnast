package com.gymnast.view.home.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.CacheUtils;
import com.gymnast.utils.JSONParseUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.view.home.adapter.SearchUserAdapter;
import com.gymnast.data.user.SearchUserEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by zzqybyb19860112 on 2016/9/4.
 */
public class SearchUserFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{
    RecyclerView recyclerView;
    List<SearchUserEntity> dataList=new ArrayList<>();
    SearchUserAdapter adapter;
    boolean isRefresh=false;
    private String fromId,token;
    private SwipeRefreshLayout srlSearch;
    public static final int HANDLE_DATA=1;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==HANDLE_DATA){
                adapter=new SearchUserAdapter(getActivity(),dataList);
                adapter.setFriends(dataList);
                recyclerView.setAdapter(adapter);
                adapter.getFilter().filter(isRefresh == true ? HomeSearchResultAcitivity.etSearch.getText().toString().trim() : HomeSearchResultAcitivity.getSearchText());
                srlSearch.setRefreshing(false);
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view=inflater.inflate(R.layout.search_common_fragment,null);
        recyclerView= (RecyclerView) view.findViewById(R.id.recycleView);
        srlSearch= (SwipeRefreshLayout) view.findViewById(R.id.srlSearch);
        RefreshUtil.refresh(srlSearch, getActivity());
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager  layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        getInfo();
        setData();
        srlSearch.setOnRefreshListener(this);
        return view;
    }
    private void getInfo() {
        SharedPreferences share= getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        fromId = share.getString("UserId","");
        token = share.getString("Token","");
    }
    private void setData() {
        ArrayList<String> cacheData= (ArrayList<String>) CacheUtils.readJson(getActivity(), SearchUserFragment.this.getClass().getName() + ".json");
        if (cacheData==null||cacheData.size()==0) {
            new Thread() {
                @Override
                public void run() {
                    String uri = API.BASE_URL + "/v1/search/model";
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("userId", fromId);
                    params.put("model", "1");
                    params.put("pageNumber", "100");
                    params.put("pages", "1");
                    String result = PostUtil.sendPostMessage(uri, params);
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        boolean atten=jsonObject.getBoolean("atten");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONParseUtil.parseNetDataSearchUser(getActivity(), result,SearchUserFragment.this.getClass().getName() + ".json", dataList,handler,HANDLE_DATA);
                }
            }.start();
        }else {
            JSONParseUtil.parseLocalDataSearchUser(getActivity(),SearchUserFragment.this.getClass().getName() + ".json", dataList,handler,HANDLE_DATA);
        }
    }
    @Override
    public void onRefresh() {
        isRefresh=true;
        setData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                srlSearch.setRefreshing(false);
            }
        }, 1000);
    }
}
