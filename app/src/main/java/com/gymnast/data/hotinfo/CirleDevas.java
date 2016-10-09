package com.gymnast.data.hotinfo;

import com.gymnast.data.net.API;
import com.gymnast.data.user.UserIconVo;
import com.gymnast.utils.StringUtil;
import java.io.Serializable;

/**
 * Created by fldyown on 16/7/4.
 */
public class CirleDevas implements Serializable{
  public int id;//圈子ID
  public int model;//模块类型4
  public String imageUrl;//图片地址（默认）
  public int sequence;//数据排序方式类型
  public int area;//首推类型
  public String bgmUrl;//图片地址（用户上传）
  public int userId;//创建用户ID
  public String title;//圈子标题
  public int createId;//圈子创建者的ID
  public String baseContent;//内容
  public long createTime;//创建时间
  public int zanCount;//点赞人次数
  public int comCount;//评论人数
  public int circleId;//圈子用户ID
  public String circleTitle;//来自什么圈子
  public int viewCount;//多少人次浏览
  public UserIconVo userIconVo;//圈子用户信息
  public String getImageUrl() {
    return API.IMAGE_URL + imageUrl;
  }
  public String getBgmUrl(){
    return API.IMAGE_URL + bgmUrl;
  }
}

