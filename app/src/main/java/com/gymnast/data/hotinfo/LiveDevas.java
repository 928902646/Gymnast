package com.gymnast.data.hotinfo;

/**
 * Created by fldyown on 16/7/1.
 */
public class LiveDevas {
  public int id;//liveID
  public int model;//model
  public String imageUrl;//大图片默认地址
  public int sequence;//数据排序
  public int area;//模块类型1首推，2看台推荐，3精品推荐
  public String bgmUrl;//大图片用户上传地址
  public int auth;//用户信息
  public int type;//不知道什么类型
  public long startTime;//开始时间
  public long endTime;//结束时间
  public int userId;//播主ID
  public String title;//直播标题
  public int lab;//直播小类型
  public String groupId;//环信直播群ID
  public int state;//直播状态
//  public String userIconVo;//播主信息
  public String nickName;//播主昵称
  public String avatar;//播主头像
  /**
   * 自己添加的字段
   */
  public long createTime;//直播创建时间
  public int userType;//用户类型
  public int authenticate;//认证情况
  public String vedioUrl;//视频直播地址
  public int watchNumber;//在线人数
  public String authInfo;//认证信息
}
//        "userIconVo": {
//        "id": 164,
//        "nickname": "郭大侠",
//        "avatar": "group1/M00/00/01/052YyFfeVGuAdISFAAApS3SUDok471.png"
//        }
