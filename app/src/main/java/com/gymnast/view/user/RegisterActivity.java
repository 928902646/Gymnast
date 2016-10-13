package com.gymnast.view.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.net.Result;
import com.gymnast.data.user.UserService;
import com.gymnast.data.user.UserServiceImpl;
import com.gymnast.data.user.VerifyCode;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.StorePhotosUtil;
import com.gymnast.utils.UploadUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.Calendar;
import java.util.Locale;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterActivity extends ImmersiveActivity {
  UserService userService;// = new UserServiceImpl();
  //verify
  LinearLayout registerVerify;
  EditText registerPhone;
  Button registerGetVerifyCode;
  EditText registerVerifyCode;
  TextView registerProtocol;
  Button registerNextBtn;
  //complete
  LinearLayout registerComplete;
  EditText registerNickname;
  EditText registerPassword;
  CheckBox registerCheckbox;
  Button registerCompleteBtn;
  com.makeramen.roundedimageview.RoundedImageView registerSetHead;
  int time = 60;
  private static String phone;
  private final int PIC_FROM_CAMERA = 2;
  Handler handler = new Handler() {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case 1:
          if (time >= 0) {
            registerGetVerifyCode.setText(time + " 秒后重试");
            time--;
            sendEmptyMessageDelayed(1, 1000);
          } else {
            removeMessages(1);
            time = 60;
            registerGetVerifyCode.setClickable(true);
            registerGetVerifyCode.setText(R.string.register_get_code);
          }
          break;
      }
    }
  };
  private Dialog dialog;
  private TextView camera,gallery,cancel;
  private String fileName;
  private Bitmap bitmap;
  private String avatar;
  private ImageView back;

  @Override protected void onCreate(Bundle savedInstanceState) {
    userService = new UserServiceImpl(); //DataManager.getService(UserService.class);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    setView();
    setListener();
  }
  private void setView() {
    registerVerify =(LinearLayout)findViewById(R.id.register_verify_form);
    registerGetVerifyCode =(Button)findViewById(R.id.register_get_verify_code);
    registerVerifyCode =(EditText)findViewById(R.id.register_verify_code);
    registerPhone =(EditText)findViewById(R.id.register_phone);
    registerProtocol =(TextView)findViewById(R.id.register_protocol);
    registerComplete =(LinearLayout)findViewById(R.id.register_complete_form);
    registerNickname =(EditText)findViewById(R.id.register_nickname);
    registerPassword =(EditText)findViewById(R.id.register_password);
    registerProtocol =(TextView)findViewById(R.id.register_protocol);
    registerNextBtn =(Button)findViewById(R.id.register_next_btn);
    registerCheckbox =(CheckBox)findViewById(R.id.register_checkbox);
    registerCompleteBtn =(Button)findViewById(R.id.register_complete_btn);
    back=(ImageView)findViewById(R.id.personal_back);
    registerSetHead =(com.makeramen.roundedimageview.RoundedImageView)findViewById(R.id.register_set_head);
  }
  private void setListener()  {
    back.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    registerGetVerifyCode.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        phone = registerPhone.getText().toString();
        if (phone != null && !"".equals(phone)) {
          Subscription s = userService.getVerifyCode(phone).subscribeOn(Schedulers.io())//
                  .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Result>() {
                    @Override public void onCompleted() {
                      Log.i("fldy", "===>:onCompleted");
                    }
                    @Override public void onError(Throwable e) {
                      Log.i("fldy","===>:" + e.getMessage());
                      Toast.makeText(RegisterActivity.this, "获取验证码失败：" + e.getMessage(),
                              Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onNext(Result result) {
                      if (result.state==50005){
                        Toast.makeText(RegisterActivity.this, "手机号已注册", Toast.LENGTH_SHORT).show();
                        registerNextBtn.setClickable(false);
                        registerNextBtn.setBackgroundColor(getResources().getColor(R.color.background));
                      }else
                      if (result.state == 200) {
                        Toast.makeText(RegisterActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                        registerNextBtn.setClickable(true);
                      }
                 //     Log.i("fldy", "===>:onNext" + result.toString());
                    }
                  });
          mCompositeSubscription.add(s);
          registerGetVerifyCode.setClickable(false);
          handler.sendEmptyMessage(1);
        } else {
          Toast.makeText(RegisterActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
        }
      }
    });
    registerNextBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String verifyCode = registerVerifyCode.getText().toString();
        if (verifyCode != null && !"".equals(verifyCode)) {
          if (phone != null && !"".equals(phone)) {
            Subscription s = userService.verifyPhone(phone, verifyCode).subscribeOn(Schedulers.io())//
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Result<VerifyCode>>() {
                      @Override public void onCompleted() {
                        Log.i("fldy", "===>:onCompleted");
                      }
                      @Override public void onError(Throwable e) {
                        Toast.makeText(RegisterActivity.this, "验证码不配：" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                      }
                      @Override public void onNext(Result<VerifyCode> result) {
                        if (result.state == 200) {
                          switchToRegister();
                        } else {
                          registerVerifyCode.setError("验证码不匹配");
                          registerVerifyCode.requestFocus();
                        }
                        Log.i("fldy", "===>:onNext" + result.toString());
                      }
                    });
            mCompositeSubscription.add(s);
          } else {
            Toast.makeText(RegisterActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
          }
        } else {
          Toast.makeText(RegisterActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
        }
      }
    });
    registerProtocol.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(RegisterActivity.this, RegisterProtocolActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
      }
    });
    /////////////////////////////////////////////////////////////////////////////register
    registerSetHead.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        showDialogs();
      }
    });
    registerCompleteBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String nickname = registerNickname.getText().toString();
        String pwd = registerPassword.getText().toString();
        String avatar =  registerSetHead.toString();
        if (nickname == null || "".equals(nickname)) {
          Toast.makeText(RegisterActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
          return;
        }
        if (pwd == null || "".equals(pwd)) {
          Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
          return;
        }
        if (phone == null) {
          Toast.makeText(RegisterActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
          return;
        }
        Subscription s =
                userService.register(phone, pwd, nickname, avatar).subscribeOn(Schedulers.io())//
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Result>() {
                  @Override public void onCompleted() {
                  }
                  @Override public void onError(Throwable e) {
                    Log.i("fldy","===>:" + e.getMessage());
                    Toast.makeText(RegisterActivity.this, "注册失败：" + e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                  }
                  @Override public void onNext(Result result) {
                    if (result.state == 200) {
                      Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                      startActivity(intent);
                      Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    }
                    Log.i("fldy", "===>:onNext" + result.toString());
                    if(result.state == 50005){
                      Toast.makeText(RegisterActivity.this, "手机号码已注册", Toast.LENGTH_SHORT).show();
                    }
                  }
                });
        mCompositeSubscription.add(s);
      }
    });
    registerCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          registerPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
          registerPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
      }
    });
  }
  private void switchToRegister() {
    registerVerify.setVisibility(View.GONE);
    registerComplete.setVisibility(View.VISIBLE);
  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode==PIC_FROM_CAMERA&&resultCode == Activity.RESULT_OK) {
      final Bitmap bitmap = getSmallBitmap(fileName);
      // 这里是先压缩质量，再调用存储方法
      new StorePhotosUtil(bitmap, fileName);
      if (bitmap!=null) {
        new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              String uri= API.BASE_URL+"/v1/upload";
              String result=UploadUtil.uploadFile2(uri, fileName);
              JSONObject object = new JSONObject(result);
              JSONObject data=object.getJSONObject("data");
              String newUrl = URI.create(data.getString("url")).getPath();
              avatar=newUrl;
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  registerSetHead.setImageBitmap(bitmap);
                }
              });
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        }).start();
      }
    } if (requestCode == 1000  &&  data != null){
      // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
      final ContentResolver resolver = getContentResolver();
      final   Uri originalUri = data.getData(); // 获得图片的uri
      try {
        Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(resolver, originalUri);
        bitmap= PicUtil.compress(bitmap1, 720 , 720);
        registerSetHead.setImageBitmap(bitmap);
      }catch (Exception e){
        e.printStackTrace();
      }
      Thread thread1=new Thread(){
        @Override
        public void run() {
          String path= UploadUtil.getRealFilePath(RegisterActivity.this,originalUri);
          avatar=UploadUtil.getNetWorkImageAddress(path, RegisterActivity.this);
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (avatar!=null){
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
      file.mkdirs();// 如果文件夹不存在，则创建文件夹
    }
    fileName=pathUrl+name;
    return fileName;
  }
  // 根据路径获得图片并压缩，返回bitmap用于显示
  public static Bitmap getSmallBitmap(String filePath) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;// 设置为ture,只读取图片的大小，不把它加载到内存中去
    BitmapFactory.decodeFile(filePath, options);
    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, 720, 1280);// 此处，选取了480x800分辨率的照片
    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;// 处理完后，同时需要记得设置为false
    return BitmapFactory.decodeFile(filePath, options);
  }
  // 计算图片的缩放值
  public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;
    if (height > reqHeight || width > reqWidth) {
      final int heightRatio = Math.round((float) height
              / (float) reqHeight);
      final int widthRatio = Math.round((float) width / (float) reqWidth);
      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    return inSampleSize;
  }
}
