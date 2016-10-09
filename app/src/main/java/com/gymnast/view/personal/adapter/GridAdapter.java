package com.gymnast.view.personal.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.gymnast.R;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.view.dialog.PicSelectDialogUtils;
import com.gymnast.view.personal.activity.ImageActivity;
import com.gymnast.view.picker.BigImageActivity;

import java.util.ArrayList;
/**
 * Created by Cymbi on 2016/8/31.
 */
public class GridAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> datas;
    public GridAdapter(Context context, ArrayList<String> datas) {
        this.context = context;
        if (datas.size()!=0){
            this.datas = datas;
        }else {
            this.datas=new ArrayList<>();
        }
    }
    @Override
    public int getCount() {
        return datas.size();
    }
    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder holder=null;
        if (convertView==null){
            convertView=View.inflate(context, R.layout.dynamic_gridview_item,null);
            holder=new MyViewHolder();
            convertView.setTag(holder);
        }else {
            holder= (MyViewHolder) convertView.getTag();
        }
        holder.ivMain= (ImageView) convertView.findViewById(R.id.ivMain);
        final String url=datas.get(position);
        PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, url, 226, 226),holder.ivMain,226,226);
        holder.ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,ImageActivity.class);
                i.putExtra("IMAGE",url);
                context.startActivity(i);
          /*         Intent intent = new Intent(context, BigImageActivity.class);
                    Bundle bd = new Bundle();
                    bd.putBoolean("isUrl", false);
                    ArrayList<Bitmap> bitmaps = new ArrayList<>();
                for(int i=0;i<datas.size();i++){
                    String item=datas.get(i);
                    final Bitmap bitmap = PicUtil.getSmallBitmap(item);
                    bitmaps.add(bitmap);
                }
                    PicSelectDialogUtils.BITMAPS = bitmaps;
                    bd.putInt("position", Integer.parseInt(url));
                    intent.putExtra("data", bd);
                context.startActivity(intent);*/
            }
        });
        return convertView;
    }
    class MyViewHolder{
        ImageView ivMain;
    }
}
