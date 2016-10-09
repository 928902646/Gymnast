package com.gymnast.utils;

import android.content.Context;
import com.gymnast.data.net.API;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitUtil {
  private static volatile Retrofit retrofit;
  public static <T> T createApi(Context context, Class<T> clazz) {
    if (retrofit == null) {
      synchronized (RetrofitUtil.class) {
        if (retrofit == null) {
          retrofit = new Retrofit.Builder().baseUrl(API.BASE_URL)
              .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
              .addConverterFactory(GsonConverterFactory.create())
              .client(OkHttpUtil.getInstance(context))
              .build();
        }
      }
    }
    return retrofit.create(clazz);
  }
}
