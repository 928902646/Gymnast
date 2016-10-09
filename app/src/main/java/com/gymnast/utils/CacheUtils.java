package com.gymnast.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzqybyb19860112 on 2016/9/24.
 */
public class CacheUtils {
    public static File CacheRoot;
             /** 
          * 存储Json文件 
          * @param context 
          * @param json     json字符串 
          * @param fileName   存储的文件名 
          * @param append    true 增加到文件末，false则覆盖掉原来的文件 
          */
      public static boolean writeJson(Context c, String json, String fileName, boolean append) {
         CacheRoot = Environment.getExternalStorageState() .equals(Environment.MEDIA_MOUNTED ) ? c .getExternalCacheDir():c.getCacheDir();
         FileOutputStream fos=null;
         ObjectOutputStream os =null;
         try {
             File ff = new File(CacheRoot, fileName);
             Log.i("tag","write------>"+ff.getPath());
             boolean boo=ff.exists();
             fos=new FileOutputStream(ff, append);
             os=new ObjectOutputStream(fos);
             if (append && boo) {
                 FileChannel fc=fos.getChannel();
                 fc.truncate(fc.position() - 4);
                 }
             os.writeObject(json);
             } catch ( Exception e) {
             e.printStackTrace();
         }  finally {
             if (fos != null) {
                 try {
                     fos.close();
                     } catch ( Exception e) {
                     e.printStackTrace();
                     }
                 } if (os != null) {
                 try { os.close(); } catch ( Exception e) {
                     e.printStackTrace();
                 }
             }
         }
          return true;
             }
             /** 
          * 读取json数据 
          * @param c 
          * @param fileName 
          * @return 返回值为list 
          */
             @SuppressWarnings("resource")
     public static List<String> readJson(Context c, String fileName) {
          CacheRoot = Environment.getExternalStorageState() .equals(Environment.MEDIA_MOUNTED ) ? c .getExternalCacheDir():c.getCacheDir();
          FileInputStream fis=null;
         ObjectInputStream ois=null;
         List<String> result=new ArrayList<String>();
         File des=new File(CacheRoot, fileName);
         try {
             fis=new FileInputStream(des);
             ois=new ObjectInputStream(fis);
             while (fis.available()>0)
             result.add((String) ois.readObject());
             } catch ( Exception e) {
             e.printStackTrace();
         } finally {
             if (fis != null) {
                  try {
                      fis.close();
                  } catch ( Exception e) {
                      e.printStackTrace();
                  }
             }
             if (ois != null) {
                 try {
                     ois.close();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }
                 return result;
             }
}
