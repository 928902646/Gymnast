package com.gymnast.view.hotinfoactivity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.TimeUtil;
import com.gymnast.view.hotinfoactivity.activity.ActivityDetailsActivity;
import com.gymnast.view.hotinfoactivity.entity.CallBackDetailEntity;
import com.gymnast.view.hotinfoactivity.entity.CallBackEntity;
import com.gymnast.view.personal.activity.PersonalDynamicDetailActivity;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;
import com.gymnast.view.personal.activity.PersonalPostsDetailActivity;
import com.gymnast.view.personal.listener.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzqybyb19860112 on 2016/9/17.
 */
public class CallBackAdapter extends RecyclerView.Adapter {
    Context context;
    List<CallBackEntity> list;

    public CallBackAdapter( Context context,List<CallBackEntity> list) {
        this.context = context;
        if (list.size()==0){
            this.list=new ArrayList<>();
        }else {
            this.list = list;
        }
    }
    public static int callBackToID=0;
    public static int callBackToPOS=0;
    public static ArrayList<CallBackDetailEntity> MSGS=new ArrayList<>();
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View viewItem =inflater.inflate(R.layout.item_recylerview_callback, null);
        viewItem.setLayoutParams(lp);
        return new CallBackViewHolder(viewItem);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int pos) {
        if (viewHolder instanceof CallBackViewHolder){
            final CallBackViewHolder holder=(CallBackViewHolder)viewHolder;
           final CallBackEntity entity=list.get(pos);
            if (list.size()!=0){
                if (pos==list.size()-1){
                    holder.tvBottomLine.setVisibility(View.INVISIBLE);
                }
            }
            PicassoUtil.handlePic(context, entity.getCallBackImgUrl(), holder.rivCallBackPic, 720, 1080);
            holder.tvCallBackNickName.setText(entity.getCallBackNickName());
            holder.tvCallBackTime.setText(TimeUtil.getDetailTimeNumber(entity.getCallBackTime()));
            holder.tvCallBackContent.setText(entity.getCallBackText());
            final ArrayList<CallBackDetailEntity> detailMSGs=entity.getEntities();
            if (detailMSGs!=null&&detailMSGs.size()>0){
                CallBackDetailAdapter adapter=new CallBackDetailAdapter(context,detailMSGs);
                WrapContentLinearLayoutManager manager=new WrapContentLinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
                holder.rvCallBackDetail.setLayoutManager(manager);
                holder.rvCallBackDetail.setAdapter(adapter);
                adapter.setOnNameClickListener(new CallBackDetailAdapter.OnNameClickListener() {
                    @Override
                    public void OnNameClick(View view, int position, ArrayList<CallBackDetailEntity> detailMSGs, int toID,String toName) {
                        callBackToID = toID;
                        callBackToPOS = pos;
                        MSGS = detailMSGs;
                        if (context instanceof ActivityDetailsActivity) {
                            ActivityDetailsActivity.type=ActivityDetailsActivity.CALL_BACK_TYPE_TWO;
                            EditText editTextActive = ((EditText) ((ActivityDetailsActivity) context).findViewById(R.id.etCallBack));
                            if (editTextActive != null) {
                                editTextActive.requestFocus();
                                editTextActive.setHint("回复" + toName);
                                ActivityDetailsActivity.isComment = false;
                                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (inputManager.isActive()) {
                                    ((ActivityDetailsActivity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                    inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                                }
                            }
                        } else if (context instanceof PersonalPostsDetailActivity) {
                            PersonalPostsDetailActivity.type=PersonalPostsDetailActivity.CALL_BACK_TYPE_TWO;
                            EditText editTextTieZi = ((EditText) ((PersonalPostsDetailActivity) context).findViewById(R.id.etCallBackTieZi));
                            TextView tvSendTieZi = ((TextView) ((PersonalPostsDetailActivity) context).findViewById(R.id.tvSendTieZi));
                            ImageView ivSendTiezi = ((ImageView) ((PersonalPostsDetailActivity) context).findViewById(R.id.ivSendTiezi));
                            if (editTextTieZi != null) {
                                editTextTieZi.requestFocus();
                                editTextTieZi.setHint("回复" + toName);
                                PersonalPostsDetailActivity.isComment = false;
                                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (inputManager.isActive()) {
                                  ((PersonalPostsDetailActivity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                                tvSendTieZi.setVisibility(View.VISIBLE);
                                ivSendTiezi.setVisibility(View.GONE);
                                }
                            }
                        }else if (context instanceof PersonalDynamicDetailActivity){
                            PersonalDynamicDetailActivity.type=PersonalDynamicDetailActivity.CALL_BACK_TYPE_TWO;
                            EditText editTextTieZi = ((EditText) ((PersonalDynamicDetailActivity) context).findViewById(R.id.etCallBackDynamic));
                            TextView tvSendTieZi = ((TextView) ((PersonalDynamicDetailActivity) context).findViewById(R.id.tvSendDynamic));
                            ImageView ivSendTiezi = ((ImageView) ((PersonalDynamicDetailActivity) context).findViewById(R.id.ivSendDynamic));
                            if (editTextTieZi != null) {
                                editTextTieZi.requestFocus();
                                editTextTieZi.setHint("回复" + toName);
                                PersonalDynamicDetailActivity.isComment = false;
                                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (inputManager.isActive()) {
                                    ((PersonalDynamicDetailActivity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                    inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                                    tvSendTieZi.setVisibility(View.VISIBLE);
                                    ivSendTiezi.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
                holder.rvCallBackDetail.setVisibility(View.VISIBLE);
            }else {
                holder.rvCallBackDetail.setVisibility(View.GONE);
            }
            holder.rivCallBackPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context, PersonalOtherHomeActivity.class);
                    int userID=entity.getCommenterId();
                    i.putExtra("UserID", userID);
                    context.startActivity(i);
                }
            });
            if(onItemClickListener!=null){
                holder.tvBackCommenter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //注意，这里的position不要用上面参数中的position，会出现位置错乱\
                        onItemClickListener.OnCallBackClick(holder.tvBackCommenter,pos,detailMSGs);
                    }
                });
            }
        }
    }
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void OnCallBackClick(View view,int position,ArrayList<CallBackDetailEntity> detailMSGs);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    class CallBackViewHolder extends RecyclerView.ViewHolder{
        ImageView rivCallBackPic;
        TextView tvCallBackNickName;
        TextView tvCallBackTime;
        TextView tvCallBackContent;
        TextView tvBackCommenter;
        TextView tvBottomLine;
        RecyclerView rvCallBackDetail;
        public CallBackViewHolder(View itemView) {
            super(itemView);
            rivCallBackPic= (ImageView) itemView.findViewById(R.id.rivCallBackPic);
            tvCallBackNickName= (TextView) itemView.findViewById(R.id.tvCallBackNickName);
            tvCallBackTime= (TextView) itemView.findViewById(R.id.tvCallBackTime);
            tvCallBackContent= (TextView) itemView.findViewById(R.id.tvCallBackContent);
            rvCallBackDetail= (RecyclerView) itemView.findViewById(R.id.rvCallBackDetail);
            tvBackCommenter= (TextView) itemView.findViewById(R.id.tvBackCommenter);
            tvBottomLine= (TextView) itemView.findViewById(R.id.tvBottomLine);
        }
    }
}
