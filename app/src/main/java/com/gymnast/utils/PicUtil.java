package com.gymnast.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.WindowManager;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by Administrator on 2016/8/3.
 */
public class PicUtil {
    public static Bitmap compress(Bitmap bitmap,int width,int height){
        return  ThumbnailUtils.extractThumbnail(bitmap, width, height);
    }
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 设置为ture,只读取图片的大小，不把它加载到内存中去
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 1280, 720);// 此处，选取了480x800分辨率的照片
        options.inJustDecodeBounds = false;// 处理完后，同时需要记得设置为false
        Bitmap bitmap=BitmapFactory.decodeFile(filePath, options);
        return bitmap;
    }
    // 计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    public static Bitmap getImageBitmap(String path) {
        try {
            URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == 200){
                InputStream inStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inStream);
                return bitmap;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String getImageUrl(Context context,String url){
        String resultUrl="";
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth()-48;
        String addUrl=width+"x384";
        String baseUrl=url.substring(0,url.lastIndexOf("."));
        String resUrl=url.substring(url.lastIndexOf("."),url.length());
        resultUrl=baseUrl+"__"+addUrl+resUrl;
        return resultUrl;
    }
    public static String getImageUrlDetail(Context context,String url,int width,int height){
        String resultUrl="";
        String type=url.substring(url.lastIndexOf("."),url.length());
        if (type.equals(".gif")){
            resultUrl=url;
        }else {
            String addUrl=width+"x"+height;
            String baseUrl=url.substring(0,url.lastIndexOf("."));
            String resUrl=url.substring(url.lastIndexOf("."),url.length());
            resultUrl=baseUrl+"__"+addUrl+resUrl;
        }
        return resultUrl;
    }
}
