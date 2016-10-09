package com.gymnast.view.live.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by zzqybyb19860112 on 2016/10/8.
 */
public class EndLiveEntity implements Serializable {
    String totalTime;
    int peopleNumber;
    int shareNumber;
    int priseNumber;
    String bitmapSmallPhotoUrl;
    String groupId;
    String nickName;
    public String getBitmapSmallPhotoUrl() {
        return bitmapSmallPhotoUrl;
    }
    public void setBitmapSmallPhotoUrl(String bitmapSmallPhotoUrl) {
        this.bitmapSmallPhotoUrl = bitmapSmallPhotoUrl;
    }
    public String getTotalTime() {
        return totalTime;
    }
    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }
    public int getPeopleNumber() {
        return peopleNumber;
    }
    public void setPeopleNumber(int peopleNumber) {
        this.peopleNumber = peopleNumber;
    }
    public int getShareNumber() {
        return shareNumber;
    }
    public void setShareNumber(int shareNumber) {
        this.shareNumber = shareNumber;
    }
    public int getPriseNumber() {
        return priseNumber;
    }
    public void setPriseNumber(int priseNumber) {
        this.priseNumber = priseNumber;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
