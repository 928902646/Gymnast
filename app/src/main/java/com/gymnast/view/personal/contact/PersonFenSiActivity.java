package com.gymnast.view.personal.contact;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.utils.GetUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.RefreshUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
public class PersonFenSiActivity extends ImmersiveActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ListView lvRightLetters;
    private SideBar sideBar;
    private TextView dialog;
    private FansAdapter adapter;
    private ClearEditText ceFilterEdit;
    private String token;
    private String id;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private ImageView ivFenSiBack;
    public static final int HANDLE_DATAS=1;
    ArrayList<Bitmap> bitmaps=new ArrayList<>();
    ArrayList<String> nickNames=new ArrayList<>();
    ArrayList<String> avatars=new ArrayList<>();
    ArrayList<Integer> UserIdList=new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLE_DATAS:
                    initView();
                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_attention);
        getInfo();
        setView();
        /**
         * 解决listview和swipeRefreshLayout滑动冲突问题
         */
        lvRightLetters.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(lvRightLetters != null && lvRightLetters.getChildCount() > 0){
                    // check if the first item of the list is visible
                    boolean firstItemVisible = lvRightLetters.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = lvRightLetters.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefresh.setEnabled(enable);
            }
        });
        getData();
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri= API.BASE_URL+"/v1/my/attention/to";
                HashMap<String,String> params=new HashMap<String, String>();
                params.put("token",token);
                params.put("accountId",id);
                String result= GetUtil.sendGetMessage(uri,params);
                try {
                    JSONObject object=new JSONObject(result);
                    JSONArray data=object.getJSONArray("data");
                    for (int i=0;i<data.length();i++){
                        JSONObject obj=data.getJSONObject(i);
                        String nickname=obj.getString("nickname");
                        String avatar=obj.getString("avatar");
                        int id=obj.getInt("id");
                        avatars.add(API.IMAGE_URL+avatar);
                        nickNames.add(nickname);
                        UserIdList.add(id);
                    }
                    for (int i=0;i<avatars.size();i++){
                        Bitmap bitmap= PicUtil.getImageBitmap(avatars.get(i));
                        bitmaps.add(bitmap);
                    }
                    handler.sendEmptyMessage(HANDLE_DATAS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getInfo() {
        SharedPreferences share = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        token=share.getString("Token","");
        id = share.getString("UserId","");
    }

    private void setView() {
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.tvDialog);
        lvRightLetters = (ListView) findViewById(R.id.lvRightLetters);
        ivFenSiBack = (ImageView) findViewById(R.id.ivFenSiBack);
        ceFilterEdit = (ClearEditText) findViewById(R.id.ceFilterEdit);
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        RefreshUtil.refresh(swipeRefresh,this);
        swipeRefresh.setOnRefreshListener(this);
    }
    public void initView() {
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBar.setTextView(dialog);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    lvRightLetters.setSelection(position);
                }
            }
        });
        lvRightLetters.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(PersonFenSiActivity.this, PersonalOtherHomeActivity.class);
                SortModel sortModel=SourceDateList.get(position);
                i.putExtra("UserID",sortModel.getId());
                startActivity(i);
            }
        });
        SourceDateList = filledData(nickNames);
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new FansAdapter(this, SourceDateList, FansAdapter.TYPE_CONCERN);
        lvRightLetters.setAdapter(adapter);
        // 根据输入框输入值的改变来过滤搜索
        ceFilterEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ivFenSiBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * 为ListView填充数据
     *
     * @return
     */
    private List<SortModel> filledData(ArrayList<String> nickNames) {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        for (int i = 0; i < nickNames.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setSmallPhoto(bitmaps.get(i));
            sortModel.setName(nickNames.get(i));
            sortModel.setId(UserIdList.get(i));
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(nickNames.get(i));
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        }
        return mSortList;
    }
    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    @Override
    public void onRefresh() {
        if(SourceDateList.size()!=0){
            SourceDateList.clear();
            initView();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 停止刷新
                    swipeRefresh.setRefreshing(false);
                }
            }, 1000);
        }else {}
    }
}