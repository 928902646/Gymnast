package com.gymnast.view.home.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.gymnast.R;
import com.gymnast.data.hotinfo.LiveDevas;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import java.util.List;
public class HotInfoLiveRecyclerViewAdapter  extends RecyclerView.Adapter<HotInfoLiveRecyclerViewAdapter.ViewHolder> {
  private final List<LiveDevas> mValues;
  private Activity activity;
  public HotInfoLiveRecyclerViewAdapter(Activity activity, List<LiveDevas> items) {
    mValues = items;
    this.activity = activity;
  }
  private OnItemClickListener onItemClickListener;
  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }
  public interface OnItemClickListener {
    void OnBigPhotoClick(View view, LiveDevas liveDevas);
  }
  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.fragment_hot_info_live, parent, false);
    return new ViewHolder(view);
  }
  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    final LiveDevas live = mValues.get(position);
    if (live != null) {
      if (live.imageUrl==null||live.imageUrl.equals("")){
        PicassoUtil.handlePic(activity, PicUtil.getImageUrlDetail(activity, StringUtil.isNullAvatar(live.bgmUrl), 494, 368), holder.livePicture, 494, 368);
      }else {
        PicassoUtil.handlePic(activity, PicUtil.getImageUrlDetail(activity, StringUtil.isNullAvatar(live.imageUrl), 494, 368), holder.livePicture, 494, 368);
      }
      holder.liveTitle.setText(live.title);
      holder.liveViewer.setText(live.watchNumber+"");
    }
    if(onItemClickListener!=null){
      holder.livePicture.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //注意，这里的position不要用上面参数中的position，会出现位置错乱\
          onItemClickListener.OnBigPhotoClick(holder.livePicture, mValues.get(position));
        }
      });
    }
  }
  @Override public int getItemCount() {
    return mValues.size();
  }
  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.hot_info_live_picture) ImageView livePicture;
    @BindView(R.id.hot_info_live_viewer) TextView liveViewer;
    @BindView(R.id.hot_info_live_state) TextView liveState;
    @BindView(R.id.hot_info_live_title) TextView liveTitle;
    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
