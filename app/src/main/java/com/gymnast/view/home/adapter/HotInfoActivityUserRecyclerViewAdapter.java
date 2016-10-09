package com.gymnast.view.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.gymnast.R;
import com.gymnast.data.hotinfo.UserDevas;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.List;
public class HotInfoActivityUserRecyclerViewAdapter  extends RecyclerView.Adapter<HotInfoActivityUserRecyclerViewAdapter.ViewHolder> {
  private final List<UserDevas> mValues;
  private Activity activity;
  public HotInfoActivityUserRecyclerViewAdapter(Activity activity, List<UserDevas> items) {
    mValues = items;
    this.activity = activity;
  }
  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_hot_info_activity_user, parent, false);
    return new ViewHolder(view);
  }
  @Override public void onBindViewHolder(final ViewHolder holder, int position) {
    final UserDevas user = mValues.get(position);
    if (user != null) {
      String photo="";
      if (user.bgmUrl==null||user.bgmUrl.equals("")){
        photo=StringUtil.isNullAvatar(user.imageUrl);
      }else {
        photo=StringUtil.isNullAvatar(user.bgmUrl);
      }
      PicassoUtil.handlePic(activity,photo, holder.activityUserHead, 320, 320);
    }
    holder.activityUserHead.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i=new Intent(activity, PersonalOtherHomeActivity.class);
        i.putExtra("UserID",user.id);
        activity.startActivity(i);
      }
    });
  }
  @Override public int getItemCount() {
    return mValues.size();
  }
  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.hot_info_activity_user_picture) RoundedImageView activityUserHead;
    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
