package com.gymnast.view.personal.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gymnast.R;
import com.gymnast.view.personal.adapter.AllAdapter;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by yf928 on 2016/7/20.
 */
public class AllFragment extends Fragment {
    private RecyclerView listitem;
    private List<LiveItems> packitem=new ArrayList<>();
    private AllAdapter packAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          View view = inflater.inflate(R.layout.fragment_recyclerview,container,false);
        listitem = (RecyclerView)view.findViewById(R.id.recyclerview);
        return view;
    }
    public AllFragment() {
        for(int i=0;i<20;i++){
          LiveItems liveItem  = new  LiveItems("",null,""+i,""+i) ;
            packitem.add(liveItem);
        }
    }
    public static AllFragment newInstance(String param1, String param2) {
        AllFragment fragment = new AllFragment();
        return fragment;
    }
    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //创建线性布局
        LinearLayoutManager circleLayout = new LinearLayoutManager(getActivity());
        packAdapter = new AllAdapter(getActivity(), packitem);
        //设置布局管理器
        listitem.setLayoutManager(circleLayout);
        listitem.setHasFixedSize(true);
        listitem.setAdapter(packAdapter);
    }
    public static class LiveItems {
        public final String liveUrl;
        public final String liveViewer;
        public final String livePicture;
        public final String liveTitle;
        public LiveItems(String liveUrl,String livePicture,  String liveViewer, String liveTitle) {
            this.liveUrl = liveUrl;
            this.liveViewer = liveViewer;
            this.liveTitle = liveTitle;
            this.livePicture=livePicture;
        }
    }
}
