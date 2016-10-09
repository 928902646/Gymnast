package com.gymnast.view.personal.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.PostUtil;
import com.gymnast.view.ImmersiveActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import android.app.AlertDialog.Builder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yf928 on 2016/8/4.
 */
public class PersonalCirclePublishActivity extends ImmersiveActivity implements View.OnClickListener{
    private ImageView back,post;
    private EditText et_post_title,et_post_content;
    private Handler mHandler = new Handler();
    private ScrollView mScrollView;
    private Dialog dialog;
    private GridView gridView1;
    private TextView tvPublish,cancel,camera,gallery;
    private final int PIC_FROM_CAMERA = 1;
    private final int IMAGE_OPEN = 0;
    private Bitmap bmp;
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter;
    private String pathImage;                       //选择图片路径
    private File file;
    private SharedPreferences share;
    private String token,id;
    private int circle_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_publish_post);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_ADJUST_PAN);
        //锁定屏幕
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getInfo();
        setView();
        initView();
        setdata();
    }
    public void getInfo() {
        share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token=share.getString("Token","");
        id = share.getString("UserId","");
        circle_id=getIntent().getIntExtra("circle_id",0);
    }
    private void setdata() {
        bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_add_to); //加号
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int size = imageItem.size();//要显示数据的个数
        int allWidth = (int) (71 * size * density);//gridview的layout_widht,要比每个item的宽度多出2个像素
        int itemWidth = (int) (69 * density);//每个item宽度
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(allWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView1.setLayoutParams(params);
        gridView1.setColumnWidth(itemWidth);
        gridView1.setStretchMode(GridView.NO_STRETCH);
        gridView1.setNumColumns(size);
        simpleAdapter = new SimpleAdapter(this,
                imageItem, R.layout.item_gridview_publish,
                new String[] { "itemImage"}, new int[] { R.id.imageView1});
		/*
		 * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如
		 * map.put("itemImage", R.drawable.img);
		 * 解决方法:
		 *              1.自定义继承BaseAdapter实现
		 *              2.ViewBinder()接口实现
		 *  参考 http://blog.csdn.net/admin_/article/details/7257901
		 */
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView1.setAdapter(simpleAdapter);
		/*
		 * 监听GridView点击事件
		 * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
		 */
        gridView1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                if( imageItem.size() == 10) { //第一张为默认图片
                    Toast.makeText(PersonalCirclePublishActivity.this, "图片数9张已满", Toast.LENGTH_SHORT).show();
                }
                else if(position == 0) { //点击图片位置为+ 0对应0张图片
                    Toast.makeText(PersonalCirclePublishActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                    //选择图片
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                    //通过onResume()刷新数据
                }
                else{
                    dialog(position);
                    //Toast.makeText(MainActivity.this, "点击第" + (position + 1) + " 号图片",
                    //		Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void setView() {
        back=(ImageView)  findViewById(R.id.personal_back);
        post=(ImageView)  findViewById(R.id.post_image);
        et_post_title=(EditText)  findViewById(R.id.et_post_title);
        et_post_content=(EditText)  findViewById(R.id.et_post_content);
        tvPublish=(TextView)  findViewById(R.id.tvPublish);
        et_post_content.setSelection(et_post_content.getText().length());
        mScrollView=(ScrollView)  findViewById(R.id.scrollView);
        gridView1= (GridView) findViewById(R.id.gridview);
        gridView1.setVisibility(View.GONE);
        tvPublish.setOnClickListener(this);
    }
    private void initView() {
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogs();
            }
        });
    }
    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new Builder(PersonalCirclePublishActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                if(position==1){
                    gridView1.setVisibility(View.GONE);
                }
                simpleAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                    dialog.dismiss();
                    gridView1.setVisibility(View.VISIBLE);
                }
            });
        /*    //拍照
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, PIC_FROM_CAMERA);
                    dialog.dismiss();
                    gridView1.setVisibility(View.VISIBLE);
                }
            });*/
            dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开图片
        if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[]{MediaStore.Images.Media.DATA},
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
                    return;
                }
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                pathImage = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }  //end if 打开图片
        if (resultCode == RESULT_OK && requestCode==PIC_FROM_CAMERA ){
            String sdStatus = Environment.getExternalStorageState();
            Uri uri = data.getData();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.i("TestFile",
                        "SD card is not avaiable/writeable right now.");
                return;
            }
            String name = new DateFormat().format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";
            Toast.makeText(this, name, Toast.LENGTH_LONG).show();
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
            FileOutputStream b = null;
            //???????????????????????????????为什么不能直接保存在系统相册位置呢？？？？？？？？？？？？
            file = new File("/sdcard/Tyj/");
            file.mkdirs();// 创建文件夹
            String fileName = "/sdcard/Tyj/"+name;
            try {
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //刷新图片
    @Override
    protected void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(pathImage)){
            Bitmap addbmp=BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            imageItem.add(map);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            float density = dm.density;
            int size = imageItem.size();//要显示数据的个数
            int allWidth = (int) (82 * size * density);//gridview的layout_widht,要比每个item的宽度多出2个像素
            int itemWidth = (int) (80 * density);//每个item宽度
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(allWidth, LinearLayout.LayoutParams.MATCH_PARENT);
            gridView1.setLayoutParams(params);
            gridView1.setColumnWidth(itemWidth);
            gridView1.setHorizontalSpacing(3);
            gridView1.setStretchMode(GridView.NO_STRETCH);
            gridView1.setNumColumns(size);
            simpleAdapter = new SimpleAdapter(this,
                    imageItem, R.layout.item_gridview_publish,
                    new String[] { "itemImage"}, new int[] { R.id.imageView1});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if(view instanceof ImageView && data instanceof Bitmap){
                        ImageView i = (ImageView)view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            gridView1.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvPublish:
                if(et_post_title.getText().equals("")){
                     Toast.makeText(PersonalCirclePublishActivity.this,"请填写标题",Toast.LENGTH_SHORT).show();
                }else if (et_post_content.getText().equals("")){
                    Toast.makeText(PersonalCirclePublishActivity.this,"请填写内容",Toast.LENGTH_SHORT).show();
                }else {
                    Posts();
                }
                break;
        }
    }

    private void Posts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri= API.BASE_URL+"/v1/circleItem/add";
                HashMap<String,String> params=new HashMap<String, String>();
                String title=et_post_title.getText().toString();
                String content=et_post_content.getText().toString();
                params.put("token",token);
                params.put("circle_id",circle_id+"");
                params.put("create_id",id);
                params.put("title",title);
                params.put("content",content);
                String result= PostUtil.sendPostMessage(uri,params);
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    if (jsonObject.getInt("state")==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalCirclePublishActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PersonalCirclePublishActivity.this,"服务器故障",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        }).start();
    }
}
