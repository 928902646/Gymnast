package com.gymnast.view.hotinfoactivity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.hotinfoactivity.entity.CallBackDetailEntity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzqybyb19860112 on 2016/9/17.
 */
public class CallBackDetailAdapter extends RecyclerView.Adapter {
    Context context;
    List<CallBackDetailEntity> list;
    public static int TYPE_COMMENT=1;
    public CallBackDetailAdapter(Context context, List<CallBackDetailEntity> list) {
        this.context = context;
        if (list.size()==0){
            this.list=new ArrayList<>();
        }else {
            this.list = list;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (viewType==TYPE_COMMENT){
            View viewItem =inflater.inflate(R.layout.item_callback_comment, null);
            viewItem.setLayoutParams(lp);
            return new CommentViewHolder(viewItem);
        }
        View viewItem =inflater.inflate(R.layout.item_rv_callback_detail, null);
        viewItem.setLayoutParams(lp);
        return new CallBackDetailViewHolder(viewItem);
    }
    @Override
    public int getItemViewType(int position) {
        CallBackDetailEntity entity= list.get(position);
        if (entity.getTo()==null||entity.getTo().equals("")) {
            return TYPE_COMMENT;
        }
        return super.getItemViewType(position);
    }
    private OnNameClickListener onNameClickListener;
    public void setOnNameClickListener(OnNameClickListener onNameClickListener) {
        this.onNameClickListener = onNameClickListener;
    }
    public interface OnNameClickListener {
        void OnNameClick(View view,int position,ArrayList<CallBackDetailEntity> detailMSGs,int toID,String toNickName);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final CallBackDetailEntity entity=list.get(position);
        if (viewHolder instanceof CallBackDetailViewHolder){
           final CallBackDetailViewHolder holder=(CallBackDetailViewHolder)viewHolder;
            holder.tvFrom.setText(entity.getFrom());
            holder.tvTo.setText(entity.getTo());
            holder.tvContent.setText(entity.getContent());
            if(onNameClickListener!=null){
                holder.tvFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //注意，这里的position不要用上面参数中的position，会出现位置错乱\
                        onNameClickListener.OnNameClick(holder.tvFrom, position, (ArrayList<CallBackDetailEntity>) list,list.get(position).getFromID(),holder.tvFrom.getText().toString());
                    }
                });
                holder.tvTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //注意，这里的position不要用上面参数中的position，会出现位置错乱\
                        onNameClickListener.OnNameClick(holder.tvTo, position, (ArrayList<CallBackDetailEntity>) list,list.get(position).getToID(),holder.tvTo.getText().toString());
                    }
                });
            }
        }else if (viewHolder instanceof CommentViewHolder){
            final CommentViewHolder holder= (CommentViewHolder) viewHolder;
            holder.tvCommenter.setText(entity.getFrom());
            holder.tvCommentText.setText("："+entity.getContent());
            if(onNameClickListener!=null){
                holder.tvCommenter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onNameClickListener.OnNameClick(holder.tvCommenter,position,(ArrayList<CallBackDetailEntity>) list,list.get(position).getFromID(),holder.tvCommenter.getText().toString());
                    }
                });
            }
        }

    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    class CallBackDetailViewHolder extends RecyclerView.ViewHolder{
        TextView tvFrom;
        TextView tvTo;
        TextView tvContent;
        public CallBackDetailViewHolder(View itemView) {
            super(itemView);
            tvFrom= (TextView) itemView.findViewById(R.id.tvFrom);
            tvTo= (TextView) itemView.findViewById(R.id.tvTo);
            tvContent= (TextView) itemView.findViewById(R.id.tvContent);
        }
    }
    class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView tvCommenter,tvCommentText;
        public CommentViewHolder(View itemView) {
            super(itemView);
            tvCommenter= (TextView) itemView.findViewById(R.id.tvCommenter);
            tvCommentText= (TextView) itemView.findViewById(R.id.tvCommentText);
        }
    }
}
