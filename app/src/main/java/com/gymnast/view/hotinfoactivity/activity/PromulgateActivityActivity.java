package com.gymnast.view.hotinfoactivity.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.DialogUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StorePhotosUtil;
import com.gymnast.utils.UploadUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.hotinfoactivity.adapter.AddSelectAdapter;
import com.gymnast.view.picker.SlideDateTimeListener;
import com.gymnast.view.picker.SlideDateTimePicker;
import com.gymnast.view.recyclerview.RecyclerAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by yf928 on 2016/8/8.
 */
public class PromulgateActivityActivity extends ImmersiveActivity  implements View.OnClickListener,  RecyclerAdapter.OnItemClickListener {
    private ArrayList<String> list=new ArrayList<String>();
    private RelativeLayout rlAddvancedSetting,rlRange,rlSite,rlApply,rlMoney,rlNumber,rlPhone;
    private LinearLayout llAdvancedDown,llDown,llTogglebuttonOpen;
    private ScrollView scrollView;
    private boolean isOpen;// 为false显示，为true不显示
    private EditText etActivityDescribe,etActivityName;
    private TextView tvActivityAddress,tvTest,tvPromulgateStartTime,tvPromulgateOverTime,camera,gallery,cancel,tvAddImage,tvPublish,tvSecletEndTime,tvSelectNumber,tvSelectPhone,tvSelectRange,tvPriceNumber;
    private ImageView ivArrows,ivHead,ivHeadBackground,ivAdd,ivPromulgateBack,add;
    private ToggleButton togglebutton,togglebutton1;
    private RadioButton buttonUp,buttonDown;
    private Dialog dialog;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private TimerTask task;
    private Date dateStartTime,dateOverTime,dateApplyTime;
    private SharedPreferences share;
    private RadioGroup myRadioGroup;
    private Integer type;
    private double prices,price;
    private AddSelectAdapter mAddSelectAdapter;
    private RecyclerView rvPhotos;
    private int examine,visible,mSelectNumber;
    private CheckBox cbPhone,cbId,cbName,cbSex,cbAge,cbAddress;
    private String address,phone,title,num,imageUrl,nowTime,token,id,fileName = "",picAddress=null;
    private long startTime,lastTime,endTime;
    private static final int KEY = 1;
    private final int PIC_FROM_CAMERA = 2;
    public static final int HANDLE_TIME_CHANGE=3;
    private Bitmap bitmap;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case KEY :
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd hh:mm", sysTime);
                    tvPromulgateStartTime.setText(sysTimeStr);
                    break;
                case  HANDLE_TIME_CHANGE:
                    String time= (String) msg.obj;
                    tvPromulgateStartTime.setText(time);
                    tvPromulgateStartTime.invalidate();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promulgate);
        getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_ADJUST_PAN);
        share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token","");
        id = share.getString("UserId","");
        setview();
        initview();
        /**
         * 刚进activity的时候更新start_time时间
         */
        Timer timer=new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                nowTime=mFormatter.format(new Date());
                Message msg=new Message();
                msg.what=HANDLE_TIME_CHANGE;
                msg.obj=nowTime;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(task,1,1000);
    }
    private void setview() {
        ivPromulgateBack=(ImageView)findViewById(R.id.ivPromulgateBack);
        rlAddvancedSetting=(RelativeLayout)  findViewById(R.id.rlAddvancedSetting);
        llAdvancedDown=(LinearLayout)  findViewById(R.id.llAdvancedDown);
        llTogglebuttonOpen=(LinearLayout)  findViewById(R.id.llTogglebuttonOpen);
        llDown=(LinearLayout)  findViewById(R.id.llDown);
        scrollView=(ScrollView)  findViewById(R.id.scrollView);
        etActivityDescribe  =(EditText)findViewById(R.id.etActivityDescribe);
        etActivityName =(EditText)findViewById(R.id.etActivityName);
        tvTest =(TextView)findViewById(R.id.tvTest);
        rlNumber =(RelativeLayout)findViewById(R.id.rlNumber);
        rlPhone =(RelativeLayout)findViewById(R.id.rlPhone);
        rlRange =(RelativeLayout)findViewById(R.id.rlRange);
        rlApply =(RelativeLayout)findViewById(R.id.rlApply);
        rlMoney =(RelativeLayout)findViewById(R.id.rlMoney);
        tvActivityAddress = (TextView) findViewById(R.id.tvActivityAddress);
        tvPromulgateStartTime =(TextView)findViewById(R.id.tvPromulgateStartTime);
        tvSelectNumber =(TextView)findViewById(R.id.tvSelectNumber);
        tvSelectPhone =(TextView)findViewById(R.id.tvSelectPhone);
        tvPromulgateOverTime =(TextView)findViewById(R.id.tvPromulgateOverTime);
        tvPublish =(TextView)findViewById(R.id.tvPublish);
        rlSite =(RelativeLayout)findViewById(R.id.rlSite);
        tvAddImage =(TextView)findViewById(R.id.tvAddImage);
        tvSelectRange =(TextView)findViewById(R.id.tvSelectRange);
        tvSecletEndTime =(TextView)findViewById(R.id.tvSecletEndTime);
        tvPriceNumber =(TextView)findViewById(R.id.tvPriceNumber);
        ivArrows=(ImageView)findViewById(R.id.ivArrows);
        ivHead=(ImageView)findViewById(R.id.ivHead);
        ivHeadBackground=(ImageView)findViewById(R.id.ivHeadBackground);
        ivAdd=(ImageView)findViewById(R.id.ivAdd);
        add=(ImageView)findViewById(R.id.add);
        etActivityDescribe.setSelection(etActivityDescribe.getText().length());
        ivArrows.setBackgroundResource(R.mipmap.icon_more_lower);
        togglebutton = (ToggleButton) findViewById(R.id.togglebutton);
        togglebutton1 = (ToggleButton) findViewById(R.id.togglebutton1);
        buttonUp = (RadioButton) findViewById(R.id.buttonUp);
        buttonDown = (RadioButton) findViewById(R.id.buttonDown);
        myRadioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        rvPhotos = (RecyclerView) findViewById(R.id.rvPhotos);
        String over = mFormatter.format(System.currentTimeMillis()+1000L*60L*60L*24L);
        tvPromulgateOverTime.setText(over);
        buttonUp.setChecked(true);
        togglebutton.setOnClickListener(this);
        togglebutton1.setOnClickListener(this);
        rlAddvancedSetting.setOnClickListener(this);
        rlNumber.setOnClickListener(this);
        rlPhone.setOnClickListener(this);
        rlRange.setOnClickListener(this);
        rlSite.setOnClickListener(this);
        ivPromulgateBack.setOnClickListener(this);
        ivHead.setOnClickListener(this);
        ivHeadBackground.setOnClickListener(this);
        tvPromulgateOverTime.setOnClickListener(this);
        tvPromulgateStartTime.setOnClickListener(this);
        rlApply.setOnClickListener(this);
        tvPublish.setOnClickListener(this);
        rlMoney.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
    }
    private void setdata() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = tvPromulgateStartTime.getText().toString();
        String over = tvPromulgateOverTime.getText().toString();
        String apply = tvSecletEndTime.getText().toString().equals("请点击选择结束时间") ? over : tvSecletEndTime.getText().toString();
        String p = tvPriceNumber.getText().toString().equals("免费") ? "0元" : tvPriceNumber.getText().toString();
        String p1 = p.substring(0, p.length() - 1);
        price = Double.parseDouble(p1);
        address = tvActivityAddress.getText().toString();
        title = etActivityName.getText().toString();
        // final String desc= et_activity_describe.getText().toString();
        phone = tvSelectPhone.getText().toString();
        try {
            dateStartTime = format.parse(start);
            dateOverTime = format.parse(over);
            dateApplyTime = format.parse(apply);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //时间戳
        startTime = dateStartTime.getTime();
        endTime = dateOverTime.getTime();
        lastTime = dateApplyTime.getTime();
        if (startTime > endTime) {
            Toast.makeText(PromulgateActivityActivity.this, "开始时间不能大于结束时间！", Toast.LENGTH_SHORT).show();
        } else if (lastTime > endTime) {
            Toast.makeText(PromulgateActivityActivity.this, "报名时间不能大于结束时间！", Toast.LENGTH_SHORT).show();
        } else if (lastTime < startTime) {
            Toast.makeText(PromulgateActivityActivity.this, "报名时间不能小于开始时间！", Toast.LENGTH_SHORT).show();
        } else {
            if (!isAll()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PromulgateActivityActivity.this, "信息填写不完全，请检查", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String uri2 = API.BASE_URL + "/v1/activity/release";
                            HashMap<String, String> params = new HashMap<>();
                            params.put("token", token);
                            params.put("createId", id);
                            params.put("title", title);
                           // params.put("desc", Html.toHtml(etActivityDescribe.getText()));//
                            params.put("desc", etActivityDescribe.getText().toString());
                            params.put("examine", examine + "");
                            params.put("image", imageUrl);//
                            params.put("startTime", startTime + "");//
                            params.put("endTime", endTime + "");//
                            // params.put("groupId", "");
                            if (lastTime != 0) {
                                params.put("lastTime", lastTime + "");
                            } else {
                            }
                            if (togglebutton.isChecked()) {
                                String request = getRequestParams();
                                params.put("memberTemplate", request);
                            } else {
                            }
                            params.put("price", prices + "");
                            params.put("address", address);
                            if (tvSelectNumber.getText() == "无限制") {
                                params.put("maxPeople", "");
                            } else {
                                params.put("maxPeople", num);
                            }
                            params.put("maxPeople", mSelectNumber + "");
                            params.put("visible", visible + "");
                            params.put("phone", phone);
                            if(type==0){
                                params.put("type", 0+"");
                            }else {
                                params.put("type", 1+"");
                            }
                            String result1 = PostUtil.sendPostMessage(uri2, params);
                            JSONObject json = new JSONObject(result1);
                            if (json.getInt("state") == 200) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PromulgateActivityActivity.this, "活动发布成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } else if (json.getInt("state") == 500) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PromulgateActivityActivity.this, "state==500", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }
    private String getRequestParams() {
        StringBuffer sb=new StringBuffer();
        sb.append(cbPhone.getText()+",");
        sb.append(cbName.getText()+",");
        if (cbId.isChecked()){
            sb.append(cbId.getText()+",");
        }else {
            sb.append("");
        }
        if (cbSex.isChecked()){
            sb.append(cbSex.getText()+",");
        }else {
            sb.append("");
        }
        if (cbAge.isChecked()){
            sb.append(cbAge.getText()+",");
        }else {
            sb.append("");
        }
        if (cbAddress.isChecked()){
            sb.append(cbAddress.getText());
        }else {
            sb.append("");
        }
        return sb.toString();
    }
    private boolean  isAll() {
        if(imageUrl==null|title==null|etActivityDescribe.getText()==null|phone==null|startTime==0|endTime==0){
            return false;
        }
        return true;
    }

    private void initview() {
        /**
         * 显示高级选项
         */
        etActivityDescribe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = etActivityDescribe.getText().toString();
                tvTest.setText(number.length()+"/"+2500);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        myRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(buttonUp.getId()==checkedId){
                    type= 0;
                }else if(buttonDown.getId()==checkedId){
                    type= 1;
                }
                type=0;
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivPromulgateBack:
                finish();
                break;
            case R.id.rlAddvancedSetting:
                llAdvancedDown.setVisibility(View.VISIBLE);
                Handler handler =  new Handler();
                if(isOpen){
                    llAdvancedDown.setVisibility(View.GONE);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    ivArrows.setBackgroundResource(R.mipmap.icon_more_lower);
                }else{
                    ivArrows.setBackgroundResource(R.mipmap.icon_more_upper);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
                isOpen=!isOpen;
                break;
            case R.id.togglebutton:
                if(togglebutton.isChecked()){
                    llTogglebuttonOpen.setVisibility(View.VISIBLE);
                    cbName= (CheckBox) findViewById(R.id.cbName);
                    cbPhone= (CheckBox) findViewById(R.id.cbPhone);
                    cbId= (CheckBox) findViewById(R.id.cbId);
                    cbSex= (CheckBox) findViewById(R.id.cbSex);
                    cbAddress= (CheckBox) findViewById(R.id.cbAddress);
                    cbAge= (CheckBox) findViewById(R.id.cbAge);
                }else {
                    llTogglebuttonOpen.setVisibility(View.GONE);
                }
                break;
            case R.id.togglebutton1:
                if(togglebutton.isChecked()){
                    examine=1;
                }else {
                    examine=0;
                }
                break;
            case R.id.rlNumber:
                Intent a =new Intent(this,SettingNumberActivity.class);
                startActivityForResult(a,101);
                break;
            case R.id.rlPhone:
                Intent b =new Intent(this,SettingPhoneActivity.class);
                startActivityForResult(b,102);
                break;
            case R.id.rlRange:
                Intent c =new Intent(this,SettingRangeActivity.class);
                startActivityForResult(c,103);
                break;
            case R.id.rlSite:
                Intent d =new Intent(this,SettingSiteActivity.class);
                startActivityForResult(d,104);
                break;
            case R.id.rlMoney:
                Intent e =new Intent(this,SettingPriceActivity.class);
                startActivityForResult(e,105);
                break;
            case R.id.ivAdd:
                Intent f =new Intent(this,AddConditionActivity.class);
                startActivityForResult(f,106);
                break;
            case R.id.ivHead:
                showDialogs();
                break;
            case R.id.ivHeadBackground:
                showDialogs();
                break;
            case R.id.tvPromulgateStartTime:
                //当开始选择时间的时候，就要结束掉更新时间的线程
                task.cancel();
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .setIs24HourTime(true)
                        .build()
                        .show();
                break;
            case R.id.tvPromulgateOverTime:
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener1)
                        .setInitialDate(new Date())
                        .setIs24HourTime(true)
                        .build()
                        .show();
                break;
            case R.id.rlApply:
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener2)
                        .setInitialDate(new Date())
                        .setIs24HourTime(true)
                        .build()
                        .show();
                break;
            case R.id.tvPublish:
                setdata();
                break;
        }
    }
    private void showDialogs() {
        View view = getLayoutInflater().inflate(R.layout.dialog_phone, null);
        dialog = new Dialog(this,R.style.Dialog_Fullscreen);
        camera=(TextView)view.findViewById(R.id.camera);
        gallery=(TextView)view.findViewById(R.id.gallery);
        cancel=(TextView)view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //从相册获取
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1000);
                ivHeadBackground.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                tvAddImage.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
        //拍照
        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fileName = getPhotopath();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                File out = new File(fileName);
                Uri uri = Uri.fromFile(out);
                // 获取拍照后未压缩的原图片，并保存在uri路径中
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent,PIC_FROM_CAMERA);
                ivHeadBackground.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                tvAddImage.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
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
    }
    //开始时间选择器的确定和取消按钮的返回操作
    SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date){
            tvPromulgateStartTime.setText(mFormatter.format(date) );
        }
        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {
            Toast.makeText(PromulgateActivityActivity.this,
                    "还未选择时间哟", Toast.LENGTH_SHORT).show();
        }
    };
    //结束时间选择器的确定和取消按钮的返回操作
    SlideDateTimeListener listener1 = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            tvPromulgateOverTime.setText(mFormatter.format(date) );
        }
        // Optional cancel listener
        @Override
        public void onDateTimeCancel(){
            Toast.makeText(PromulgateActivityActivity.this,
                    "还未选择时间哟", Toast.LENGTH_SHORT).show();
        }
    };
    //报名结束时间选择器的确定和取消按钮的返回操作
    SlideDateTimeListener listener2 = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            tvSecletEndTime.setText(mFormatter.format(date) );
        }
        // Optional cancel listener
        @Override
        public void onDateTimeCancel(){
            Toast.makeText(PromulgateActivityActivity.this,
                    "还未选择时间哟", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PIC_FROM_CAMERA&&resultCode == Activity.RESULT_OK) {
            final Bitmap bitmap = PicUtil.getSmallBitmap(fileName);
            // 这里是先压缩质量，再调用存储方法
            new StorePhotosUtil(bitmap, fileName);
            if (bitmap!=null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String uri= API.BASE_URL+"/v1/upload";
                            String result=UploadUtil.uploadFile2(uri, fileName);
                            JSONObject object= null;
                            object = new JSONObject(result);
                            JSONObject data=object.getJSONObject("data");
                            String newUrl = URI.create(data.getString("url")).getPath();
                            imageUrl=newUrl;
                            /*String uri2= API.BASE_URL+"/v1/account/info/edit";
                            HashMap<String, String> params = new HashMap<>();
                            params.put("token", token);
                            params.put("account_id", id);
                            params.put("avatar", newUrl);
                            String result1 = PostUtil.sendPostMessage(uri2, params);*/
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ivHead.setImageBitmap(bitmap);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }   if (requestCode == 1000  &&  data != null){
            // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
            final ContentResolver resolver = getContentResolver();
            final   Uri originalUri = data.getData(); // 获得图片的uri
            try {
                Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                bitmap= PicUtil.compress(bitmap1, 720 , 720);
                ivHead.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
            Thread thread1=new Thread(){
                @Override
                public void run() {
                    String path=UploadUtil.getAbsoluteImagePath(PromulgateActivityActivity.this,originalUri);
                    picAddress=UploadUtil.getNetWorkImageAddress(path, PromulgateActivityActivity.this);
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
        if(requestCode==101){
            if(resultCode==11){
                num=data.getStringExtra("Number");
                if(num.length()!=0){
                    try{
                        mSelectNumber = Integer.parseInt(num);
                        tvSelectNumber.setText(mSelectNumber + " 人");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    tvSelectNumber.setText("无限制");
                }

            }
        }
        if(requestCode==102){
            if(resultCode==12){
                String s =  data.getStringExtra("Phone");
                tvSelectPhone.setText(s);
            }
        }
        if(requestCode==103){
            if(resultCode==13){
                String s =  data.getStringExtra("RadioButton");
                tvSelectRange.setText(s);
                visible =data.getIntExtra("RadiobuttonNumber",0);
            }
        }
        if(requestCode==104){
            if(resultCode==14){
                String s =  data.getStringExtra("address");
                tvActivityAddress.setText(s);
            }
        }
        if(requestCode==105){
            if(resultCode==15) {
                double s = data.getDoubleExtra("price", 0.0);
                if(s==0.0){
                    tvPriceNumber.setText("免费");
                    prices=0.0;
                }else {
                    tvPriceNumber.setText(s+"元");
                    prices=s;
                }}
        }
        if(requestCode==106){
            if(resultCode==16){
                String s=data.getStringExtra("add");
                if (s != null ) {
                    list.add(s);
                    mAddSelectAdapter = new AddSelectAdapter(this, list, R.layout.gridview_select);
                    rvPhotos.setLayoutManager(new GridLayoutManager(this, 7));
                    rvPhotos.setItemAnimator(new DefaultItemAnimator());
                    rvPhotos.setAdapter(mAddSelectAdapter);
                    mAddSelectAdapter.setOnItemClickListener(this);
                    mAddSelectAdapter.notifyItemRangeChanged(list.size(), list.size());
                    mAddSelectAdapter.setOnItemClickListener(this);
                }
            }
        }
    }
    /**
     * 路径
     * @return
     */
    private String getPhotopath() {
        // 照片全路径
        String fileName = "";
        // 文件夹路径
        String pathUrl = Environment.getExternalStorageDirectory()+"/Zyx/";
        //照片名
        String name = new DateFormat().format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".png";
        File file = new File(pathUrl);
        if (!file.exists()) {
            Log.e("TAG", "第一次创建文件夹");
            file.mkdirs();// 如果文件夹不存在，则创建文件夹
        }
        fileName=pathUrl+name;
        return fileName;
    }

    @Override
    public void OnItemClickListener(View view, int position) {

    }
}
