package com.gymnast.utils;

import android.util.Log;
import com.gymnast.App;

/**
 * Created by zzqybyb19860112 on 2016/8/25.
 */
public class LogUtil {
    public static void i(String tag,String content){
        if (App.debug){
            Log.i(tag,content);
        }else {
        }
    }
}
