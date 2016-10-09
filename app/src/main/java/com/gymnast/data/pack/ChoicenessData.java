package com.gymnast.data.pack;

import java.io.Serializable;

/**
 * Created by Cymbi on 2016/9/6.
 */
public class ChoicenessData implements Serializable {
    public String title;
    public int id;
    public int circleItemCount;
    public String headImgUrl;
    public boolean isconcern;

    public boolean isconcern() {
        return isconcern;
    }

    public void setIsconcern(boolean isconcern) {
        this.isconcern = isconcern;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCircleItemCount() {
        return circleItemCount;
    }
    public void setCircleItemCount(int circleItemCount) {
        this.circleItemCount = circleItemCount;
    }
    public String getHeadImgUrl() {
        return headImgUrl;
    }
    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }
}
