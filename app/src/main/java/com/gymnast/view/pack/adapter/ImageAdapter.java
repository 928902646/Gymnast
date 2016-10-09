package com.gymnast.view.pack.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Cymbi on 2016/9/19.
 */
 public  class ImageAdapter extends PagerAdapter{
    ArrayList<ImageView> viewlist;
    Context context;
    public ImageAdapter( Context context,ArrayList<ImageView> viewlist) {
        this.context=context;
        if (viewlist.size()==0){
            this.viewlist=new ArrayList<>();
        }else {this.viewlist = viewlist;}

    }
    @Override
    public int getCount() {
        //设置成最大，使用户看不到边界
        return Integer.MAX_VALUE;
    }
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0==arg1;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //Warning：不要在这里调用removeView
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //对ViewPager页号求模取出View列表中要显示的项
        ImageView image=viewlist.get(position%viewlist.size());
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp=image.getParent();
        if(vp!=null){
            ViewGroup vg=(ViewGroup) vp;
            vg.removeView(image);
        }
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        container.addView(viewlist.get(position%viewlist.size()));
        return viewlist.get(position%viewlist.size());
    }
}

