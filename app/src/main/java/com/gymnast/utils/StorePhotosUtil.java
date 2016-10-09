package com.gymnast.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
public class StorePhotosUtil {
	FileOutputStream fos;
	private Bitmap bitmap;
	private String fileName;
	public StorePhotosUtil(Bitmap bitmap, String fileName) {
		this.bitmap=bitmap;
		this.fileName=fileName;
		//调用存储方法
		store();
	}
	private void store() {
		try {
			if (bitmap!=null) {
				fos = new FileOutputStream(fileName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try {
				if (bitmap!=null) {
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
