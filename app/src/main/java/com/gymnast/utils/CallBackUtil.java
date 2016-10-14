package com.gymnast.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gymnast.data.net.API;
import com.gymnast.view.hotinfoactivity.entity.CallBackDetailEntity;
import com.gymnast.view.hotinfoactivity.entity.CallBackEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by zzqybyb19860112 on 2016/9/18.
 */
public class CallBackUtil {
    public  static void getCallBackData(final int comment_type, final int comment_id,final List<CallBackEntity> list,final Handler handler, final int status){
        new Thread(){
            @Override
            public void run() {
                try{
                    ArrayList<String> userABC=new ArrayList<String>();
                    if (list.size()!=0){
                        list.clear();
                        userABC.clear();
                    }
                    String uri = API.BASE_URL + "/v1/comment/query/" + comment_type + "/" + comment_id;
                    HashMap<String, String> params = new HashMap<String, String>();
                    String result= GetUtil.sendGetMessage(uri, params);
                    Log.i("tag","result------->"+result);
                    JSONObject object=new JSONObject(result);
                    JSONArray array=object.getJSONArray("data");
                    for (int i=0;i<array.length();i++){
                        JSONObject data=array.getJSONObject(i);
                        CallBackEntity entity=new CallBackEntity();
                        JSONObject userVo=data.getJSONObject("userVo");
                        entity.setCallBackImgUrl(StringUtil.isNullAvatar(userVo.getString("avatar")));
                        userABC.add(userVo.getString("nickName"));
                        entity.setCommentID(data.getInt("id"));
                        entity.setCallBackNickName(userVo.getString("nickName"));
                        entity.setCallBackText(data.getString("commentContent"));
                        entity.setCallBackTime(data.getLong("createTime"));
                        entity.setCommenterId(data.getInt("commentAccount"));
                        JSONArray comment=data.getJSONArray("replyVos");
                        ArrayList<CallBackDetailEntity> detailList=new ArrayList<>();
                        if (comment==null){
                            continue;
                        }
                        for (int j=0;j<comment.length();j++){
                            JSONObject commentDetail=comment.getJSONObject(j);
                            CallBackDetailEntity detail=new CallBackDetailEntity();
                            detail.setContent(commentDetail.getString("replyContent"));
                            detail.setFromID(commentDetail.getInt("replyFromUser"));
                            detail.setToID(commentDetail.getInt("replyToUser"));
                            String from;
                            String to;
                            String jsonFrom=StringUtil.isNullNickName(commentDetail.getString("fromUserVo"));
                            if (jsonFrom==null||jsonFrom.equals("")){
                                from="";
                            }else {
                                JSONObject jsonObject=new JSONObject(jsonFrom);
                                from=jsonObject.getString("nickName");
                            }
                            String jsonTo=StringUtil.isNullNickName(commentDetail.getString("toUserVo"));
                            if (jsonTo==null||jsonTo.equals("")){
                                to="";
                            }else {
                                JSONObject jsonObject=new JSONObject(jsonTo);
                                to=jsonObject.getString("nickName");
                            }
                            detail.setFrom(from);
                            detail.setTo(to);
                            detailList.add(detail);
                            userABC.add(from);
                            userABC.add(to);
                        }
                        entity.setEntities(detailList);
                        list.add(entity);
                    }
                    Message message=new Message();
                    message.what=status;
                    message.obj=userABC;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void handleCallBackMSG(final int comment_type, final int comment_id, final int comment_account, final String comment_content){
        new Thread(){
            @Override
            public void run() {
                try{
                    String uri=API.BASE_URL+"/v1/comment/insert";
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("comment_type",comment_type+"");
                    params.put("comment_id",comment_id+"");
                    params.put("comment_account",comment_account+"");
                    params.put("comment_content", comment_content + "");
                    PostUtil.sendPostMessage(uri, params);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void handleBack(final String token, final int reply_parent_id, final int reply_from_user, final int reply_to_user, final String reply_content, final ArrayList<CallBackDetailEntity> detailMSGs, final Handler handler, final int status, final String from, final String to){
            new Thread(){
                @Override
                public void run() {
                    try{
                        String uri=API.BASE_URL+"/v1/reply/addReply";
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put("token",token+"");
                        params.put("reply_parent_id",reply_parent_id+"");
                        params.put("reply_from_user",reply_from_user+"");
                        params.put("reply_to_user",reply_to_user+"");
                        params.put("reply_content", reply_content + "");
                        String result=PostUtil.sendPostMessage(uri,params);
                        JSONObject object=new JSONObject(result);
                        if (object.getInt("state")==200){
                            CallBackDetailEntity entity=new CallBackDetailEntity();
                            entity.setFrom(from);
                            entity.setTo(to);
                            entity.setContent(reply_content);
                            detailMSGs.add(entity);
                        }
                        handler.sendEmptyMessage(status);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();

    }
}
