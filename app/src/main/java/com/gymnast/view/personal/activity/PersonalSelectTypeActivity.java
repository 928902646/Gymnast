package com.gymnast.view.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.SelectType;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.personal.adapter.SelectTypeAdapter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Cymbi on 2016/8/29.
 */
public class PersonalSelectTypeActivity extends ImmersiveActivity {
    private TextView cancel;
    private ListView listview;
    private List<SelectType> list=new ArrayList<>();
    public static final int HANFLE_DATA_UPDATE=1;
    private SelectTypeAdapter adapter;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANFLE_DATA_UPDATE:
                   adapter= new SelectTypeAdapter(PersonalSelectTypeActivity.this,list);
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type);
        setview();
        gatdata();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectType item = list.get(position);
                String typename= item.getTypeName().toString();
                int typeid=item.getId();
                Intent i=new Intent();
                i.putExtra("typename",typename);
                i.putExtra("typeid",typeid);

                setResult(10,i);
                finish();
            }
        });
    }
    private void setview() {
        cancel=(TextView)findViewById(R.id.cancel);
        listview=(ListView)findViewById(R.id.listview);
    }
    private void gatdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String uri= API.BASE_URL+"/v1/circleType/getCircleTypeList";
                    HttpGet get=new HttpGet(uri);
                    HttpClient client=new DefaultHttpClient();
                    HttpResponse response= client.execute(get);
                    String result=EntityUtils.toString(response.getEntity());
                    JSONObject obj=new JSONObject(result);
                    JSONArray data = obj.getJSONArray("data");
                    if(obj.getInt("state")==200){
                        for (int i=0 ; i<data.length(); i++){
                            JSONObject object=data.getJSONObject(i);
                            String typename= object.getString("typeName");
                            SelectType type =new SelectType();
                            type.setTypeName(typename);
                            list.add(type);
                        }
                        handler.sendEmptyMessage(HANFLE_DATA_UPDATE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
