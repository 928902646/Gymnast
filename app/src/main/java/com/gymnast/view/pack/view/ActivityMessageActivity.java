package com.gymnast.view.pack.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.recyclerview.RecycleHolder;
import com.gymnast.view.recyclerview.RecyclerAdapter;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by yf928 on 2016/8/4.
 */
public class ActivityMessageActivity extends ImmersiveActivity {
    private RecyclerView listitem;
    private RecyclerAdapter<String> textAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_dynamic_details);
        setView();
        initView();
    }
    private void initView() {
        LinearLayoutManager circleLayout = new LinearLayoutManager(this);
        listitem.setLayoutManager(circleLayout);
        listitem.setHasFixedSize(true);
        textAdapter = new RecyclerAdapter<String>(this, getData(), R.layout.item_message_list) {
            @Override
            public void convert(RecycleHolder holder, String data, int position) {
            }
        };
        listitem.setAdapter(textAdapter);
    }
    public List<String> getData() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            list.add(""+i);
        }
        return list;
    }
    private void setView() {
        listitem =(RecyclerView)findViewById(R.id.recycler_address);
    }
}
