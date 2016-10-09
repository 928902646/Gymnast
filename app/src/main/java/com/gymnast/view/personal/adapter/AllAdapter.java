package com.gymnast.view.personal.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.gymnast.R;
import com.gymnast.view.personal.fragment.AllFragment;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yf928 on 2016/7/20.
 */
public class AllAdapter extends RecyclerView.Adapter<AllAdapter.ViewHolder> {
    private final List<AllFragment.LiveItems> mValues;
    private Activity activity;
    public AllAdapter(Activity activity, List<AllFragment.LiveItems> items) {
        mValues=items;
        this.activity = activity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_item,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AllFragment.LiveItems list = mValues.get(position);
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.commodity)
        ImageView commodity;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
