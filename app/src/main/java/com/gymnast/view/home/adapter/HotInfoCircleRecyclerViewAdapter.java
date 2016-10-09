package com.gymnast.view.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.gymnast.R;
import com.gymnast.data.hotinfo.CirleDevas;
import com.gymnast.utils.GlobalInfoUtil;
import com.gymnast.utils.PicassoUtil;
import com.gymnast.utils.StringUtil;
import com.gymnast.view.personal.activity.PersonalCircleActivity;
import com.gymnast.view.personal.activity.PersonalOtherHomeActivity;
import com.gymnast.view.personal.activity.PersonalPostsDetailActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
public class HotInfoCircleRecyclerViewAdapter extends RecyclerView.Adapter<HotInfoCircleRecyclerViewAdapter.ViewHolder> {
  private final List<CirleDevas> mValues;
  private Activity activity;
  public HotInfoCircleRecyclerViewAdapter(Activity activity, List<CirleDevas> items) {
    mValues = items;
    this.activity = activity;
  }
  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.fragment_hot_info_circle, parent, false);
    return new ViewHolder(view);
  }
  @Override public void onBindViewHolder(final ViewHolder holder, int position) {
    final CirleDevas circle = mValues.get(position);
    if (circle != null) {
      String imageUrl= StringUtil.isNullAvatar(circle.userIconVo.avatar);
      PicassoUtil.handlePic(activity, imageUrl, holder.circleUserHead, 320, 320);
      SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日 HH:mm");
      String time=sdf.format(new Date(circle.createTime))+"";
      if (time.startsWith("0")){
        time=time.substring(1);
      }
      holder.circleArticleTime.setText(time);
      holder.circleArticleTitle.setText(circle.title+"");
      holder.circleArticleContent.setText(circle.baseContent + "");
      holder.circleFrom.setText(circle.circleTitle+"");
      holder.circleUserName.setText(circle.userIconVo.nickname+"");
      holder.circleViewer.setText(circle.viewCount+"次浏览");
      holder.circleLike.setText(circle.zanCount+"");
      holder.circleDiscuss.setText(circle.comCount+"");
      if(circle.userIconVo.authInfo!=null&&!circle.userIconVo.authInfo.equals("")){
        holder.tvAuthInfo.setVisibility(View.VISIBLE);
        holder.tvAuthInfo.setText(circle.userIconVo.authInfo+"");
      }else {
        holder.tvAuthInfo.setVisibility(View.GONE);
      }
      holder.circleFrom.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent i=new Intent(activity, PersonalCircleActivity.class);
            i.putExtra("CircleId",circle.circleId);
          activity.startActivity(i);
        }
      });
      holder.circleUserHead.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent i=new Intent(activity, PersonalOtherHomeActivity.class);
          i.putExtra("UserID",circle.userIconVo.id);
          activity.startActivity(i);
        }
      });
    }
    holder.linearLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i=new Intent(activity,PersonalPostsDetailActivity.class);
        i.putExtra("TieZiID",circle.id);
        activity.startActivity(i);
      }
    });
  }
  @Override public int getItemCount() {
    return mValues.size();
  }
  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.hot_info_circle_user_head1) RoundedImageView circleUserHead;
    @BindView(R.id.hot_info_circle_user_name1) TextView circleUserName;
    @BindView(R.id.hot_info_circle_article_time1) TextView circleArticleTime;
    @BindView(R.id.hot_info_circle_article_title1) TextView circleArticleTitle;
    @BindView(R.id.hot_info_circle_article_content1) TextView circleArticleContent;
    @BindView(R.id.hot_info_circle_viewer1) TextView circleViewer;//浏览人次数
    @BindView(R.id.hot_info_circle_discuss1) TextView circleDiscuss;//消息条数
    @BindView(R.id.hot_info_circle_like1) TextView circleLike;//点赞人次数
    @BindView(R.id.linearLayout)LinearLayout linearLayout;
    @BindView(R.id.hot_info_circle_from1) TextView circleFrom;
    @BindView(R.id.tvAuthInfo) TextView tvAuthInfo;
    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
