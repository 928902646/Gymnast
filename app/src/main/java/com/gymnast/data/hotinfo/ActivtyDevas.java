package com.gymnast.data.hotinfo;

import com.gymnast.data.net.API;

/**
 * Created by fldyown on 16/7/1.
 */
public class ActivtyDevas {
  public int id;//模块id---ActiveID
  public int model;//模塊类型
  public String imageUrl;//图片地址（默认）
  public int sequence;//数据排序方式类型
  public int area;//首推类型
  public String bgmUrl;//图片地址（用户上传）
  public int userId;//模块id
  public String title;//活动标题
  public String getImageUrl( ){
    return API.IMAGE_URL + imageUrl;
  }
  public String getBgmUrl(){
    return API.IMAGE_URL + bgmUrl;
  }
}
