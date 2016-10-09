package com.gymnast.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.gymnast.data.net.API;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.UUID;
/**
 * Created by zzqybyb19860112 on 2016/8/15.
 */
public class UploadUtil {
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    public static String uploadFile(File file, String RequestURL) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"avatar\"; filename=\""+ file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                    }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                //int res = conn.getResponseCode();
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                    }
                result = sb1.toString();
                }
            } catch (Exception e) {
            e.printStackTrace();
            }
        return result;
       }
    public static String uploadFile2(String url,String path){
        HttpClient httpClient=new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent","SOHUWapRebot");
        httpPost.setHeader("Accept-Language","zh-cn,zh");
        httpPost.setHeader("Accept-Charset","GBK,utf-8;");
        httpPost.setHeader("Connection","keep-alive");//keep-alive
        MultipartEntity mutiEntity = new MultipartEntity();
        File file = new File(path);
        mutiEntity.addPart("avatar", new FileBody(file));
        httpPost.setEntity(mutiEntity);
        String content=null;
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity =httpResponse.getEntity();
            content= EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
    public static String getAbsoluteImagePath(Activity activity,Uri uri) {
        String imagePath = "";
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor =activity.managedQuery(uri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                imagePath = cursor.getString(column_index);
            }
        }
        return imagePath;
    }
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 设置为ture,只读取图片的大小，不把它加载到内存中去
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 720, 1280);// 此处，选取了480x800分辨率的照片
        options.inJustDecodeBounds = false;// 处理完后，同时需要记得设置为false
        return BitmapFactory.decodeFile(filePath, options);
    }
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
    public static String saveBitMapToFile(Context context, String fileName, Bitmap bitmap, boolean isCover) {
        if(null == context || null == bitmap) {
            return null;
        }
        if(TextUtils.isEmpty(fileName)) {
            return null;
        }
        FileOutputStream fOut = null;
        try {
            File file = null;
            String fileDstPath = "";
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                // 保存到sd卡
                fileDstPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "MyFile" + File.separator + fileName;

                File homeDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "MyFile" + File.separator);
                if (!homeDir.exists()) {
                    homeDir.mkdirs();
                }
            } else {
                // 保存到file目录
                fileDstPath = context.getFilesDir().getAbsolutePath()
                        + File.separator + "MyFile" + File.separator + fileName;
                File homeDir = new File(context.getFilesDir().getAbsolutePath()
                        + File.separator + "MyFile" + File.separator);
                if (!homeDir.exists()) {
                    homeDir.mkdir();
                }
            }
            file = new File(fileDstPath);
            if (!file.exists() || isCover) {
                // 简单起见，先删除老文件，不管它是否存在。
                file.delete();
                fOut = new FileOutputStream(file);
                if (fileName.endsWith(".jpg")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fOut);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                }
                fOut.flush();
                bitmap.recycle();
            }
            return fileDstPath;
        } catch (Exception e) {
            return null;
        } finally {
            if(null != fOut) {
                try {
                    fOut.close();
                } catch (Exception e) {
                }
            }
        }
    }
    public static String getNetWorkImageAddress(final String path,final Context context) {
        String newUrl =null;
                try {
                    String uri= API.BASE_URL+"/v1/upload";
                    Bitmap bitmap=UploadUtil.getSmallBitmap(path);
                    String name=path.substring(path.lastIndexOf("/") + 1, path.length());
                    String newPath=UploadUtil.saveBitMapToFile(context, name, bitmap, true);
                    String result=UploadUtil.uploadFile2(uri, newPath);
                    JSONObject object=new JSONObject(result);
                    JSONObject data=object.getJSONObject("data");
                    newUrl = URI.create(data.getString("url")).getPath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        return newUrl;
    }
}