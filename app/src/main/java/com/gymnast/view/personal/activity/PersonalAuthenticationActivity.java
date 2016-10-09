package com.gymnast.view.personal.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StorePhotosUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.utils.UploadUtil;
import com.gymnast.view.ImmersiveActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import butterknife.OnTextChanged;

/**
 * Created by Cymbi on 2016/9/20.
 */
public class PersonalAuthenticationActivity extends ImmersiveActivity implements View.OnClickListener{
    private EditText etName,etPhone,etId,etTag;
    private ImageView btnAddIdImage,btnAddWorkImage;
    private ImageView ivArrows;
    private boolean isOpen;
    private LinearLayout llAdvanced;
    private Dialog dialog;
    private TextView camera;
    private TextView gallery;
    private TextView cancel;
    private final int PIC_FROM_CAMERA = 2;
    private String fileName;
    private String imageUrl,imageUrls;
    private SharedPreferences share;
    private String token,id;
    private TextView text,text1,tvAuthentication;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.certification_activity);
        getInfo();
        getIsAuth();
        setView();
        initView();
    }

    private void getIsAuth() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri=API.BASE_URL+"/v1/account/auth_info";
                HashMap<String,String > params=new HashMap<String, String>();
                params.put("account_id",id);
                params.put("token",token);
                String result=GetUtil.sendGetMessage(uri,params);
                try {
                    JSONObject obj=new JSONObject(result);
                    if(obj.getInt("state")==200){
                        JSONObject data=obj.getJSONObject("data");
                        final String authName= data.getString("authName");
                        final String authIDCard= data.getString("authIDCard");
                        final String authMob= data.getString("authMob");
                        final String authfile= data.getString("authfile");
                        final String authinfo= data.getString("authinfo");
                        final String authfilework= data.getString("authfilework");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                etName.setText(authName);
                                etId.setText(authIDCard);
                                etPhone.setText(authMob);
                                etTag.setText(authinfo);
                                PicassoUtil.handlePic(PersonalAuthenticationActivity.this, PicUtil.getImageUrlDetail(PersonalAuthenticationActivity.this, StringUtil.isNullAvatar(authfile), 320, 320),btnAddIdImage,320,320);
                                PicassoUtil.handlePic(PersonalAuthenticationActivity.this, PicUtil.getImageUrlDetail(PersonalAuthenticationActivity.this, StringUtil.isNullAvatar(authfilework), 320, 320),btnAddWorkImage,320,320);
                                text.setVisibility(View.GONE);
                                text1.setVisibility(View.GONE);
                                etName.setFocusable(false);
                                etId.setFocusable(false);
                                etPhone.setFocusable(false);
                                etTag.setFocusable(false);
                                btnAddIdImage.setClickable(false);
                                btnAddWorkImage.setClickable(false);
                                tvAuthentication.setClickable(false);
                                tvAuthentication.setText("认证申请中");
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
        token = share.getString("Token","");
        id = share.getString("UserId","");
    }
    private void setView() {
        etName= (EditText)findViewById(R.id.etName);
        etId= (EditText)findViewById(R.id.etId);
        etPhone= (EditText)findViewById(R.id.etPhone);
        etTag= (EditText)findViewById(R.id.etTag);
        btnAddIdImage= (ImageView)findViewById(R.id.btnAddIdImage);
        btnAddWorkImage= (ImageView)findViewById(R.id.btnAddWorkImage);
        ivArrows= (ImageView)findViewById(R.id.ivArrows);
        text= (TextView)findViewById(R.id.text);
        text1= (TextView)findViewById(R.id.text1);
        tvAuthentication= (TextView)findViewById(R.id.tvAuthentication);
        llAdvanced= (LinearLayout)findViewById(R.id.llAdvanced);
        ivArrows.setOnClickListener(this);
        btnAddIdImage.setOnClickListener(this);
        btnAddWorkImage.setOnClickListener(this);
        tvAuthentication.setOnClickListener(this);
    }

    private void initView() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str= etName.getText().toString();
                String string = stringFilter(str.toString());
                if(!str.equals(string)){
                    etName.setText(string);
                    //设置新的光标所在位置
                    etName.setSelection(etName.getText().length());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
    private void showDialogss() {
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
                startActivityForResult(intent,1);
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
        String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".png";
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
        if (requestCode==PIC_FROM_CAMERA&&resultCode == Activity.RESULT_OK){
            bitmap = PicUtil.getSmallBitmap(fileName);
            // 这里是先压缩质量，再调用存储方法
            new StorePhotosUtil(bitmap, fileName);
            if (bitmap!=null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String uri= API.BASE_URL+"/v1/upload";
                            String result= UploadUtil.uploadFile2(uri, fileName);
                            JSONObject object = new JSONObject(result);
                            JSONObject data=object.getJSONObject("data");
                            String newUrl = URI.create(data.getString("url")).getPath();
                            imageUrl=newUrl;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnAddIdImage.setImageBitmap(bitmap);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        if (requestCode==1&&resultCode == Activity.RESULT_OK){
            bitmap = PicUtil.getSmallBitmap(fileName);
            // 这里是先压缩质量，再调用存储方法
            new StorePhotosUtil(bitmap, fileName);
            if (bitmap!=null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String uri= API.BASE_URL+"/v1/upload";
                            String result= UploadUtil.uploadFile2(uri, fileName);
                            JSONObject object = new JSONObject(result);
                            JSONObject data=object.getJSONObject("data");
                            String newUrl = URI.create(data.getString("url")).getPath();
                            imageUrls=newUrl;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnAddWorkImage.setImageBitmap(bitmap);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    public static String stringFilter(String str)throws PatternSyntaxException {
        // 只允许汉字
        String   regEx  =  "[^\u4E00-\u9FA5]";
        Pattern p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(str);
        return   m.replaceAll("").trim();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivArrows:
                if(isOpen){
                    llAdvanced.setVisibility(View.VISIBLE);
                }else {
                    llAdvanced.setVisibility(View.GONE);
                }
                isOpen=!isOpen;
                break;
            case R.id.btnAddIdImage:
                showDialogs();
                text.setVisibility(View.GONE);
                break;
            case R.id.btnAddWorkImage:
                showDialogss();
                text1.setVisibility(View.GONE);
                break;
            case R.id.tvAuthentication:
                setData();
                break;

        }
    }

    private void setData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String authName=etName.getText().toString();
                final String authIDCard=etId.getText().toString();
                final String authMob=etPhone.getText().toString();
                final String authInfo=etTag.getText().toString();
                String uri=API.BASE_URL+"/v1/account/auth_info/edit";
                HashMap<String,String> params=new HashMap<String, String>();
                params.put("token",token);
                params.put("account_id",id);
                params.put("authName",authName);
                params.put("authIDCard",authIDCard);
                params.put("authMob",authMob);
                params.put("authFile",imageUrl);
                params.put("authFileWork",imageUrls);
                params.put("authInfo",authInfo);
                String result=PostUtil.sendPostMessage(uri,params);
                try {
                    JSONObject object=new JSONObject(result);
                    if( object.getInt("state")==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalAuthenticationActivity.this,"申请提交成功，请耐性等候",Toast.LENGTH_SHORT).show();
                                SharedPreferences share=getSharedPreferences("Authentication",MODE_PRIVATE);
                                SharedPreferences.Editor etr = share.edit();
                                //通过编辑器添加数据
                                etr.putString("authName", authName);
                                etr.putString("authIDCard", authIDCard);
                                etr.putString("authMob", authMob);
                                etr.putString("authInfo", authInfo);
                                etr.putString("imageUrl", imageUrl);
                                etr.putString("imageUrls", imageUrls);
                                etr.apply();
                                finish();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}