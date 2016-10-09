package com.gymnast.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.gymnast.App;
import com.gymnast.R;
import com.gymnast.view.home.HomeActivity;
import com.gymnast.view.user.LoginActivity;

/**
 * Created by zzqybyb1986 on 2016/8/20.
 */
public class DialogUtil {
    public static Dialog createDialog(Context context,String title,String message){
        ProgressDialog  dialog = new ProgressDialog(context);
            dialog.setTitle(title);//设置标题
            dialog.setMessage(message); //设置说明文字
            dialog.setIndeterminate(false); //设置进度条是否为不明确(来回旋转)
            dialog.setCanceledOnTouchOutside(false); //设置点击屏幕不消失
            dialog.setCancelable(true); //设置进度条是否可以按退回键取消
            Window wd= dialog.getWindow(); //获取屏幕管理器
            WindowManager.LayoutParams lp = wd.getAttributes();
            lp.alpha = 0.8f; //设置循环框的透明度
            wd.setAttributes(lp); //设置弹出框的透明度
            wd.setGravity(Gravity.CENTER); //设置水平居中
            dialog.show();
        return dialog;
    }
    public static void goBackToLogin(final Context context,String title,String message){
         Dialog dialog=new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(R.mipmap.wrong)
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        dialogInterface.dismiss();
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        App.isStateOK=false;
                        Intent intent = new Intent(context, HomeActivity.class);
                        context.startActivity(intent);
                        dialogInterface.dismiss();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
