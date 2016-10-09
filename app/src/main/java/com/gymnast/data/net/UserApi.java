package com.gymnast.data.net;

import com.gymnast.data.user.VerifyCode;
import java.util.HashMap;
import okhttp3.RequestBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import rx.Observable;

/**
 * Created by fldyown on 16/6/14.
 */
public interface UserApi {
  /**
   * 获取验证码
   */
  @POST("/v1/sendRegisterCode") @FormUrlEncoded Observable<Result> getVerifyCode(
      @FieldMap HashMap<String, String> params);
  /**
   * 验证手机号
   */
  @POST("/v1/account/validate/code") @FormUrlEncoded Observable<Result<VerifyCode>> verifyPhone(
      @FieldMap HashMap<String, String> params);
  /**
   * 注册账号
   */
  @Multipart
  @POST("/v1/account/register") Observable<Result> register(
      @PartMap HashMap<String, RequestBody> params);
  /**
   * 忘记密码
   */
  @POST("/v1/account/retrievepwd") @FormUrlEncoded Observable<Result> retrievePassword(
      @FieldMap HashMap<String, String> params);
  /**
   * 登录
   */
  @POST("/v1/account/login") @FormUrlEncoded Observable<Result<UserData>> login(
      @FieldMap HashMap<String, String> params);
}
