package com.gymnast;


/**
 * 测试一下111111
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;
import com.gymnast.data.user.Data;
import com.gymnast.data.user.User;
import com.gymnast.utils.DialogUtil;
import com.gymnast.view.home.HomeActivity;
import com.gymnast.view.live.activity.LiveActivity;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
public class App extends Application {
    private static App mApp;
    public static final int STATE_A=1;
    public static final int STATE_B=2;
    public static final int STATE_C=3;
    public static final int STATE_D=4;
    public static boolean isStateOK=true;
    public static boolean isNetStateOK=true;
   static Activity nowActivity;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case STATE_A:
                    DialogUtil.goBackToLogin(nowActivity, "是否重新登陆？", "账号在其他地方登陆,您被迫下线！！！");
                    SharedPreferences share1=nowActivity.getSharedPreferences("UserInfo",MODE_PRIVATE);
                    share1.edit().clear().commit();
                    Toast.makeText(nowActivity, "环信帐号已经被管理员从后台移除", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_B:
                    DialogUtil.goBackToLogin(nowActivity, "是否重新登陆？", "账号在其他地方登陆,您被迫下线！！！");
                    SharedPreferences share2=nowActivity.getSharedPreferences("UserInfo",MODE_PRIVATE);
                    share2.edit().clear().commit();
                    break;
                case STATE_C:
                    Toast.makeText(nowActivity, "当前帐号连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_D:
                    Toast.makeText(nowActivity, "当前网络状态不佳，请检查网络设置", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public static App getInstence() {
        if (mApp == null)
            throw new NullPointerException("application is null!please check!");
        return mApp;
    }
    public static boolean debug = true;
    public static User user;
    public static Data data;
  private static Context mContext = null;
  public static int  NOWUSER = 0;
  @Override public void onCreate() {
    super.onCreate();
	mApp = this;
      mContext = getApplicationContext();
      initImageLoader(getApplicationContext());
      this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
          @Override
          public void onActivityCreated(Activity activity, Bundle bundle) {
          }
          @Override
          public void onActivityStarted(Activity activity) {
          }
          @Override
          public void onActivityResumed(Activity activity) {
              nowActivity=activity;
          }
          @Override
          public void onActivityPaused(Activity activity) {
          }
          @Override
          public void onActivityStopped(Activity activity) {
          }
          @Override
          public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
          }
          @Override
          public void onActivityDestroyed(Activity activity) {
          }
      });
      EMOptions options = new EMOptions();
      options.setAcceptInvitationAlways(false);
      int pid = android.os.Process.myPid();
      String processAppName = getAppName(pid);
    // 如果APP启用了远程的service，此application:onCreate会被调用2次
    // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
    // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
      if (processAppName == null ||!processAppName.equalsIgnoreCase(mContext.getPackageName())) {
          Log.e("TAG", "enter the service process!");
          // 则此application::onCreate 是被service 调用的，直接返回
          return;
      }
      EMClient.getInstance().init(mContext, options);
      //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
      EMClient.getInstance().setDebugMode(true);
      EMClient.getInstance().addConnectionListener(new MyConnectionListener());//实现ConnectionListener接口
  }
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                    }
                } catch (Exception e) {
                Log.d("Process", "Error>> :"+ e.toString());
                }
            }
        return processName;
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    public static void initImageLoader(Context context) {
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
            context).threadPriority(Thread.NORM_PRIORITY - 2)
            .denyCacheImageMultipleSizesInMemory()
            .diskCacheFileNameGenerator(new Md5FileNameGenerator())
            .diskCacheSize(50 * 1024 * 1024)
            // 50 Mb
            .tasksProcessingOrder(QueueProcessingType.LIFO)
            .writeDebugLogs() // Remove for release app
            .build();
    // Initialize ImageLoader with configuration.
    ImageLoader.getInstance().init(config);
  }
  public static Context getContext() {
    return mContext;
  }
    public static boolean checkNetwork(){
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo.isAvailable()) {
            return true;
        }
        return false;
    }
    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }
    /**
     * 路径
     *
     * @return
     */
    public String getPhotopath() {
        // 照片全路径
        String fileName = "";
        // 文件夹路径
        String pathUrl = Environment.getExternalStorageDirectory() + "/Zyx/";
        //照片名
        String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".png";
        File file = new File(pathUrl);
        if (!file.exists()) {
            file.mkdirs();// 如果文件夹不存在，则创建文件夹
            Log.e("TAG", "第一次创建文件夹"+   file.getAbsoluteFile());
        }
        fileName = pathUrl + name;
        return fileName;
    }
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }
        @Override
        public void onDisconnected(final int error) {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                       handler.sendEmptyMessage(STATE_A);
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        handler.sendEmptyMessage(STATE_B);
                    }else {
                        if (NetUtils.hasNetwork(getContext())) {
                            handler.sendEmptyMessage(STATE_C);
                        } else {
                            handler.sendEmptyMessage(STATE_D);
                        }
                    }
                }
    }
}
