package com.gymnast.view.hotinfoactivity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gymnast.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cymbi on 2016/9/14.
 */
public class AuditAdapter extends RecyclerView.Adapter {
    Context context;
    List<String> keyList;
    List<String> valueList;

    public AuditAdapter(Context context, List<String> keyList,List<String> valueList) {
        this.context = context;
        if(keyList.size()==0){
            this.keyList=new ArrayList<>();
        }else {
            this.keyList = keyList;
        }
        if(valueList.size()==0){
            this.valueList=new ArrayList<>();
        }else {
            this.valueList = valueList;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater= LayoutInflater.from(context);
        View view= mInflater.inflate(R.layout.item_recycle_audit,null);
        return new AuditHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof AuditHolder){
            AuditHolder viewholder= (AuditHolder)holder;
                if(keyList.get(position).equals("地址")){
                            viewholder.tvText.setText(valueList.get(position));
                            Drawable drawable=context.getResources().getDrawable(R.mipmap.btn_adress);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            viewholder.tvText.setCompoundDrawables(drawable,null,null,null);
                            viewholder.tvText.setCompoundDrawablePadding(40);
                }else if(keyList.get(position).equals("身份证")){
                            viewholder.tvText.setText(valueList.get(position));
                            Drawable drawable=context.getResources().getDrawable(R.mipmap.btn_means);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            viewholder.tvText.setCompoundDrawables(drawable,null,null,null);
                            viewholder.tvText.setCompoundDrawablePadding(40);
            }
        }
    }
    @Override
    public int getItemCount() {
        return valueList.size();
    }
    class AuditHolder extends RecyclerView.ViewHolder{
        private final TextView tvText;
        public AuditHolder(View itemView) {
            super(itemView);
            tvText=(TextView) itemView.findViewById(R.id.tvText);
        }
    }
}
