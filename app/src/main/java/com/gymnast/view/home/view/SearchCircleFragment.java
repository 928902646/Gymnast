package com.gymnast.view.home.view;

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
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.CircleData;
import com.gymnast.utils.CacheUtils;
import com.gymnast.utils.JSONParseUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.view.home.adapter.SearchCircleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zzqybyb19860112 on 2016/9/4.
 */
public class SearchCircleFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView recyclerView;
    private View view;
    List<CircleData> dataList=new ArrayList<>();;
     SearchCircleAdapter adapter;
    private SwipeRefreshLayout srlSearch;
    boolean isRefresh=false;
    public static final int HANDLE_DATA=1;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLE_DATA:
                    adapter = new SearchCircleAdapter(getActivity(),dataList);
                    adapter.setFriends(dataList);
                    recyclerView.setAdapter(adapter);
                    adapter.getFilter().filter(isRefresh == true ? HomeSearchResultAcitivity.etSearch.getText().toString().trim() : HomeSearchResultAcitivity.getSearchText());
                    srlSearch.setRefreshing(false);
                    break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_common_fragment, container, false);
        setView();
        getData();
        srlSearch.setOnRefreshListener(this);
        return  view;
    }
    private void setView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager  layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        srlSearch= (SwipeRefreshLayout) view.findViewById(R.id.srlSearch);
        RefreshUtil.refresh(srlSearch, getActivity());
        recyclerView.setVisibility(View.VISIBLE);
    }
    public void getData() {
        ArrayList<String> cacheData= (ArrayList<String>) CacheUtils.readJson(getActivity(), SearchCircleFragment.this.getClass().getName() + ".json");
        if (cacheData==null||cacheData.size()==0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String uri = API.BASE_URL + "/v1/search/model";
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("model", "2");
                    params.put("pageNumber", "100");
                    params.put("pages", "1");
                    String result = PostUtil.sendPostMessage(uri, params);
                    JSONParseUtil.parseNetDataSearchCircle(getActivity(), result, SearchCircleFragment.this.getClass().getName() + ".json", dataList, handler, HANDLE_DATA);
                }
            }).start();
        }else {
            JSONParseUtil.parseLocalDataSearchCircle(getActivity(), SearchCircleFragment.this.getClass().getName() + ".json", dataList, handler, HANDLE_DATA);
        }
    }
    @Override
    public void onRefresh() {
        isRefresh=true;
        getData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                srlSearch.setRefreshing(false);
            }
        }, 1000);
    }
}

