package com.gymnast.view.personal.activity;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.personal.adapter.AllMsgAdapter;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by yf928 on 2016/8/1.
 */
public class PersonalAllMsgActivity extends ImmersiveActivity implements View.OnClickListener{
    private TextView allmsg;
    private PopupWindow mPopupwindow;
    private boolean isShow;//当前状态为true：window不显示，textview图标向下，flase:window显示，图标向上
    private Drawable drawablelower,drawableTop;
    private ImageView triangle,back;
    private RecyclerView listitem;
    private List<LiveItems> packitem=new ArrayList<>();
    private List<LiveItems> packitem1=new ArrayList<>();
    private AllMsgAdapter packAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allmsg);
        setview();
        LinearLayoutManager circleLayout = new LinearLayoutManager(this);
        packAdapter = new AllMsgAdapter(this, packitem);
        //设置布局管理器
        listitem.setLayoutManager(circleLayout);
        listitem.setAdapter(packAdapter);
        drawableTop= getResources().getDrawable(R.mipmap.icon_open_upper);
        drawablelower= getResources().getDrawable(R.mipmap.icon_open_lower);
        initview();
    }
    public PersonalAllMsgActivity() {
        for(int i=0;i<20;i++){
            LiveItems liveItem  = new  LiveItems("",null,""+i,""+i) ;
            packitem.add(liveItem);
        }
    }
    public void PersonalActivityAllMsg2() {
        for(int i=0;i<5;i++){
            LiveItems liveItem  = new  LiveItems("",null,""+i,""+i) ;
            packitem1.add(liveItem);
        }
    }
    private void initview() {
        allmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShow){
                    mPopupwindow.dismiss();
                    drawablelower.setBounds(0, 0, drawablelower.getMinimumWidth(), drawablelower.getMinimumHeight());
                    allmsg.setCompoundDrawables(null,null,drawablelower,null);
                    triangle.setVisibility(View.GONE);
                }else {
                    showPopupWindow();
                    drawableTop.setBounds(0, 0, drawableTop.getMinimumWidth(), drawableTop.getMinimumHeight());
                    allmsg.setCompoundDrawables(null,null,drawableTop,null);
                    triangle.setVisibility(View.VISIBLE);
                }
                isShow=!isShow;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void showPopupWindow() {
        View contenview =  LayoutInflater.from(PersonalAllMsgActivity.this).inflate(R.layout.activity_popup_item,null);
        mPopupwindow= new PopupWindow(contenview);
        mPopupwindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupwindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //是否响应touch事件
        //mPopWindow.setTouchable(false);
        //设置可以获得焦点
        // mPopupwindow.setFocusable(true);
        //设置弹窗内可点击
        mPopupwindow.setTouchable(true);
        //设置弹窗外可点击
         mPopupwindow.setOutsideTouchable(true);
        //外部是否可以点击
       mPopupwindow.setBackgroundDrawable(new BitmapDrawable());
        LinearLayout all = (LinearLayout) contenview.findViewById(R.id.all);
        LinearLayout system = (LinearLayout) contenview.findViewById(R.id.system);
        LinearLayout interaction = (LinearLayout) contenview.findViewById(R.id.interaction);
        LinearLayout transaction = (LinearLayout) contenview.findViewById(R.id.transaction);
        all.setOnClickListener(this);
        system.setOnClickListener(this);
        interaction.setOnClickListener(this);
        transaction.setOnClickListener(this);
        mPopupwindow.showAsDropDown(allmsg);
    }
    private void setview() {
        allmsg =(TextView)findViewById(R.id.allmsg);
        triangle =(ImageView)findViewById(R.id.triangle);
        back =(ImageView)findViewById(R.id.personal_back);
        listitem =(RecyclerView) findViewById(R.id.allmsg_recycler);
    }
    public static PersonalAllMsgActivity newInstance(String param1, String param2) {
        PersonalAllMsgActivity activity = new PersonalAllMsgActivity();
        return activity;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.all:
                Toast.makeText(this, "all", Toast.LENGTH_SHORT).show();
            break;
            case R.id.system: {
                Toast.makeText(this, "system", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.interaction: {
                Toast.makeText(this, "interaction", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.transaction: {
                Toast.makeText(this, "transaction", Toast.LENGTH_SHORT).show();
            }
            break;
        }
        mPopupwindow.dismiss();
        drawablelower.setBounds(0, 0, drawablelower.getMinimumWidth(), drawablelower.getMinimumHeight());
        allmsg.setCompoundDrawables(null,null,drawablelower,null);
        triangle.setVisibility(View.GONE);
        isShow=!isShow;
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
