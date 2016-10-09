package com.gymnast.view.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.gymnast.R;
import com.gymnast.data.hotinfo.ConcerDevas;
import com.gymnast.data.net.API;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.view.personal.activity.PersonalDynamicDetailActivity;
import java.util.List;

public class HotInfoDynamicRecyclerViewAdapter  extends RecyclerView.Adapter<HotInfoDynamicRecyclerViewAdapter.ViewHolder> {
  private final List<ConcerDevas> mValues;
  private Activity activity;
  public HotInfoDynamicRecyclerViewAdapter(Activity activity, List<ConcerDevas> items) {
    mValues = items;
    this.activity = activity;
  }
  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.fragment_hot_info_dynamic, parent, false);
    return new ViewHolder(view);
  }
  @Override public void onBindViewHolder(final ViewHolder holder, int position) {
    final ConcerDevas dynamic = mValues.get(position);
    if (dynamic != null) {
      String imageUrl="";
      if (dynamic.bgmUrl==null||dynamic.bgmUrl.equals("")){
        imageUrl=dynamic.imageUrl;
      }else {
        imageUrl=dynamic.bgmUrl;
      }
      if (imageUrl.contains(",")){
        String urls[]=imageUrl.split(",");
        String imageUrl2= API.IMAGE_URL+urls[0];
        PicassoUtil.handlePic(activity, PicUtil.getImageUrl(activity,API.IMAGE_URL+imageUrl2), holder.dynamicPicture, 670, 372);
      }else {
        PicassoUtil.handlePic(activity, PicUtil.getImageUrl(activity,  API.IMAGE_URL+imageUrl), holder.dynamicPicture, 670, 372);
      }

      holder.dynamicPicture.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent=new Intent(activity,PersonalDynamicDetailActivity.class);
          intent.putExtra("CirleID",dynamic.id);
          activity.startActivity(intent);
        }
      });
    }
  }
  @Override public int getItemCount() {
    return mValues.size();
  }
  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.hot_info_dynamic_picture) ImageView dynamicPicture;
    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
