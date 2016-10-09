package com.gymnast.view.personal.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.DialogUtil;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StorePhotosUtil;
import com.gymnast.utils.UploadUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.citypacker.CityPackerActivity;
import com.gymnast.view.wheeladapter.NumericWheelAdapter;
import com.gymnast.view.widget.OnWheelScrollListener;
import com.gymnast.view.widget.WheelView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
/**
 * Created by yf928 on 2016/7/25.
 */
public class PersonalEditDataActivity extends ImmersiveActivity implements View.OnClickListener{
    private ImageView ivEditMSGBack;
    private RelativeLayout rlEditDataName,rlEditSex,rlEditPhoto,rlEditBirthday,rlEditSignature,rlEditAddress;
    private TextView tvEditNickname,tvEditSex,tvEditSignature,tvEditBirthday,tvEditAddress,camera,gallery,cancel;
    private String token,id,fileName;
    private AlertDialog.Builder builder;
    private SharedPreferences share;
    private WheelView year,month,day;
    private final int PIC_FROM_CAMERA = 1;
    PopupWindow menuWindow;
    private Dialog cameradialog;
    private com.makeramen.roundedimageview.RoundedImageView rivEditPhoto;
    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
    private String return_birthdays, picAddress=null;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_edit_data);
        share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token","");
        id = share.getString("UserId","");
        setView();
        setData();
    }
    private void setData(){
        new Thread(new Runnable() {
            private String name,address,signature,birthday,return_avatar,sex;
            private long timebirthday;
            private int Sex;
            @Override
            public void run() {
                String uri = API.BASE_URL + "/v1/account/center_info";
                HashMap<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("account_id", id);
                String result = GetUtil.sendGetMessage(uri, params);
                if (TextUtils.isEmpty(token)) {
                } else {
                    try {
                        JSONObject obj = new JSONObject(result);
                        JSONObject data = obj.getJSONObject("data");
                        name = data.getString("nickname");
                        if(data.getString("sex")==null){
                            sex="请填写性别";
                        }else {
                            Sex=data.getInt("sex");
                        }
                        if (data.getString("avatar").equals("")){
                            return_avatar="group1/M00/00/00/052YyFfXxLKARvQWAAAbNiA-OGw444.png";
                        }else {
                            return_avatar=data.getString("avatar");
                        }
                        if(!data.getString("address").equals("")){
                            address=data.getString("address");
                        }else {
                            address="请填写地址";
                        }
                        if(data.getLong("birthday")!=0){
                            timebirthday=data.getLong("birthday");
                            return_birthdays= simpleDateFormat.format(timebirthday);
                            birthday= return_birthdays.substring(0, 10);
                        }else {
                            birthday="请填写生日";
                        }
                        if(!data.getString("signature").equals("")){
                            signature=data.getString("signature");
                        }else {
                            signature="来个签名吧~";
                        }
                        final Bitmap bitmap= PicUtil.getImageBitmap(API.IMAGE_URL + return_avatar);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvEditNickname.setText(name);
                                if (Sex==0){
                                    tvEditSex.setText("女");
                                }else  if(Sex==1){
                                    tvEditSex.setText("男");
                                }else {
                                    tvEditSex.setText(sex);
                                }
                                tvEditSignature.setText(signature);
                                tvEditAddress.setText(address);
                                tvEditBirthday.setText(birthday);
                                rivEditPhoto.setImageBitmap(bitmap);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalEditDataActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).start();
    }
    private void setView() {
        ivEditMSGBack= (ImageView)findViewById(R.id.ivEditMSGBack);
        rlEditDataName= (RelativeLayout)findViewById(R.id.rlEditDataName);
        rlEditSex= (RelativeLayout)findViewById(R.id.rlEditSex);
        rlEditAddress= (RelativeLayout)findViewById(R.id.rlEditAddress);
        rlEditSignature= (RelativeLayout)findViewById(R.id.rlEditSignature);
        rlEditBirthday= (RelativeLayout)findViewById(R.id.rlEditBirthday);
        rlEditPhoto= (RelativeLayout)findViewById(R.id.rlEditPhoto);
        tvEditNickname= (TextView)findViewById(R.id.tvEditNickname);
        tvEditSex= (TextView)findViewById(R.id.tvEditSex);
        tvEditSignature= (TextView)findViewById(R.id.tvEditSignature);
        tvEditAddress= (TextView)findViewById(R.id.tvEditAddress);
        tvEditBirthday= (TextView)findViewById(R.id.tvEditBirthday);
        rivEditPhoto= (com.makeramen.roundedimageview.RoundedImageView)findViewById(R.id.rivEditPhoto);
        ivEditMSGBack.setOnClickListener(this);
        rlEditSex.setOnClickListener(this);
        rlEditDataName.setOnClickListener(this);
        rlEditSignature.setOnClickListener(this);
        rlEditAddress.setOnClickListener(this);
        rlEditBirthday.setOnClickListener(this);
        rlEditPhoto.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }
    /**
     * 初始化popupWindow
     * @param view
     */
    private void showPopWindow(View view) {
        menuWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        menuWindow.setFocusable(true);
        menuWindow.setBackgroundDrawable(new BitmapDrawable());
        menuWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        menuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                menuWindow=null;
            }
        });
    }
    /**
     * 活动结束的监听
     */
    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }
        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = 2015;//
            int n_month = month.getCurrentItem() + 1;//
            initDay(n_year,n_month);
        }
    };
    /**
     * @param year
     * @param month
     * @return
     */
    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }
    /**
     * 初始化年
     */
    private void initYear(int curYear) {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,1950, curYear);
        numericWheelAdapter.setLabel(" 年");
        //		numericWheelAdapter.setTextSize(15);  设置字体大小
        year.setViewAdapter(numericWheelAdapter);
        year.setCyclic(true);
    }
    /**
     * 初始化月
     */
    private void initMonth() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this,1, 12, "%02d");
        numericWheelAdapter.setLabel(" 月");
        //		numericWheelAdapter.setTextSize(15);  设置字体大小
        month.setViewAdapter(numericWheelAdapter);
        month.setCyclic(true);
    }
    /**
     * 初始化天
     */
    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(this,1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel(" 日");
        //		numericWheelAdapter.setTextSize(15);  设置字体大小
        day.setViewAdapter(numericWheelAdapter);
        day.setCyclic(true);
    }
    /**
     * 显示日期
     */
    private void showDateDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(PersonalEditDataActivity.this)
                .create();
        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.datapick);
        // 设置宽高
        //   window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出的动画效果
        window.setWindowAnimations(R.style.log_out_dialog);
        dialog.setCanceledOnTouchOutside(true);
        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);
        year = (WheelView) window.findViewById(R.id.year);
        initYear(curYear);
        month = (WheelView) window.findViewById(R.id.month);
        initMonth();
        day = (WheelView) window.findViewById(R.id.day);
        initDay(curYear,curMonth);
        year.setCurrentItem(curYear - 1950);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);
        year.setVisibleItems(7);
        month.setVisibleItems(7);
        day.setVisibleItems(7);
        // 设置监听
        Button ok = (Button) window.findViewById(R.id.set);
        Button cancel = (Button) window.findViewById(R.id.cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            public String str;
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            str = (year.getCurrentItem()+1950) + "-"+ (month.getCurrentItem()+1)+"-"+(day.getCurrentItem()+1);
                            Log.e("str",str);
                            String uri= API.BASE_URL+"/v1/account/info/edit";
                            Map<String, String> params=new HashMap<>();
                            SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
                            Date date=simpleDateFormat.parse(str);
                            Long birthday_time= date.getTime();
                            Log.e("birthday_time",birthday_time+"");
                            params.put("token",token);
                            params.put("account_id",id);
                            params.put("birthday",birthday_time+"");
                            String result= PostUtil.sendPostMessage(uri,params);
                            JSONObject json =  new JSONObject(result);
                            if(json.getInt("state")==200){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvEditBirthday.setText(str);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                dialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }
    private void showDialogs() {
        View view = getLayoutInflater().inflate(R.layout.dialog_phone, null);
        cameradialog = new Dialog(this,R.style.Dialog_Fullscreen);
        camera=(TextView)view.findViewById(R.id.camera);
        gallery=(TextView)view.findViewById(R.id.gallery);
        cancel=(TextView)view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameradialog.dismiss();
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
                cameradialog.dismiss();
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
                cameradialog.dismiss();
            }
        });
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
                            String uri2= API.BASE_URL+"/v1/account/info/edit";
                            HashMap<String, String> params = new HashMap<>();
                            params.put("token", token);
                            params.put("account_id", id);
                            params.put("avatar", newUrl);
                            String result1 = PostUtil.sendPostMessage(uri2, params);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rivEditPhoto.setImageBitmap(bitmap);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        if (requestCode == 1000  &&  data != null){
            // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
            final ContentResolver resolver = getContentResolver();
            final   Uri originalUri = data.getData(); // 获得图片的uri
            try {
                Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                bitmap= PicUtil.compress(bitmap1, 720, 480);
                rivEditPhoto.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
            Thread thread1=new Thread(){
                @Override
                public void run() {
                    String path=UploadUtil.getAbsoluteImagePath(PersonalEditDataActivity.this,originalUri);
                    picAddress=UploadUtil.getNetWorkImageAddress(path, PersonalEditDataActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (picAddress!=null){
                                cameradialog.dismiss();
                            }
                        }
                    });
                }
            };
            thread1.setPriority(8);
            thread1.start();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivEditMSGBack:
                finish();
                break;
            case R.id.rlEditDataName:
                Intent a=  new Intent(PersonalEditDataActivity.this,PersonalEditNickNameActivity.class);
                startActivity(a);
                break;
            case R.id.rlEditSignature:
                Intent b=  new Intent(PersonalEditDataActivity.this,PersonalSignatureActivity.class);
                startActivity(b);
                break;
            case R.id.rlEditAddress:
                Intent c=  new Intent(PersonalEditDataActivity.this,CityPackerActivity.class);
                startActivity(c);
                break;
            case R.id.rlEditBirthday:
                showDateDialog();
                break;
            case R.id.rlEditPhoto:
                showDialogs();
                setData();
                break;
            case R.id.rlEditSex:
                builder = new AlertDialog.Builder(PersonalEditDataActivity.this);
                builder.setTitle("请选择性别");
                final String[] sexList = {"女", "男"};
                //    设置一个单项选择下拉框
                /**
                 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
                 * 第二个参数代表索引，指定默认哪一个单选框被勾选上
                 * 第三个参数给每一个单选项绑定一个监听器
                 */
                builder.setSingleChoiceItems(sexList, 0, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String uri= API.BASE_URL+"/v1/account/info/edit";
                                    Map<String, String> params=new HashMap<>();
                                    params.put("token",token);
                                    params.put("account_id",id);
                                    params.put("sex",which+"");
                                    String result= PostUtil.sendPostMessage(uri,params);
                                    Log.e("result",result);
                                    JSONObject json =  new JSONObject(result);
                                    if(json.getInt("state")==200){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tvEditSex.setText(sexList[which]);
                                                Toast.makeText(PersonalEditDataActivity.this,"您的性别是:"+sexList[which],Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }
    }
}
