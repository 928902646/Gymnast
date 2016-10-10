package com.gymnast.view.live.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gymnast.R;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.view.live.customview.BarrageView;
import com.gymnast.view.live.entity.BarrageViewEntity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zzqybyb19860112 on 2016/10/9.
 */
public class BarrageViewAdapter extends RecyclerView.Adapter {
    Context context;
    List<BarrageViewEntity> barrageList;

    public BarrageViewAdapter(Context context, List<BarrageViewEntity> barrageList) {
        this.context = context;
        if (barrageList.size()==0){
            this.barrageList=new ArrayList<>();
        }else {
            this.barrageList = barrageList;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barrage, null);
        return new BarrageHolder(viewItem);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        BarrageHolder holder= (BarrageHolder) viewHolder;
        if (position==barrageList.size()-1){
            holder.itemView.setPadding(0,0,0,30);
        }else {
            holder.itemView.setPadding(0,0,0,20);
        }
        BarrageViewEntity entity=barrageList.get(position);
        PicassoUtil.handlePic(context,entity.getPicUrl(),holder.rivPhoto,54,54);
        holder.tvBarrageText.setText(entity.getContent());
    }
    @Override
    public int getItemCount() {
        return barrageList.size();
    }
    class BarrageHolder extends RecyclerView.ViewHolder{
        com.makeramen.roundedimageview.RoundedImageView rivPhoto;
        TextView tvBarrageText;
        public BarrageHolder(View itemView) {
            super(itemView);
            rivPhoto= (com.makeramen.roundedimageview.RoundedImageView) itemView.findViewById(R.id.rivPhoto);
            tvBarrageText= (TextView) itemView.findViewById(R.id.tvBarrageText);
        }
    }
}
