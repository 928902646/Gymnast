package com.gymnast.view.hotinfoactivity.entity;

import java.util.ArrayList;

/**
 * Created by zzqybyb19860112 on 2016/9/17.
 */
public class CallBackEntity {
    String callBackImgUrl;//评论者的头像地址
    String callBackNickName;//评论者的昵称
    long callBackTime;//评论时间
    String callBackText;//评论内容
    int commenterId;//评论者的系统id
    int commentID;
    ArrayList<CallBackDetailEntity> entities;

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public int getCommenterId() {
        return commenterId;
    }
    public void setCommenterId(int commenterId) {
        this.commenterId = commenterId;
    }
    public String getCallBackImgUrl() {
        return callBackImgUrl;
    }

    public void setCallBackImgUrl(String callBackImgUrl) {
        this.callBackImgUrl = callBackImgUrl;
    }

    public String getCallBackNickName() {
        return callBackNickName;
    }

    public void setCallBackNickName(String callBackNickName) {
        this.callBackNickName = callBackNickName;
    }

    public long getCallBackTime() {
        return callBackTime;
    }

    public void setCallBackTime(long callBackTime) {
        this.callBackTime = callBackTime;
    }

    public String getCallBackText() {
        return callBackText;
    }

    public void setCallBackText(String callBackText) {
        this.callBackText = callBackText;
    }

    public ArrayList<CallBackDetailEntity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<CallBackDetailEntity> entities) {
        this.entities = entities;
    }
}
