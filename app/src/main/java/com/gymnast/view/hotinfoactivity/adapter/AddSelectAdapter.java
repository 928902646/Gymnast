package com.gymnast.view.hotinfoactivity.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.recyclerview.RecycleHolder;
import com.gymnast.view.recyclerview.RecyclerAdapter;
import java.util.ArrayList;

/**
 * Created by zh on 2016/8/22.
 */
public class AddSelectAdapter extends RecyclerAdapter<String> implements View.OnClickListener {
    public boolean isDel = false;
    public AddSelectAdapter(Context mContext, ArrayList mDatas, int mLayoutId) {
        super(mContext, mDatas, mLayoutId);
    }
    @Override
    public void convert(final RecycleHolder holder, String data, int position) {
        TextView picView = holder.<TextView>findView(R.id.tvAdd);
        final ImageView delPicView = holder.<ImageView>findView(R.id.photo_del_iv);
        int truePosition = holder.getLayoutPosition();
        picView.setText(data);
        if (truePosition != mDatas.size()-1) {
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
