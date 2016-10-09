package com.gymnast.data.hotinfo;

import com.gymnast.data.net.API;
import java.io.Serializable;

/**
 * Created by fldyown on 16/7/4.
 */
public class ConcerDevas implements Serializable{
  public int id;//动态ID
  public int model;//模块类型
  public String imageUrl;//图片地址（默认）
  public int sequence;//数据排序方式类型
  public int area;//首推类型
  public String bgmUrl;//图片地址（用户上传）
  public int userId;//用户ID
  public String getImageUrl() {
    return API.IMAGE_URL + imageUrl;
  }
}
