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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.App;
import com.gymnast.R;
import com.gymnast.data.hotinfo.ConcerDevas;
import com.gymnast.data.net.API;
import com.gymnast.data.pack.ConcernData;
import com.gymnast.data.personal.DynamicData;
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
import com.gymnast.view.personal.adapter.GridAdapter;
import com.gymnast.view.personal.listener.MyTextWatcher;
import com.gymnast.view.personal.listener.WrapContentLinearLayoutManager;
import com.gymnast.view.user.LoginActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cymbi on 2016/9/2.
 */
public class PersonalDynamicDetailActivity extends ImmersiveActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,View.OnLayoutChangeListener {


    private ImageView mPersonal_menu,personal_back,mDynamic_head,ivClose;
    private TextView mDynamic_name,mDynamic_time,mDynamic_Title,mDynamic_context,star_type,tvCollect,tvReport,tvDelete, tvSpacial,tvTop;
    private GridView mGridview;
    private Dialog cameradialog;
    LinearLayout llShareToFriends,llShareToWeiChat,llShareToQQ,llShareToQQZone,llShareToMicroBlog;
    public static int shareNumber=0;//分享次数
    private SimpleDateFormat sdf =new SimpleDateFormat("MM月-dd日 HH:mm");
    View view;
    int circleUserID;
    TextView tvSendDynamic;
    EditText etCallBackDynamic;
    ImageView ivSendDynamic;
    RecyclerView rvCallBack;
    int notifyPos=0;
    SwipeRefreshLayout reflesh;
    String firstCommenter="";
    boolean isPraised=false;
    String token,nowUserId,nickName,avatar;
    int dynamicID;
    List<CallBackEntity> commentList=new ArrayList<>();
    CallBackAdapter commentAdapter;
    public static boolean isComment=true;
    public static final int HANDLE_COMMENT_DATA=1;
    public static final int HANDLE_MAIN_USER_BACK=2;
    public static final int HANDLE_PRAISE=3;
    public static final int HANDLE_CANCEL_PRAISE=4;
    public static final int HANDLE_UNKNOWN_ERROR=5;
    public static final int HANDLE_START_PRAISE=6;
    static ArrayList<CallBackDetailEntity> detailMSGs;
    ArrayList<String> userABC=new ArrayList<>();
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_START_PRAISE:
                    boolean isStartPraised= (boolean) msg.obj;
                    ivSendDynamic.setVisibility(View.VISIBLE);
                    if (isStartPraised){
                        ivSendDynamic.setImageResource(R.mipmap.like_pressed);
                    }else {
                        ivSendDynamic.setImageResource(R.mipmap.like_normal);
                    }
                    ivSendDynamic.invalidate();
                    break;
                case HANDLE_UNKNOWN_ERROR:
                    Toast.makeText( PersonalDynamicDetailActivity.this,"未知错误，请重试！",Toast.LENGTH_SHORT).show();
                    break;
                case HANDLE_CANCEL_PRAISE:
                    Toast.makeText( PersonalDynamicDetailActivity.this,"已取消点赞！",Toast.LENGTH_SHORT).show();
                    ivSendDynamic.setImageResource(R.mipmap.like_normal);
                    break;
                case HANDLE_PRAISE:
                    Toast.makeText( PersonalDynamicDetailActivity.this,"已点赞！",Toast.LENGTH_SHORT).show();
                    ivSendDynamic.setImageResource(R.mipmap.like_pressed);
                    break;
                case HANDLE_MAIN_USER_BACK:
                    commentAdapter.notifyItemChanged(notifyPos);
                    break;
                case HANDLE_COMMENT_DATA:
                    userABC = (ArrayList<String>) msg.obj;
                    commentAdapter = new CallBackAdapter(PersonalDynamicDetailActivity.this, commentList);
                    WrapContentLinearLayoutManager manager = new WrapContentLinearLayoutManager(PersonalDynamicDetailActivity.this, LinearLayoutManager.VERTICAL, false);
                    rvCallBack.setLayoutManager(manager);
                    rvCallBack.setAdapter(commentAdapter);
                    commentAdapter.setOnItemClickListener(new CallBackAdapter.OnItemClickListener() {
                        @Override
                        public void OnCallBackClick(View view, int position, ArrayList<CallBackDetailEntity> detailMSGs) {
                            isComment = false;
                            notifyPos = position;
                            PersonalDynamicDetailActivity.detailMSGs = detailMSGs;
                            PersonalDynamicDetailActivity.type=PersonalDynamicDetailActivity.CALL_BACK_TYPE_ONE;
                            String backWho = commentList.get(position).getCallBackNickName();
                            firstCommenter = backWho;
                            etCallBackDynamic.requestFocus();
                            etCallBackDynamic.setText("");
                            etCallBackDynamic.setHint("回复" + backWho);
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (inputManager.isActive()) {
                                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                                PersonalDynamicDetailActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                tvSendDynamic.setVisibility(View.VISIBLE);
                                ivSendDynamic.setVisibility(View.GONE);
                            }
                        }
                    });
                    break;
            }}};
    //屏幕高度
    private int screenHeight=0;
    //软件盘弹起后所占高度阀值
    private int keyHeight=0;
    private View activityRootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.dynamic_details);
        getBaseInfo();
        setView();
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight/3;
        activityRootView.addOnLayoutChangeListener(this);
        setData();
        setCallBackView();
        getPageView();
    }
    private void getPageView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri=API.BASE_URL+"/v1/pageViwes";
                HashMap<String,String>params=new HashMap<>();
                params.put("types",1+"");
                params.put("typeId",dynamicID+"");
                PostUtil.sendPostMessage(uri,params);
            }
        }).start();
    }
    private void getBaseInfo() {
        SharedPreferences share = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token", "");
        nowUserId = share.getString("UserId","");//当前用户id
        nickName = share.getString("NickName", "");
        avatar = share.getString("Avatar", "");
        dynamicID=  getIntent().getIntExtra("CirleID",0);
    }
    private void autoRefresh(){
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(etCallBackDynamic.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        RefreshUtil.refresh(reflesh, this);
        setBaseState();
        setCallBackView();
        rvCallBack.scrollToPosition(0);
        ivSendDynamic.setVisibility(View.VISIBLE);
        tvSendDynamic .setVisibility(View.GONE);
        etCallBackDynamic.setFocusable(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                reflesh.setRefreshing(false);
            }
        }, 500);
    }
    private void setView() {
        reflesh = (SwipeRefreshLayout) findViewById(R.id.srlReflesh);
        RefreshUtil.refresh(reflesh, this);
        etCallBackDynamic= (EditText) findViewById(R.id.etCallBackDynamic);
        etCallBackDynamic.setHint("说点什么吧。。。");
        etCallBackDynamic.setHintTextColor(Color.parseColor("#999999"));
        ivSendDynamic= (ImageView) findViewById(R.id.ivSendDynamic);
        rvCallBack= (RecyclerView) findViewById(R.id.rvCallBack);
        tvSendDynamic=(TextView) findViewById(R.id.tvSendDynamic);
        mPersonal_menu=(ImageView)findViewById(R.id.personal_menu);
        personal_back=(ImageView)findViewById(R.id.personal_back);
        mDynamic_head=(ImageView)findViewById(R.id.dynamic_head);
        mDynamic_name=(TextView)findViewById(R.id.dynamic_name);
        mDynamic_time=(TextView)findViewById(R.id.dynamic_time);
        mDynamic_Title=(TextView)findViewById(R.id.dynamic_Title);
        mDynamic_context=(TextView)findViewById(R.id.dynamic_context);
        activityRootView=findViewById(R.id.activityRootView);
        star_type=(TextView)findViewById(R.id.star_type);
        mGridview=(GridView)findViewById(R.id.gridview);
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
        cameradialog = new Dialog(this,R.style.Dialog_Fullscreen);
        ivClose=(ImageView) view.findViewById(R.id.ivClose);
        mPersonal_menu.setOnClickListener(this);
        personal_back.setOnClickListener(this);
        mDynamic_head.setOnClickListener(this);
        mDynamic_name.setOnClickListener(this);
        mDynamic_time.setOnClickListener(this);
        mDynamic_Title.setOnClickListener(this);
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
        reflesh.setOnRefreshListener(this);
        tvSendDynamic.setOnClickListener(this);
        ivSendDynamic.setOnClickListener(this);
        etCallBackDynamic.addTextChangedListener(new MyTextWatcher(tvSendDynamic, ivSendDynamic, etCallBackDynamic));
    }
    private void setData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri = API.BASE_URL + "/v1/concern/getOne";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("concernId", dynamicID + "");
                if (nowUserId!=null){
                    params.put("accountId",nowUserId);
                }
                Log.i("tag","dynamicID="+dynamicID);
                String result = GetUtil.sendGetMessage(uri, params);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getInt("state") == 200) {
                        final ArrayList<String> imageURL = new ArrayList<String>();
                        JSONObject data = object.getJSONObject("data");
                        JSONObject concern = data.getJSONObject("concern");
                        circleUserID=concern.getInt("userId");
                        JSONObject userVo = concern.getJSONObject("userVo");
                        boolean isCollection = data.getBoolean("isCollection");
                        isPraised = data.getBoolean("isZan");
                        final long createTime = concern.getLong("createTime");
                        final String topicTitle = concern.getString("topicTitle");
                        final String topicContent = concern.getString("topicContent");
                        final String nickName = userVo.getString("nickName");
                        String zanCounts = concern.getString("zanCounts");
                        String commentCounts = concern.getString("commentCounts");
                        String urls = concern.getString("imgUrl");
                        final String avatar = userVo.getString("avatar");
                        if (urls == null | urls.equals("null") | urls.equals("")) {

                        } else {
                            String[] imageUrls = urls.split(",");
                            for (int j = 0; j < imageUrls.length; j++) {
                                imageURL.add(API.IMAGE_URL + imageUrls[j]);
                            }
                        }
                        Message message=new Message();
                        message.obj=isPraised;
                        message.what=HANDLE_START_PRAISE;
                        handler.sendMessage(message);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PicassoUtil.handlePic(PersonalDynamicDetailActivity.this, PicUtil.getImageUrlDetail(PersonalDynamicDetailActivity.this, StringUtil.isNullAvatar(avatar), 320, 320), mDynamic_head, 320, 320);
                                mDynamic_name.setText(nickName);
                                if(!topicTitle.equals("null")&&topicTitle!=null){
                                    mDynamic_Title.setText(topicTitle);
                                }
                                mDynamic_time.setText(sdf.format(new Date(createTime)) + "");
                                mDynamic_context.setText("  "+topicContent);
                                GridAdapter adapter = new GridAdapter(PersonalDynamicDetailActivity.this, imageURL);
                                mGridview.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvSendDynamic:
                handleSendMSG();
                break;
            case R.id.ivSendDynamic:
                priseActive();
                break;
            case R.id.personal_back:
                finish();
                break;
            case R.id.personal_menu:
                showdialog();
                break;
            case R.id.dynamic_head:
                Intent intent=new Intent(PersonalDynamicDetailActivity.this,PersonalOtherHomeActivity.class);
                intent.putExtra("UserID",circleUserID);
                PersonalDynamicDetailActivity.this.startActivity(intent);
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
    private void showdialog() {
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
    private void priseActive() {
        new Thread(){
            @Override
            public void run() {
                try{
                    String uri= API.BASE_URL+"/v1/zan/add";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("token",token);
                    params.put("bodyId",dynamicID+"");
                    params.put("bodyType",2+"");
                    params.put("accountId",nowUserId);
                    String result= PostUtil.sendPostMessage(uri, params);
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
    private void setCallBackView() {
        CallBackUtil.getCallBackData(3, dynamicID, commentList, handler, HANDLE_COMMENT_DATA);
        reflesh.setRefreshing(false);
    }
    String toNickName;
    public static final int CALL_BACK_TYPE_ONE=1111;
    public static final int CALL_BACK_TYPE_TWO=2222;
    public static int type;
    private void handleSendMSG() {
        toNickName=etCallBackDynamic.getHint().toString().trim().substring(2);
        if(!App.isStateOK|token.equals("")){//未登录
            Toast.makeText(PersonalDynamicDetailActivity.this,"您还没有登录，请登陆！",Toast.LENGTH_SHORT).show();
            PersonalDynamicDetailActivity.this.startActivity(new Intent(PersonalDynamicDetailActivity.this,LoginActivity.class));
            return;
        }else {
            String comment = etCallBackDynamic.getText().toString().trim();
            if (isComment) {
                if (comment.equals("")) {
                    Toast.makeText(PersonalDynamicDetailActivity.this, "回复内容为空！", Toast.LENGTH_SHORT).show();//非空判断
                    setBaseState();
                    return;
                } else {//评论帖子
                    etCallBackDynamic.setText("");
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(etCallBackDynamic.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    CallBackUtil.handleCallBackMSG(3, dynamicID, Integer.valueOf(nowUserId), comment);
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
                if (comment.equals("")) {//非空判断
                    Toast.makeText(PersonalDynamicDetailActivity.this, "回复内容为空！", Toast.LENGTH_SHORT).show();//非空判断
                    isComment = true;
                    setBaseState();
                    return;
                } else {
                    if (toNickName.equals(nickName)) {//回复自己
                        Toast.makeText(PersonalDynamicDetailActivity.this, "不能回复自己！", Toast.LENGTH_SHORT).show();
                        isComment = true;
                        setBaseState();
                        return;
                    } else {//回复他人
                        if (type == CALL_BACK_TYPE_TWO) {//2号类型回复
                            int callBackPOS=CallBackAdapter.callBackToPOS;
                            int commentID = commentList.get(callBackPOS).getCommentID();
                            int toID = CallBackAdapter.callBackToID;
                            ArrayList<CallBackDetailEntity> detailMSGList = CallBackAdapter.MSGS;
                            CallBackUtil.handleBack(token, commentID, Integer.valueOf(nowUserId), toID, comment, detailMSGList, handler, HANDLE_MAIN_USER_BACK, nickName, toNickName);
                            isComment = true;
                            autoRefresh();
                        } else {//1号类型回复
                            int commentID = commentList.get(notifyPos).getCommentID();
                            CallBackUtil.handleBack(token, commentID, Integer.valueOf(nowUserId), -1, comment, detailMSGs, handler, HANDLE_MAIN_USER_BACK, nickName, "");
                            isComment = true;
                            autoRefresh();
                        }
                    }
                }
            }
        }
    }
    private void setBaseState(){
        etCallBackDynamic.setHint("说点什么吧。。。");
        etCallBackDynamic.setHintTextColor(Color.parseColor("#999999"));
        etCallBackDynamic.setText("");
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        activityRootView.addOnLayoutChangeListener(this);
        etCallBackDynamic.setHint("说点什么吧。。。");
        etCallBackDynamic.setHintTextColor(Color.parseColor("#999999"));
        etCallBackDynamic.setText("");
        setData();
        if (commentList.size()!=0){
            commentList.clear();
            setCallBackView();
        }
    }
    @Override
    public void onRefresh() {
        setCallBackView();
        setData();
        etCallBackDynamic.setHint("说点什么吧。。。");
        etCallBackDynamic.setHintTextColor(Color.parseColor("#999999"));
        etCallBackDynamic.setText("");
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

    @Override
    public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){
           /* View view=getLayoutInflater().inflate(R.layout.log_out_dialog, null);
            Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
            dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = dialog.getWindow();
            // 设置显示动画
            window.setWindowAnimations(R.style.log_out_dialog);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y=oldBottom - bottom;
            dialog.onWindowAttributesChanged(wl);
            dialog.show();*/
            Toast.makeText(PersonalDynamicDetailActivity.this,"软键盘弹起",Toast.LENGTH_SHORT).show();
        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){
            Toast.makeText(PersonalDynamicDetailActivity.this,"软键盘收起",Toast.LENGTH_SHORT).show();


        }
    }
}
