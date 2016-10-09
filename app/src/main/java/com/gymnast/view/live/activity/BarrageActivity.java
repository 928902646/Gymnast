package com.gymnast.view.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.live.adapter.BarrageItemAdapter;
import com.gymnast.view.live.entity.BarrageItem;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BarrageActivity extends ImmersiveActivity implements View.OnClickListener{
    ImageView ivBack;
    RecyclerView rvBarrage;
    EditText etSendBarrage;
    TextView tvSendBarrage;
    private BarrageItemAdapter adapter;
    ArrayList<BarrageItem> lists=new ArrayList<>();
    private String tokenAll,userId,nickName,avatar,groupId;
    private int liveId;
    SimpleDateFormat sdf;
    public static final int HANDLE_DATA=1;
    public static final int HANDLE_SEND_BARRAGE=2;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLE_DATA:
                    adapter = new BarrageItemAdapter(BarrageActivity.this,lists);
                    rvBarrage.setAdapter(adapter);
                    rvBarrage.scrollToPosition(0);
                    break;
                case HANDLE_SEND_BARRAGE:
                    lists.add((BarrageItem) msg.obj);
                    adapter = new BarrageItemAdapter(BarrageActivity.this,lists);
                    rvBarrage.setAdapter(adapter);
                    adapter.notifyItemChanged(lists.size()-1);
                    rvBarrage.scrollToPosition(adapter.getItemCount()-1);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrage);
        Intent intent=getIntent();
        tokenAll=intent.getStringExtra("tokenAll");
        liveId=intent.getIntExtra("liveId", 0);
        userId=intent.getStringExtra("userId");
        nickName=intent.getStringExtra("nickName");
        avatar=intent.getStringExtra("avatar");
        groupId=intent.getStringExtra("groupId");
        setViews();
        getData();
        setDataAndView();
        addListeners();
    }
    private void addListeners() {
        ivBack.setOnClickListener(this);
        tvSendBarrage.setOnClickListener(this);
    }
    private void setDataAndView() {
        rvBarrage.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvBarrage.setLayoutManager(layoutManager);
        adapter = new BarrageItemAdapter(this,lists);
        rvBarrage.setAdapter(adapter);
    }
    private void setViews() {
        ivBack= (ImageView) findViewById(R.id.ivBack);
        rvBarrage= (RecyclerView) findViewById(R.id.rvBarrage);
        etSendBarrage= (EditText) findViewById(R.id.etSendBarrage);
        tvSendBarrage= (TextView) findViewById(R.id.tvSendBarrage);
    }
    public void getData(){
        Thread threadGetData=new Thread(){
            @Override
            public void run() {
                String uri=API.BASE_URL+"/v1/live/barrage/list";
                HashMap<String,String> params=new HashMap<>();
                params.put("liveId", liveId + "");
                String result=PostUtil.sendPostMessage(uri,params);
                Log.i("tag", "barrageActivity" + result);
                try{
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray array=jsonObject.getJSONArray("barrages");
                    sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time;
                    for (int i=0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        String name=object.getString("nickName");
                        String photoUrl= StringUtil.isNullAvatar(object.getString("avatar"));
                        Log.i("tag","avatar2"+avatar);
                        String content=object.getString("content");
                        photoUrl=API.IMAGE_URL + PicUtil.getImageUrlDetail(BarrageActivity.this, photoUrl, 40, 40);
                        BarrageItem item=new BarrageItem();
                        item.setPriseNumber(0);
                        item.setBody(content);
                        long createTime=object.getLong("createTime");
                        time=sdf.format(new Date(createTime));
                        item.setTime(time);
                        item.setName(name);
                        item.setPhotoUrl(photoUrl);
                        lists.add(item);
                    }
                    handler.sendEmptyMessage(HANDLE_DATA);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        threadGetData.setPriority(3);
        threadGetData.start();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivBack:
                finish();
                break;
            case R.id.tvSendBarrage:
                if (etSendBarrage.getText().toString().equals("")){
                    Toast.makeText(BarrageActivity.this,"不能发送空弹幕消息",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    final String body=etSendBarrage.getText().toString();
                    Thread threadToSend=new Thread(){
                        @Override
                        public void run() {
                            try{
                                String uri= API.BASE_URL+"/v1/live/barrage/create";
                                HashMap<String,String> params=new HashMap<String, String>();
                                params.put("token",tokenAll);
                                params.put("liveId",liveId+"");
                                params.put("userId",userId);
                                params.put("content", body);
                                String result= PostUtil.sendPostMessage(uri, params);
                                Log.i("tag", result);
                                EMMessage message = EMMessage.createTxtSendMessage(body, groupId);
                                // 增加自己特定的属性
                                message.setAttribute("photoUrl", avatar);
                                message.setFrom(userId);
                                message.setChatType(EMMessage.ChatType.GroupChat);
                                message.setMessageStatusCallback(new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        Log.i("tag", "弹幕消息发送成功！");
                                    }
                                    @Override
                                    public void onError(int i, String s) {
                                        Log.i("tag", "弹幕消息发送失败！");
                                    }
                                    @Override
                                    public void onProgress(int i, String s) {
                                        Log.i("tag", "弹幕消息发送中！");
                                    }
                                });
                                EMClient.getInstance().chatManager().sendMessage(message);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    };
                    threadToSend.setPriority(8);
                    threadToSend.start();
                    etSendBarrage.setText("");
                    BarrageItem item=new BarrageItem();
                    item.setPhotoUrl(avatar);
                    item.setPriseNumber(0);
                    item.setTime(sdf.format(new Date()));
                    item.setBody(body);
                    item.setName(nickName);
                    Message msg=new Message();
                    msg.what=HANDLE_SEND_BARRAGE;
                    msg.obj=item;
                    handler.sendMessage(msg);
                }
                break;
        }
    }
}
