package com.gymnast.view.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.PostsData;
import com.gymnast.utils.PostUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.home.adapter.SearchTieZiAdapter;
import com.gymnast.view.personal.activity.PersonalPostsDetailActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cymbi on 2016/9/21.
 */
public class MorePostsActivity extends ImmersiveActivity {
    private RecyclerView recyclerview;
    private ImageView back;
    private List<PostsData> listdata=new ArrayList<>();
    public static final int HANFLE_DATA_UPDATE=1;
    SearchTieZiAdapter adapter;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANFLE_DATA_UPDATE:
                    adapter = new SearchTieZiAdapter(MorePostsActivity.this,listdata);
                    recyclerview.setAdapter(adapter);
                    adapter.setOnItemClickListener(new SearchTieZiAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClickListener(View view, int position) {
                            PostsData item=  listdata.get(position);
                            Intent i=new Intent(MorePostsActivity.this,PersonalPostsDetailActivity.class);
                            i.putExtra("item",item);
                            startActivity(i);
                        }
                    });
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_posts_activity);
        setView();
        getData();
    }

    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri= API.BASE_URL+"/v1/search/model";
                HashMap<String,String> params=new HashMap<String, String>();
                params.put("model","6");
                params.put("pageNumber","100");
                params.put("pages","1");
                String result= PostUtil.sendPostMessage(uri, params);
                Log.i("tag", "TieZi" + result);
                try {
                    JSONObject object=new JSONObject(result);
                    if (object.getInt("state")==200){
                        JSONArray data=object.getJSONArray("data");
                        for(int i=0;i<data.length();i++){
                            JSONObject obj=data.getJSONObject(i);
                            PostsData postsData=new PostsData();
                            String title=obj.getString("title");
                            long createTime= obj.getLong("createTime");
                            String content=obj.getString("content")==null|obj.getString("content").equals("null")|obj.getString("content").equals("")?"":obj.getString("content");
                            String circleId=obj.getString("circleId");
                            String from=obj.getString("circleName");
                            int zanCount=obj.getInt("zanCount");
                            int msgCount=obj.getInt("msgCount");
                            String accountTemp= StringUtil.isNullAuth(obj.getString("account"));
                            String avatar="";
                            String nickName="";
                            int createID=0;
                            if (!accountTemp.equals("")){
                                JSONObject account=new JSONObject(accountTemp);
                                nickName =account.getString("nickName");
                                avatar=account.getString("avatar");
                                createID=account.getInt("id");
                            }
                            postsData.setAvatar(avatar);
                            postsData.setNickname(nickName);
                            postsData.setCreateTime(createTime);
                            postsData.setTitle(title);
                            postsData.setCreateId(createID);
                            postsData.setContent(content);
                            postsData.setCircleId(Integer.valueOf(circleId));
                            postsData.setZanCount(zanCount);
                            postsData.setMsgCount(msgCount);
                            postsData.setCircleTitle(from);
                            listdata.add(postsData);
                        }
                    }else{
                        listdata=new ArrayList<PostsData>();
                    }
                    handler.sendEmptyMessage(HANFLE_DATA_UPDATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void setView() {
       recyclerview= (RecyclerView) findViewById(R.id.more_posts_recyclerview);
       back= (ImageView) findViewById(R.id.more_posts_back);
       back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               finish();
           }
       });
    }
}
