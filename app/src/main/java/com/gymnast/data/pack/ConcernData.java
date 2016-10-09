package com.gymnast.data.pack;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Cymbi on 2016/9/28.
 */
public class ConcernData implements Serializable {
    //动态id
    private int id;
    //创建时间
    private long createTime;
    //用户id
    private int userId;
    //不知道是什么type
    private int type;
    //内容的Title
    private String topicTitle;
    //内容
    private String topicContent;
    //图片地址
    private ArrayList<String> imgUrl;
    //视频地址
    private String videoUrl;
    //不知道,不清楚是否为String
    private String topicVisible;
    //不清楚
    private int state;
    //这条动态的id
    private String fromId;
    //这条动态的类型
    private String fromType;
    //储存用户的名字和头像和认证状态:userVo
    //用户名字
    private String nickName;
    //用户头像
    private String avatar;
    //认证状态
    private int authenticate;
    //点赞数量
    private int zanCounts;
    //回复消息数量
    private int commentCounts;
    //认证的名字
    private String authInfo;
    //浏览数量
    private int pageviews;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTopicContent() {
        return topicContent;
    }

    public void setTopicContent(String topicContent) {
        this.topicContent = topicContent;
    }

    public ArrayList<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(ArrayList<String> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTopicVisible() {
        return topicVisible;
    }

    public void setTopicVisible(String topicVisible) {
        this.topicVisible = topicVisible;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(int authenticate) {
        this.authenticate = authenticate;
    }

    public int getZanCounts() {
        return zanCounts;
    }

    public void setZanCounts(int zanCounts) {
        this.zanCounts = zanCounts;
    }

    public int getCommentCounts() {
        return commentCounts;
    }

    public void setCommentCounts(int commentCounts) {
        this.commentCounts = commentCounts;
    }

    public String getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(String authInfo) {
        this.authInfo = authInfo;
    }

    public int getPageviews() {
        return pageviews;
    }

    public void setPageviews(int pageviews) {
        this.pageviews = pageviews;
    }
}
