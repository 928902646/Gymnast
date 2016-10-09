package com.gymnast.view.home.view;

import android.content.Intent;
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
import com.gymnast.data.personal.PostsData;
import com.gymnast.utils.CacheUtils;
import com.gymnast.utils.JSONParseUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.view.home.adapter.SearchTieZiAdapter;
import com.gymnast.view.personal.activity.PersonalPostsDetailActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yf928 on 2016/8/3.
 */
public class SearchTieZiFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView recyclerView;
    private List<PostsData> dataList=new ArrayList<>();
    public static final int HANDLE_DATA=1;
    SearchTieZiAdapter adapter;
    SwipeRefreshLayout srlSearch;
    boolean isRefresh=false;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLE_DATA:
                    adapter = new SearchTieZiAdapter(getActivity(),dataList);
					adapter.setFriends(dataList);
                    recyclerView.setAdapter(adapter);
                    adapter.getFilter().filter(isRefresh == true ? HomeSearchResultAcitivity.etSearch.getText().toString().trim() : HomeSearchResultAcitivity.getSearchText());
                    adapter.setOnItemClickListener(new SearchTieZiAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClickListener(View view, int position) {
                            if (dataList.size() != 0) {
                                PostsData item = dataList.get(position);
                                Intent i = new Intent(getActivity(), PersonalPostsDetailActivity.class);
                                int TieZiID=item.getId();
                                int UserID=item.getUserid();
                                i.putExtra("TieZiID",TieZiID);
                                //   i.putExtra("UserID",UserID);
                                getActivity().startActivity(i);
                            }
                        }
                    });
                    srlSearch.setRefreshing(false);
                    break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.search_common_fragment,container,false);
        srlSearch= (SwipeRefreshLayout) view.findViewById(R.id.srlSearch);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager  layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        getData();
        srlSearch.setOnRefreshListener(this);
        return view;
    }
    public void getData() {
        ArrayList<String> cacheData= (ArrayList<String>) CacheUtils.readJson(getActivity(), SearchTieZiFragment.this.getClass().getName() + ".json");
        if (cacheData==null||cacheData.size()==0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String uri = API.BASE_URL + "/v1/search/model";
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("model", "6");
                    params.put("pageNumber", "100");
                    params.put("pages", "1");
                    String result = PostUtil.sendPostMessage(uri, params);
                    Log.i("tag", "TieZi" + result);
                    JSONParseUtil.parseNetDataSearchTieZi(getActivity(), result, SearchTieZiFragment.this.getClass().getName() + ".json", dataList, handler, HANDLE_DATA);
                }
            }).start();
        }else {
            JSONParseUtil.parseLocalDataSearchTieZi(getActivity(), SearchTieZiFragment.this.getClass().getName() + ".json", dataList,handler,HANDLE_DATA);
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
