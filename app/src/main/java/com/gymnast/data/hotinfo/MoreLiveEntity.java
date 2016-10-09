package com.gymnast.data.hotinfo;

import android.graphics.Bitmap;
import java.io.Serializable;
/**
 * Created by 永不言败 on 2016/8/9.
 */
public class MoreLiveEntity implements Serializable {
    Bitmap mainPhoto,bigPicture;
    String liveName,mainName;
    int totalNumber;
    public MoreLiveEntity() {
    }
    public Bitmap getMainPhoto() {
        return mainPhoto;
    }
    public void setMainPhoto(Bitmap mainPhoto) {
        this.mainPhoto = mainPhoto;
    }
    public Bitmap getBigPicture() {
        return bigPicture;
    }
    public void setBigPicture(Bitmap bigPicture) {
        this.bigPicture = bigPicture;
    }
    public String getLiveName() {
        return liveName;
    }
    public void setLiveName(String liveName) {
        this.liveName = liveName;
    }
    public String getMainName() {
        return mainName;
    }
    public void setMainName(String mainName) {
        this.mainName = mainName;
    }
    public int getTotalNumber() {
        return totalNumber;
    }
    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }
}
