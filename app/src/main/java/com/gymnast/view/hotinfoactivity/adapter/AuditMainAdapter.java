package com.gymnast.view.hotinfoactivity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gymnast.R;
import com.gymnast.data.hotinfo.AuditData;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.hotinfoactivity.activity.AuditActivity;
import com.gymnast.view.hotinfoactivity.customView.MyCMDTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cymbi on 2016/9/16.
 */
public class AuditMainAdapter extends RecyclerView.Adapter {
    Context context;
    List<AuditData> list;
    public AuditMainAdapter(Context context, List<AuditData> list) {
        this.context = context;
        if(list.size()==0){
            this.list =new ArrayList<>();
        }else {
            this.list = list;
        }
    }
    List<CheckBox> listCheckBox=new ArrayList<>();
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.item_audit,parent,false);
        return new MainHolder(view);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MainHolder){
            final MainHolder viewholder=(MainHolder)holder;
            AuditData auditData=list.get(position);
            ((AuditActivity)context).setOnItemClickListener(new AuditActivity.CheckBoxShowCMD() {
                @Override
                public void showOrHide(boolean isShow) {
                    for (CheckBox checkBox:listCheckBox){
                    if (isShow){
                        checkBox.setVisibility(View.GONE);
                    }else {
                        checkBox.setVisibility(View.VISIBLE);
                    }
                }
                }
            });
            PicassoUtil.handlePic(context, PicUtil.getImageUrlDetail(context, StringUtil.isNullAvatar(auditData.getAvatar()), 72, 72),viewholder.ivAvatar,72,72);
            viewholder.tvUserName.setText(auditData.getUsername());
            viewholder.tvRealName.setText(auditData.getRealname());
            viewholder.tvSex.setText(auditData.getSex());
            String age=auditData.getAge().toString();
            if(!age.equals("")&&age!=null&&!age.equals("null")){
                viewholder.tvAge.setVisibility(View.VISIBLE);
                viewholder.tvAge.setText(age);
            }else {
                viewholder.tvAge.setVisibility(View.GONE);
            }
            if(!auditData.getAddress().equals("")&&auditData.getAddress()!=null&&!auditData.getAddress().equals("null")){
                viewholder.tvAddress.setVisibility(View.VISIBLE);
                viewholder.tvAddress.setText(auditData.getAddress());
            }else {
                viewholder.tvAddress.setVisibility(View.GONE);
            }
            if(!auditData.getId().equals("")&&auditData.getId()!=null&&!auditData.getId().equals("null")){
                viewholder.tvID.setVisibility(View.VISIBLE);
                viewholder.tvID.setText(auditData.getId());
            }else {
                viewholder.tvID.setVisibility(View.GONE);
            }
            viewholder.tvPhone.setText(auditData.getPhone());
            if(auditData.getExamineType()==0){
                viewholder.tvPass.setText("未通过");
            }else {
                viewholder.tvPass.setText("通过");
            }
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    class MainHolder extends RecyclerView.ViewHolder{
        private final ImageView ivAvatar;
        private final TextView tvUserName,tvRealName,tvAddress,tvPhone,tvSex,tvAge,tvID,tvPass;
        private CheckBox cbSelect;
        public MainHolder(View itemView) {
            super(itemView);
            ivAvatar= (ImageView)itemView.findViewById(R.id.ivAvatar);
            tvUserName=(TextView)itemView.findViewById(R.id.tvUserName);
            tvRealName=(TextView)itemView.findViewById(R.id.tvRealName);
            tvAddress=(TextView)itemView.findViewById(R.id.tvAddress);
            tvPhone=(TextView)itemView.findViewById(R.id.tvPhone);
            tvSex=(TextView)itemView.findViewById(R.id.tvSex);
            tvAge=(TextView)itemView.findViewById(R.id.tvAge);
            tvID=(TextView)itemView.findViewById(R.id.tvID);
            tvPass=(TextView)itemView.findViewById(R.id.tvPass);
            cbSelect= (CheckBox) itemView.findViewById(R.id.cbSelect);
            listCheckBox.add(cbSelect);
        }
    }
}