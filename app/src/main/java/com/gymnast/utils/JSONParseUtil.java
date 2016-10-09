package com.gymnast.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.JsonObject;
import com.gymnast.data.hotinfo.NewActivityItemDevas;
import com.gymnast.data.net.API;
import com.gymnast.data.personal.CircleData;
import com.gymnast.data.personal.DynamicData;
import com.gymnast.data.personal.PostsData;
import com.gymnast.data.user.SearchUserEntity;
import com.gymnast.view.live.activity.LiveActivity;
import com.gymnast.view.live.entity.LiveItem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzqybyb19860112 on 2016/9/24.12343122232
 */
public class JSONParseUtil {
    public static void parseNetDataSearchTieZi(Context context,String result,String name, List<PostsData> dataList,Handler handler,int state){
        CacheUtils.writeJson(context, result, name, false);
        Log.i("tag", "writeToCache-------->" + name + "----result=" + result);
        handleDataTieZi(result, dataList, handler, state);
    }
    private static void handleDataTieZi(String result,List<PostsData> dataList,Handler handler,int state){
        try {
            if (dataList.size()!=0){
                dataList.clear();
            }
            JSONObject object=new JSONObject(result);
            if (object.getInt("state")==200){
                JSONArray data=object.getJSONArray("data");
                for(int i=0;i<data.length();i++){
                    JSONObject obj=data.getJSONObject(i);
                    PostsData postsData=new PostsData();
                    String title=obj.getString("title");
                    int id=obj.getInt("id");
                    long createTime= obj.getLong("createTime");
                    String content=obj.getString("content")==null|obj.getString("content").equals("null")|obj.getString("content").equals("")?"":obj.getString("content");
                    String circleId=obj.getString("circleId");
                    String from=obj.getString("circleName");
                    int zanCount=obj.getInt("zanCount");
                    int msgCount=obj.getInt("msgCount");
                    String accountTemp=StringUtil.isNullAuth(obj.getString("account"));
                    String avatar="";
                    String nickName="";
                    int createID=0;
                    if (!accountTemp.equals("")){
                        JSONObject account=new JSONObject(accountTemp);
                        nickName =account.getString("nickName");
                        avatar=account.getString("avatar");
                        createID=account.getInt("id");
                    }
                    JSONObject account= obj.getJSONObject("account");
                    String authTmp=account.getString("auth");
                    int userid=0;
                    if (authTmp!=null&&!authTmp.equals("")&&!authTmp.equals("null")){
                        JSONObject auth=new JSONObject(authTmp);
                        userid=auth.getInt("id");
                    }
                    postsData.setAvatar(avatar);
                    postsData.setUserid(userid);
                    postsData.setId(id);
                    postsData.setNickname(nickName);
                    postsData.setCreateTime(createTime);
                    postsData.setTitle(title);
                    postsData.setCreateId(createID);
                    postsData.setContent(content);
                    postsData.setCircleId(Integer.valueOf(circleId));
                    postsData.setZanCount(zanCount);
                    postsData.setMsgCount(msgCount);
                    postsData.setCircleTitle(from);
                    dataList.add(postsData);
                }
            }else{
                dataList=new ArrayList<PostsData>();
            }
            handler.sendEmptyMessage(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void parseLocalDataSearchTieZi(Context context,String name, List<PostsData> dataList,Handler handler,int state){
        List<String> cacheData= (ArrayList<String>) CacheUtils.readJson(context,name);
        String result = cacheData.get(0);
        Log.i("tag","readFromCache-------->"+name+"----result="+result);
        handleDataTieZi(result, dataList, handler, state);
    }
    public static void parseNetDataSearchLive(Context context,String result,String name, List<LiveItem> dataList,Handler handler,int state){
        CacheUtils.writeJson(context, result, name, false);
        Log.i("tag", "writeToCache-------->" + name + "----result=" + result);
        handleDataLive(context, result, dataList, handler, state);
    }
    private static void handleDataLive(Context context,String result,List<LiveItem> dataList,Handler handler,int handleState)  {
        try {
            if (dataList.size() != 0) {
                dataList.clear();
            }
            JSONObject json = new JSONObject(result);
            final SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            final String userId = share.getString("UserId", "");
            if (json.getInt("state") == 200) {
                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    LiveItem live = new LiveItem();
                    String imageUrl = StringUtil.isNullImageUrl(object.getString("bgmUrl"));
                    String title = object.getString("title");
                    String groupId = StringUtil.isNullGroupId(object.getString("groupId"));
                    int liveId = object.getInt("id");
                    long startTime = object.getLong("createTime");
                    int state = object.getInt("state");
                    JSONObject account = object.getJSONObject("account");
                    String authTemp = StringUtil.isNullAuth(account.getString("auth"));
                    String authInfoTemp = "";
                    if (authTemp != null && !authTemp.equals("")) {
                        JSONObject auth = new JSONObject(authTemp);
                        authInfoTemp = auth.getString("authinfo");
                    }
                    String nickName = account.getString("nickName");
                    String avatar = StringUtil.isNullAvatar(account.getString("avatar"));
                    String liveOwnerId = account.getString("id");
                    int number = object.getString("number") == null ? 0 : object.getInt("number");
                    live.setUserType(liveOwnerId.equals(userId) ? LiveActivity.USER_MAIN : LiveActivity.USER_OTHER);
                    live.setBigPictureUrl(imageUrl);
                    live.setLiveId(liveId);
                    live.setTitle(title);
                    live.setGroupId(groupId);
                    live.setMainPhotoUrl(API.IMAGE_URL + avatar);
                    live.setCurrentNum(number);
                    live.setLiveOwnerId(liveOwnerId);
                    live.setStartTime(startTime);
                    live.setAuthInfo(authInfoTemp);
                    live.setNickName(nickName);
                    live.setLiveState(state);
                    dataList.add(live);
                }
            } else {
                dataList = new ArrayList<LiveItem>();
            }
            handler.sendEmptyMessage(handleState);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void parseLocalDataSearchLive(Context context,String name, List<LiveItem> dataList,Handler handler,int state){
        List<String> cacheData= (ArrayList<String>) CacheUtils.readJson(context,name);
        String result = cacheData.get(0);
        Log.i("tag","readFromCache-------->"+name+"----result="+result);
        handleDataLive(context, result, dataList, handler, state);
    }
    public static void parseNetDataSearchDynamic(Context context,String result,String name, List<DynamicData> dataList,Handler handler,int state){
        CacheUtils.writeJson(context, result, name, false);
        Log.i("tag", "writeToCache-------->" + name + "----result=" + result);
        handleDataDynamic(result, dataList, handler, state);
    }
    private static void handleDataDynamic(String result,List<DynamicData> dataList,Handler handler,int state)  {
        try {
            if (dataList.size() != 0) {
                dataList.clear();
            }
            JSONObject json = new JSONObject(result);
            if (json.getInt("state") == 200) {
                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    ArrayList<String> imageURL = new ArrayList<String>();
                    JSONObject object = data.getJSONObject(i);
                    int dynamicId = object.getInt("id");//动态的id
                    String topicContent = object.getString("topicContent");
                    int zanCount = object.getInt("zanCount");
                    int msgCount = object.getInt("msgCount");
                    long createTime = object.getLong("createTime");
                    String urls = object.getString("imageUrl");
                    if (urls == null | urls.equals("null") | urls.equals("")) {
                        imageURL.add(StringUtil.isNullImageUrl(""));
                    } else {
                        if (urls.contains(",")) {
                            String[] imageUrls = urls.split(",");
                            for (int j = 0; j < imageUrls.length; j++) {
                                imageURL.add(API.IMAGE_URL + imageUrls[j]);
                            }
                        } else {
                            imageURL.add(API.IMAGE_URL + urls);
                        }
                    }
                    JSONObject account = object.getJSONObject("account");
                    int userid = account.getInt("id");
                    String nickName = account.getString("nickName");
                    String avatar = StringUtil.isNullAvatar(account.getString("avatar"));
                    int authenticate = account.getInt("authenticate");
                    String authTemp = StringUtil.isNullAuth(account.getString("auth"));
                    String authinfo = "";
                    if (!authTemp.equals("")) {
                        JSONObject auth = new JSONObject(authTemp);
                        authinfo = auth.getString("authinfo");
                    }
                    int pageViews = object.getInt("pageviews");
                    DynamicData data1 = new DynamicData();
                    data1.setId(dynamicId);
                    data1.setAuthenticate(authenticate);
                    data1.setImgUrl(imageURL);
                    data1.setNickName(nickName);
                    data1.setTopicContent(topicContent);
                    data1.setAvatar(avatar);
                    data1.setCommentCounts(msgCount);
                    data1.setZanCounts(zanCount);
                    data1.setCreateTime(createTime);
                    data1.setUserId(userid);
                    data1.setAuthInfo(authinfo);
                    data1.setPageviews(pageViews);
                    dataList.add(data1);
                }
            } else {
                dataList = new ArrayList<DynamicData>();
            }
            handler.sendEmptyMessage(state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void parseLocalDataSearchDynamic(Context context,String name, List<DynamicData> dataList,Handler handler,int state){
        List<String> cacheData= (ArrayList<String>) CacheUtils.readJson(context,name);
        String result = cacheData.get(0);
        Log.i("tag","readFromCache-------->"+name+"----result="+result);
        handleDataDynamic(result, dataList, handler, state);
    }
    public static void parseNetDataSearchCircle(Context context,String result,String name, List<CircleData> dataList,Handler handler,int state){
        CacheUtils.writeJson(context, result, name, false);
        Log.i("tag", "writeToCache-------->" + name + "----result=" + result);
        handleDataCircle(result, dataList, handler, state);
    }
    private static void handleDataCircle(String result,List<CircleData> dataList,Handler handler,int state) {
        try {
            if (dataList.size() != 0) {
                dataList.clear();
            }
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("state") == 200) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    CircleData circleData = new CircleData();
                    int id = object.getInt("id");
                    String title = object.getString("title");
                    circleData.setConcerned(object.getBoolean("atten"));
                    circleData.setTitle(title);
                    circleData.setId(id);
                    String headImgUrl = StringUtil.isNullAvatar(object.getString("headImgUrl"));
                    String accountTemp = StringUtil.isNullAuth(object.getString("account"));
                    int circleItemCount = 0;
                    if (!accountTemp.equals("")) {
                        JSONObject account = new JSONObject(accountTemp);
                        circleItemCount = account.getInt("authenticate");
                        String authTemp = StringUtil.isNullAuth(account.getString("auth"));
                        if (!authTemp.equals("") && !authTemp.equals("null")) {
                            JSONObject auth = new JSONObject(authTemp);
                            circleData.setDetails(auth.getString("authinfo"));
                        } else {
                            circleData.setDetails("");
                        }
                    } else {
                        circleData.setDetails("");
                    }
                    circleData.setHeadImgUrl(headImgUrl);
                    circleData.setCircleItemCount(circleItemCount);
                    dataList.add(circleData);
                }
            } else {
                dataList = new ArrayList<CircleData>();
            }
            handler.sendEmptyMessage(state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void parseLocalDataSearchCircle(Context context,String name, List<CircleData> dataList,Handler handler,int state){
        List<String> cacheData= (ArrayList<String>) CacheUtils.readJson(context,name);
        String result=cacheData.get(0);
        Log.i("tag","readFromCache-------->"+name+"----result="+result);
        handleDataCircle(result, dataList, handler, state);
    }
    public static void parseNetDataSearchActive(Context context,String result,String name, List<NewActivityItemDevas> dataList,Handler handler,int state){
        CacheUtils.writeJson(context, result, name, false);
        Log.i("tag", "writeToCache-------->" + name + "----result=" + result);
        handleDataActive(result, dataList, handler, state);
    }
    private static void handleDataActive(String result,List<NewActivityItemDevas> dataList,Handler handler,int state)  {
        try {
            if (dataList.size() != 0) {
                dataList.clear();
            }
            JSONObject json = new JSONObject(result);
            if (json.getInt("state") == 200) {
                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    NewActivityItemDevas itemDevas = new NewActivityItemDevas();
                    int id = object.getInt("id");
                    String title = object.getString("title");
                    String imgUrls = object.getString("imgUrls");
                    long startTime = object.getLong("startTime");
                    int zanCount = object.getInt("zanCount");
                    int msgCount = object.getInt("msgCount");
                    JSONObject objectUser = object.getJSONObject("user");
                    String nickname = objectUser.getString("nickname");
                    int userID = objectUser.getInt("id");
                    itemDevas.setActiveId(id);
                    itemDevas.setTitle(title);
                    itemDevas.setImgUrls(imgUrls);
                    itemDevas.setStartTime(startTime);
                    itemDevas.setZanCount(zanCount);
                    itemDevas.setMsgCount(msgCount);
                    itemDevas.setNickname(nickname);
                    itemDevas.setUserID(userID);
                    itemDevas.setPageViews(object.getInt("pageviews"));
                    dataList.add(itemDevas);
                }
            } else {
                dataList = new ArrayList<NewActivityItemDevas>();
            }
            handler.sendEmptyMessage(state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void parseLocalDataSearchActive(Context context,String name, List<NewActivityItemDevas> dataList,Handler handler,int state){
        List<String> cacheData= (ArrayList<String>) CacheUtils.readJson(context,name);
        String result=cacheData.get(0);
        Log.i("tag","readFromCache-------->"+name+"----result="+result);
        handleDataActive(result, dataList, handler, state);
    }
    public static void parseNetDataSearchUser(Context context,String result,String name, List<SearchUserEntity> dataList,Handler handler,int state){
        CacheUtils.writeJson(context, result, name, false);
        Log.i("tag","writeToCache-------->"+name+"----result="+result);
        handleDataUser(result, dataList, handler, state);
    }
    private static void handleDataUser(String result,List<SearchUserEntity> dataList,Handler handler,int state)  {
        try {
            if (dataList.size() != 0) {
                dataList.clear();
            }
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("state") == 200) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    SearchUserEntity entity = new SearchUserEntity();
                    entity.setName(object.getString("nickName"));
                    entity.setId(object.getInt("id"));
                    entity.setFollowed(object.getBoolean("atten"));
                    entity.setPhotoUrl(object.getString("avatar"));
                    entity.setAuthenticate(object.getInt("authenticate"));
                    String authTemp = StringUtil.isNullAuth(object.getString("auth"));
                    if (!authTemp.equals("") && !authTemp.equals("null")) {
                        JSONObject auth = new JSONObject(authTemp);
                        entity.setType(auth.getString("authinfo"));
                    }
                    dataList.add(entity);
                }
            } else {
                dataList = new ArrayList<SearchUserEntity>();
            }
            handler.sendEmptyMessage(state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void parseLocalDataSearchUser(Context context,String name, List<SearchUserEntity> dataList,Handler handler,int state){
        List<String> cacheData= (ArrayList<String>) CacheUtils.readJson(context,name);
        String result=cacheData.get(0);
        Log.i("tag","readFromCache-------->"+name+"----result="+result);
        handleDataUser(result, dataList, handler, state);
    }
    public static void parseNetDataStand(Context context,String result,String name,List<LiveItem> liveItems,Handler handler,int state){
            CacheUtils.writeJson(context, result, name, false);
            Log.i("tag", "writeToCache-------->" + name + "----result=" + result);
            handleDataStand(context, result, liveItems, handler, state);
    }
    private static void handleDataStand(Context context,String result,List<LiveItem> liveItems,Handler handler,int stateFinal)  {
        try {
            if (liveItems.size() != 0) {
                liveItems.clear();
            }
            SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String userId = share.getString("UserId", "");
            JSONObject object = new JSONObject(result);
            String state = object.getString("state");
            if (state.equals("302")) {
                handler.sendEmptyMessage(stateFinal);
                return;
            }
            JSONArray array = object.getJSONArray("data");
            for (int j = 0; j < array.length(); j++) {
                JSONObject jsonObject = array.getJSONObject(j);
                LiveItem liveItem = new LiveItem();
                liveItem.setBigPictureUrl(StringUtil.isNullAvatar(jsonObject.getString("bgmUrl")));
                liveItem.setLiveId(jsonObject.getInt("id"));
                liveItem.setGroupId(StringUtil.isNullGroupId(jsonObject.getString("groupId")));
                liveItem.setCurrentNum(Integer.valueOf(StringUtil.isNullDATA(jsonObject.getString("watchNumber"))));
                liveItem.setLiveState(jsonObject.getInt("state"));
                liveItem.setStartTime(jsonObject.getLong("startTime"));
                liveItem.setTitle(jsonObject.getString("title"));
                JSONObject userObj = jsonObject.getJSONObject("userIconVo");
                int user = userObj.getInt("id");
                if (userId.equals(user + "")) {
                    liveItem.setUserType(LiveActivity.USER_MAIN);
                } else {
                    liveItem.setUserType(LiveActivity.USER_OTHER);
                }
                liveItem.setLiveOwnerId(user + "");
                liveItem.setMainPhotoUrl(StringUtil.isNullAvatar(userObj.getString("avatar")));
                liveItems.add(liveItem);
            }
            handler.sendEmptyMessage(stateFinal);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void parseLocalDataStand(Context context,String name,List<LiveItem> liveItems,Handler handler,int state){
        List<String> cacheData= (ArrayList<String>) CacheUtils.readJson(context,name);
        String result=cacheData.get(0);
        Log.i("tag","readFromCache-------->"+name+"----result=" + result);
        handleDataStand(context, result, liveItems,handler,state);
    }

    public static void parseNetDataStandBanner(Context context,String result,String name,List<String> bitmapList,List<String> titleList,List<String> numberList,List<LiveItem> liveItems,Handler handler,int stateFinal){
        CacheUtils.writeJson(context, result, name, false);
        Log.i("tag", "writeToCache-------->" + name + "----result=" + result);
        handleDataStandBanner(context, result, bitmapList,titleList,numberList, liveItems,handler,stateFinal);
    }
    private static void handleDataStandBanner(Context context,String result,List<String> bitmapList,List<String> titleList,List<String> numberList,List<LiveItem> liveItems,Handler handler,int stateFinal){
        try{
            if (bitmapList.size()!=0)bitmapList.clear();
            if (titleList.size()!=0)titleList.clear();
            if (numberList.size()!=0)numberList.clear();
            if (liveItems.size()!=0)liveItems.clear();
            SharedPreferences share = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String userId = share.getString("UserId", "");
            JSONObject object = new JSONObject(result);
            String state = object.getString("state");
            if (!state.equals("200")) {
                return;
            }
            JSONObject data=object.getJSONObject("data");
            JSONArray array=data.getJSONArray("liveDevas");
            //补充数据
            LiveItem liveItemCopy=new LiveItem();
            String bitmapCopy="";
            String titleCopy="";
            String numberCopy="";
            //补充数据
            for (int i=0;i<array.length();i++){
                JSONObject jsonObject = array.getJSONObject(i);
                LiveItem liveItem = new LiveItem();
                String bitmapUri=StringUtil.isNullAvatar(jsonObject.getString("bgmUrl"));
                liveItem.setBigPictureUrl(bitmapUri);
                liveItem.setLiveId(jsonObject.getInt("id"));
                liveItem.setGroupId(StringUtil.isNullGroupId(jsonObject.getString("groupId")));
                String number=StringUtil.isNullDATA(jsonObject.getString("watchNumber"));
                liveItem.setCurrentNum(Integer.valueOf(number));
                liveItem.setLiveState(jsonObject.getString("state")==null||jsonObject.getString("state").equals("null")?0:jsonObject.getInt("state"));
                liveItem.setStartTime(jsonObject.getLong("startTime"));
                String title=jsonObject.getString("title");
                liveItem.setTitle(title);
                JSONObject userObj = jsonObject.getJSONObject("userIconVo");
                int user = userObj.getInt("id");
                if (userId.equals(user + "")) {
                    liveItem.setUserType(LiveActivity.USER_MAIN);
                } else {
                    liveItem.setUserType(LiveActivity.USER_OTHER);
                }
                liveItem.setLiveOwnerId(user + "");
                liveItem.setMainPhotoUrl(StringUtil.isNullAvatar(userObj.getString("avatar")));
                //设置补充数据
                if (i==0){
                    liveItemCopy=liveItem;
                    bitmapCopy= bitmapUri;
                    titleCopy=title;
                    numberCopy=number;
                }
                //设置补充数据
                liveItems.add(liveItem);
                bitmapList.add(bitmapUri);
                titleList.add(title);
                numberList.add(number);
            }
            //添加补充数据
            liveItems.add(liveItemCopy);
            bitmapList.add(bitmapCopy);
            titleList.add(titleCopy);
            numberList.add(numberCopy);
            //添加补充数据
            Message message=new Message();
            message.what=stateFinal;
            message.obj=bitmapList;
            handler.sendMessage(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void parseLocalDataStandBanner(Context context,String name,List<String> bitmapList,List<String> titleList,List<String> numberList,List<LiveItem> liveItems,Handler handler,int stateFinal) {
        List<String> cacheData= (ArrayList<String>) CacheUtils.readJson(context,name);
        String result=cacheData.get(0);
        Log.i("tag", "readFromCache-------->" + name + "----result="+result);
        handleDataStandBanner(context, result, bitmapList,titleList,numberList, liveItems,handler,stateFinal);
    }
}
