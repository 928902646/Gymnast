package com.gymnast.data.hotinfo;

import java.io.Serializable;
/**
 * Created by 永不放弃 on 2016/7/30.
 */
public class LiveMessage implements Serializable {
    String iconUrl;
    String pictureUrl;
    String timeUntilNow;
    long createTime;
    String content;
    public LiveMessage() {
    }
    public long getCreateTime() {
        return createTime;
    }
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public String getIconUrl() {
        return iconUrl;
    }
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    public String getPictureUrl() {
        return pictureUrl;
    }
    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
    public String getTimeUntilNow() {
        return timeUntilNow;
    }
    public void setTimeUntilNow(String timeUntilNow) {
        this.timeUntilNow = timeUntilNow;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
