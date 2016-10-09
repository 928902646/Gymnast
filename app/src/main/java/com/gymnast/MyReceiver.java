package com.gymnast;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.gymnast.data.net.API;
import com.gymnast.view.home.HomeActivity;
import com.gymnast.view.live.activity.CloseLiveActivity;
import com.gymnast.view.live.activity.LiveActivity;

public class MyReceiver extends BroadcastReceiver {
    private NetworkInfo wifiNetInfo;
    private NetworkInfo mobileNetInfo;
   public static Dialog dialog;
    public MyReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final Activity activity=App.nowActivity;
        String action=intent.getAction();
        dialog = new AlertDialog.Builder(activity)
                .setTitle("网络设置")
                .setIcon(R.mipmap.timg)
                .setMessage("请您选择是否设置您的网络？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent("android.settings.WIFI_SETTINGS");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            Log.i("tag", "网络状态发生改变");
            ConnectivityManager manager= (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            mobileNetInfo=manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            wifiNetInfo=manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!mobileNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                Toast.makeText(activity, "亲，网络有点不给力呢！", Toast.LENGTH_SHORT).show();
                App.isNetStateOK=false;
                dialog.show();
            }else {
                if(manager.getActiveNetworkInfo().equals(mobileNetInfo)){
                    Toast.makeText(activity,"当前网络为数据连接，请注意您的流量使用情况!!!",Toast.LENGTH_SHORT).show();
                    App.isNetStateOK=true;
                }else if (manager.getActiveNetworkInfo().equals(wifiNetInfo)){
                    Toast.makeText(activity,"当前网络为WiFi连接!",Toast.LENGTH_SHORT).show();
                    App.isNetStateOK=true;
                }
                dialog.dismiss();
            }
        }
    }
}
