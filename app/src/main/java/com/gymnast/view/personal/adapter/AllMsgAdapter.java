package com.gymnast.view.personal.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gymnast.R;
import com.gymnast.view.personal.activity.PersonalAllMsgActivity;
import java.util.List;
import butterknife.ButterKnife;
/**
 * Created by yf928 on 2016/8/1.
 */
public class AllMsgAdapter extends RecyclerView.Adapter<AllMsgAdapter.ViewHolder> {
    private final List<PersonalAllMsgActivity.LiveItems> mValues;
    private Activity activity;
    public AllMsgAdapter(Activity activity,List<PersonalAllMsgActivity.LiveItems> mValues){
        this.activity=activity;
        this.mValues = mValues;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PersonalAllMsgActivity.LiveItems list = mValues.get(position);
    }
    @Override
    public int getItemCount() {
        return  mValues.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
