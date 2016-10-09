package com.gymnast.data.hotinfo;

import com.gymnast.App;
import com.gymnast.data.net.HotInfoApi;
import com.gymnast.data.net.Result;
import com.gymnast.utils.RetrofitUtil;
import rx.Observable;
/**
 * Created by fldyown on 16/6/30.
 */
public class HotInfoServiceImpl implements HotInfoService {
  HotInfoApi api;
  public HotInfoServiceImpl() {
    api = RetrofitUtil.createApi(App.getContext(), HotInfoApi.class);
  }
  @Override public Observable<Result<HotInfoData>> getAllHotInfo() {
    return api.getAllHotInfo();
  }
}
