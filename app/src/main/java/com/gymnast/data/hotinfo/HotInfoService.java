package com.gymnast.data.hotinfo;

import com.gymnast.data.Service;
import com.gymnast.data.net.Result;
import rx.Observable;

/**
 * Created by fldyown on 16/6/30.
 */
public interface HotInfoService extends Service{
  public Observable<Result<HotInfoData>> getAllHotInfo();
}
