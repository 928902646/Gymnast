package com.gymnast.view.live.activity;

import android.app.Dialog;
import android.content.Context;
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
import com.gymnast.utils.LiveUtil;
import com.gymnast.utils.LogUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.utils.UploadUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.HomeActivity;
import com.gymnast.view.live.adapter.MessageAdapter;
import com.gymnast.view.live.customview.BarrageView;
import com.gymnast.view.live.entity.EndLiveEntity;
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
    public static ArrayList<BarrageView> barrageViewsAll=new ArrayList<>();//弹幕界面保存信息
    private TranslateAnimation translateAnimation;
    private Random random;
    private Intent intent;
    boolean isCollected=false;
    boolean isPraised=false;
    private int liveId=0;
    String bigPictureUrl;
    private Bitmap bitmapSmallPhoto=null;
    private Bitmap bitmapSmallUserPhoto=null;
    long startTime=0L;
    public static long liveTime=0L;//直播持续时间
    public static int peopleNumber=0;//观众人数
    public static int shareNumber=0;//分享次数
    public static int priseNumber=0;//被点赞次数
    private String tokenAll,liveOwnerId,userId,nickName,imgUrl,avatar,mainPhotoUrl,groupId="",title;
    String url= API.BASE_URL+"/v1/live/text/create";
    public static boolean isShowing=true;
    public static final int HANDLE_TIME_CHANGE=888;
    public static final int HANDLE_MAINUSER_SEND_PICTURE_LIVE=333;
    public static final int HANDLE_OTHERUSER_RECEIVE_PICTURE_LIVE=555;
    public static final int HANDLE_SEND_TEXT_MESSAGE=999;
    public static final int HANDLE_RECEIVE_TEXT_MESSAGE=222;
    public static final int HANDLE_RECEIVE_BARRAGE_MESSAGE=111;
    public static final int HANDLE_INIT_MESSAGE=444;
    public static final int HANDLE_END_LIVE=777;
    public static final int HANDLE_NUMBER_CHANGE=666;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what){
                case HANDLE_NUMBER_CHANGE:
                    Toast.makeText(LiveActivity.this,"用户"+nickName+"进来了",Toast.LENGTH_SHORT).show();
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
                    BarrageMSG barrageMSG= (BarrageMSG) msg.obj;
                    BarrageView barrageView1=new BarrageView(LiveActivity.this,null,barrageMSG.getPhoto(),barrageMSG.getContent(),nickName);
                    barrageView1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    barrageViewsAll.add(barrageView1);
                    flMain.addView(barrageView1);
                    if (isShowing) {
                        showBarrage();
                        barrageViewsAll.clear();
                    }
                    flMain.invalidate();
                    break;
                case HANDLE_END_LIVE:
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
        }
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
                        if (!msg.getFrom().equals(liveOwnerId)){
                            if (isShowing) {
                                String content = msg.getBody().toString();
                                content = content.substring(5, content.length() - 1);
                                String photoUrl =msg.getStringAttribute("photoUrl", null);
                                Log.i("tag","getBarrage-----"+photoUrl);
                                Bitmap photo = PicUtil.getImageBitmap(PicUtil.getImageUrlDetail(LiveActivity.this, photoUrl, 720, 1080));
                                Log.i("tag", "收到弹幕消息:" + content);
                                BarrageMSG barrageMSG = new BarrageMSG();
                                barrageMSG.setContent(content);
                                barrageMSG.setPhoto(photo);
                                Message msgToHandler = new Message();
                                msgToHandler.what = HANDLE_RECEIVE_BARRAGE_MESSAGE;
                                msgToHandler.obj = barrageMSG;
                                handler.sendMessage(msgToHandler);
                            }
                        }else {
                            String text=msg.getBody().toString();
                            text=text.substring(5,text.length()-1);
                            String stopCommand =msg.getStringAttribute("1B643AEC5CD0034236DDE2E1465D366D", null);
                            if(stopCommand.equals("1B643AEC5CD0034236DDE2E1465D366D")){
                                Log.i("tag","收到结束指令");
                                handler.sendEmptyMessage(HANDLE_END_LIVE);
                            }else {
                                LiveMessage message=new LiveMessage();
                                message.setPictureUrl("");
                                Log.i("tag","getText------"+avatar);
                                message.setIconUrl(mainPhotoUrl);
                                message.setTimeUntilNow("刚刚");
                                message.setCreateTime(System.currentTimeMillis());
                                message.setContent(text);
                                Log.i("tag", "收到消息！text=" + text);
                                messageList.add(message);
                                handler.sendEmptyMessage(HANDLE_RECEIVE_TEXT_MESSAGE);
                            }
                        }
                    }else if (msg.getType().equals(EMMessage.Type.IMAGE)){
                        EMImageMessageBody body = (EMImageMessageBody)msg.getBody();
                        LiveMessage message=new LiveMessage();
                        message.setIconUrl(mainPhotoUrl);
                        message.setTimeUntilNow("刚刚");
                        message.setPictureUrl(body.getRemoteUrl());
                        Log.i("tag", "收到消息！from-----" + body.getRemoteUrl());
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
                handler.sendEmptyMessage(HANDLE_NUMBER_CHANGE);
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
                    Log.i("tag","liveID="+liveId);
                    params.put("veiwUserId", userId + "");
                    String result = PostUtil.sendPostMessage(uri, params);
                    JSONObject object=new JSONObject(result);
                    JSONObject data=object.getJSONObject("data");
                    peopleNumber=data.getInt("watchNumber");
                    isCollected=data.getBoolean("isColl");
                    isPraised=data.getBoolean("isZan");
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
    private static class BarrageMSG implements Serializable{
        String content;
        Bitmap photo;
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
        public Bitmap getPhoto() {
            return photo;
        }
        public void setPhoto(Bitmap photo) {
            this.photo = photo;
        }
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
        new Thread(){
            @Override
            public void run() {
                bitmapSmallPhoto= PicUtil.getImageBitmap(mainPhotoUrl);
                bitmapSmallUserPhoto= PicUtil.getImageBitmap(PicUtil.getImageUrlDetail(LiveActivity.this,avatar,720,1080));
            }
        }.start();
        tvOnlineNumber.setText(peopleNumber + "人在线");
        tvTabTitle.setText(title);
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
                             EMClient.getInstance().groupManager().joinGroup(groupId);
                            peopleNumber+=1;
                            handler.sendEmptyMessage(HANDLE_NUMBER_CHANGE);
                            Log.i("tag","用户"+userId+"申请进入群聊!群聊ID="+groupId);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            Log.i("tag","进入群聊失败："+e.toString());
                        }
                    }
                }.start();
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
        for (int i=0;i<array.length();i++){
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
            liveMessage.setPictureUrl(imgUrl);
            messages.add(liveMessage);
        }
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
                priseNumber+=1;
                Toast.makeText(this,"已为直播积累一点人气！",Toast.LENGTH_SHORT).show();
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
                Bitmap photo1=bitmapSmallUserPhoto;
                if (etOtherUser.getText().toString().equals("")){
                    Toast.makeText(this,"不能发送空弹幕",Toast.LENGTH_SHORT).show();
                    return;
                }
                final String content=etOtherUser.getText().toString();
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            String uri=API.BASE_URL+"/v1/live/barrage/create";
                            HashMap<String,String> params=new HashMap<String, String>();
                            params.put("token",tokenAll);
                            params.put("liveId",liveId+"");
                            params.put("userId",userId);
                            params.put("content", content);
                            PostUtil.sendPostMessage(uri, params);
                            EMMessage message = EMMessage.createTxtSendMessage(content, groupId);
                            // 增加自己特定的属性
                            message.setAttribute("photoUrl", avatar);
                            message.setFrom(userId);
                            message.setChatType(EMMessage.ChatType.GroupChat);
                            message.setMessageStatusCallback(new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    Log.i("tag","弹幕消息发送成功！");
                                }
                                @Override
                                public void onError(int i, String s) {
                                    Log.i("tag","弹幕消息发送失败！");
                                }
                                @Override
                                public void onProgress(int i, String s) {
                                    Log.i("tag","弹幕消息发送中！");
                                }
                            });
                            EMClient.getInstance().chatManager().sendMessage(message);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();
                BarrageView barrageView1=new BarrageView(this,null,photo1,content,nickName);
                barrageView1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                barrageViewsAll.add(barrageView1);
                flMain.addView(barrageView1);
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(etOtherUser.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                showBarrage();
                etOtherUser.setText("");
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
        new Thread(){
            @Override
            public void run() {
                try{
                    String uri=API.BASE_URL+"/v1/live/end";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("token",tokenAll);
                    params.put("liveId", liveId + "");
                    String result=PostUtil.sendPostMessage(uri,params);
                    JSONObject jsonObject=new JSONObject(result);
                    JSONObject data=jsonObject.getJSONObject("data");
                    EndLiveEntity entity=new EndLiveEntity();
                    long endTime=data.getLong("liveTime");
                    String totalTime="";
                    if (endTime<=1000L*60L*60L){
                        totalTime= (int) (endTime/60000)+"分钟";
                    }else {
                        totalTime= (int) (endTime/3600000)+"小时"+(endTime%3600000)/60000+"分钟";
                    }
                    entity.setTotalTime(totalTime);
                    entity.setGroupId(groupId);
                    entity.setShareNumber(shareNumber);
                    entity.setBitmapSmallPhotoUrl(mainPhotoUrl);
                    entity.setNickName(nickName);
                    entity.setPeopleNumber(data.getInt("watchNumber"));
                    entity.setPriseNumber(data.getInt("zanCount"));
                    Message msgEnd=new Message();
                    msgEnd.obj=entity;
                    msgEnd.what=HANDLE_END_LIVE;
                    handler.sendMessage(msgEnd);
                    EMMessage message = EMMessage.createTxtSendMessage("1B643AEC5CD0034236DDE2E1465D366D", groupId);
                    // 增加自己特定的属性
                    message.setAttribute("1B643AEC5CD0034236DDE2E1465D366D", "1B643AEC5CD0034236DDE2E1465D366D");
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
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        Toast.makeText(this,"已关闭直播！",Toast.LENGTH_SHORT).show();
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
            showBarrage();
            Toast.makeText(this,"弹幕打开",Toast.LENGTH_SHORT).show();
            isShowing=true;
        }else {
            if (barrageViewsAll.size()!=0) {
                for (int i = 0; i < barrageViewsAll.size(); i++) {
                    barrageViewsAll.get(i).getAnimation().cancel();
                }
                barrageViewsAll.clear();
            }
            Toast.makeText(this,"弹幕关闭",Toast.LENGTH_SHORT).show();
            isShowing=false;
        }
    }
    public void showBarrage() {
        if (isShowing){
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int x = rect.width();
            int y = rect.height();
            float windowX=x*1.0f;
            float windowY=y*1.0f;
            random=new Random();
        for(int i=0;i<barrageViewsAll.size();i++){
            BarrageView barrageView=barrageViewsAll.get(i);
            int randomFromY=random.nextInt(31)+30;
            float fromY=randomFromY*windowY/100.0f;
            float toY=randomFromY*windowY/100.0f;
            translateAnimation = new TranslateAnimation(windowX,0-6000,fromY,toY);//FromX,ToX,FromY,ToY
            int speed=(random.nextInt(3)+5);
            translateAnimation.setDuration(2000 * speed);
            translateAnimation.setFillAfter(true);
            barrageView.startAnimation(translateAnimation);
            barrageView.invalidate();
            flMain.invalidate();
            if (barrageView.getAnimation().hasEnded()) {
                flMain.removeView(barrageView);//.hasEnded()
                barrageViewsAll.remove(barrageView);
            }
            barrageViewsAll.clear();
        }
        }
    }
    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1000);
    }
    private void sendTextMsg() {
        final String imgUrl = "";
        final String content = etMainUser.getText().toString();
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
                LiveMessage message1=new LiveMessage();
                message1.setPictureUrl("");
                message1.setIconUrl(mainPhotoUrl);
                message1.setTimeUntilNow("刚刚");
                message1.setCreateTime(System.currentTimeMillis());
                message1.setContent(content);
                Message msg=new Message();
                msg.what=HANDLE_SEND_TEXT_MESSAGE;
                msg.obj=message1;
                handler.sendMessage(msg);
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(etMainUser.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String result= LiveUtil.sendLiveMessage(url, tokenAll, liveId, imgUrl, content);
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    String state=jsonObject.getString("state");
                    LogUtil.i("tag",state+"直播文本信息上传结果");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000  &&  data != null){
            // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
            try {
                final Uri originalUri = data.getData(); // 获得图片的uri
                new Thread(){
                    @Override
                    public void run() {
                        String path = UploadUtil.getAbsoluteImagePath(LiveActivity.this, originalUri);
                        imgUrl=API.IMAGE_URL+UploadUtil.getNetWorkImageAddress(path,LiveActivity.this);
                        String result=LiveUtil.sendLiveMessage(url, tokenAll, liveId, imgUrl, "");
                        try{
                            JSONObject jsonObject=new JSONObject(result);
                            String state=jsonObject.getString("state");
                            LogUtil.i("tag", state + "直播图片信息上传结果");
                            EMMessage message = EMMessage.createImageSendMessage(path, false, groupId);
                            message.setChatType(EMMessage.ChatType.GroupChat);
                            message.setFrom(liveOwnerId);
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
                            LiveMessage message1=new LiveMessage();
                            message1.setIconUrl(mainPhotoUrl);
                            message1.setTimeUntilNow("刚刚");
                            message1.setPictureUrl(PicUtil.getImageUrlDetail(LiveActivity.this, imgUrl, 328, 122));
                            message1.setContent("");
                            message1.setCreateTime(System.currentTimeMillis());
                            Message msg1=new Message();
                            msg1.what=HANDLE_MAINUSER_SEND_PICTURE_LIVE;
                            msg1.obj=message1;
                            handler.sendMessage(msg1);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onDestroy() {
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDestroy();
        new Thread(){
            @Override
            public void run() {
                String uri=API.BASE_URL+"/v1/live/out ";
                HashMap<String,String> params=new HashMap<String, String>();
                params.put("token",tokenAll);
                params.put("id",liveId+"");
                String result=PostUtil.sendPostMessage(uri,params);
                Log.i("tag","end"+result);
            }
        }.start();
        try {
            EMClient.getInstance().groupManager().leaveGroup(groupId);//groupId
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }
}
