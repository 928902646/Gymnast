package com.gymnast.data.personal;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Cymbi on 2016/10/11.
 */
public class CollectionData implements Serializable{
    //model类型：1活动，2直播，3圈子，4帖子，5动态
    private int model;
    //指向model id
    private int modelId;
    //创建时间
    private long createTime;
    //浏览量
    private int pageViews;
    //被收藏的用户id
    private int userid;
    //被收藏的用户名
    private String nickname;
    //被收藏的用户头像
    private String avatar;
    //被收藏的用户认证信息
    private String authInfo;
    //收藏数量
    private int collection;
    //标题title
    private String title;
    //活动缩略图
    private String imgUrls;
    //活动内容
    private String descContent;
    //活动开始时间
    private long startTime;
    //点赞人数
    private int zanCount;
    //回复数量
    private int commentCount;
    //来自哪个圈子
    private String circleTitle;
    //多个图片
    private ArrayList<String> imgUrl;

    public ArrayList<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(ArrayList<String> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getPageViews() {
        return pageViews;
    }

    public void setPageViews(int pageViews) {
        this.pageViews = pageViews;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(String authInfo) {
        this.authInfo = authInfo;
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
        this.imgUrls = imgUrls;
    }

    public String getDescContent() {
        return descContent;
    }

    public void setDescContent(String descContent) {
        this.descContent = descContent;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getZanCount() {
        return zanCount;
    }

    public void setZanCount(int zanCount) {
        this.zanCount = zanCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getCircleTitle() {
        return circleTitle;
    }

    public void setCircleTitle(String circleTitle) {
        this.circleTitle = circleTitle;
    }
}
