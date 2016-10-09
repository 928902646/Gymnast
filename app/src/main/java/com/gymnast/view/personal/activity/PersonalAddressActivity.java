package com.gymnast.view.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.personal.adapter.AddressAdapter;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by yf928 on 2016/7/25.
 */
public class PersonalAddressActivity extends ImmersiveActivity {
    private RecyclerView listitem;
    private List<LiveItems> packItems=new ArrayList<>();
    private AddressAdapter packAdapter;
    private CheckBox checkbox;
    private ImageView back;
    private TextView add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_address);
        setview();
        LinearLayoutManager LinearLayout = new LinearLayoutManager(this);
        packAdapter = new AddressAdapter(this, packItems);
        listitem.setLayoutManager(LinearLayout);
        listitem.setAdapter(packAdapter);
        initView();
    }
    private void setview() {
        back= (ImageView)findViewById(R.id.personal_back);
        add = (TextView)findViewById(R.id.add);
        listitem=(RecyclerView)findViewById(R.id.recycler_address);
        checkbox=(CheckBox)findViewById(R.id.address_checkBox);
    }
    private void initView() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PersonalAddressActivity.this,PersonalAddressAddActivity.class);
                startActivity(i);
            }
        });
    }
    public PersonalAddressActivity() {
        for (int i = 0; i < 10; i++) {
            LiveItems liveItem = new LiveItems("测试", null, "12345" + i, "谁VS谁" + i);
            packItems.add(liveItem);
        }
    }
    public static PersonalAddressActivity newInstance(String param1, String param2) {
        PersonalAddressActivity acitvity = new PersonalAddressActivity();
        return acitvity;
    }
    public static class LiveItems {
        public final String liveUrl;
        public final String livePicture;
        public final String liveViewer;
        public final String liveTitle;
        public LiveItems(String liveUrl, String livePicture, String liveViewer, String liveTitle) {
            this.liveUrl = liveUrl;
            this.livePicture = livePicture;
            this.liveViewer = liveViewer;
            this.liveTitle = liveTitle;
        }
    }

}
