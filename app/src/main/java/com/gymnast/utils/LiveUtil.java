package com.gymnast.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.gymnast.App;
import com.gymnast.MyReceiver;
import com.gymnast.data.net.API;
import com.gymnast.view.live.activity.LiveActivity;
import com.gymnast.view.live.entity.LiveItem;
import com.gymnast.view.user.LoginActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;

import org.json.JSONObject;

import java.util.HashMap;
/**
 * Created by zzqybyb19860112 on 2016/8/19.
 */
public class LiveUtil {
    public static final int UPDATE_STATE_OK = 1;
    public static final int MAINUSER_IN_OK = 2;
    public static final int MAINUSER_IN_ERROR = 3;
    public static final int OTHERUSER_IN_OK = 4;
    public static final int OTHERUSER_IN_ERROR = 5;

    /**
     * url 请求地址
     * token 系统token
     * liveId 请求用的直播Id
     * imgUrl发送图片消息时上传到服务器返回的地址，可以只发图片
     * content 图文直播的文字内容
     */
    public static String sendLiveMessage(final String url, final String token, final int liveId, final String imgUrl, final String content) {
        String result = null;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("liveId", liveId + "");
        params.put("imgUrl", imgUrl);
        params.put("content", content);
        result = PostUtil.sendPostMessage(url, params);
        return result;
    }
    public static void doNext(Activity activity, LiveItem live) {
        Intent intent = new Intent(activity, LiveActivity.class);
        intent.putExtra("type", live.getUserType());
        intent.putExtra("bigPictureUrl", PicUtil.getImageUrl(activity, live.getBigPictureUrl()));
        intent.putExtra("liveId", live.getLiveId());
        intent.putExtra("title", live.getTitle());
        intent.putExtra("groupId", live.getGroupId());
        intent.putExtra("mainPhotoUrl", PicUtil.getImageUrl(activity, live.getMainPhotoUrl()));
        intent.putExtra("liveOwnerId", live.getLiveOwnerId());
        intent.putExtra("liveState",live.getLiveState());
        activity.startActivity(intent);
    }
    public static void doIntoLive(Activity activity, final Handler handler, final LiveItem liveItem) {
        final SharedPreferences share = activity.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        final String userId = share.getString("UserId", "");
        final String token = share.getString("Token", "");
        if (App.isNetStateOK){
        if (token.equals("")||! App.isStateOK) {
            Toast.makeText(activity, "亲，您还没有登陆呢！", Toast.LENGTH_SHORT).show();
            activity.startActivity(new Intent(activity, LoginActivity.class));
        } else {
            if (liveItem.getUserType() == LiveActivity.USER_MAIN) {//播主进入界面
                long nowTime = System.currentTimeMillis();
                if (nowTime < liveItem.getStartTime()) {
                    Toast.makeText(activity, "直播时间未到，您不能提前开始直播！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (liveItem.getLiveState() == 0) {
                        if (liveItem.getGroupId() == null || liveItem.getGroupId().equals("")) {
                            new Thread() {
                                @Override
                                public void run() {
                                    EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                                    option.maxUsers = 1000;
                                    option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                                    String[] allMembers = new String[]{};
                                    EMGroup group = null;
                                    try {
                                        group = EMClient.getInstance().groupManager().createGroup(liveItem.liveOwnerId + "", liveItem.getTitle(), allMembers, null, option);
                                        if (group != null) {
                                            Log.i("tag", "创建环信群组成功");
                                            String groupId = group.getGroupId();
                                            String url2 = API.BASE_URL + "/v1/live/update_live";
                                            HashMap<String, String> params2 = new HashMap<>();
                                            params2.put("token", token);
                                            params2.put("id", liveItem.liveOwnerId + "");
                                            params2.put("groupId", groupId);
                                            liveItem.setGroupId(groupId);
                                            Log.i("tag", "groupId========" + groupId);
                                            String result2 = PostUtil.sendPostMessage(url2, params2);
                                            Log.i("tag", "result2------------->" + result2);
                                            String url3 = API.BASE_URL + "/v1/live/update_status";
                                            HashMap<String, String> params3 = new HashMap<>();
                                            params3.put("token", token);
                                            params3.put("id", userId + "");
                                            params3.put("status", 1 + "");
                                            String result3 = PostUtil.sendPostMessage(url3, params3);
                                            Log.i("tag", "result3------------->" + result3);
                                            JSONObject obj = new JSONObject(result3);
                                            if (obj.getInt("state") == 200) {//用户状态正常
                                                handler.sendEmptyMessage(UPDATE_STATE_OK);
                                            }
                                        } else {
                                            Log.i("tag", "创建环信群组失败");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        } else {
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        String url = API.BASE_URL + "/v1/live/update_status";
                                        HashMap<String, String> param = new HashMap<>();
                                        param.put("token", token);
                                        param.put("id", liveItem.getLiveId() + "");
                                        param.put("status", 1 + "");
                                        String result1 = PostUtil.sendPostMessage(url, param);
                                        Log.i("tag", "更新直播状态结果：" + result1);
                                        JSONObject obj = new JSONObject(result1);
                                        if (obj.getInt("state") == 200) {//用户状态正常
                                            handler.sendEmptyMessage(UPDATE_STATE_OK);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }
                    } else {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    String url = API.BASE_URL + "/v1/live/in";
                                    HashMap<String, String> param = new HashMap<>();
                                    param.put("token", token);
                                    param.put("id", userId);
                                    String result = PostUtil.sendPostMessage(url, param);
                                    JSONObject object = new JSONObject(result);
                                    if (object.getInt("state") == 200) {
                                        handler.sendEmptyMessage(MAINUSER_IN_OK);
                                    } else {
                                        handler.sendEmptyMessage(MAINUSER_IN_ERROR);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
            } else {//普通观众进入界面
                    if (liveItem.getGroupId() == null || liveItem.getGroupId().equals("")) {
                        Toast.makeText(activity, "播主还未开启直播！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    String url = API.BASE_URL + "/v1/live/in";
                                    HashMap<String, String> param = new HashMap<>();
                                    param.put("token", token);
                                    param.put("id", userId);
                                    String result = PostUtil.sendPostMessage(url, param);
                                    JSONObject object = new JSONObject(result);
                                    if (object.getInt("state") == 200) {
                                        handler.sendEmptyMessage(OTHERUSER_IN_OK);
                                    } else {
                                        handler.sendEmptyMessage(OTHERUSER_IN_ERROR);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
        }
        }else {
            Toast.makeText(activity, "亲，没有网络啦！", Toast.LENGTH_SHORT).show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyReceiver.dialog.show();
                }
            }, 3000);
        }
    }
}
