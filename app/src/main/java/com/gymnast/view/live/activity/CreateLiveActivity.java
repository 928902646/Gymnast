package com.gymnast.view.live.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.App;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.DialogUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.utils.UploadUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.HomeActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
public class CreateLiveActivity extends ImmersiveActivity implements View.OnClickListener{
    TextView tvToSetTime,tvPopup1,tvPopup2,tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8;
    EditText etInputTitle;
    LinearLayout llSetTime;
    ImageView ivSelectPic,ivCloseAll;
    Button btnConfirm,btnCancel,btnVideoLive,btnTextLive;
    NumberPicker pickerMonth,pickerDay,pickerHour,pickerMinute;
    PopupWindow mPopWindow;
    private Bitmap bitmap;
    private String title,avatar,token,groupId,userId,year,picAddress=null;
    private int month,day,hour,minute;
    private long disTime;
    private long startTime;
    ProgressDialog dialog;
    Calendar calendar=Calendar.getInstance(Locale.CHINESE);
    public static final int CREATE_LIVE_RESULT=1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CREATE_LIVE_RESULT:
                    final int liveId = (int) msg.obj;
                    String liveTime = tvToSetTime.getText().toString();
                    String[] time = liveTime.split("：");
                    String realTime = time[1];
                    final Intent intent = new Intent(CreateLiveActivity.this, LiveActivity.class);
                    intent.putExtra("type", LiveActivity.USER_MAIN);
                    intent.putExtra("groupId", groupId);
                    final String url = PicUtil.getImageUrl(CreateLiveActivity.this, API.IMAGE_URL + picAddress);
                    String avatarMain= StringUtil.isNullAvatar(avatar);
                    final String urlMainPhoto = PicUtil.getImageUrlDetail(CreateLiveActivity.this, avatarMain, 72, 72);
                    intent.putExtra("bigPictureUrl", url);
                    intent.putExtra("mainPhotoUrl", urlMainPhoto);
                    intent.putExtra("title", title);
                    intent.putExtra("currentNum", 0);
                    intent.putExtra("liveId", liveId);
                    intent.putExtra("liveOwnerId", userId);
                    dialog.dismiss();
                    if (realTime.equals("现在")) {
                        Toast.makeText(CreateLiveActivity.this, "直播已创建，马上开始", Toast.LENGTH_SHORT).show();
                        if (App.isStateOK){
                            CreateLiveActivity.this.startActivity(intent);
                            CreateLiveActivity.this.finish();
                        }else {
                            Intent intent1=new Intent(CreateLiveActivity.this,HomeActivity.class);
                            CreateLiveActivity.this.startActivity(intent1);
                            CreateLiveActivity.this.finish();
                        }
                    } else {
                        Toast.makeText(CreateLiveActivity.this, "直播已创建，" + realTime + "开始", Toast.LENGTH_SHORT).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intentToLive = new Intent("ACTION_TO_START_LIVE");
                                intentToLive.putExtra("type", LiveActivity.USER_MAIN);
                                intentToLive.putExtra("bigPictureUrl", url);
                                intentToLive.putExtra("mainPhotoUrl", urlMainPhoto);
                                intentToLive.putExtra("title", title);
                                intentToLive.putExtra("currentNum", 0);
                                intentToLive.putExtra("groupId", groupId);
                                intentToLive.putExtra("liveId", liveId);
                                intentToLive.putExtra("liveOwnerId", userId);
                                CreateLiveActivity.this.startActivity(intentToLive);
                                CreateLiveActivity.this.finish();
                            }
                        }, disTime);
                        Intent intent2 = new Intent(CreateLiveActivity.this, HomeActivity.class);
                        CreateLiveActivity.this.startActivity(intent2);
                        CreateLiveActivity.this.finish();
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_live);
        tvToSetTime= (TextView) findViewById(R.id.tvToSetTime);
        etInputTitle= (EditText) findViewById(R.id.etInputTitle);
        tvPopup1= (TextView) findViewById(R.id.tvPopup1);
        tvPopup2= (TextView) findViewById(R.id.tvPopup2);
        ivSelectPic= (ImageView) findViewById(R.id.ivSelectPic);
        ivCloseAll= (ImageView) findViewById(R.id.ivCloseAll);
        llSetTime= (LinearLayout) findViewById(R.id.llSetTime);
        btnVideoLive= (Button) findViewById(R.id.btnVideoLive);
        btnTextLive= (Button) findViewById(R.id.btnTextLive);
        btnConfirm= (Button) llSetTime.findViewById(R.id.btnConfirm);
        btnCancel= (Button) llSetTime.findViewById(R.id.btnCancel);
        pickerMonth= (NumberPicker)llSetTime.findViewById(R.id.pickerMonth);
        pickerDay= (NumberPicker) llSetTime.findViewById(R.id.pickerDay);
        pickerHour= (NumberPicker)llSetTime. findViewById(R.id.pickerHour);
        pickerMinute= (NumberPicker)llSetTime. findViewById(R.id.pickerMinute);
        SharedPreferences share = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        avatar=share.getString("Avatar", "");
        token=share.getString("Token","");
        userId=share.getString("UserId","");
        startTime=System.currentTimeMillis();
        setBaseTime();
        ivCloseAll.setOnClickListener(this);
        btnVideoLive.setOnClickListener(this);
        btnTextLive.setOnClickListener(this);
        ivSelectPic.setOnClickListener(this);
        tvPopup1.setOnClickListener(this);
        tvPopup2.setOnClickListener(this);
        tvToSetTime.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        Animation translateAnimation1=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnimation1.setDuration(1000);
        translateAnimation1.setFillAfter(true);
        Animation translateAnimation2=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        translateAnimation2.setDuration(1000);
        translateAnimation2.setFillAfter(true);
        switch (v.getId()){
            case R.id.ivCloseAll:
               finish();
                break;
            case R.id.btnVideoLive:
               Toast.makeText(this,"该功能还未上线，敬请关注！",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnTextLive:
                if (App.checkNetwork()){
                        if (etInputTitle.getText().toString().equals("")){
                            Toast.makeText(this,"亲，您还没有设置直播标题！",Toast.LENGTH_SHORT).show();
                            return ;
                        }else if (bitmap==null){
                            Toast.makeText(this,"亲，您还没有设置直播图片！",Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        createTextLive();
                }else {
                  Toast.makeText(this,"当前无网络连接",Toast.LENGTH_SHORT).show();
                  return;
                }
                break;
            case R.id.ivSelectPic:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1000);
                break;
            case  R.id.tvToSetTime:
               llSetTime.startAnimation(translateAnimation1);
                llSetTime.setVisibility(View.VISIBLE);
            break;
           case R.id.btnCancel:
               llSetTime.startAnimation(translateAnimation2);
               llSetTime.setVisibility(View.GONE);
               break;
            case R.id.btnConfirm:
                setLiveStartTime();
                llSetTime.startAnimation(translateAnimation2);
                break;
            case R.id.tvPopup1:
                showPopupWindow1();
                break;
            case R.id.tvPopup2:
                showPopupWindow2();
                break;
            case R.id.pop_guard:
                tvPopup1.setText(tv1.getText());
                mPopWindow.dismiss();
                break;
            case R.id.pop_star:
                tvPopup1.setText(tv2.getText());
                mPopWindow.dismiss();
                break;
            case R.id.pop_worldcup:
                tvPopup1.setText(tv3.getText());
                mPopWindow.dismiss();
                break;
            case R.id.pop_nba:
                tvPopup1.setText(tv4.getText());
                mPopWindow.dismiss();
                break;
            case R.id.pop_wta:
                tvPopup1.setText(tv5.getText());
                mPopWindow.dismiss();
                break;
            case R.id.pop_all:
                tvPopup2.setText(tv6.getText());
                mPopWindow.dismiss();
                break;
            case R.id.pop_concern:
                tvPopup2.setText(tv7.getText());
                mPopWindow.dismiss();
                break;
            case R.id.pop_fensi:
                tvPopup2.setText(tv8.getText());
                mPopWindow.dismiss();
                break;
        }
    }
    private void createTextLive() {
        dialog=(ProgressDialog)DialogUtil.createDialog(CreateLiveActivity.this,"直播","正在为您创建直播，请稍候。。。");
        dialog.show();
        Thread thread2=new Thread(){
            @Override
            public void run() {
                try{
                        String uri1 = API.BASE_URL + "/v1/live/create";
                        HashMap<String, String> params1 = initParams();
                        String result = PostUtil.sendPostMessage(uri1, params1);
                        JSONObject obj = new JSONObject(result);
                        int res = Integer.valueOf(obj.getString("id"));
                    EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                    option.maxUsers = 1000;
                    option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    String []allMembers=new String[]{};
                    EMGroup group = EMClient.getInstance().groupManager().createGroup(res+"",title,allMembers, null, option);
                    if (group!=null){
                        Log.i("tag","创建环信群组成功");
                        groupId=group.getGroupId();
                        String url2=API.BASE_URL+"/v1/live/update_live";
                        HashMap<String,String> params2=new HashMap<>();
                        params2.put("token",token);
                        params2.put("id",res+"");
                        params2.put("groupId", groupId);
                       String result2= PostUtil.sendPostMessage(url2,params2);
                        Log.i("tag","上传环信groupID结果"+result2);
                    }else {
                        Log.i("tag","创建环信群组失败");
                    }
                    String liveTime = tvToSetTime.getText().toString();
                    String[] time = liveTime.split("：");
                    String realTime = time[1];
                    String url=API.BASE_URL+"/v1/live/update_status";
                    HashMap<String,String> param=new HashMap<>();
                    param.put("token",token);
                    param.put("id",res+"");
                    if (realTime.equals("现在")){
                        param.put("status",1+"");
                    }else {
                        param.put("status",0+"");
                    }
                    String result1=PostUtil.sendPostMessage(url, param);
                    Log.i("tag","更新直播状态结果111："+result1);
                        Message msg = new Message();
                        msg.what = CREATE_LIVE_RESULT;
                        msg.obj = res;
                        handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread2.setPriority(3);
        thread2.start();
    }
    private HashMap<String,String> initParams() {
        HashMap<String,String> params=new HashMap<>();
        params.put("token",token);
        int auth=0, lab=0;;
        if (tvPopup1.getText().equals("广场")){
            lab=1;
        }else if (tvPopup1.getText().equals("大咖")){
            lab=2;
        }else if (tvPopup1.getText().equals("世界杯")){
            lab=3;
        }else if (tvPopup1.getText().equals("NBA")){
            lab=4;
        }else if (tvPopup1.getText().equals("WTA")){
            lab=5;
        }
        if (tvPopup2.getText().equals("所有人")){
            auth=1;
        }else if (tvPopup2.getText().equals("关注")){
            auth=2;
        }else if (tvPopup2.getText().equals("粉丝")) {
            auth = 3;
        }
        params.put("auth",auth+"");//1-公开，默认 2-我的粉丝可见 3-我关注人可见 4-包括2、3情况
        params.put("type", "1");//图文直播
        title=etInputTitle.getText().toString();
        params.put("start", startTime + "");
        params.put("title",title);
        params.put("lab", lab + "");//直播所属标签，1-NBA
        params.put("bgmUrl",picAddress);
        return params;
    }
    protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000  &&  data != null){
            // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
           final ContentResolver resolver = getContentResolver();
          final   Uri originalUri = data.getData(); // 获得图片的uri
            try {
                 Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                bitmap= PicUtil.compress(bitmap1, 170, 140);
                ivSelectPic.setImageBitmap(bitmap);
                dialog=(ProgressDialog)DialogUtil.createDialog(CreateLiveActivity.this,"直播","正在上传图片，请稍候。。。");
                dialog.show();
            }catch (Exception e){
                e.printStackTrace();
            }
                Thread thread1=new Thread(){
                    @Override
                    public void run() {
                        String path=UploadUtil.getAbsoluteImagePath(CreateLiveActivity.this,originalUri);
                        picAddress=UploadUtil.getNetWorkImageAddress(path, CreateLiveActivity.this);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (picAddress!=null){
                                    dialog.dismiss();
                                }
                                }
                            });
                    }
                };
           thread1.setPriority(8);
            thread1.start();
        }
    }
    private void showPopupWindow1() {
        View contentView = LayoutInflater.from(CreateLiveActivity.this).inflate(R.layout.popupwindow_type1_live, null);
        mPopWindow = new PopupWindow(contentView, 172, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        mPopWindow.showAsDropDown(tvPopup1);
        //设置各个控件的点击响应
        tv1 = (TextView)contentView.findViewById(R.id.pop_guard);
        tv2 = (TextView)contentView.findViewById(R.id.pop_star);
        tv3 = (TextView)contentView.findViewById(R.id.pop_worldcup);
        tv4 = (TextView)contentView.findViewById(R.id.pop_nba);
        tv5 = (TextView)contentView.findViewById(R.id.pop_wta);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);
    }
    private void showPopupWindow2() {
        View contentView = LayoutInflater.from(CreateLiveActivity.this).inflate(R.layout.popupwindow_type2_live, null);
        mPopWindow = new PopupWindow(contentView,
                172, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        mPopWindow.showAsDropDown(tvPopup2);
        //设置各个控件的点击响应
        tv6 = (TextView)contentView.findViewById(R.id.pop_all);
        tv7 = (TextView)contentView.findViewById(R.id.pop_concern);
        tv8 = (TextView)contentView.findViewById(R.id.pop_fensi);
        tv6.setOnClickListener(this);
        tv7.setOnClickListener(this);
        tv8.setOnClickListener(this);
    }
    private void setLiveStartTime() {
        month = pickerMonth.getValue() - 1;
        day=pickerDay.getValue();
        hour=pickerHour.getValue();
        minute=pickerMinute.getValue();
        calendar.set(Integer.valueOf(year),month,day,hour,minute,0);
        startTime=calendar.getTimeInMillis();
       disTime=calendar.getTimeInMillis()-System.currentTimeMillis();
        int after;
        if(disTime<-60000){
            Toast.makeText(this,"时间已过期，请重新点击选择！",Toast.LENGTH_SHORT).show();
        }else{
            if (disTime<60000){
                tvToSetTime.setText("直播开始时间：马上");
            }else if (disTime<3600000){
                after= (int) (disTime/60000);
                tvToSetTime.setText("直播开始时间："+after+"分后");
            }else if (disTime<86400000){
                after= (int) (disTime/3600000);
                tvToSetTime.setText("直播开始时间："+after+"时后");
            }else{
                after= (int) (disTime/86400000);
                tvToSetTime.setText("直播开始时间："+after+"天后");
            }
        }
        llSetTime.setVisibility(View.GONE);
    }
    public static  final int TYPE_RUNNIAN=1;
    public static  final int TYPE_PINGNIAN=0;
    private int checkRunNian(){
        int yearTime=Integer.valueOf(year);
        if (yearTime%100==0){
            if (yearTime%400==0){
                return TYPE_RUNNIAN;
            }
        }else{
            if (yearTime%4==0){
                return TYPE_RUNNIAN;
            }
        }
        return TYPE_PINGNIAN;
    }
    private void setBaseTime() {
        pickerMonth.setMaxValue(12);
        pickerMonth.setMinValue(1);
        pickerMonth.setValue(calendar.get(Calendar.MONTH)+1);
        pickerDay.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int yearType=checkRunNian();
                if (yearType==TYPE_RUNNIAN){
                    if (pickerMonth.getValue()==2){
                        pickerDay.setMaxValue(29);
                    }
                }else {
                    pickerDay.setMaxValue(28);
                }
                if (pickerMonth.getValue()==1|pickerMonth.getValue()==3|pickerMonth.getValue()==5|pickerMonth.getValue()==7|pickerMonth.getValue()==8|pickerMonth.getValue()==10|pickerMonth.getValue()==12){
                    pickerDay.setMaxValue(31);
                }else if(pickerMonth.getValue()==4|pickerMonth.getValue()==6|pickerMonth.getValue()==9|pickerMonth.getValue()==11){
                    pickerDay.setMaxValue(30);
                }
            }
        });
        pickerDay.setMaxValue(31);
        pickerDay.setMinValue(1);
        pickerDay.setValue(calendar.get(Calendar.DAY_OF_MONTH));
        pickerHour.setMaxValue(23);
        pickerHour.setMinValue(0);
        pickerHour.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        pickerMinute.setMaxValue(59);
        pickerMinute.setMinValue(0);
        pickerMinute.setValue(calendar.get(Calendar.MINUTE));
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time=sdf.format(new Date());
        year=time.substring(0, 4);
    }
}
