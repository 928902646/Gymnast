package com.gymnast.data.hotinfo;

import java.io.Serializable;

/**
 * Created by Cymbi on 2016/8/19.
 */
public class NewActivityItemDevas implements Serializable {
    public int getActiveId() {
        return activeId;
    }
    public void setActiveId(int activeId) {
        this.activeId = activeId;
    }
    private int activeId;
    boolean isCollected;

    public boolean isCollected() {
        return isCollected;
    }

    public void setIsCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }

    //收藏人数
    public int collection;
    //Title
    public String title;
    //详细信息
    public String descContent;
    //封面地址
    public String imgUrls;
    //活动开始时间
    public Long startTime;
    //活动结束时间
    public Long endTime;
    //报名结束时间
    public Long lastTime;
    //线下活动地址
    public String address;
    //活动发布人名字
    public String nickname;
    //活动价格
    public int price;
    //最大人数限制
    public String maxPeople;
    //联系电话
    public String phone;
    //线上活动跳转地址
    public String targetUrl;
    //活动是否需要审核:0不需要审核,1需要审核
    public int examine;
    //活动启用状态:0启用,1禁用
    public String activityType;
    //活动类型:0是线上,1是线下
    public int type;
    //报名活动计数
    public int memberCount;
    //报名活动模块
    public String memberTemplate;
    String authInfo;
    int zanCount,msgCount;
    int mask;
    int userID;
    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    int pageViews;//多少人浏览
    public int getPageViews() {
        return pageViews;
    }
    public void setPageViews(int pageViews) {
        this.pageViews = pageViews;
    }
    public int getZanCount() {
        return zanCount;
    }
    public void setZanCount(int zanCount) {
        this.zanCount = zanCount;
    }
    public int getMsgCount() {
        return msgCount;
    }
    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }
    public int getMask() {
        return mask;
    }
    public void setMask(int mask) {
        this.mask = mask;
    }
    public String getAuthInfo() {
        return authInfo;
    }
    public void setAuthInfo(String authInfo) {
        this.authInfo = authInfo;
    }
    //哪些人可以看 0表示所有人,1表示我的关注，2表示我的粉丝
    public int visible;
    public String getDescContent() {
        return descContent;
    }
    public void setDescContent(String descContent) {
        this.descContent = descContent;
    }
    public Long getEndTime() {
        return endTime;
    }
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
    public Long getLastTime() {
        return lastTime;
    }
    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }
    public String getMaxPeople() {
        return maxPeople;
    }
    public void setMaxPeople(String maxPeople) {
        this.maxPeople = maxPeople;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getTargetUrl() {
        return targetUrl;
    }
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
    public int getExamine() {
        return examine;
    }
    public void setExamine(int examine) {
        this.examine = examine;
    }
    public String getActivityType() {
        return activityType;
    }
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getMemberCount() {
        return memberCount;
    }
    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
    public String getMemberTemplate() {
        return memberTemplate;
    }
    public void setMemberTemplate(String memberTemplate) {
        this.memberTemplate = memberTemplate;
    }
    public int getVisible() {
        return visible;
    }
    public void setVisible(int visible) {
        this.visible = visible;
    }
    public int getCollection() {
        return collection;
    }
    public void setCollection(int collection) {
        this.collection = collection;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getImgUrls() {
        return imgUrls;
    }
    public void setImgUrls(String imgUrls) {
       this.imgUrls=imgUrls;
    }
    public Long getStartTime() {
        return startTime;
    }
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
}
