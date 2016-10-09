package com.gymnast.data.hotinfo;

import com.gymnast.data.net.API;
import java.io.Serializable;

/**
 * Created by fldyown on 16/7/1.
 */
public class UserDevas implements Serializable {
  public int id;//用户ID
  public int model;//模块类型
  public String imageUrl;//默认头像地址
  public int sequence;//数据排序方式类型
  public int area;//首推类型
  public String bgmUrl;//图片地址（用户上传）
  public String userId;//用户ID2
  public String nickname;//用户昵称
  public String getImageUrl() {
    return API.IMAGE_URL + imageUrl;
  }
  public String getBgmUrl(){
    return API.IMAGE_URL + bgmUrl;
  }
}
