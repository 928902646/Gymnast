package com.gymnast.data.personal;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Cymbi on 2016/8/27.
 */
public class DynamicData implements Serializable {
    //动态id
    private int id;
    //用户id
    private int userId;
    //创建时间
    private Long createTime;
    //1为个人动态，2为活动动态，3为明星动态，4为圈子动态
    private int type;
    //动态图片
    private String topicTitle;
    //内容
    private String topicContent;
    //图片地址
    private ArrayList<String> imgUrl;
    //视频直播
    private String videoUrl;
    //可见范围0所有可见，1好友可见
    private int topicVisible;
    //
    private int pageviews;//多少人浏览
    //这个人发布的动态的id
    private String fromId;
    //动态发起人的id
    private int fromType;
    //动态状态，-2未屏蔽，-1为删除,0为正常
    private int state;
    //用户姓名
    private String nickName;
    //用户头像
    private String avatar;
    //认证
    private int authenticate;
    //赞
    private int zanCounts;
    //回复
    private int commentCounts;
    //用户类型
    private String authInfo;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPageviews() {
        return pageviews;
    }
    public void setPageviews(int pageviews) {
        this.pageviews = pageviews;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getAuthInfo() {
        return authInfo;
    }
    public void setAuthInfo(String authInfo) {
        this.authInfo = authInfo;
    }
    public Long getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
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
    public int getTopicVisible() {
        return topicVisible;
    }
    public void setTopicVisible(int topicVisible) {
        this.topicVisible = topicVisible;
    }
    public String getFromId() {
        return fromId;
    }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
    public int getFromType() {
        return fromType;
    }
    public void setFromType(int fromType) {
        this.fromType = fromType;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
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
}
