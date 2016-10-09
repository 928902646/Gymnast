package com.gymnast.view.live.entity;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by 永不言败 on 2016/8/8.
 */
public class BarrageItem implements Parcelable {
   String photoUrl,name,time,body;
    int priseNumber;
    public BarrageItem() {
    }
    protected BarrageItem(Parcel in) {
        photoUrl = in.readString();
        name = in.readString();
        time = in.readString();
        priseNumber = in.readInt();
        body = in.readString();
    }
    public static final Creator<BarrageItem> CREATOR = new Creator<BarrageItem>() {
        @Override
        public BarrageItem createFromParcel(Parcel in) {
            return new BarrageItem(in);
        }
        @Override
        public BarrageItem[] newArray(int size) {
            return new BarrageItem[size];
        }
    };
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getPriseNumber() {
        return priseNumber;
    }
    public void setPriseNumber(int priseNumber) {
        this.priseNumber = priseNumber;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoUrl);
        dest.writeString(name);
        dest.writeString(time);
        dest.writeInt(priseNumber);
        dest.writeString(body);
    }
}
