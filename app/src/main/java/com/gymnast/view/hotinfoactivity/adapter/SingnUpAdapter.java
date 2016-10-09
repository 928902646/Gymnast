package com.gymnast.view.hotinfoactivity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.gymnast.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cymbi on 2016/9/10.
 */
public class SingnUpAdapter extends RecyclerView.Adapter {
    Context context;
    List<String> list;
    public SingnUpAdapter(Context context, List<String> list) {
        this.context = context;
        if(list.size()==0){
            this.list=new ArrayList<>();
        }else {
            this.list = list;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater= LayoutInflater.from(context);
        View view= mInflater.inflate(R.layout.item_edit_info,parent,false);
        return new SingnupHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SingnupHolder){
            SingnupHolder  holder=(SingnupHolder)viewHolder;
            holder.tvTitle.setText(list.get(position));
            holder.edit.setHint("请输入"+list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SingnupHolder extends RecyclerView.ViewHolder{
        private final TextView tvTitle;
        private final EditText edit;
        public SingnupHolder(View itemView) {
            super(itemView);
            tvTitle=(TextView) itemView.findViewById(R.id.tvTitle);
            edit=(EditText) itemView.findViewById(R.id.edit);
        }
    }
}
