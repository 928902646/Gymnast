package com.gymnast.view.personal.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.personal.adapter.WalletAdapter;
import com.gymnast.view.user.LoginActivity;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by yf928 on 2016/8/3.
 */
public class WalletFragmengt  extends Fragment{
    private RecyclerView listitem;
    private WalletAdapter packAdapter;
    private List<LiveItems> packitem=new ArrayList<>();
    private View view;
    private String token;
    private LinearLayout linear;
    private TextView tv_login;
    private LinearLayout login_ll;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wallet,container,false);
        SharedPreferences share = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token",null);
        setView();
        initView();
        return view;
    }
    private void setView() {
        listitem = (RecyclerView)view.findViewById(R.id.recyclerview);
        listitem.setHasFixedSize(true);
        linear= (LinearLayout) view.findViewById(R.id.linear);
        login_ll= (LinearLayout) view.findViewById(R.id.login_ll);
        tv_login= (TextView) view.findViewById(R.id.tv_login);
    }
    private void initView() {
        if(TextUtils.isEmpty(token)){
            login_ll.setVisibility(View.VISIBLE);
            linear.setVisibility(View.GONE);
        }else {
            linear.setVisibility(View.VISIBLE);
            login_ll.setVisibility(View.GONE);
        }
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
    public WalletFragmengt() {
        for(int i=0;i<20;i++){
            LiveItems liveItem  = new  LiveItems("",null,""+i,""+i) ;
            packitem.add(liveItem);
        }
    }
    public static WalletFragmengt newInstance(String param1, String param2) {
        WalletFragmengt fragment = new WalletFragmengt();
        return fragment;
    }
    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //创建线性布局
        LinearLayoutManager circleLayout = new LinearLayoutManager(getActivity());
        circleLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        packAdapter = new WalletAdapter(getActivity(), packitem);
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
