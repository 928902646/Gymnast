package com.gymnast.view.hotinfoactivity.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.App;
import com.gymnast.R;
import com.gymnast.data.hotinfo.RecentActivityDetail;
import com.gymnast.data.hotinfo.UserDevas;
import com.gymnast.data.net.API;
import com.gymnast.utils.CallBackUtil;
import com.gymnast.utils.CollectUtil;
import com.gymnast.utils.DataCleanManagerUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.utils.TimeUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.adapter.HotInfoActivityUserRecyclerViewAdapter;
import com.gymnast.view.hotinfoactivity.adapter.CallBackAdapter;
import com.gymnast.view.hotinfoactivity.entity.CallBackDetailEntity;
import com.gymnast.view.hotinfoactivity.entity.CallBackEntity;
import com.gymnast.view.personal.listener.WrapContentLinearLayoutManager;
import com.gymnast.view.user.LoginActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cymbi on 2016/8/22.
 */
public class ActivityDetailsActivity extends ImmersiveActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{
    private TextView tvDetailTitle,tvDetailMoney,tvDetailPeopleNumber,tvDetailSignUp,tvDetailAddress,tvDetailTime,tvDetailPhone,tvDetailCountDown,tvCollect,tvReport,tvDelete, tvSpacial,tvTop;
    private ImageView ivDetailImage,ivClose,ivBack,ivMoreChoice;
    private View view;
    SwipeRefreshLayout reflesh;
    private RecyclerView rvCallBack;
    private RecyclerView rvActiveUserList;
    private EditText etCallBack;
    private TextView tvCallBackSend;
    RelativeLayout rlAll;
    LinearLayout llShareToFriends,llShareToWeiChat,llShareToQQ,llShareToQQZone,llShareToMicroBlog;
    private WebView tvDescContent;
    public static int shareNumber=0;//分享次数
    private long startTime,endTime,lastTime;
    private String phone,descContent,title,imageUrl,address,token,userId,nickName,phoneNew,maxPeople="0";
    private int price,surplusPeople,x,memberCount;
    private int Userid;
    private int UserId;
    int activeID;
    int notifyPos=0;
    String avatar;
    private SharedPreferences share;
    RecentActivityDetail recentActiveData;
    List<CallBackEntity> commentList=new ArrayList<>();
    List<UserDevas> userWantToGo=new ArrayList<>();
    CallBackAdapter commentAdapter;
    boolean isCollected;
    HotInfoActivityUserRecyclerViewAdapter userLikeAdapter;
    public static boolean isComment=true;
    public static final int HANDLE_BANNER_ACTIVE_DATA=1;
    public static final int HANDLE_NEW_ACTIVE_DATA=2;
    public static final int HANDLE_SEARCH_ACTIVE_DATA=3;
    public static final int HANDLE_HISTORY_DATA=4;
    public static final int HANDLE_COMMENT_DATA=5;
    public static final int HANDLE_MAIN_USER_BACK=6;
    public static final int HANDLE_USER_WANT_TO_GO=7;
    static ArrayList<CallBackDetailEntity> detailMSGs;
    ArrayList<String> userABC=new ArrayList<>();
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CollectUtil.TO_COLLECT:
                    Toast.makeText(ActivityDetailsActivity.this,"已收藏！",Toast.LENGTH_SHORT).show();
                    tvCollect.setText("取消收藏");
                    isCollected=true;
                    break;
                case CollectUtil.CANCEL_COLLECT:
                    Toast.makeText(ActivityDetailsActivity.this,"已取消收藏！",Toast.LENGTH_SHORT).show();
                    tvCollect.setText("收藏");
                    isCollected=false;
                    break;
                case CollectUtil.ERROR:
                    Toast.makeText(ActivityDetailsActivity.this,"服务器异常！",Toast.LENGTH_SHORT).show();
                    break;
                case HANDLE_USER_WANT_TO_GO:
                    userLikeAdapter=new HotInfoActivityUserRecyclerViewAdapter(ActivityDetailsActivity.this,userWantToGo);
                    WrapContentLinearLayoutManager manager1=new WrapContentLinearLayoutManager(ActivityDetailsActivity.this,LinearLayoutManager.HORIZONTAL,false);
                    rvActiveUserList.setLayoutManager(manager1);
                    rvActiveUserList.setAdapter(userLikeAdapter);
                    break;
                case HANDLE_MAIN_USER_BACK:
                    commentAdapter.notifyItemChanged(notifyPos);
                    break;
                case HANDLE_COMMENT_DATA:
                    userABC= (ArrayList<String>) msg.obj;
                    commentAdapter=new CallBackAdapter(ActivityDetailsActivity.this,commentList);
                    WrapContentLinearLayoutManager manager=new WrapContentLinearLayoutManager(ActivityDetailsActivity.this,LinearLayoutManager.VERTICAL,false);
                    rvCallBack.setLayoutManager(manager);
                    rvCallBack.setAdapter(commentAdapter);
                    commentAdapter.setOnItemClickListener(new CallBackAdapter.OnItemClickListener() {
                        @Override
                        public void OnCallBackClick(View view,int position, ArrayList<CallBackDetailEntity> detailMSGs) {
                            isComment=false;
                            notifyPos=position;
                            ActivityDetailsActivity.detailMSGs=detailMSGs;
                            ActivityDetailsActivity.type=ActivityDetailsActivity.CALL_BACK_TYPE_ONE;
                            String backWho=commentList.get(position).getCallBackNickName();
                            firstCommenter=backWho;
                            etCallBack.requestFocus();
                            etCallBack.setText("");
                            etCallBack.setHint("回复" + backWho);
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (inputManager.isActive()) {
                                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                                ActivityDetailsActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                            }
                        }
                    });
                    break;
                case HANDLE_BANNER_ACTIVE_DATA:
                    handleData();
                    break;
                case HANDLE_NEW_ACTIVE_DATA:
                    handleData();
                    break;
                case HANDLE_SEARCH_ACTIVE_DATA:
                    handleData();
                    break;
                case HANDLE_HISTORY_DATA:
                    handleHistoryData();
                    break;
            }
        }
    };
    private void autoRefresh(){
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(etCallBack.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        RefreshUtil.refresh(reflesh, this);
        setBaseState();
        setCallBackView();
        commentAdapter.notifyDataSetChanged();
        rvCallBack.scrollToPosition(3);
        etCallBack.setFocusable(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                reflesh.setRefreshing(false);
            }
        }, 1000);
    }
    private void handleHistoryData() {
        ivMoreChoice.setVisibility(View.GONE);
        tvDetailTime.setText(TimeUtil.getDetailTime(startTime) + "~" + TimeUtil.getDetailTime(endTime));
        tvDetailTitle.setText(title + "");
        PicassoUtil.handlePic(ActivityDetailsActivity.this, PicUtil.getImageUrlDetail(ActivityDetailsActivity.this, StringUtil.isNullAvatar(imageUrl), x, 1920), ivDetailImage, x, 720);
        if(price==0){
            tvDetailMoney.setText("免费");
        }else {
            tvDetailMoney.setText("￥"+price);
        }
        tvDetailAddress.setText(address+"");
        tvDetailPhone.setText(phone + "");
        tvDetailPeopleNumber.setVisibility(View.INVISIBLE);
        tvDetailCountDown.setText("活动已结束");
        WindowManager wm = (WindowManager) ActivityDetailsActivity.this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        if(width > 520){
            this.tvDescContent.setInitialScale(160);
        }else if(width > 450){
            this.tvDescContent.setInitialScale(140);
        }else if(width > 300){
            this.tvDescContent.setInitialScale(120);
        }else{
            this.tvDescContent.setInitialScale(100);
        }
        tvDescContent.loadDataWithBaseURL(null, descContent, "text/html", "utf-8", null);
        tvDescContent.getSettings().setJavaScriptEnabled(true);
        // 设置启动缓存 ;
        tvDescContent.getSettings().setAppCacheEnabled(true);
        tvDescContent.setWebChromeClient(new WebChromeClient());
        tvDescContent.getSettings().setJavaScriptEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_activity_details);
        Rect rect = new Rect();
        ActivityDetailsActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        x = rect.width();
        getInfo();
        setView();
        setCallBackView();
        initData();
        try{ Userid=Integer.parseInt(userId);}catch (Exception e){e.printStackTrace();}
        getSignUpInfo();
        getCollectionMenber();
        getPageView();
    }
    private void getPageView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri=API.BASE_URL+"/v1/pageViwes";
                HashMap<String,String>params=new HashMap<>();
                params.put("types",2+"");
                params.put("typeId",activeID+"");
                PostUtil.sendPostMessage(uri,params);
            }
        }).start();
    }

    private void getCollectionMenber() {
        new Thread(){
            @Override
            public void run() {
                try{
                    String uri=API.BASE_URL+"/v1/collect/users ";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("model","1");
                    params.put("modelId",activeID+"");
                    String result=PostUtil.sendPostMessage(uri,params);
                    JSONObject object=new JSONObject(result);
                    if (object.getInt("state")!=200){
                        userWantToGo=new ArrayList<UserDevas>();
                        return;
                    }
                    JSONArray array=object.getJSONArray("data");
                    for (int i=0;i<array.length();i++){
                        JSONObject data=array.getJSONObject(i);
                        UserDevas userDevas=new UserDevas();
                        userDevas.id=data.getInt("id");
                        userDevas.nickname=data.getString("nickname");
                        userDevas.imageUrl=data.getString("avatar");
                        userDevas.bgmUrl=data.getString("avatar");
                        userWantToGo.add(userDevas);
                    }
                    handler.sendEmptyMessage(HANDLE_USER_WANT_TO_GO);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    String firstCommenter="";
    private void setCallBackView() {
        CallBackUtil.getCallBackData(2, activeID, commentList, handler, HANDLE_COMMENT_DATA);
        reflesh.setRefreshing(false);
    }
    private void getSignUpInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri=API.BASE_URL+"/v1/activity/memberPeople";
                HashMap<String,String> params=new HashMap<String, String>();
                params.put("token",token);
                params.put("activityId",activeID+"");
                params.put("userId",userId);
                String result=PostUtil.sendPostMessage(uri,params);
                try {
                    JSONObject object=new JSONObject(result);
                    if(object.getInt("state")==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvDetailSignUp.setText("已报名");
                                tvDetailSignUp.setBackgroundColor(getResources().getColor(R.color.background));
                                tvDetailSignUp.setClickable(false);
                            }
                        });
                    }else
                    if(object.getInt("state")==10002){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvDetailSignUp.setText("报名");
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void getInfo() {
        share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token", "");
        userId = share.getString("UserId","");//当前用户id
        nickName = share.getString("NickName", "");
        avatar = share.getString("Avatar", "");
        phoneNew = share.getString("Phone","");
        Intent data=getIntent();
        activeID=data.getIntExtra("ActiveID", 0);
        UserId=data.getIntExtra("UserId", 0);
        recentActiveData= (RecentActivityDetail) data.getSerializableExtra("RecentActivityDetail");
    }
    private void setView() {
        reflesh = (SwipeRefreshLayout) findViewById(R.id.srlReflesh);
        RefreshUtil.refresh(reflesh,this);
        etCallBack= (EditText) findViewById(R.id.etCallBack);
        etCallBack.setHint("说点什么吧。。。");
        etCallBack.setText("");
        etCallBack.setTextColor(Color.parseColor("#999999"));
        tvCallBackSend= (TextView) findViewById(R.id.tvCallBackSend);
        rvCallBack= (RecyclerView) findViewById(R.id.rvCallBack);
        rvActiveUserList= (RecyclerView) findViewById(R.id.rvActiveUserList);
        rlAll= (RelativeLayout) findViewById(R.id.rlAll);
        ivDetailImage=(ImageView)  findViewById(R.id.ivDetailImage);
        ivBack=(ImageView)  findViewById(R.id.ivDetailBack);
        tvDetailTitle=(TextView)  findViewById(R.id.tvDetailTitle);
        tvDetailCountDown=(TextView)  findViewById(R.id.tvDetailCountDown);
        tvDetailMoney=(TextView)  findViewById(R.id.tvDetailMoney);
        tvDetailPeopleNumber=(TextView)  findViewById(R.id.tvDetailPeopleNumber);
        tvDetailSignUp=(TextView)  findViewById(R.id.tvDetailSignUp);
        tvDetailAddress=(TextView)  findViewById(R.id.tvDetailAddress);
        tvDetailTime=(TextView)  findViewById(R.id.tvDetailTime);
        tvDetailPhone=(TextView)  findViewById(R.id.tvDetailPhone);
        tvDescContent=(WebView)  findViewById(R.id.tvDescContent);
        ivMoreChoice= (ImageView) findViewById(R.id.ivMoreChoice);
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
        tvTop.setVisibility(View.GONE);
        tvSpacial.setVisibility(View.GONE);
        tvDelete.setVisibility(View.GONE);
        ivMoreChoice.setOnClickListener(this);
        llShareToFriends.setOnClickListener(this);
        llShareToWeiChat.setOnClickListener(this);
        llShareToQQ.setOnClickListener(this);
        llShareToQQZone.setOnClickListener(this);
        llShareToMicroBlog.setOnClickListener(this);
        tvCollect.setOnClickListener(this);
        tvReport.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvTop.setOnClickListener(this);
        tvSpacial.setOnClickListener(this);
        tvDetailSignUp.setOnClickListener(this);
        tvDetailPeopleNumber.setOnClickListener(this);
        tvCallBackSend.setOnClickListener(this);
        reflesh.setOnRefreshListener(this);
        tvDetailPhone.setOnClickListener(this);
        final Dialog dialog= new Dialog(this,R.style.Dialog_Fullscreen);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
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
        ivMoreChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.VISIBLE);
                dialog.show();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initData() {
        if (recentActiveData==null&&activeID!=0){
            getMSGFromNetwork(activeID);
        }else {
            memberCount=recentActiveData.getLoverNum();
            maxPeople=recentActiveData.getMaxpeople();
            startTime=recentActiveData.getStartTime();
            endTime=recentActiveData.getEndTime();
            phone=recentActiveData.getPhone();
            descContent=recentActiveData.getDetail();
            title=recentActiveData.getTitle();
            activeID=recentActiveData.getId();
            price=recentActiveData.getPrice();
            imageUrl=recentActiveData.getPictureURL();
            address=recentActiveData.getAddress();
            handler.sendEmptyMessage(HANDLE_HISTORY_DATA);
        }
        reflesh.setRefreshing(false);
    }
    public void getMSGFromNetwork(final int ID){
        new Thread(){
            @Override
            public void run() {
                try {
                    String url= API.BASE_URL+"/v1/activity/query";
                    HashMap<String,String> params=new HashMap<>();
                    params.put("id",ID+"");
                    params.put("pageNumber",1+"");
                    params.put("createId",userId);
                    params.put("page", 1 + "");
                    String result= PostUtil.sendPostMessage(url,params);
                    JSONObject object=new JSONObject(result);
                    JSONArray array=object.getJSONArray("data");
                    JSONObject data=array.getJSONObject(0);
                    memberCount=data.getInt("memberCount");
                    isCollected=((data.getInt("isCollection"))==1)?true:false;
                    activeID=data.getInt("id");
                    maxPeople=data.getString("maxPeople");
                    startTime=data.getLong("startTime");
                    endTime=data.getLong("endTime");
                    lastTime=data.getLong("lastTime");
                    phone=data.getString("phone");
                    descContent=data.getString("descContent");
                    title=data.getString("title");
                    price=data.getInt("price");
                    imageUrl=data.getString("imgUrls");
                    address=data.getString("address");
                    handler.sendEmptyMessage(HANDLE_NEW_ACTIVE_DATA);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void handleData(){
        Long systemTime = new Date(System.currentTimeMillis()).getTime();//获取当前时间
        surplusPeople = Integer.valueOf(maxPeople)-memberCount; //剩余报名人数
        tvDetailTime.setText(TimeUtil.getDetailTime(startTime)+"~"+TimeUtil.getDetailTime(endTime));
        tvDetailTitle.setText(title+"");
        PicassoUtil.handlePic(ActivityDetailsActivity.this, PicUtil.getImageUrlDetail(ActivityDetailsActivity.this, StringUtil.isNullAvatar(imageUrl), x, 1920),ivDetailImage,x,720);
        if(price==0){
            tvDetailMoney.setText("免费");
        }else {
            tvDetailMoney.setText("￥"+price);
        }
        tvDetailAddress.setText(address+"");
        tvDetailPhone.setText(phone+"");
        if(maxPeople==null){
            tvDetailPeopleNumber.setText("已报名"+memberCount);
        }else {
            if(surplusPeople>100000){
                tvDetailPeopleNumber.setText("已报名"+memberCount+", "+"无限制");
            }else {
                tvDetailPeopleNumber.setText("已报名" + memberCount + ", " + "剩余" + surplusPeople + "个名额");
            }
        }
        if(systemTime>=startTime&&systemTime<=endTime){
            tvDetailCountDown.setText("活动已开始");
        }
        if(systemTime>=startTime&&systemTime>=endTime){
            tvDetailCountDown.setText("活动已结束");
            tvDetailSignUp.setBackgroundColor(getResources().getColor(R.color.background));
            tvDetailSignUp.setClickable(false);
        }
        if(systemTime<startTime){
            String disTime=TimeUtil.getDisTime(systemTime, startTime);
            tvDetailCountDown.setText("活动倒计时："+disTime);
            tvDetailSignUp.setText("报名");
        }
        WindowManager wm = (WindowManager) ActivityDetailsActivity.this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        if(width > 520){
            this.tvDescContent.setInitialScale(160);
        }else if(width > 450){
            this.tvDescContent.setInitialScale(140);
        }else if(width > 300){
            this.tvDescContent.setInitialScale(120);
        }else{
            this.tvDescContent.setInitialScale(100);
        }
        tvDescContent.loadDataWithBaseURL(null, descContent, "text/html", "utf-8", null);
        tvDescContent.getSettings().setJavaScriptEnabled(true);
        // 设置启动缓存 ;
        tvDescContent.getSettings().setAppCacheEnabled(true);
        tvDescContent.setWebChromeClient(new WebChromeClient());
        tvDescContent.getSettings().setJavaScriptEnabled(true);
        if (isCollected){
            tvCollect.setText("取消收藏");
        }else {
            tvCollect.setText("收藏");
        }
    }
    private void shareToFriends() {
        if (!checkLogIn()){return;};
        Toast.makeText(this, "分享到朋友圈！", Toast.LENGTH_SHORT).show();
        shareNumber++;
    }
    private void shareToWeiChat() {
        if (!checkLogIn()){return;};
        Toast.makeText(this,"分享到微信！",Toast.LENGTH_SHORT).show();
        shareNumber++;
    }
    private void shareToQQ() {
        if (!checkLogIn()){return;};
        Toast.makeText(this,"分享到QQ！",Toast.LENGTH_SHORT).show();
        shareNumber++;
    }
    private void shareToQQZone() {
        if (!checkLogIn()){return;};
        Toast.makeText(this,"分享到QQ空间！",Toast.LENGTH_SHORT).show();
        shareNumber++;
    }
    private void shareToMicroBlog() {
        if (!checkLogIn()){return;};
        Toast.makeText(this,"分享到微博！",Toast.LENGTH_SHORT).show();
        shareNumber++;
    }
    private void collect() {
        if (!checkLogIn()){return;};
        if (isCollected){
            CollectUtil.goToCancelCollect(handler, token, 1, activeID);
        }else {
            CollectUtil.goToCollect(handler,token,1,activeID);
        }
    }
    private void report() {
        if (!checkLogIn()){return;};
        Toast.makeText(this,"已举报！",Toast.LENGTH_SHORT).show();
    }
    private void delete() {
        if (!checkLogIn()){return;};
        Toast.makeText(this,"已删除！",Toast.LENGTH_SHORT).show();
    }
    private void spacial() {
        if (!checkLogIn()){return;};
        Toast.makeText(this,"已加精！",Toast.LENGTH_SHORT).show();
    }
    private void toTop() {
        if (!checkLogIn()){return;};
        Toast.makeText(this,"已置顶！",Toast.LENGTH_SHORT).show();
    }
    private boolean checkLogIn(){
        boolean isStateOk=true;
        if (!App.isNetStateOK){
            Toast.makeText(this,"亲，没网了呢！",Toast.LENGTH_SHORT).show();
            isStateOk=false;
        }else {
            if (token.equals("") || !App.isStateOK) {
                Toast.makeText(this, "您还没登陆呢，亲！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ActivityDetailsActivity.this, LoginActivity.class);
                ActivityDetailsActivity.this.startActivity(intent);
                isStateOk = false;
            }
        }
        return isStateOk;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvCallBackSend:
                handleSendMSG();
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
            case R.id.tvDetailPhone:
                final AlertDialog builder = new AlertDialog.Builder(ActivityDetailsActivity.this).create();
                builder.setView(getLayoutInflater().inflate(R.layout.activity_personal_dialog, null));
                builder.show();
                //去掉dialog四边的黑角
                builder.getWindow().setBackgroundDrawable(new BitmapDrawable());
                TextView text=(TextView) builder.getWindow().findViewById(R.id.text);
                text.setText("直接拨打"+tvDetailPhone.getText()+"?");
                builder.getWindow().findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
                TextView dialog_confirm=(TextView)builder.getWindow().findViewById(R.id.dialog_confirm);
                dialog_confirm.setText("拨打");
                dialog_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+tvDetailPhone.getText().toString().trim()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        builder.dismiss();
                    }
                });
                break;
            case R.id.tvDetailPeopleNumber:
                if (!checkLogIn()){return;};
                //首页banner还没有传参数过来
                if(UserId==Userid){
                    Intent i=new Intent(ActivityDetailsActivity.this,AuditActivity.class);
                    i.putExtra("activityId",activeID);
                    startActivity(i);
                }else {}
                break;
            case R.id.tvDetailSignUp:
                if (!checkLogIn()){return;};
                Intent i=new Intent(ActivityDetailsActivity.this,SignUpActivity.class);
                i.putExtra("activityId",activeID);
                i.putExtra("imageUrl",imageUrl);
                i.putExtra("title",title);
                i.putExtra("price",price);
                startActivityForResult(i,100);
                break;
        }
    }
    String toNickName;
    public static final int CALL_BACK_TYPE_ONE=1111;
    public static final int CALL_BACK_TYPE_TWO=2222;
    public static int type;
    private void handleSendMSG() {
        toNickName=etCallBack.getHint().toString().trim().substring(2);
        if(!App.isStateOK|token.equals("")){
            Toast.makeText(ActivityDetailsActivity.this,"您还没有登录，请登陆！",Toast.LENGTH_SHORT).show();
            ActivityDetailsActivity.this.startActivity(new Intent(ActivityDetailsActivity.this,LoginActivity.class));
            return;
        }else {
            String comment = etCallBack.getText().toString().trim();
            if (isComment) {
                if (comment.equals("")) {
                    Toast.makeText(ActivityDetailsActivity.this, "回复内容为空！", Toast.LENGTH_SHORT).show();//非空判断
                    setBaseState();
                    return;
                } else {//评论活动
                    etCallBack.setText("");
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(etCallBack.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    CallBackUtil.handleCallBackMSG(2, activeID, Integer.valueOf(userId), comment);
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
            } else {
                if (comment.equals("")) {
                    Toast.makeText(ActivityDetailsActivity.this, "回复内容为空！", Toast.LENGTH_SHORT).show();//非空判断
                    isComment = true;
                    setBaseState();
                    return;
                }else {
                    if (toNickName.equals(nickName)) {
                        Toast.makeText(ActivityDetailsActivity.this, "不能回复自己！", Toast.LENGTH_SHORT).show();
                        isComment = true;
                        setBaseState();
                        return;
                    } else {
                        if (type==CALL_BACK_TYPE_TWO){//2号类型回复
                            int callBackPOS=CallBackAdapter.callBackToPOS;
                            int commentID = commentList.get(callBackPOS).getCommentID();
                            int toID = CallBackAdapter.callBackToID;
                            ArrayList<CallBackDetailEntity> detailMSGList = CallBackAdapter.MSGS;
                            CallBackUtil.handleBack(token, commentID, Integer.valueOf(userId), toID, comment, detailMSGList, handler, HANDLE_MAIN_USER_BACK, nickName, toNickName);
                            isComment = true;
                            autoRefresh();
                        }else{//1号类型回复
                            int commentID = commentList.get(notifyPos).getCommentID();
                            etCallBack.setText("");
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
        etCallBack.setHint("说点什么吧。。。");
        etCallBack.setText("");
        etCallBack.setTextColor(Color.parseColor("#999999"));
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            if(resultCode==10){
                View view=getLayoutInflater().inflate(R.layout.apply_dialog, null);
                final Dialog dialog = new Dialog(ActivityDetailsActivity.this, R.style.transparentFrameWindowStyle);
                ImageView  im_cancel=(ImageView) view.findViewById(R.id.im_cancel);
                im_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        handleData();
        etCallBack.setHint("说点什么吧。。。");
        etCallBack.setText("");
        etCallBack.setTextColor(Color.parseColor("#999999"));
        if (commentList.size()!=0){
            commentList.clear();
            setCallBackView();
        }
        getSignUpInfo();
    }
    @Override
    public void onRefresh() {
        setCallBackView();
        handleData();
        etCallBack.setHint("说点什么吧。。。");
        etCallBack.setText("");
        etCallBack.setTextColor(Color.parseColor("#999999"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                reflesh.setRefreshing(false);
            }
        }, 1000);
    }
}
