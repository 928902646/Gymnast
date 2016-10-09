package com.gymnast.view.personal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.CircleData;
import com.gymnast.data.personal.PostsData;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.DividerItemDecoration;
import com.gymnast.view.personal.activity.PersonalCircleActivity;
import com.gymnast.view.personal.activity.PersonalPostsDetailActivity;
import com.gymnast.view.personal.adapter.CircleItemAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yf928 on 2016/8/3.
 */
public class CircleItemFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView listitem;
    private List<PostsData> listdata=new ArrayList<>();
    public static final int HANFLE_DATA_UPDATE=1;
    private CircleItemAdapter adapter;
    private int CircleId;
    private View view;
    private SwipeRefreshLayout swipeRefresh;
    private  int max=100;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANFLE_DATA_UPDATE:
                    adapter = new CircleItemAdapter(getActivity(),listdata);
                    listitem.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
                    listitem.setAdapter(adapter);
                    adapter.setOnItemClickListener(new CircleItemAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClickListener(View view, int position) {
                            if(listdata.size()!=0){
                                PostsData item=  listdata.get(position);
                                Intent i=new Intent(getActivity(),PersonalPostsDetailActivity.class);
                                i.putExtra("TieZiID",item.getId());
                                getActivity().startActivity(i);
                            }
                        }
                    });
                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                    break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_personal_circle,container,false);
        setView();
        getData();
        CircleId = getArguments().getInt("CircleId");
        return view;
    }
    private void setView() {
        listitem = (RecyclerView)view.findViewById(R.id.recyclerview);
        swipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefresh);
        RefreshUtil.refresh(swipeRefresh,getActivity());
        swipeRefresh.setOnRefreshListener(this);
    }
    @Override public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    public void getData() {
        CircleData data= (CircleData) getActivity().getIntent().getSerializableExtra("CircleData");
        int circleId = 0;
        if (data!=null){
            circleId=data.getId();
        }
        final int finalCircleId = circleId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri= API.BASE_URL+"/v1/circleItem/list";
                Map<String,String> params= new HashMap<String,String>();
                params.put("start",0+"");
                params.put("pageSize",100+"");
                if(CircleId!=0){
                    params.put("circleId",CircleId+"");
                }else {
                    params.put("circleId","");
                }
                String result= PostUtil.sendPostMessage(uri,params);
                try {
                    JSONObject object=new JSONObject(result);
                    JSONArray jsondata = object.getJSONArray("data");
                    for(int i=0;i<jsondata.length();i++){
                        JSONObject obj=jsondata.getJSONObject(i);
                        String title=obj.getString("title");
                        long createTime= obj.getLong("createTime");
                        String content=obj.getString("baseContent");
                        String imgUrl= StringUtil.isNullAvatar(obj.getString("imgUrl"));
                        imgUrl= PicUtil.getImageUrlDetail(getActivity(), imgUrl, 320, 320);
                        String nickname=obj.getString("nickname");
                        String avatar= StringUtil.isNullAvatar(obj.getString("avatar"));
                        String circleTitle=obj.getString("circleTitle");
                        int state=obj.getInt("state");
                        int zanCount=obj.getInt("zanCount");
                        int meetCount=obj.getInt("meetCount");
                        int circleId= obj.getInt("circleId");
                        int createId= obj.getInt("createId");
                        int Id= obj.getInt("id");
                        JSONObject pageViews=obj.getJSONObject("pageViews");
                        int pageviews= pageViews.getInt("pageviews");
                        PostsData data=new PostsData();
                        data.setCreateTime(createTime);
                        data.setTitle(title);
                        data.setContent(content);
                        data.setImgUrl(imgUrl);
                        data.setNickname(nickname);
                        data.setAvatar(avatar);
                        data.setCircleTitle(circleTitle);
                        data.setState(state);
                        data.setZanCount(zanCount);
                        data.setMeetCount(meetCount);
                        data.setCircleId(circleId);
                        data.setCreateId(createId);
                        data.setId(Id);
                        data.setPageviews(pageviews);
                        listdata.add(data);
                    }
                    handler.sendEmptyMessage(HANFLE_DATA_UPDATE);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (listdata.size()!=0){
            listdata.clear();
            getData();
        }else {}
    }

    @Override
    public void onRefresh() {
        if(listdata.size()!=0){
            listdata.clear();
            getData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 停止刷新
                    swipeRefresh.setRefreshing(false);
                }
            }, 1000);
        }else {
        }
    }
}
