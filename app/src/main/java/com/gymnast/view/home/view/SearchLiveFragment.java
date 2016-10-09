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
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.CacheUtils;
import com.gymnast.utils.DialogUtil;
import com.gymnast.utils.JSONParseUtil;
import com.gymnast.utils.LiveUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.view.home.adapter.SearchLiveAdapter;
import com.gymnast.view.live.entity.LiveItem;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by zzqybyb19860112 on 2016/9/4.
 */
public class SearchLiveFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView recyclerView;
    List<LiveItem> dataList=new ArrayList<>();
    SearchLiveAdapter adapter;
    LiveItem liveItem;
    private SwipeRefreshLayout srlSearch;
    boolean isRefresh=false;
    public static final  int HANDLE_DATA=6;
    public static final int UPDATE_STATE_OK=1;
    public static final int MAINUSER_IN_OK=2;
    public static final int MAINUSER_IN_ERROR=3;
    public static final int OTHERUSER_IN_OK=4;
    public static final int OTHERUSER_IN_ERROR=5;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLE_DATA:
                    adapter=new SearchLiveAdapter(getActivity(),dataList);
                    adapter.setFriends(dataList);
                    recyclerView.setAdapter(adapter);
                    adapter.getFilter().filter(isRefresh == true ? HomeSearchResultAcitivity.etSearch.getText().toString().trim() : HomeSearchResultAcitivity.getSearchText());
                    srlSearch.setRefreshing(false);
                    adapter.setOnItemClickListener(new SearchLiveAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemPhotoClick(View view, LiveItem live) {
                            if (view.getId() == R.id.rivSearchPhoto){
                                liveItem = live;
                                Intent intent1 = new Intent(getActivity(), PersonalOtherHomeActivity.class);
                                int userDevasID=Integer.valueOf(liveItem.getLiveOwnerId());
                                intent1.putExtra("UserID", userDevasID);
                                getActivity().startActivity(intent1);
                            }else  if (view.getId() == R.id.ivSearchBig){
                                liveItem = live;
                                LiveUtil.doIntoLive(getActivity(),handler,liveItem);
                            }
                        }
                    });
                    break;
                case UPDATE_STATE_OK:
                    Toast.makeText(getActivity(),"您开启了直播",Toast.LENGTH_SHORT).show();
                    LiveUtil.doNext(getActivity(), liveItem);
                    break;
                case MAINUSER_IN_OK:
                    Toast.makeText(getActivity(),"您开启了直播",Toast.LENGTH_SHORT).show();
                    LiveUtil.doNext(getActivity(), liveItem);
                    break;
                case MAINUSER_IN_ERROR:
                    DialogUtil.goBackToLogin(getActivity(), "是否重新登陆？", "账号在其他地方登陆,您被迫下线！！！");
                    break;
                case OTHERUSER_IN_OK:
                    Toast.makeText(getActivity(),"您已进入直播室",Toast.LENGTH_SHORT).show();
                    LiveUtil.doNext(getActivity(), liveItem);
                    break;
                case OTHERUSER_IN_ERROR:
                    DialogUtil.goBackToLogin(getActivity(), "是否重新登陆？", "账号在其他地方登陆,您被迫下线！！！");
                    break;
        }
    }
    };
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.search_common_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        srlSearch= (SwipeRefreshLayout) view.findViewById(R.id.srlSearch);
        RefreshUtil.refresh(srlSearch, getActivity());
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager  layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        setData();
        srlSearch.setOnRefreshListener(this);
        return  view;
    }
    private void setData() {
        ArrayList<String> cacheData= (ArrayList<String>) CacheUtils.readJson(getActivity(), SearchLiveFragment.this.getClass().getName() + ".json");
        if (cacheData==null||cacheData.size()==0) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        String uri = API.BASE_URL + "/v1/search/model";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("model", "4");
                        params.put("pageNumber", "100");
                        params.put("pages", "1");
                        String result = PostUtil.sendPostMessage(uri, params);
                        JSONParseUtil.parseNetDataSearchLive(getActivity(), result, SearchLiveFragment.this.getClass().getName() + ".json", dataList, handler, HANDLE_DATA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {
            JSONParseUtil.parseLocalDataSearchLive(getActivity(), SearchLiveFragment.this.getClass().getName() + ".json", dataList, handler, HANDLE_DATA);
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
