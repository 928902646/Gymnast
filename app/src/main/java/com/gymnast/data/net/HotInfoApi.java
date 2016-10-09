package com.gymnast.data.net;

import com.gymnast.data.hotinfo.HotInfoData;
import retrofit2.http.POST;
import rx.Observable;
/**
 * Created by fldyown on 16/6/30.
 */
public interface HotInfoApi {
  /**
   * 获取首页信息
   */
  @POST("/v1/deva/getAll") Observable<Result<HotInfoData>> getAllHotInfo();
}
