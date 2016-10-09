package com.gymnast.data.pack;

import java.io.Serializable;

/**
 * Created by Cymbi on 2016/9/6.
 */
public class CircleBannerData implements Serializable {
    public String title;
    public String content;
    public String imgUrl;
    public int id;
    public int circleId;
    public int createId;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCircleId() {
        return circleId;
    }
    public void setCircleId(int circleId) {
        this.circleId = circleId;
    }
    public int getCreateId() {
        return createId;
    }
    public void setCreateId(int createId) {
        this.createId = createId;
    }
}
