package com.gymnast.view.personal.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.App;
import com.gymnast.R;
import com.gymnast.data.hotinfo.CirleDevas;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.PostsData;
import com.gymnast.utils.CallBackUtil;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.hotinfoactivity.adapter.CallBackAdapter;
import com.gymnast.view.hotinfoactivity.entity.CallBackDetailEntity;
import com.gymnast.view.hotinfoactivity.entity.CallBackEntity;
import com.gymnast.view.personal.listener.MyTextWatcher;
import com.gymnast.view.personal.listener.WrapContentLinearLayoutManager;
import com.gymnast.view.user.LoginActivity;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cymbi on 2016/9/2.
 */
public class PersonalPostsDetailActivity extends ImmersiveActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{
    private SimpleDateFormat sdr;
    private ImageView circle_head,back,personal_menu,ivClose;
    private TextView tvNickName;
    private TextView tvTitle,tvTime,tvConcern,tvCollect,tvReport,tvDelete, tvSpacial,tvTop;
    private WebView webView;
    private PostsData mPostsData;
    private Dialog cameradialog;
    LinearLayout llShareToFriends,llShareToWeiChat,llShareToQQ,llShareToQQZone,llShareToMicroBlog;
    public static int shareNumber=0;//分享次数
    View view;
    TextView tvSendTieZi;
    EditText etCallBackTieZi;
    ImageView ivSendTiezi;
    RecyclerView rvCallBack;
    int notifyPos=0;
    SwipeRefreshLayout reflesh;
    String firstCommenter="";
    String token,userId,nickName,avatar;
    private int tieZiID,createId;
    List<CallBackEntity> commentList=new ArrayList<>();
    CallBackAdapter commentAdapter;
    private TextView tvAuthInfo;
    private int UserID;
    public static boolean isComment=true;
    public static final int HANDLE_WHOLE_DATA=3;
    public static final int HANDLE_COMMENT_DATA=5;
    public static final int HANDLE_MAIN_USER_BACK=6;
    public static final int HANDLE_PRAISE=7;
    public static final int HANDLE_CANCEL_PRAISE=8;
    public static final int HANDLE_UNKNOWN_ERROR=9;
    static ArrayList<CallBackDetailEntity> detailMSGs;
    ArrayList<String> userABC=new ArrayList<>();
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_WHOLE_DATA:
                    PicassoUtil.handlePic(PersonalPostsDetailActivity.this, PicUtil.getImageUrlDetail(PersonalPostsDetailActivity.this, StringUtil.isNullAvatar(circleAvatar), 320, 320),circle_head,320,320);
                    tvNickName.setText(circleNickName);
                    tvTime.setText(sdr.format(new Date(createTime)) + "");
                    tvTitle.setText(title);
                    WindowManager wm = (WindowManager) PersonalPostsDetailActivity.this.getSystemService(Context.WINDOW_SERVICE);
                    int width = wm.getDefaultDisplay().getWidth();
                    if(width > 520){
                        webView.setInitialScale(160);
                    }else if(width > 450){
                        webView.setInitialScale(140);
                    }else if(width > 300){
                        webView.setInitialScale(120);
                    }else{
                        webView.setInitialScale(100);
                    }
                    webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
                    webView.getSettings().setJavaScriptEnabled(true);
                    // 设置启动缓存 ;
                    webView.getSettings().setAppCacheEnabled(true);
                    webView.setWebChromeClient(new WebChromeClient());
                    webView.getSettings().setJavaScriptEnabled(true);
                    boolean isStartPraised= (boolean) msg.obj;
                    if (isStartPraised){
                        ivSendTiezi.setImageResource(R.mipmap.like_pressed);
                    }else {
                        ivSendTiezi.setImageResource(R.mipmap.like_normal);
                    }
                    ivSendTiezi.invalidate();
                    break;
                case HANDLE_UNKNOWN_ERROR:
                    Toast.makeText( PersonalPostsDetailActivity.this,"未知错误，请重试！",Toast.LENGTH_SHORT).show();
                    break;
                case HANDLE_CANCEL_PRAISE:
                    ivSendTiezi.setImageResource(R.mipmap.like_normal);
                    Toast.makeText( PersonalPostsDetailActivity.this,"已取消点赞！",Toast.LENGTH_SHORT).show();
                    break;
                case HANDLE_PRAISE:
                    ivSendTiezi.setImageResource(R.mipmap.like_pressed);
                    Toast.makeText( PersonalPostsDetailActivity.this,"已点赞！",Toast.LENGTH_SHORT).show();
                    break;
                case HANDLE_MAIN_USER_BACK:
                    commentAdapter.notifyItemChanged(notifyPos);
                    break;
                case HANDLE_COMMENT_DATA:
                    userABC = (ArrayList<String>) msg.obj;
                    commentAdapter = new CallBackAdapter(PersonalPostsDetailActivity.this, commentList);
                    WrapContentLinearLayoutManager manager = new WrapContentLinearLayoutManager(PersonalPostsDetailActivity.this, LinearLayoutManager.VERTICAL, false);
                    rvCallBack.setLayoutManager(manager);
                    rvCallBack.setAdapter(commentAdapter);
                    commentAdapter.setOnItemClickListener(new CallBackAdapter.OnItemClickListener() {
                        @Override
                        public void OnCallBackClick(View view, int position, ArrayList<CallBackDetailEntity> detailMSGs) {
                            isComment = false;
                            notifyPos = position;
                            PersonalPostsDetailActivity.detailMSGs = detailMSGs;
                            PersonalPostsDetailActivity.type=PersonalPostsDetailActivity.CALL_BACK_TYPE_ONE;
                            String backWho = commentList.get(position).getCallBackNickName();
                            firstCommenter = backWho;
                            etCallBackTieZi.requestFocus();
                            etCallBackTieZi.setText("");
                            etCallBackTieZi.setHint("回复" + backWho);
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (inputManager.isActive()) {
                                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                                PersonalPostsDetailActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                tvSendTieZi.setVisibility(View.VISIBLE);
                                ivSendTiezi.setVisibility(View.GONE);
                            }
                        }
                    });
                    break;
            }}};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_posts_details);
        getBaseInfo();
        setView();
        getData();
        getPageView();
        setCallBackView();
        initView();
        setListeners();
    }
    private void getPageView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri=API.BASE_URL+"/v1/pageViwes";
                HashMap<String,String>params=new HashMap<>();
                params.put("types",3+"");
                params.put("typeId",tieZiID+"");
                PostUtil.sendPostMessage(uri,params);
            }
        }).start();
    }
    private void getBaseInfo() {
        SharedPreferences share = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token", "");
        userId = share.getString("UserId","");//当前用户id
        nickName = share.getString("NickName", "");
        avatar = share.getString("Avatar", "");
        tieZiID= getIntent().getIntExtra("TieZiID", 0);
        UserID= getIntent().getIntExtra("UserID", 0);
        if (userId.equals("")||userId==null){
            isLogin=false;
        }else {
            isLogin=true;
        }
    }
    private void autoRefresh(){
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(etCallBackTieZi.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        RefreshUtil.refresh(reflesh, this);
        setBaseState();
        setCallBackView();
        rvCallBack.scrollToPosition(0);
        ivSendTiezi.setVisibility(View.VISIBLE);
        tvSendTieZi .setVisibility(View.GONE);
        etCallBackTieZi.setFocusable(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                reflesh.setRefreshing(false);
            }
        }, 500);
    }
    private void setListeners() {
        llShareToFriends.setOnClickListener(this);
        llShareToWeiChat.setOnClickListener(this);
        llShareToQQ.setOnClickListener(this);
        llShareToQQZone.setOnClickListener(this);
        llShareToMicroBlog.setOnClickListener(this);
        tvCollect.setOnClickListener(this);
        tvReport.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvSpacial.setOnClickListener(this);
        tvTop.setOnClickListener(this);
        ivClose.setOnClickListener(this);
    }
    private void initView() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        personal_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });
    }
    boolean isParised=false;//是否点赞
    boolean isCollected=false;//是否收藏
    String title="";//帖子的标题
    long createTime=0L;//帖子的创建时间
    String circleNickName="";//帖主的昵称
    String circleAvatar="";//帖主的头像地址
    String content="";//帖字的内容
    ArrayList<String> adminIDs=new ArrayList<>();//管理员ID集合
    int createCircleID=0;//创建者ID
    String groupId="";
    int state=0;//直播的状态
    private void getData() {
        sdr = new SimpleDateFormat("yyyy-MM-dd");
        new Thread(){
            @Override
            public void run() {
                try{
                    String uri=API.BASE_URL+"/v1/circleItem/getOne";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("circleItemId",tieZiID+"");
                    if (!userId.equals("")&&userId!=null){
                        params.put("accountId",userId);
                    }
                    String result= GetUtil.sendGetMessage(uri,params);
                    JSONObject object=new JSONObject(result);
                    JSONObject data=object.getJSONObject("data");
                    isParised=data.getBoolean("isZan");
                    isCollected=data.getBoolean("isCollection");
                    String circleItem=data.getString("circleItem");
                    if (circleItem!=null&&!circleItem.equals("")){
                        JSONObject item=new JSONObject(circleItem);
                        title=item.getString("title");
                        createId=item.getInt("createId");
                        createTime=item.getLong("createTime");
                        content=item.getString("content");
                        String userVo=item.getString("userVo");
                        if (userVo!=null&&!userVo.equals("")){
                            JSONObject userData=new JSONObject(userVo);
                            circleNickName=userData.getString("nickName");
                            circleAvatar=userData.getString("avatar");
                        }
                        String circleVo=item.getString("circleVo");
                        if (circleVo!=null&&!circleVo.equals("")){
                            JSONObject circleData=new JSONObject(circleVo);
                            String adminIds=circleData.getString("adminIds");
                            groupId=circleData.getString("groupId")==null?"":(circleData.getLong("groupId")+"");
                            state=circleData.getInt("state");
                            createCircleID=circleData.getString("circleMasterId")==null||circleData.getString("circleMasterId").equals("null")||circleData.getString("circleMasterId").equals("")?0:(Integer.parseInt(circleData.getString("circleMasterId")));
                            if (adminIds!=null&&!adminIds.equals("")){
                                String [] IDS=adminIds.split(",");
                                for (int i=0;i<IDS.length;i++){
                                    adminIDs.add(IDS[i]);
                                }
                            }
                        }
                    }
                    Message message=new Message();
                    message.what=HANDLE_WHOLE_DATA;
                    message.obj=isParised;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void setView() {
        reflesh = (SwipeRefreshLayout) findViewById(R.id.srlReflesh);
        RefreshUtil.refresh(reflesh, this);
        etCallBackTieZi= (EditText) findViewById(R.id.etCallBackTieZi);
        etCallBackTieZi.setHint("说点什么吧。。。");
        etCallBackTieZi.setHintTextColor(Color.parseColor("#999999"));
        ivSendTiezi= (ImageView) findViewById(R.id.ivSendTiezi);
        rvCallBack= (RecyclerView) findViewById(R.id.rvCallBack);
        tvSendTieZi=(TextView) findViewById(R.id.tvSendTieZi);
        back= (ImageView) findViewById(R.id.personal_back);
        circle_head=(ImageView) findViewById(R.id.circle_head);
        personal_menu=(ImageView) findViewById(R.id.personal_menu);
        tvNickName=(TextView) findViewById(R.id.nickname);
        tvConcern=(TextView) findViewById(R.id.attention);
        tvAuthInfo=(TextView) findViewById(R.id.authInfo);
        tvTime=(TextView) findViewById(R.id.time);
        tvTitle = (TextView) findViewById(R.id.Title);
        webView=(WebView)findViewById(R.id.webview);
        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        view = getLayoutInflater().inflate(R.layout.share_dialog, null);
        llShareToFriends= (LinearLayout) view.findViewById(R.id.llShareToFriends);
        llShareToWeiChat= (LinearLayout) view.findViewById(R.id.llShareToWeiChat);
        llShareToQQ= (LinearLayout) view.findViewById(R.id.llShareToQQ);
        llShareToQQZone= (LinearLayout) view.findViewById(R.id.llShareToQQZone);
        llShareToMicroBlog= (LinearLayout) view.findViewById(R.id.llShareToMicroBlog);
        tvCollect= (TextView) view.findViewById(R.id.tvCollect);
        tvReport= (TextView) view.findViewById(R.id.tvReport);
        tvDelete= (TextView) view.findViewById(R.id.tvDelete);
        tvTop= (TextView) view.findViewById(R.id.tvTop);
        tvSpacial= (TextView) view.findViewById(R.id.tvSpacial);
        ivClose= (ImageView) view.findViewById(R.id.ivClose);
        tvSendTieZi.setOnClickListener(this);
        ivSendTiezi.setOnClickListener(this);
        circle_head.setOnClickListener(this);
        reflesh.setOnRefreshListener(this);
        etCallBackTieZi.addTextChangedListener(new MyTextWatcher(tvSendTieZi, ivSendTiezi, etCallBackTieZi));
    }
    private void shareToFriends() {
        Toast.makeText(this, "分享到朋友圈！", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this,"已收藏！",Toast.LENGTH_SHORT).show();
    }
    private void report() {
        Toast.makeText(this,"已举报！",Toast.LENGTH_SHORT).show();
    }
    private void delete() {
        Toast.makeText(this,"已删除！",Toast.LENGTH_SHORT).show();
    }
    private void spacial() {
        Toast.makeText(this,"已加精！",Toast.LENGTH_SHORT).show();
    }
    private void toTop() {
        Toast.makeText(this,"已置顶！",Toast.LENGTH_SHORT).show();
    }
    private void showdialog() {
        cameradialog = new Dialog(this,R.style.Dialog_Fullscreen);
        cameradialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = cameradialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        cameradialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        cameradialog.setCanceledOnTouchOutside(true);
       cameradialog.show();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.circle_head:
                    Intent i=new Intent(PersonalPostsDetailActivity.this,PersonalOtherHomeActivity.class);
                    i.putExtra("UserID",createId);
                    startActivity(i);
                break;
            case R.id.tvSendTieZi:
                handleSendMSG();
                break;
            case R.id.ivSendTiezi:
                if (!isLogin){
                    Toast.makeText(this,"您还没有登录呢，亲！",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this,LoginActivity.class));
                    finish();
                }else {
                    priseActive();
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
            case R.id.tvDelete:
                delete();
                break;
            case R.id.tvSpacial:
                spacial();
                break;
            case R.id.tvTop:
                toTop();
                break;
            case R.id.ivClose:
                cameradialog.dismiss();
                break;
        }
    }
    private void priseActive() {
        new Thread(){
            @Override
            public void run() {
                try{
                    String uri= API.BASE_URL+"/v1/zan/add";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("token",token);
                    params.put("bodyId",tieZiID+"");
                    params.put("bodyType",5+"");
                    params.put("accountId",userId);
                    String result= PostUtil.sendPostMessage(uri,params);
                    JSONObject obj=new JSONObject(result);
                    int state=obj.getInt("state");
                    if (state==200){
                       if (isParised){
                           handler.sendEmptyMessage(HANDLE_CANCEL_PRAISE);
                       }else {
                           handler.sendEmptyMessage(HANDLE_PRAISE);
                       }
                        isParised=!isParised;
                    }else {
                        handler.sendEmptyMessage(HANDLE_UNKNOWN_ERROR);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void setCallBackView() {
        CallBackUtil.getCallBackData(1, tieZiID, commentList, handler, HANDLE_COMMENT_DATA);
        reflesh.setRefreshing(false);
    }
    String toNickName;
    public static final int CALL_BACK_TYPE_ONE=1111;
    public static final int CALL_BACK_TYPE_TWO=2222;
    public static int type;
    private boolean isLogin=true;
    private void handleSendMSG() {
        toNickName=etCallBackTieZi.getHint().toString().trim().substring(2);
        if(!App.isStateOK|token.equals("")){//未登录
            Toast.makeText(PersonalPostsDetailActivity.this,"您还没有登录，请登陆！",Toast.LENGTH_SHORT).show();
            PersonalPostsDetailActivity.this.startActivity(new Intent(PersonalPostsDetailActivity.this,LoginActivity.class));
            return;
        }else {//已登录
            String comment = etCallBackTieZi.getText().toString().trim();
            if (isComment) {//评论
                if (comment.equals("")) {//非空判断
                    Toast.makeText(PersonalPostsDetailActivity.this, "回复内容为空！", Toast.LENGTH_SHORT).show();
                    setBaseState();
                    return;
                } else {//评论帖子
                    etCallBackTieZi.setText("");
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(etCallBackTieZi.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    CallBackUtil.handleCallBackMSG(1, tieZiID, Integer.valueOf(userId), comment);
                    CallBackEntity entity = new CallBackEntity();
                    entity.setCallBackImgUrl(StringUtil.isNullAvatar(avatar));
                    entity.setCallBackNickName(nickName);
                    entity.setCallBackTime(System.currentTimeMillis());
                    entity.setCallBackText(comment);
                    entity.setEntities(new ArrayList<CallBackDetailEntity>());
                    commentList.add(entity);
                    commentAdapter.notifyItemChanged(commentList.size() - 1);
                    rvCallBack.scrollToPosition(0);
                    autoRefresh();
                }
            } else {//回复ABCD
                if (comment.equals("")) {//非空判断
                    Toast.makeText(PersonalPostsDetailActivity.this, "回复内容为空！", Toast.LENGTH_SHORT).show();//非空判断
                    isComment = true;
                    setBaseState();
                    return;
                }else {
                        if (toNickName.equals(nickName)) {//回复自己
                            Toast.makeText(PersonalPostsDetailActivity.this, "不能回复自己！", Toast.LENGTH_SHORT).show();
                            isComment = true;
                            setBaseState();
                            return;
                        } else {//回复他人
                            if (type==CALL_BACK_TYPE_TWO){//2号类型回复
                                int callBackPOS=CallBackAdapter.callBackToPOS;
                                int commentID = commentList.get(callBackPOS).getCommentID();
                                int toID = CallBackAdapter.callBackToID;
                                ArrayList<CallBackDetailEntity> detailMSGList = CallBackAdapter.MSGS;
                                CallBackUtil.handleBack(token, commentID, Integer.valueOf(userId), toID, comment, detailMSGList, handler, HANDLE_MAIN_USER_BACK, nickName, toNickName);
                                isComment = true;
                                autoRefresh();
                            }else {//1号类型回复
                                int commentID = commentList.get(notifyPos).getCommentID();
                                etCallBackTieZi.setText("");
                                CallBackUtil.handleBack(token, commentID, Integer.valueOf(userId), -1, comment, detailMSGs, handler, HANDLE_MAIN_USER_BACK, nickName, "");
                                isComment = true;
                                autoRefresh();
                            }
                    }
                }
            }
        }
    }
    private void  setBaseState(){
        etCallBackTieZi.setHint("说点什么吧。。。");
        etCallBackTieZi.setText("");
        etCallBackTieZi.setTextColor(Color.parseColor("#999999"));
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        etCallBackTieZi.setHint("说点什么吧。。。");
        etCallBackTieZi.setTextColor(Color.parseColor("#999999"));
        etCallBackTieZi.setText("");
        getData();
        if (commentList.size()!=0){
            commentList.clear();
            setCallBackView();
        }
    }
    @Override
    public void onRefresh() {
        setCallBackView();
        getData();
        etCallBackTieZi.setHint("说点什么吧。。。");
        etCallBackTieZi.setTextColor(Color.parseColor("#999999"));
        etCallBackTieZi.setText("");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                reflesh.setRefreshing(false);
            }
        }, 1000);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isComment=true;
    }
}
