package com.gymnast.view.live.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gymnast.App;
import com.gymnast.R;
import com.gymnast.data.hotinfo.LiveMessage;
import com.gymnast.data.net.API;
import com.gymnast.utils.DialogUtil;
import com.gymnast.utils.LiveUtil;
import com.gymnast.utils.LogUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.utils.UploadUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.HomeActivity;
import com.gymnast.view.live.adapter.BarrageViewAdapter;
import com.gymnast.view.live.adapter.MessageAdapter;
import com.gymnast.view.live.customview.BarrageView;
import com.gymnast.view.live.entity.BarrageViewEntity;
import com.gymnast.view.live.entity.EndLiveEntity;
import com.gymnast.view.user.LoginActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class LiveActivity extends ImmersiveActivity implements View.OnClickListener{
    RecyclerView recyclerView;
    RecyclerView rvBarrage;
    FrameLayout flMain;
    MessageAdapter adapter;
    EMMessageListener msgListener;
    ArrayList<LiveMessage> messageList=new ArrayList<>();
    public static final int USER_MAIN=1;
    public static final int USER_OTHER=2;
    static int user_now;
    LinearLayout llOtherUser,llMainUser,llShareToFriends,llShareToWeiChat,llShareToQQ,llShareToQQZone,llShareToMicroBlog;
    EditText etMainUser,etOtherUser;
    ImageView ivSelectPic,ivBarrage,ivBigPicture,ivShowOrHideBarrage,ivPrise,personal_back,ivMoreToDo,ivClose;
    TextView tvOnlineNumber,tvTabTitle,tvBarrageNumber,tvSendBarrage,tvCollect,tvReport,tvDelete, tvSpacial,tvTop;
    Button btnSend;
    private Intent intent;
    boolean isCollected=false;
    boolean isPraised=false;
    private int liveId=0;
    String bigPictureUrl;
    long startTime=0L;
    private int LIVE_STATE=0;
    BarrageViewAdapter barrageViewAdapter;
    List<BarrageViewEntity> barrageList=new ArrayList<>();
    public static int peopleNumber=0;//观众人数
    public static int shareNumber=0;//分享次数
    private String tokenAll,liveOwnerId,userId,nickName,imgUrl,avatar,mainPhotoUrl,groupId="",title;
    String url= API.BASE_URL+"/v1/live/text/create";
    public static boolean isShowing=true;
    public static final int HANDLE_TIME_CHANGE=1;
    public static final int HANDLE_MAINUSER_SEND_PICTURE_LIVE=2;
    public static final int HANDLE_OTHERUSER_RECEIVE_PICTURE_LIVE=3;
    public static final int HANDLE_SEND_TEXT_MESSAGE=4;
    public static final int HANDLE_RECEIVE_TEXT_MESSAGE=5;
    public static final int HANDLE_RECEIVE_BARRAGE_MESSAGE=6;
    public static final int HANDLE_INIT_MESSAGE=7;
    public static final int HANDLE_END_LIVE=8;
    public static final int HANDLE_NEWUSER_IN=9;
    public static final int HANDLE_NEWUSER_OUT=10;
    public static final int HANDLE_BARRAGE_BASE_DATA=11;
    public static final int HANDLE_UNKNOWN_ERROR=12;
    public static final int HANDLE_CANCEL_PRAISE=13;
    public static final int HANDLE_PRAISE=14;
    public static final int HANDLE_START_PRAISE_STATUS=15;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what){
                case HANDLE_UNKNOWN_ERROR://点赞的不明错误
                    Toast.makeText( LiveActivity.this,"未知错误，请重试！",Toast.LENGTH_SHORT).show();
                    break;
                case HANDLE_START_PRAISE_STATUS://初始化点赞情况
                    boolean isStartPraised= (boolean) msg.obj;
                    if (isStartPraised){
                        ivPrise.setImageResource(R.mipmap.like_pressed);
                    }else {
                        ivPrise.setImageResource(R.mipmap.like_normal);
                    }
                    ivPrise.invalidate();
                    break;
                case HANDLE_CANCEL_PRAISE://用户取消点赞
                    ivPrise.setImageResource(R.mipmap.like_normal);
                    Toast.makeText( LiveActivity.this,"已取消点赞！",Toast.LENGTH_SHORT).show();
                    break;
                case HANDLE_PRAISE://用户点赞
                    ivPrise.setImageResource(R.mipmap.like_pressed);
                    Toast.makeText( LiveActivity.this,"已点赞！",Toast.LENGTH_SHORT).show();
                    break;
                case HANDLE_BARRAGE_BASE_DATA://初始化历史弹幕数据
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LiveActivity.this);
                    rvBarrage.setLayoutManager(layoutManager);
                    if (barrageList.size()<=3){
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        rvBarrage.setLayoutParams(lp);
                    }else {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 340);
                        rvBarrage.setLayoutParams(lp);
                    }
                    barrageViewAdapter=new BarrageViewAdapter(LiveActivity.this,barrageList);
                    rvBarrage.setAdapter(barrageViewAdapter);
                    barrageViewAdapter.notifyDataSetChanged();
                    break;
                case HANDLE_NEWUSER_IN://新用户进入直播室
                    String nickNameIn= ( (String) msg.obj).split("@")[1];
                    Log.i("tag",msg.obj+"-------OBJ-----IN");
                    Toast.makeText(LiveActivity.this,"用户"+nickNameIn+"进来了",Toast.LENGTH_SHORT).show();
                    peopleNumber+=1;
                    tvOnlineNumber.setText(peopleNumber + "人在线");
                    tvOnlineNumber.invalidate();
                    break;
                case HANDLE_NEWUSER_OUT://用户离开直播室
                    Log.i("tag",msg.obj+"-------OBJ----------OUT");
                    String nickNameOut=( (String) msg.obj).split("@")[1];
                    Toast.makeText(LiveActivity.this,"用户"+nickNameOut+"离开了",Toast.LENGTH_SHORT).show();
                    peopleNumber-=1;
                    tvOnlineNumber.setText(peopleNumber + "人在线");
                    tvOnlineNumber.invalidate();
                    break;
                case HANDLE_INIT_MESSAGE://初始化数据
                    messageList.addAll((ArrayList<LiveMessage>) msg.obj);
                    adapter = new MessageAdapter(LiveActivity.this,messageList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.scrollToPosition(0);
                    recyclerView.invalidate();
                    break;
                case HANDLE_SEND_TEXT_MESSAGE://播主发送文本消息
                    etMainUser.setText("");
                    messageList.add((LiveMessage) msg.obj);
                    adapter = new MessageAdapter(LiveActivity.this,messageList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(messageList.size()-1);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    recyclerView.invalidate();
                    break;
                case HANDLE_MAINUSER_SEND_PICTURE_LIVE://播主发送图片
                    LiveMessage message= (LiveMessage) msg.obj;
                    messageList.add( message);
                    adapter = new MessageAdapter(LiveActivity.this,messageList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(messageList.size()-1);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                    recyclerView.invalidate();
                    Uri uri = Uri.parse("android.resource://"+LiveActivity.this.getPackageName()+"/"+ R.raw.message);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
                    r.play();
                    break;
                case HANDLE_TIME_CHANGE://处理时间改变
                    tvBarrageNumber.setText(messageList.size() + "");
                    adapter = new MessageAdapter(LiveActivity.this,messageList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                    recyclerView.invalidate();
                    break;
                case HANDLE_RECEIVE_TEXT_MESSAGE://普通观众收到文本消息
                    LiveMessage msgLiveAdd= (LiveMessage) msg.obj;
                    messageList.add(msgLiveAdd);
                    adapter = new MessageAdapter(LiveActivity.this,messageList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                    recyclerView.invalidate();
                    break;
                case HANDLE_OTHERUSER_RECEIVE_PICTURE_LIVE://普通观众收到图片消息
                    LiveMessage message1= (LiveMessage) msg.obj;
                    messageList.add( message1);
                    adapter = new MessageAdapter(LiveActivity.this,messageList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(messageList.size()-1);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                    recyclerView.invalidate();
                    Uri uri1 = Uri.parse("android.resource://"+LiveActivity.this.getPackageName()+"/"+ R.raw.message);
                    Ringtone r1 = RingtoneManager.getRingtone(getApplicationContext(), uri1);
                    r1.play();
                    break;
                case HANDLE_RECEIVE_BARRAGE_MESSAGE://播主或其他观众收到弹幕消息
                    BarrageViewEntity barrageMSG= (BarrageViewEntity) msg.obj;
                    barrageList.add(barrageMSG);
                    RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(LiveActivity.this);
                    rvBarrage.setLayoutManager(layoutManager2);
                    if (isShowing) {
                        if (barrageList.size()<=3){
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            rvBarrage.setLayoutParams(lp);
                        }else {
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 340);
                            rvBarrage.setLayoutParams(lp);
                        }
                        barrageViewAdapter=new BarrageViewAdapter(LiveActivity.this,barrageList);
                        rvBarrage.setAdapter(barrageViewAdapter);
                        barrageViewAdapter.notifyItemChanged(barrageList.size()-1);
                        rvBarrage.scrollToPosition(barrageViewAdapter.getItemCount()-1);
                    }
                    flMain.invalidate();
                    break;
                case HANDLE_END_LIVE://处理直播结束逻辑
                   final EndLiveEntity entity= (EndLiveEntity) msg.obj;
                    if (user_now==USER_MAIN){
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent(LiveActivity.this,CloseLiveActivity.class);
                                intent.putExtra("EndLiveEntity",entity);
                                LiveActivity.this.startActivity(intent);
                                LiveActivity.this.finish();
                            }
                        },3000);
                    }else {
                              handler.postDelayed(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(LiveActivity.this,"直播已结束！",Toast.LENGTH_LONG).show();
                                      Intent intent1=new Intent(LiveActivity.this,HomeActivity.class);
                                      LiveActivity.this.startActivity(intent1);
                                      LiveActivity.this.finish();
                                  }
                              },3000);
                    }
                    break;
            }
        }
    };
    TimerTask task=new TimerTask() {
        @Override
        public void run() {
            long nowTime=System.currentTimeMillis();
            for (int i=0;i<messageList.size();i++){
               long disTime=nowTime- messageList.get(i).getCreateTime();
                String time=checkTime(disTime);
                messageList.get(i).setTimeUntilNow(time);
            }
            handler.sendEmptyMessage(HANDLE_TIME_CHANGE);
        }
    };
    private String checkTime(long disTime) {
        String time="";
        if (disTime<60000){
           time="刚刚";
        }else if (disTime<3600000&&disTime>=60000){
            time=disTime/60000+"分钟前";
        }else if (disTime<86400000&&disTime>=3600000){
            time=disTime/3600000+"小时前";
        }else if (disTime<864000000&&disTime>=86400000){
            time=disTime/86400000+"天前";
        }else {
            time="很久以前";
        }
        return time;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_live);
        setBaseData();
        SharedPreferences share = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        tokenAll=share.getString("Token", "");
        userId=share.getString("UserId", "");
        nickName = share.getString("NickName", "");
        avatar=share.getString("Avatar", "");
        avatar=StringUtil.isNullAvatar(avatar);
        startTime=System.currentTimeMillis();
        recyclerView= (RecyclerView) findViewById(R.id.recycleView);
        rvBarrage= (RecyclerView) findViewById(R.id.rvBarrage);
        adapter = new MessageAdapter(LiveActivity.this,messageList);
        recyclerView.setAdapter(adapter);
        personal_back= (ImageView) findViewById(R.id.personal_back);
        ivMoreToDo= (ImageView) findViewById(R.id.ivMoreToDo);
        flMain= (FrameLayout) findViewById(R.id.flMain);
        ivBigPicture= (ImageView) findViewById(R.id.ivBigPicture);
        tvOnlineNumber= (TextView) findViewById(R.id.tvOnlineNumber);
        tvTabTitle= (TextView) findViewById(R.id.tvTabTitle);
        llMainUser= (LinearLayout) findViewById(R.id.llMainUser);
        etMainUser= (EditText) findViewById(R.id.etMainUser);//播主输入直播内容
        ivSelectPic= (ImageView)llMainUser.findViewById(R.id.ivSelectPic);//播主选择图片
        ivBarrage= (ImageView) findViewById(R.id.ivBarrage);//播主显示或隐藏弹幕
        btnSend= (Button) findViewById(R.id.btnSend);//发送直播内容
        llOtherUser= (LinearLayout) findViewById(R.id.llOtherUser);
        etOtherUser= (EditText) llOtherUser.findViewById(R.id.etOtherUser);//普通用户发送弹幕
        ivShowOrHideBarrage= (ImageView) llOtherUser.findViewById(R.id.ivShowOrHideBarrage);//普通用户显示或隐藏弹幕
        ivPrise= (ImageView) llOtherUser.findViewById(R.id.ivPrise);//普通用户给直播点赞
        tvBarrageNumber= (TextView) llOtherUser.findViewById(R.id.tvBarrageNumber);//直播弹幕总数
        tvSendBarrage= (TextView) llOtherUser.findViewById(R.id.tvSendBarrage);//发送弹幕
        checkUserType();
        if (groupId==null|groupId.equals("")){
            getGroupID();
        }else {}
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        initData();
        Timer timer=new Timer();
        timer.schedule(task, 0, 60000);
        addListeners();
        msgListener= new EMMessageListener() {
            @Override
            public void onMessageReceived(final List<EMMessage> messages) {
                for (EMMessage msg : messages) {
                    if (msg.getType().equals(EMMessage.Type.TXT)) {
                        if (!msg.getFrom().equals(liveOwnerId)){//一般用户收到文本信息
                                String content = msg.getBody().toString();
                            Log.i("tag","TEXT-------content"+content);
                                content = content.substring(5, content.length() - 1);
                                String photoUrl =msg.getStringAttribute("photoUrl", null);
                            if (content.equals("A8F6C870C92E4D672212E58A089DEEC9")){
                                Message message=new Message();
                                message.what=HANDLE_NEWUSER_IN;
                                message.obj=photoUrl;
                                handler.sendMessage(message);
                            }else if (content.equals("F00647B3C94654F3BDE6E3C5944ABADD")){
                                Message message=new Message();
                                message.what=HANDLE_NEWUSER_OUT;
                                message.obj=photoUrl;
                                handler.sendMessage(message);
                            }else {
                                Log.i("tag", "收到弹幕消息:" + content);
                                Log.i("tag", "收到弹幕消息:" + photoUrl);
                                BarrageViewEntity barrageMSG = new BarrageViewEntity();
                                barrageMSG.setContent(content);
                                barrageMSG.setPicUrl(photoUrl);
                                Message msgToHandler = new Message();
                                msgToHandler.what = HANDLE_RECEIVE_BARRAGE_MESSAGE;
                                msgToHandler.obj = barrageMSG;
                                handler.sendMessage(msgToHandler);
                            }
                        }else {
                            String text=msg.getBody().toString();
                            text=text.substring(5,text.length()-1);
                            Log.i("tag","TEXT-------text----"+text);
                            String photoUrl =msg.getStringAttribute("photoUrl", null);
                            if (text!=null){
                                if(text.equals("1B643AEC5CD0034236DDE2E1465D366D")){
                                    Log.i("tag","收到结束指令");
                                    handler.sendEmptyMessage(HANDLE_END_LIVE);
                                }else {
                                    LiveMessage message=new LiveMessage();
                                    message.setPictureUrl("null");
                                    Log.i("tag", "getText------" + photoUrl);
                                    message.setIconUrl(photoUrl);
                                    message.setTimeUntilNow("刚刚");
                                    message.setCreateTime(System.currentTimeMillis());
                                    message.setContent(text);
                                    Log.i("tag", "收到消息！text=" + text);
                                    Message msgToHandler=new Message();
                                    msgToHandler.what=HANDLE_RECEIVE_TEXT_MESSAGE;
                                    msgToHandler.obj=message;
                                    handler.sendMessage(msgToHandler);
                                }
                            }
                        }
                    }else if (msg.getType().equals(EMMessage.Type.IMAGE)){
                        EMImageMessageBody body = (EMImageMessageBody)msg.getBody();
                        String photoUrl =msg.getStringAttribute("photoUrl", null);
                        LiveMessage message=new LiveMessage();
                        message.setIconUrl(photoUrl);
                        message.setTimeUntilNow("刚刚");
                        message.setPictureUrl(body.getRemoteUrl());
                        Log.i("tag", "收到消息！get-----" +body.getRemoteUrl());
                        message.setContent("");
                        message.setCreateTime(System.currentTimeMillis());
                        Message msg1=new Message();
                        msg1.what=HANDLE_OTHERUSER_RECEIVE_PICTURE_LIVE;
                        msg1.obj=message;
                        handler.sendMessage(msg1);
                              }
                };
            }
            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }
            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
                //收到已读回执
            }
            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                //收到已送达回执
            }
            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        EMClient.getInstance().groupManager().addGroupChangeListener(new EMGroupChangeListener() {
            @Override
            public void onInvitationReceived(String s, String s1, String s2, String s3) {
            }
            @Override
            public void onApplicationReceived(String s, String s1, String s2, String s3) {
            }
            @Override
            public void onApplicationAccept(String s, String s1, String s2) {
            }
            @Override
            public void onApplicationDeclined(String s, String s1, String s2, String s3) {
            }
            @Override
            public void onInvitationAccepted(String s, String s1, String s2) {
                handler.sendEmptyMessage(HANDLE_NEWUSER_IN);
            }
            @Override
            public void onInvitationDeclined(String s, String s1, String s2) {
            }
            @Override
            public void onUserRemoved(String s, String s1) {
            }
            @Override
            public void onGroupDestroyed(String s, String s1) {
            }
            @Override
            public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {
            }
        });
    }
    private void setBaseData() {
        intent=getIntent();
        int type=intent.getIntExtra("type", 0);
        user_now=type;
        liveId=getIntent().getIntExtra("liveId", 0);
       Thread getBaseInfoThread= new Thread(){
            @Override
            public void run() {
                try {
                    String uri = API.BASE_URL + "/v1/live/get";
                    HashMap<String, String> params = new HashMap<>();
                    params.put("id", liveId + "");
                    Log.i("tag", "liveID=" + liveId);
                    params.put("veiwUserId", userId + "");
                    Log.i("tag", "userId=" + userId);
                    String result = PostUtil.sendPostMessage(uri, params);
                    JSONObject object=new JSONObject(result);
                    JSONObject data=object.getJSONObject("data");
                    LIVE_STATE=data.getInt("state");
                    peopleNumber=data.getInt("watchNumber");
                    isCollected=data.getBoolean("isColl");
                    isPraised=data.getBoolean("isZan");
                    Message message=new Message();
                    message.what=HANDLE_START_PRAISE_STATUS;
                    message.obj=isPraised;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        getBaseInfoThread.setPriority(9);
        getBaseInfoThread.start();
        groupId = getIntent().getStringExtra("groupId");
        bigPictureUrl=intent.getStringExtra("bigPictureUrl");
        title=intent.getStringExtra("title");
        liveOwnerId=getIntent().getStringExtra("liveOwnerId");
        mainPhotoUrl=intent.getStringExtra("mainPhotoUrl");
    }
    private void getGroupID() {
        new Thread(){
            @Override
            public void run() {
                EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                option.maxUsers = 1000;
                option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                String []allMembers=new String[]{};
                EMGroup group = null;
                try {
                    group = EMClient.getInstance().groupManager().createGroup(liveOwnerId+"",title,allMembers, null, option);
                    if (group!=null){
                        Log.i("tag","创建环信群组成功");
                        groupId=group.getGroupId();
                        String url2=API.BASE_URL+"/v1/live/update_live";
                        HashMap<String,String> params2=new HashMap<>();
                        params2.put("token",tokenAll);
                        params2.put("id",liveOwnerId+"");
                        params2.put("groupId", groupId);
                        String result2= PostUtil.sendPostMessage(url2,params2);
                        Log.i("tag",result2);
                    }else {
                        Log.i("tag","创建环信群组失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void addListeners() {
        ivMoreToDo.setOnClickListener(this);
        personal_back.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        ivSelectPic.setOnClickListener(this);
        ivBarrage.setOnClickListener(this);
        ivShowOrHideBarrage.setOnClickListener(this);
        ivPrise.setOnClickListener(this);
        tvBarrageNumber.setOnClickListener(this);
        tvSendBarrage.setOnClickListener(this);
    }
    private void checkUserType() {
        //判断当前app使用者是否是直播发起人
        tvOnlineNumber.setText(peopleNumber + "人在线");
        tvTabTitle.setText(title);
        Log.i("tag", "bigPictureUrl-------->" + bigPictureUrl);
        Picasso.with(this).load(bigPictureUrl).into(ivBigPicture);
        switch (user_now){
            case USER_MAIN:
                llMainUser.setVisibility(View.VISIBLE);
                llOtherUser.setVisibility(View.GONE);
                personal_back.setVisibility(View.GONE);
                ivMoreToDo.setImageResource(R.mipmap.icon_stop_playing);
                App.NOWUSER=USER_MAIN;
                break;
            case USER_OTHER:
                App.NOWUSER=USER_OTHER;
                llMainUser.setVisibility(View.GONE);
                llOtherUser.setVisibility(View.VISIBLE);
                ivMoreToDo.setImageResource(R.mipmap.nav_more);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                             EMClient.getInstance().groupManager().joinGroup(groupId);//
                            EMMessage message = EMMessage.createTxtSendMessage("A8F6C870C92E4D672212E58A089DEEC9", groupId);
                            // 增加自己特定的属性
                            message.setAttribute("photoUrl",avatar+"@"+nickName );//进入特殊指令
                            message.setFrom(userId);
                            message.setChatType(EMMessage.ChatType.GroupChat);
                            EMClient.getInstance().chatManager().sendMessage(message);
                            Log.i("tag","用户"+userId+"申请进入群聊!群聊ID="+groupId);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            Log.i("tag","进入群聊失败："+e.toString());
                        }
                    }
                }.start();
                peopleNumber+=1;
                tvOnlineNumber.setText(peopleNumber+"人在线");
                tvOnlineNumber.invalidate();
                break;
        }
    }
    private List<LiveMessage> getLiveMessages(){
        ArrayList<LiveMessage> messages =new ArrayList<>();
        String timeUtilNow="刚刚";
        String  systemText="亲爱的用户您好，欢迎进入体育家图文直播平台，为了营造一个健康舒适的环境，请您遵守以下几条守则，谢谢您的理解：\n" +
                "1、不能以任何形式形式播放和宣传带有色情、暴力血腥、消极反动以及有擦边球嫌疑的节目；\n" +
                "2、禁止播放版权类型的电影和电视剧；\n" +
                "3、禁止播放国家明令禁止的视听类节目；\n" +
                "4、禁止播放转播体育比赛有关的内容；\n" +
                "5、禁止宣传非本直播平台的链接；\n" +
                "6、禁止以直接直播或网页观看的形式播放非体育家拥有版权的游戏比赛；\n" +
                "7、禁止直播消极游戏，影响其他玩家游戏等不良行为；\n" +
                "8、禁止房间添加虚假的游戏标签，导致游戏分类不符合真实情况的行为；\n" +
                "违反的主播除了关闭直播间外，我们还将保留追究相应的法律责任的权利。" +
                "若您申请的直播间在48小时内没有被审核通过，请联系客服028-85198488。";
        long longTime= System.currentTimeMillis();
        String pictureUrl="null";
            LiveMessage liveMessage1=new LiveMessage();
            liveMessage1.setContent(systemText);
            liveMessage1.setCreateTime(longTime);
            liveMessage1.setPictureUrl(pictureUrl);
            liveMessage1.setIconUrl(StringUtil.isNullAvatar(""));
            liveMessage1.setTimeUntilNow(timeUtilNow);
        messages.add(liveMessage1);
        try{
        String getMsgUri= API.BASE_URL+"/v1/live/text/list";
        HashMap<String,String> params=new HashMap<>();
        params.put("liveId",liveId+"");
        String result= PostUtil.sendPostMessage(getMsgUri,params);
            JSONObject jsonObject=new JSONObject(result);
            JSONArray array=jsonObject.getJSONArray("data");
            for (int i=array.length()-1;i>=0;i--){
                JSONObject object=array.getJSONObject(i);
                long createTime=object.getLong("createTime");
                String content=object.getString("content");
                String imgUrl=object.getString("imgUrl");
                LiveMessage liveMessage=new LiveMessage();
                liveMessage.setContent(content);
                String timeUntil=checkTime(System.currentTimeMillis()-createTime);
                liveMessage.setTimeUntilNow(timeUntil);
                liveMessage.setCreateTime(createTime);
                liveMessage.setIconUrl(mainPhotoUrl);
                liveMessage.setPictureUrl(API.IMAGE_URL+imgUrl);
                messages.add(liveMessage);
            }
            String barrageUri=API.BASE_URL+"/v1/live/barrage/history";
            HashMap<String,String> paramsBarrage=new HashMap<>();
            paramsBarrage.put("liveId",liveId+"");
            Log.i("tag", "liveID---------->"+liveId);
            String resultBarrage=PostUtil.sendPostMessage(barrageUri,paramsBarrage);
            JSONObject object=new JSONObject(resultBarrage);
            String  barrageObj=object.getString("barrages");
            if (barrageObj!=null&&!barrageObj.equals("")&&!barrageObj.equals("null")){
                JSONArray barrageArray=object.getJSONArray("barrages");
                for (int i=barrageArray.length()-1;i>=0;i--){
                    JSONObject object1=barrageArray.getJSONObject(i);
                    BarrageViewEntity entity=new BarrageViewEntity();
                    entity.setPicUrl(StringUtil.isNullAvatar(object1.getString("avatar")));
                    entity.setContent(object1.getString("content"));
                    barrageList.add(entity);
                }
            }
            handler.sendEmptyMessage(HANDLE_BARRAGE_BASE_DATA);
        }catch (Exception e){
            e.printStackTrace();
        }
       return messages;
    }
    private void initData() {
        Thread threadGetMsg=new Thread(){
            @Override
            public void run() {
                ArrayList<LiveMessage> messages= (ArrayList<LiveMessage>) getLiveMessages();
                Message message=new Message();
                message.what=HANDLE_INIT_MESSAGE;
                message.obj=messages;
                handler.sendMessage(message);
            }
        };
        threadGetMsg.start();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivMoreToDo://返回
                toDoMore();
                break;
            case R.id.personal_back://返回
                finish();
                break;
            case R.id.ivSelectPic://播主发送图片
                selectPicture();
                break;
            case R.id.btnSend://播主发送文字信息
                sendTextMsg();
                break;
            case R.id.ivBarrage://播主显示弹幕或隐藏弹幕
               showOrHideBarrage();
                break;
            case R.id.ivShowOrHideBarrage://普通用户显示或隐藏弹幕
                showOrHideBarrage();
                break;
            case R.id.ivPrise://点赞次数加1
                if (LIVE_STATE==-1){
                    Toast.makeText(this, "直播已结束，亲！", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    priseLive();
                }
                break;
            case R.id.tvBarrageNumber:
                Intent intent=new Intent(this,BarrageActivity.class);
                intent.putExtra("tokenAll",tokenAll);
                intent.putExtra("liveId",liveId);
                intent.putExtra("userId", userId);
                intent.putExtra("nickName", nickName);
                intent.putExtra("avatar", avatar);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
                break;
            case R.id.tvSendBarrage:
                if (LIVE_STATE==-1){
                    Toast.makeText(this, "直播已结束，亲！", Toast.LENGTH_SHORT).show();
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(etMainUser.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return;
                }else {
                    if (etOtherUser.getText().toString().trim().equals("")) {
                        Toast.makeText(this, "不能发送空弹幕", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String content = etOtherUser.getText().toString().trim();
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                String uri = API.BASE_URL + "/v1/live/barrage/create";
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("token", tokenAll);
                                params.put("liveId", liveId + "");
                                params.put("userId", userId);
                                params.put("content", content);
                                PostUtil.sendPostMessage(uri, params);
                                EMMessage message = EMMessage.createTxtSendMessage(content, groupId);
                                // 增加自己特定的属性
                                message.setAttribute("photoUrl",avatar);
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
                                BarrageViewEntity entity = new BarrageViewEntity();
                                entity.setContent(content);
                                entity.setPicUrl(avatar);
                                Message message1 = new Message();
                                message1.obj = entity;
                                message1.what = HANDLE_RECEIVE_BARRAGE_MESSAGE;
                                handler.sendMessage(message1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(etOtherUser.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    etOtherUser.setText("");
                }
                break;
            case R.id.llShareToFriends:
                shareToFriends();
                break;
            case R.id.llShareToWeiChat:
                shareToWeiChat();
                break;
            case R.id.llShareToQQ:
                shareToQQ();
                break;
            case R.id.llShareToQQZone:
                shareToQQZone();
                break;
            case R.id.llShareToMicroBlog:
                shareToMicroBlog();
                break;
            case R.id.tvCollect:
                collect();
                break;
            case R.id.tvReport:
                report();
                break;
            case R.id.tvSpacial:
                spacial();
                break;
            case R.id.tvTop:
                toTop();
                break;
        }
    }
    private void priseLive() {
        new Thread(){
            @Override
            public void run() {
                try{
                    String uri= API.BASE_URL+"/v1/zan/add";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("token",tokenAll);
                    params.put("bodyId",liveId+"");
                    params.put("bodyType",4+"");
                    params.put("accountId",userId);
                    String result= PostUtil.sendPostMessage(uri,params);
                    JSONObject obj=new JSONObject(result);
                    int state=obj.getInt("state");
                    if (state==200){
                        if (isPraised){
                            handler.sendEmptyMessage(HANDLE_CANCEL_PRAISE);
                        }else {
                            handler.sendEmptyMessage(HANDLE_PRAISE);
                        }
                        isPraised=!isPraised;
                    }else {
                        handler.sendEmptyMessage(HANDLE_UNKNOWN_ERROR);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void shareToFriends() {
        Toast.makeText(this,"分享到朋友圈！",Toast.LENGTH_SHORT).show();
        shareNumber++;
    }
    private void shareToWeiChat() {
        Toast.makeText(this,"分享到微信！",Toast.LENGTH_SHORT).show();
        shareNumber++;
    }
    private void shareToQQ() {
        Toast.makeText(this,"分享到QQ！",Toast.LENGTH_SHORT).show();
        shareNumber++;
    }
    private void shareToQQZone() {
        Toast.makeText(this,"分享到QQ空间！",Toast.LENGTH_SHORT).show();
        shareNumber++;
    }
    private void shareToMicroBlog() {
        Toast.makeText(this,"分享到微博！",Toast.LENGTH_SHORT).show();
        shareNumber++;
    }
    private void collect() {
        Toast.makeText(this,"已收藏该直播！",Toast.LENGTH_SHORT).show();
    }
    private void report() {
        Toast.makeText(this,"已举报该直播！",Toast.LENGTH_SHORT).show();
    }
    private void spacial() {
        Toast.makeText(this,"已加精！",Toast.LENGTH_SHORT).show();
    }
    private void toTop() {
        Toast.makeText(this,"已置顶！",Toast.LENGTH_SHORT).show();
    }
    private void delete() {
        if (LIVE_STATE == -1) {
            finish();
        } else {
            new Thread() {
                @Override
                public void run() {
                    try {
                        String uri = API.BASE_URL + "/v1/live/end";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("token", tokenAll);
                        params.put("liveId", liveId + "");
                        String result = PostUtil.sendPostMessage(uri, params);
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject data = jsonObject.getJSONObject("data");
                        EndLiveEntity entity = new EndLiveEntity();
                        long endTime = data.getLong("liveTime");
                        String totalTime = "";
                        if (endTime <= 1000L * 60L * 60L) {
                            totalTime = (int) (endTime / 60000) + "分钟";
                        } else {
                            totalTime = (int) (endTime / 3600000) + "小时" + (endTime % 3600000) / 60000 + "分钟";
                        }
                        entity.setTotalTime(totalTime);
                        entity.setGroupId(groupId);
                        entity.setShareNumber(shareNumber);
                        entity.setBitmapSmallPhotoUrl(mainPhotoUrl);
                        entity.setNickName(nickName);
                        entity.setPeopleNumber(data.getInt("watchNumber"));
                        entity.setPriseNumber(data.getInt("zanCount"));
                        Message msgEnd = new Message();
                        msgEnd.obj = entity;
                        msgEnd.what = HANDLE_END_LIVE;
                        handler.sendMessage(msgEnd);
                        EMMessage message = EMMessage.createTxtSendMessage("1B643AEC5CD0034236DDE2E1465D366D", groupId);
                        // 增加自己特定的属性
                        message.setAttribute("photoUrl", "1B643AEC5CD0034236DDE2E1465D366D");//关闭特殊指令
                        message.setFrom(liveOwnerId);
                        message.setChatType(EMMessage.ChatType.GroupChat);
                        message.setMessageStatusCallback(new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                Log.i("tag", "关闭成功！");
                            }

                            @Override
                            public void onError(int i, String s) {
                                Log.i("tag", "关闭失败！原因是" + s);
                            }

                            @Override
                            public void onProgress(int i, String s) {
                                Log.i("tag", "关闭中！");
                            }
                        });
                        EMClient.getInstance().chatManager().sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            Toast.makeText(this, "已关闭直播！", Toast.LENGTH_SHORT).show();
        }
    }
    private void toDoMore() {
        if (user_now==USER_MAIN){
            delete();
        }else{
            View view = getLayoutInflater().inflate(R.layout.share_dialog, null);
             llShareToFriends= (LinearLayout) view.findViewById(R.id.llShareToFriends);
             llShareToWeiChat= (LinearLayout) view.findViewById(R.id.llShareToWeiChat);
             llShareToQQ= (LinearLayout) view.findViewById(R.id.llShareToQQ);
             llShareToQQZone= (LinearLayout) view.findViewById(R.id.llShareToQQZone);
             llShareToMicroBlog= (LinearLayout) view.findViewById(R.id.llShareToMicroBlog);
             tvCollect= (TextView) view.findViewById(R.id.tvCollect);
             tvReport= (TextView) view.findViewById(R.id.tvReport);
             tvDelete= (TextView) view.findViewById(R.id.tvDelete);
            tvDelete.setVisibility(View.INVISIBLE);
            ivClose= (ImageView) view.findViewById(R.id.ivClose);
            tvTop= (TextView) view.findViewById(R.id.tvTop);
            tvSpacial= (TextView) view.findViewById(R.id.tvSpacial);
            llShareToFriends.setOnClickListener(this);
            llShareToWeiChat.setOnClickListener(this);
            llShareToQQ.setOnClickListener(this);
            llShareToQQZone.setOnClickListener(this);
            llShareToMicroBlog.setOnClickListener(this);
            tvCollect.setOnClickListener(this);
            tvReport.setOnClickListener(this);
            tvSpacial.setOnClickListener(this);
            tvTop.setOnClickListener(this);
             final Dialog dialog = new Dialog(this,R.style.Dialog_Fullscreen);
            dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = dialog.getWindow();
            // 设置显示动画
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = getWindowManager().getDefaultDisplay().getHeight();
            // 以下这两句是为了保证按钮可以水平满屏
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            // 设置显示位置
            dialog.onWindowAttributesChanged(wl);
            // 设置点击外围解散
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }
    private void showOrHideBarrage() {
        if (!isShowing){
            Toast.makeText(this,"弹幕打开",Toast.LENGTH_SHORT).show();
            rvBarrage.setVisibility(View.VISIBLE);
            isShowing=true;
        }else {
            Toast.makeText(this,"弹幕关闭",Toast.LENGTH_SHORT).show();
            rvBarrage.setVisibility(View.GONE);
            isShowing=false;
        }
    }
    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1000);
    }
    private void sendTextMsg() {
        if (LIVE_STATE==-1){
            Toast.makeText(this, "直播已结束，亲！", Toast.LENGTH_SHORT).show();
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(etMainUser.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            return;
        }else {
            final String imgUrl = "";
            final String content = etMainUser.getText().toString().trim();
            if (content.equals("")) {
                Toast.makeText(this, "不能发送空消息！", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread() {
                @Override
                public void run() {
                    EMMessage message = EMMessage.createTxtSendMessage(content, groupId);
                    message.setChatType(EMMessage.ChatType.GroupChat);
                    message.setFrom(liveOwnerId);
                    message.setAttribute("photoUrl",mainPhotoUrl);
                    message.setMessageStatusCallback(new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            Log.i("tag", "消息发送成功");
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.i("tag", "消息发送失败" + s);
                        }

                        @Override
                        public void onProgress(int i, String s) {
                            Log.i("tag", "消息发送中");
                        }
                    });
                    EMClient.getInstance().chatManager().sendMessage(message);
                    LiveMessage message1 = new LiveMessage();
                    message1.setPictureUrl("");
                    message1.setIconUrl(mainPhotoUrl);
                    message1.setTimeUntilNow("刚刚");
                    message1.setCreateTime(System.currentTimeMillis());
                    message1.setContent(content);
                    Message msg = new Message();
                    msg.what = HANDLE_SEND_TEXT_MESSAGE;
                    msg.obj = message1;
                    handler.sendMessage(msg);
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(etMainUser.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    String result = LiveUtil.sendLiveMessage(url, tokenAll, liveId, imgUrl, content);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String state = jsonObject.getString("state");
                        LogUtil.i("tag", state + "直播文本信息上传结果");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LIVE_STATE==-1){
            Toast.makeText(this, "直播已结束，亲！", Toast.LENGTH_SHORT).show();
            return;
        }else {
            if (requestCode == 1000 && data != null) {
                // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
                try {
                    final Uri originalUri = data.getData(); // 获得图片的uri
                    new Thread() {
                        @Override
                        public void run() {
                            String path = UploadUtil.getAbsoluteImagePath(LiveActivity.this, originalUri);
                            imgUrl = API.IMAGE_URL + UploadUtil.getNetWorkImageAddress(path, LiveActivity.this);
                            String result=LiveUtil.sendLiveMessage(url, tokenAll, liveId, imgUrl, "");
                            try {
                                LogUtil.i("tag", path + "----直播图片信息上传结果:"+result);
                                EMMessage message = EMMessage.createImageSendMessage(path, false, groupId);
                                message.setChatType(EMMessage.ChatType.GroupChat);
                                message.setFrom(liveOwnerId);
                                message.setAttribute("photoUrl", mainPhotoUrl);
                                message.setMessageStatusCallback(new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        Log.i("tag", "消息发送成功");
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        Log.i("tag", "消息发送失败" + s);
                                    }

                                    @Override
                                    public void onProgress(int i, String s) {
                                        Log.i("tag", "消息发送中");
                                    }
                                });
                                EMClient.getInstance().chatManager().sendMessage(message);
                                LiveMessage message1 = new LiveMessage();
                                message1.setIconUrl(mainPhotoUrl);
                                message1.setTimeUntilNow("刚刚");
                                message1.setPictureUrl(imgUrl);
                                Log.i("tag", "send---------" + imgUrl);
                                message1.setContent("");
                                message1.setCreateTime(System.currentTimeMillis());
                                Message msg1 = new Message();
                                msg1.what = HANDLE_MAINUSER_SEND_PICTURE_LIVE;
                                msg1.obj = message1;
                                handler.sendMessage(msg1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDestroy();
        }
    @Override
    public void onBackPressed() {
        if (LIVE_STATE == -1) {
           super.onBackPressed();
        } else {
            if (user_now == USER_OTHER) {
                Toast.makeText(this, "您已离开直播室！", Toast.LENGTH_SHORT).show();
                new Thread() {
                    @Override
                    public void run() {
                        String uri = API.BASE_URL + "/v1/live/out ";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("token", tokenAll);
                        params.put("id", liveId + "");
                        String result = PostUtil.sendPostMessage(uri, params);
                        Log.i("tag", "end" + result);
                        EMMessage message = EMMessage.createTxtSendMessage("F00647B3C94654F3BDE6E3C5944ABADD", groupId);
                        // 增加自己特定的属性
                        message.setAttribute("photoUrl", avatar+"@"+nickName);//退出特殊指令
                        message.setFrom(userId);
                        message.setChatType(EMMessage.ChatType.GroupChat);
                        EMClient.getInstance().chatManager().sendMessage(message);
                    }
                }.start();
                try {
                    Thread.sleep(1000);
                    EMClient.getInstance().groupManager().leaveGroup(groupId);//groupId
                    super.onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Dialog dialog = new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("结束")
                        .setIcon(R.mipmap.wrong)
                        .setMessage("是否结束直播，请选择？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                delete();
                            }
                        }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        }
    }
}
