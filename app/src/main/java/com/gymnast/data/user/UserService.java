package com.gymnast.data.user;

import com.gymnast.data.Service;
import com.gymnast.data.net.Result;
import com.gymnast.data.net.UserData;
import rx.Observable;
/**
 * Created by fldyown on 16/6/14.
 */
public interface UserService extends Service{
  public Observable<Result> getVerifyCode(String phone);
  public Observable<Result<VerifyCode>> verifyPhone(String phone, String code);
  public Observable<Result> register(String phone, String pwd, String nickname, String avatar);
  public Observable<Result> retrievePassword(String phone, String pwd, String re_pwd);
  public Observable<Result<UserData>> login(String phone, String pwd);
}
