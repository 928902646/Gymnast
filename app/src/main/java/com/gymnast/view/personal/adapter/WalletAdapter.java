package com.gymnast.view.personal.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gymnast.R;
import com.gymnast.view.personal.fragment.WalletFragmengt;
import java.util.List;
import butterknife.ButterKnife;
/**
 * Created by yf928 on 2016/8/3.
 */
public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {
    private final List<WalletFragmengt.LiveItems> mValues;
    private Activity activity;
    public WalletAdapter(Activity activity, List<WalletFragmengt.LiveItems> items) {
        mValues=items;
        this.activity = activity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WalletFragmengt.LiveItems list = mValues.get(position);
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
