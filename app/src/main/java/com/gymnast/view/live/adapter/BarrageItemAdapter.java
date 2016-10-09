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
import com.gymnast.view.live.entity.BarrageItem;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Created by 永不言败 on 2016/8/8.
 */
public class BarrageItemAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<BarrageItem> lists;
    public BarrageItemAdapter(Context context,ArrayList<BarrageItem> lists){
        this.context=context;
        if (lists.size()==0){
            this.lists=new ArrayList<>();
        }else {
            this.lists=lists;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycleview_barrage, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewItem.setLayoutParams(lp);
        return new BarrageViewHolder(viewItem);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final BarrageViewHolder viewHolder= (BarrageViewHolder) holder;
        BarrageItem item=lists.get(position);
        PicassoUtil.handlePic(context, item.getPhotoUrl(), viewHolder.civPhoto, 72, 72);
        viewHolder.tvTime.setText(item.getTime());
        viewHolder.tvBody.setText(item.getBody());
        final int[] priseNo = {item.getPriseNumber()};
        viewHolder.tvPriseNumber.setText(priseNo[0] +"");//其他人点了赞也要能显示
        viewHolder.tvPriseNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priseNo[0]++;
                viewHolder.tvPriseNumber.setText(priseNo[0] +"");
                viewHolder.tvPriseNumber.invalidate();
            }
        });
        viewHolder.tvName.setText(item.getName());
    }
    @Override
    public int getItemCount() {
        return lists.size();
    }
    class BarrageViewHolder extends RecyclerView.ViewHolder{
        CircleImageView civPhoto;
        TextView tvTime,tvBody,tvPriseNumber,tvName;
        public BarrageViewHolder(View itemView) {
            super(itemView);
            civPhoto= (CircleImageView) itemView.findViewById(R.id.civPhoto);
            tvTime= (TextView) itemView.findViewById(R.id.tvTime);
            tvBody= (TextView) itemView.findViewById(R.id.tvBody);
            tvPriseNumber= (TextView) itemView.findViewById(R.id.tvPriseNumber);
            tvName= (TextView) itemView.findViewById(R.id.tvName);
        }
    }
}

