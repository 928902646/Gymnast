package com.gymnast.view.personal.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.data.personal.SelectType;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Cymbi on 2016/8/29.
 */
public class SelectTypeAdapter extends BaseAdapter {
    private  List<SelectType> mValues ;
    private Activity activity;
    private TextView select_text;
    public SelectTypeAdapter( Activity activity,List<SelectType> mValues){
        this.activity = activity;
        if(mValues.size()==0){
            this.mValues = new ArrayList<>();
        }else {
            this.mValues=mValues;
        }
    }
    @Override
    public int getCount() {
        return mValues.size();
    }
    @Override
    public Object getItem(int position) {
        return mValues.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater  inflater = LayoutInflater.from(activity);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.type_item, null);
        }
        select_text=(TextView)convertView.findViewById(R.id.select_text);
        SelectType typeitem=  mValues.get(position);
        select_text.setText(typeitem.getTypeName());
        return convertView;
    }
}
