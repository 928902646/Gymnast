package com.gymnast.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.gymnast.App;
import com.gymnast.data.net.API;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by zzqybyb19860112 on 2016/9/22.
 */
public class CollectUtil {
    public static final int TO_COLLECT=111111;
    public static final int CANCEL_COLLECT=222222;
    public static final int ERROR=333333;
    public  static void toCollect(Context context, final Handler handler, final int modelID,final TextView tvLove){
        SharedPreferences share=context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        final String token=share.getString("Token", "");
        if (token==null|| !App.isStateOK||token.equals("")){
            Toast.makeText(context, "您还没有登陆呢！", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                try{
                    String uri= API.BASE_URL+"/v1/collect/create";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("token",token);
                    params.put("model","1");
                    params.put("modelId",modelID+"");
                    String result= PostUtil.sendPostMessage(uri,params);
                    JSONObject  object=new JSONObject(result);
                    Message message=new Message();
                    message.obj=tvLove;
                    if (object.getInt("state")==200){
                        message.what=TO_COLLECT;
                        handler.sendMessage(message);
                    }else {
                        message.what=ERROR;
                        handler.sendMessage(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void goToCollect(final Handler handler, final String token, final int model, final int modelID){
        new Thread(){
            @Override
            public void run() {
                try{
                    String uri= API.BASE_URL+"/v1/collect/create";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("token",token);
                    params.put("model",model+"");
                    params.put("modelId",modelID+"");
                    String result= PostUtil.sendPostMessage(uri,params);
                    JSONObject  object=new JSONObject(result);
                    if (object.getInt("state")==200){
                        handler.sendEmptyMessage(TO_COLLECT);
                    }else {
                        handler.sendEmptyMessage(ERROR);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void goToCancelCollect(final Handler handler, final String token,final int model, final int modelID){
        new Thread(){
            @Override
            public void run() {
                try{
                    String uri= API.BASE_URL+"/v1/collect/concel";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("token",token);
                    params.put("model",model+"");
                    params.put("modelId",modelID+"");
                    String result= PostUtil.sendPostMessage(uri,params);
                    JSONObject  object=new JSONObject(result);
                    if (object.getInt("state")==200){
                        handler.sendEmptyMessage(CANCEL_COLLECT);
                    }else {
                        handler.sendEmptyMessage(ERROR);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
