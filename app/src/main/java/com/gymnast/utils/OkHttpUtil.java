package com.gymnast.utils;

import android.content.Context;
import com.gymnast.App;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
public class OkHttpUtil {
  private static OkHttpClient okHttp = null;
  public static OkHttpClient getInstance(Context context) {
    if (okHttp == null) {
      synchronized (OkHttpUtil.class) {
        if (okHttp == null) {
          /**
           * 设置缓存
           */
          File sdcache = context.getExternalCacheDir();
          int cacheSize = 10 * 1024 * 1024;
          // log用拦截器
          HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
          // 开发模式记录整个body，否则只记录基本信息如返回200，http协议版本等
          if (App.debug) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
          } else {
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
          }
          //// 如果使用到HTTPS，我们需要创建SSLSocketFactory，并设置到client
          //SSLSocketFactory sslSocketFactory = null;
          //try {
          //  // 这里直接创建一个不做证书串验证的TrustManager
          //  final TrustManager[] trustAllCerts = new TrustManager[] {
          //      new X509TrustManager() {
          //        @Override public void checkClientTrusted(X509Certificate[] chain, String authType)
          //            throws CertificateException {
          //        }
          //        @Override public void checkServerTrusted(X509Certificate[] chain, String authType)
          //            throws CertificateException {
          //        }
          //        @Override public X509Certificate[] getAcceptedIssuers() {
          //          return new X509Certificate[] {};
          //        }
          //      }
          //  };
          //  // Install the all-trusting trust manager
          //  final SSLContext sslContext = SSLContext.getInstance("SSL");
          //  sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
          //  // Create an ssl socket factory with our all-trusting manager
          //  sslSocketFactory = sslContext.getSocketFactory();
          //} catch (Exception e) {
          //  Log.e("fldy", "==>:" + e.getMessage());
          //}
          okHttp = new OkHttpClient.Builder()
               //HeadInterceptor实现了Interceptor，用来往Request Header添加一些业务相关数据，如APP版本，token信息
              .addInterceptor(new Interceptor() {
                @Override public Response intercept(Chain chain) throws IOException {
                  Response response = chain.proceed(chain.request());
                  //ResponseBody body = response.body();
                  //Log.i("fldy", "body: " + body.string());
                  return response;
                }
              })
              .addInterceptor(logging)
              // 连接超时时间设置
              .connectTimeout(10, TimeUnit.SECONDS)
              // 读取超时时间设置
              //.readTimeout(10, TimeUnit.SECONDS).sslSocketFactory(sslSocketFactory)
              // 信任所有主机名
              //.hostnameVerifier(new HostnameVerifier() {
              //  @Override public boolean verify(String hostname, SSLSession session) {
              //    return false;
              //  }
              //})
              // 这里我们使用host name作为cookie保存的key
              .cookieJar(new CookieJar() {
                private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
                @Override public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                  cookieStore.put(HttpUrl.parse(url.host()), cookies);
                }
                @Override public List<Cookie> loadForRequest(HttpUrl url) {
                  List<Cookie> cookies = cookieStore.get(HttpUrl.parse(url.host()));
                  return cookies != null ? cookies : new ArrayList<Cookie>();
                }
              })  .writeTimeout(20, TimeUnit.SECONDS)
                  .readTimeout(20, TimeUnit.SECONDS)
                  .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize)) .build();
        }
      }
    }
    return okHttp;
  }
}