package com.gymnast.view.pack.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PostUtil;
import com.gymnast.view.BaseToolbarActivity;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.dialog.PicSelectDialogUtils;
import com.gymnast.view.pack.adapter.PhotoPickerAdapter;
import com.gymnast.view.recyclerview.RecyclerAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.BindView;
/**
 * Created by yf928 on 2016/8/10.
 */
public class PlayActivityIssueActivity extends ImmersiveActivity implements RecyclerAdapter.OnItemClickListener ,View.OnClickListener{
    private ArrayList<Bitmap> mPics = new ArrayList<>();
    private PhotoPickerAdapter mPhotoPickerAdapter;
    private Bitmap mDefaultBitmap;
    private int visible,type=3;
    private SharedPreferences share;
    private String token,id,cernTitle="null";
    private RecyclerView rvIssue;
    private TextView tvIssueSelectRange;
    private TextView tvIssueEdit;
    private EditText etIssueContent;
    private RelativeLayout rlIssueRange;
    private TextView save;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_issue);
        initData();
        initViews();
    }
    private void initViews() {
        initPhotePicker();
        initEditText();
    }
    /**
     * 检测输入框字数
     */
    private void initEditText() {
        etIssueContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = etIssueContent.getText().toString();
                tvIssueEdit.setText(number.length()+"/"+2500);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initData() {
        share= getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token = share.getString("Token","");
        id = share.getString("UserId","");
        rvIssue=(RecyclerView)findViewById(R.id.rvIssue);
        tvIssueSelectRange=(TextView)findViewById(R.id.tvIssueSelectRange);
        tvIssueEdit=(TextView)findViewById(R.id.tvIssueEdit);
        etIssueContent=(EditText)findViewById(R.id.etIssueContent);
        rlIssueRange=(RelativeLayout)findViewById(R.id.rlIssueRange);
        save=(TextView)findViewById(R.id.tvSetPhoneSave);
        back=(ImageView)findViewById(R.id.personal_back);
        rlIssueRange.setOnClickListener(this);
        save.setOnClickListener(this);
        back.setOnClickListener(this);
    }
    /**
     * 初始化照片选择器
     */
    private void initPhotePicker() {
        //默认显示图片
        mDefaultBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_add_to);
        mPics.add(mDefaultBitmap);
        mPhotoPickerAdapter = new PhotoPickerAdapter(this, mPics, R.layout.item_photo_picker);
        rvIssue.setLayoutManager(new GridLayoutManager(this, 7));
        rvIssue.setItemAnimator(new DefaultItemAnimator());
        rvIssue.setAdapter(mPhotoPickerAdapter);
        mPhotoPickerAdapter.setOnItemClickListener(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PicSelectDialogUtils.IMAGE_OPEN:
                    //相册返回
                    addPic();
                    break;
                case PicSelectDialogUtils.PIC_FROM_CAMERA:
                    addPic();
                    break;
            }
        }
        if(requestCode==100){
            if(resultCode==10){
                String s =  data.getStringExtra("RadioButton");
                tvIssueSelectRange.setText(s);
                visible =data.getIntExtra("RadiobuttonNumber",0);
            }
        }
    }
    /**
     * 添加图片
     */
    private void addPic() {
        Bitmap bitmap = PicUtil.getSmallBitmap(PicSelectDialogUtils.CURRENT_FILENAME);
        if (bitmap != null && mPics.size() > 0) {
            mPics.remove(mPics.size() - 1);
            mPics.add(bitmap);
            mPics.add(mDefaultBitmap);
            mPhotoPickerAdapter.notifyItemRangeChanged(mPics.size() - 2, mPics.size());
        }
    }
    @Override
    public void OnItemClickListener(View view, int position) {
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rlIssueRange:
                Intent i=new Intent(this,PackRangeActivity.class);
                startActivityForResult(i,100);
                break;
            case R.id.tvSetPhoneSave:
               update();
                break;
            case R.id.personal_back:
                finish();
                break;
        }
    }
    private void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                String content = etIssueContent.getText().toString();
                String uri= API.BASE_URL+"/v1/cern/insert";
                HashMap<String ,String> params=new HashMap<String, String>();
                params.put("userId",id);
                params.put("type",type+"");
                params.put("cernTitle",cernTitle);
                params.put("content",content);
                params.put("visible",visible+"");
                String reasult= PostUtil.sendPostMessage(uri,params);
                    JSONObject obj=new JSONObject(reasult);
                    if(obj.getInt("state")==200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PlayActivityIssueActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PlayActivityIssueActivity.this,"发布失败 ",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
