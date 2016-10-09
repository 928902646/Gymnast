package com.gymnast.data.hotinfo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/27.
 */
public class RecentActivityDetail implements Serializable {
    String pictureURL;//imageUrl
    String detail;//descContent=
    String from;
    String address;
    String time;
    int loverNum;//memberCount
    String type;
    String maxpeople;
    String phone;
    int userid;
    int modelID;
    int id;

    public int getModelID() {
        return modelID;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    boolean isCollected;

    public boolean isCollected() {
        return isCollected;
    }

    public void setIsCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    long startTime,endTime,lastTime;
    String title;
    int price;
    public long getLastTime() {
        return lastTime;
    }
    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public String getMaxpeople() {
        return maxpeople;
    }
    public void setMaxpeople(String maxpeople) {
        this.maxpeople = maxpeople;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public RecentActivityDetail() {
    }
    public String getPictureURL() {
        return pictureURL;
    }
    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getLoverNum() {
        return loverNum;
    }
    public void setLoverNum(int loverNum) {
        this.loverNum = loverNum;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
