package com.gymnast.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by zzqybyb19860112 on 2016/8/29.
 */
public class PicassoUtil {
    public static void handlePic(Context context,String url,ImageView view,int width,int height){
       String type=url.substring(url.lastIndexOf("."),url.length());
        if (type.equals(".gif")){
            Glide.with(context).load(url).into(view);
        }else {
            Picasso.with(context).load(url).config(Bitmap.Config.RGB_565).resize(width, height)
                    .centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(view);
        }
    }
}
