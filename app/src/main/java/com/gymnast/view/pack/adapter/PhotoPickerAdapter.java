package com.gymnast.view.pack.adapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import com.gymnast.R;
import com.gymnast.view.dialog.PicSelectDialogUtils;
import com.gymnast.view.picker.BigImageActivity;
import com.gymnast.view.recyclerview.RecycleHolder;
import com.gymnast.view.recyclerview.RecyclerAdapter;
import java.util.ArrayList;
/**
 * Created by zh on 2016/8/22.
 */
public class PhotoPickerAdapter extends RecyclerAdapter<Bitmap> implements View.OnClickListener {
    public boolean isDel = false;
    public PhotoPickerAdapter(Context mContext, ArrayList mDatas, int mLayoutId) {
        super(mContext, mDatas, mLayoutId);
    }
    @Override
    public void convert(final RecycleHolder holder, Bitmap data, int position) {
        ImageView picView = holder.<ImageView>findView(R.id.photo_iv);
        final ImageView delPicView = holder.<ImageView>findView(R.id.photo_del_iv);
        int truePosition = holder.getLayoutPosition();
        picView.setImageBitmap(data);
        if (truePosition != mDatas.size() - 1) {
            delPicView.setVisibility(isDel ? View.VISIBLE : View.GONE);
            picView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!isDel) {
                        isDel = true;
                        notifyDataSetChanged();
                    }
                    return true;
                }
            });
        } else {
            delPicView.setVisibility(View.GONE);
        }
        delPicView.setOnClickListener(this);
        delPicView.setTag(truePosition);
        picView.setOnClickListener(this);
        picView.setTag(truePosition);
    }
    @Override
    public void onClick(View view) {
        final int position = (int) view.getTag();
        switch (view.getId()) {
            case R.id.photo_iv:
                if (position != getItemCount() - 1) {
                    Intent intent = new Intent(mContext, BigImageActivity.class);
                    Bundle bd = new Bundle();
                    bd.putBoolean("isUrl", false);
                    ArrayList<Bitmap> bitmaps = new ArrayList<>(mDatas);
                    bitmaps.remove(bitmaps.size() - 1);
                    PicSelectDialogUtils.BITMAPS = bitmaps;
                    bd.putInt("position", position);
                    intent.putExtra("data", bd);
                    mContext.startActivity(intent);
                } else {
                    PicSelectDialogUtils.showDialogs((Activity) mContext);
                }
                break;
            case R.id.photo_del_iv:
                new AlertDialog.Builder(mContext).setTitle("提示!").setMessage("确认删除?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatas.remove(position);
                        //以下为数据刷新操作
                        notifyItemRemoved(position);
                        if (mDatas.size() == 1)
                            notifyItemChanged(0);
                        else
                            notifyItemRangeChanged(position, getItemCount());
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
                break;
        }
    }
}
