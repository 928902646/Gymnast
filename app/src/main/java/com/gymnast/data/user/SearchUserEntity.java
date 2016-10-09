package com.gymnast.data.user;

import java.io.Serializable;
/**
 * Created by 永不言败 on 2016/8/9.
 */
public class SearchUserEntity implements Serializable {
    String name;//名字或昵称
    String type;//用户认证类型
    String photoUrl;//头像地址
    int authenticate;//是否认证：0未认证，1待审核，2已认证，3认证失败
    int id;//用户ID
    boolean followed;//当前用户是否关注对方用户
    private int authId;
    private String authinfo;
    private String authfile;

    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    public String getAuthinfo() {
        return authinfo;
    }

    public void setAuthinfo(String authinfo) {
        this.authinfo = authinfo;
    }

    public String getAuthfile() {
        return authfile;
    }

    public void setAuthfile(String authfile) {
        this.authfile = authfile;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public int getAuthenticate() {
        return authenticate;
    }
    public void setAuthenticate(int authenticate) {
        this.authenticate = authenticate;
    }
    public boolean isFollowed() {
        return followed;
    }
    public void setFollowed(boolean followed) {
        this.followed = followed;
    }
    public SearchUserEntity() {
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
