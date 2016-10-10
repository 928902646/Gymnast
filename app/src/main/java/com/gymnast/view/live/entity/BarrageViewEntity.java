package com.gymnast.view.live.entity;

import java.io.Serializable;

/**
 * Created by zzqybyb19860112 on 2016/10/9.
 */
public class BarrageViewEntity implements Serializable {
    private String picUrl;
    private String content;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
