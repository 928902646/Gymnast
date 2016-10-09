package com.gymnast.view.personal.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gymnast.R;
import com.gymnast.view.personal.activity.PersonalAddressActivity;
import java.util.List;
import butterknife.ButterKnife;
/**
 * Created by yf928 on 2016/7/25.
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private final List<PersonalAddressActivity.LiveItems> mValues;
    private Activity activity;
    public AddressAdapter(Activity activity,List<PersonalAddressActivity.LiveItems> mValues){
        this.activity=activity;
        this.mValues = mValues;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PersonalAddressActivity.LiveItems list = mValues.get(position);
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
