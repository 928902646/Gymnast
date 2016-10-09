package com.gymnast.view.live.entity;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by zzqybyb1986 on 2016/8/29.
 */
public class LiveItem implements Parcelable {
    public  int currentNum;
    public  String  bigPictureUrl;
    public  String title;
    public  String  mainPhotoUrl;
    public String liveOwnerId;
    public int liveId;
    public int liveState;
    public long startTime;
    public int userType;
    public String groupId;
    public String getLookType() {
        return lookType;
    }
    public void setLookType(String lookType) {
        this.lookType = lookType;
    }
    public String lookType;
    public String authInfo;
    public String nickName;
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getAuthInfo() {
        return authInfo;
    }
    public void setAuthInfo(String authInfo) {
        this.authInfo = authInfo;
    }
    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public int getLiveState() {
        return liveState;
    }
    public void setLiveState(int liveState) {
        this.liveState = liveState;
    }
    public String getLiveOwnerId() {
        return liveOwnerId;
    }
    public void setLiveOwnerId(String liveOwnerId) {
        this.liveOwnerId = liveOwnerId;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public int getUserType() {
        return userType;
    }
    public void setUserType(int userType) {
        this.userType = userType;
    }
    public int getLiveId() {
        return liveId;
    }
    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }
    public String getBigPictureUrl() {
        return bigPictureUrl;
    }
    public void setBigPictureUrl(String bigPictureUrl) {
        this.bigPictureUrl = bigPictureUrl;
    }
    public String getMainPhotoUrl() {
        return mainPhotoUrl;
    }
    public void setMainPhotoUrl(String mainPhotoUrl) {
        this.mainPhotoUrl = mainPhotoUrl;
    }
    public  LiveItem(){}
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getCurrentNum() {
        return currentNum;
    }
    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }
    protected LiveItem(Parcel in) {
        currentNum = in.readInt();
        bigPictureUrl = in.readString();
        title = in.readString();
        mainPhotoUrl = in.readString();
        liveId=in.readInt();
        liveState=in.readInt();
        userType=in.readInt();
        groupId=in.readString();
        authInfo=in.readString();
        nickName=in.readString();
        liveOwnerId=in.readString();
        startTime=in.readLong();
    }
    public static  Creator<LiveItem> CREATOR = new Creator<LiveItem>() {
        @Override
        public LiveItem createFromParcel(Parcel in) {
            return new LiveItem(in);
        }
        @Override
        public LiveItem[] newArray(int size) {
            return new LiveItem[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(currentNum);
        dest.writeInt(userType);
        dest.writeInt(liveId);
        dest.writeInt(liveState);
        dest.writeString(bigPictureUrl);;
        dest.writeString(title);
        dest.writeString(mainPhotoUrl);
        dest.writeString(groupId);
        dest.writeString(liveOwnerId);
        dest.writeString(authInfo);
        dest.writeString(nickName);
        dest.writeLong(startTime);
    }
}
