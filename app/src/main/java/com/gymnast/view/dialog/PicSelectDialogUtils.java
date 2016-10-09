package com.gymnast.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.gymnast.App;
import com.gymnast.R;
import java.io.File;
import java.util.List;
/**
 * Created by zh on 2016/8/22.
 */
public class PicSelectDialogUtils {
    private static Dialog dialog;
    public final static int IMAGE_OPEN = 0;
    public final static int PIC_FROM_CAMERA = 2;
    public static String CURRENT_FILENAME;
    public static List<Bitmap> BITMAPS;
    public static void showDialogs(final Activity context) {
        CURRENT_FILENAME = App.getInstence().getPhotopath();
        if (context == null)
            return;
        if (dialog != null)
            dialog = null;
        dialog = new Dialog(context, R.style.Dialog_Fullscreen);
        View view = View.inflate(context, R.layout.dialog_phone, null);
        TextView camera = (TextView) view.findViewById(R.id.camera);
        TextView gallery = (TextView) view.findViewById(R.id.gallery);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //从相册获取
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                File out = new File(CURRENT_FILENAME);
                Uri uri = Uri.fromFile(out);
                // 获取拍照后未压缩的原图片，并保存在uri路径中
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                context.startActivityForResult(intent, IMAGE_OPEN);
                dialog.dismiss();
                dialog = null;
            }
        });
        //拍照
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                File out = new File(CURRENT_FILENAME);
                Uri uri = Uri.fromFile(out);
                // 获取拍照后未压缩的原图片，并保存在uri路径中
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                context.startActivityForResult(intent, PIC_FROM_CAMERA);
                dialog.dismiss();
                dialog = null;
            }
        });
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = context.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
