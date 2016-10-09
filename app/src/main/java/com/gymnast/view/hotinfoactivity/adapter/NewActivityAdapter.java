package com.gymnast.view.hotinfoactivity.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gymnast.R;
import com.gymnast.data.hotinfo.NewActivityItemDevas;
import com.gymnast.data.hotinfo.RecentBigActivity;
import com.gymnast.utils.CollectUtil;
import com.gymnast.utils.PicUtil;
import com.gymnast.utils.TimeUtil;
import com.gymnast.view.home.view.HomeSearchResultAcitivity;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by Administrator on 2016/7/27.
 */
public class NewActivityAdapter extends RecyclerView.Adapter implements Filterable {
    private final List<NewActivityItemDevas> mValues ;
    List<NewActivityItemDevas> mCopyInviteMessages;
    List<NewActivityItemDevas> inviteMessages;
    RecentBigActivity recentBigActivity;
    private Activity activity;
    private OnItemClickListener onItemClickListener;
    int TYPE_ITEM=1;
    int TYPE_BASEINFO=2;
    private static final int VIEW_TYPE = -1;
    public NewActivityAdapter(Activity activity, List<NewActivityItemDevas> mValues) {
        this.activity=activity;
        if(mValues.size()==0){
            this.mValues=new ArrayList<>();
        }else {
            this.mValues=mValues;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyleview_recent_activity, null);
       // View viewBaseInfo = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_activity_item_3, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemview.setLayoutParams(lp);
     //   viewBaseInfo.setLayoutParams(lp);
        if (viewType == TYPE_ITEM) {
            return new PersonViewHolder(itemview);
        } /*else if (viewType==TYPE_BASEINFO){
            return new PersonViewHolderBig(viewBaseInfo);
        }*/
           View view=LayoutInflater.from(activity).inflate(R.layout.empty_view, parent, false);
            view.setLayoutParams(lp);
            return new empty(view);
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CollectUtil.ERROR:
                    Toast.makeText(activity, "您已经收藏过了！", Toast.LENGTH_SHORT).show();
                    break;
                case CollectUtil.TO_COLLECT:
                    TextView tvLove= (TextView) msg.obj;
                    Drawable collectedPic1=activity.getResources().getDrawable(R.mipmap.icon_collection_click);
                    collectedPic1.setBounds(0,0,collectedPic1.getMinimumWidth(), collectedPic1.getMinimumHeight());
                    tvLove.setCompoundDrawables(collectedPic1,null,null,null);
                    String text=tvLove.getText().toString().trim();
                    String tmpText=text.substring(0, text.lastIndexOf("人"));
                    int startNumber=0;
                    if (!tmpText.endsWith("K")&&!tmpText.endsWith("万")){
                        startNumber=Integer.parseInt(tmpText);
                        startNumber++;
                    }
                    tvLove.setText(startNumber+"人收藏");
                    Toast.makeText(activity,"已收藏！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PersonViewHolder){
            final PersonViewHolder viewHolder = (PersonViewHolder)holder;
            final NewActivityItemDevas newActivityItemDevas=  mValues.get(position);
            Picasso.with(activity).load(PicUtil.getImageUrl(activity, newActivityItemDevas.getImgUrls())).into(viewHolder.ivImage);
            viewHolder.tvaddress.setText(newActivityItemDevas.getAddress());
            viewHolder.tvcollection.setText(TimeUtil.setLoveNum(newActivityItemDevas.getCollection())+"人收藏");
            viewHolder.tvnickname.setText(newActivityItemDevas.getNickname());
            viewHolder.tvstartTime.setText(TimeUtil.checkTime(newActivityItemDevas.getStartTime()));
            viewHolder.tvTitle.setText(newActivityItemDevas.getTitle());
            if(newActivityItemDevas.getPrice()==0){
                viewHolder.tvprice.setBackground(activity.getResources().getDrawable(R.drawable.border_radius_cornner_green));
                viewHolder.tvprice.setTextColor(activity.getResources().getColor(R.color.green));
            }else{
                viewHolder.tvprice.setBackground(activity.getResources().getDrawable(R.drawable.border_radius_cornner_red));
                viewHolder.tvprice.setTextColor(activity.getResources().getColor(R.color.login_btn_normal_color));
            }
            if (newActivityItemDevas.getPrice()==0){
                viewHolder.tvprice.setText("免费");
            }else {
                viewHolder.tvprice.setText("￥" + newActivityItemDevas.getPrice());
            }
            final boolean isCollected=newActivityItemDevas.isCollected();
            if (isCollected){
                Drawable collectedPic=activity.getResources().getDrawable(R.mipmap.icon_collection_click);
                collectedPic.setBounds(0,0,collectedPic.getMinimumWidth(), collectedPic.getMinimumHeight());
                viewHolder.tvcollection.setCompoundDrawables(collectedPic, null, null, null);
            }else {
                Drawable collectedPic=activity.getResources().getDrawable(R.mipmap.icon_collection);
                collectedPic.setBounds(0,0,collectedPic.getMinimumWidth(), collectedPic.getMinimumHeight());
                viewHolder.tvcollection.setCompoundDrawables(collectedPic, null, null, null);
            }
            viewHolder.tvcollection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CollectUtil.toCollect(activity,handler, newActivityItemDevas.getActiveId(), viewHolder.tvcollection);
                }
            });
        }
        /*else if (holder instanceof PersonViewHolderBig){
            PersonViewHolderBig viewHolder= (PersonViewHolderBig) holder;
            viewHolder.ivDetailBig.setImageResource(recentBigActivity.getBigPic());
            viewHolder.ivBig1.setImageResource(recentBigActivity.getPictureA());
            viewHolder.ivBig2.setImageResource(recentBigActivity.getPictureB());
            viewHolder.ivBig3.setImageResource(recentBigActivity.getPictureC());
            viewHolder.ivBig4.setImageResource(recentBigActivity.getPictureD());
            viewHolder.tvBigTitle.setText(recentBigActivity.getBigTitle());
            viewHolder.tvBigWholeNum.setText(recentBigActivity.getBigNum()+"个活动");
            viewHolder.tvBigName1.setText(recentBigActivity.getBigTitleA());
            viewHolder.tvBigName2.setText(recentBigActivity.getBigTitleB());
            viewHolder.tvBigName3.setText(recentBigActivity.getBigTitleC());
            viewHolder.tvBigName4.setText(recentBigActivity.getBigTitleD());
            if (recentBigActivity.getBigTypeA().equals("免费")){
                viewHolder.tvBigType1.setTextColor(activity.getResources().getColor(R.color.green));
            }else {
                viewHolder.tvBigType1.setTextColor(activity.getResources().getColor(R.color.login_btn_normal_color));
            }
            if (recentBigActivity.getBigTypeB().equals("免费")){
                viewHolder.tvBigType2.setTextColor(activity.getResources().getColor(R.color.green));
            }else {
                viewHolder.tvBigType2.setTextColor(activity.getResources().getColor(R.color.login_btn_normal_color));
            }
            if (recentBigActivity.getBigTypeC().equals("免费")){
                viewHolder.tvBigType3.setTextColor(activity.getResources().getColor(R.color.green));
            }else {
                viewHolder.tvBigType3.setTextColor(activity.getResources().getColor(R.color.login_btn_normal_color));
            }
            if (recentBigActivity.getBigTypeD().equals("免费")){
                viewHolder.tvBigType4.setTextColor(activity.getResources().getColor(R.color.green));
            }else {
                viewHolder.tvBigType4.setTextColor(activity.getResources().getColor(R.color.login_btn_normal_color));
            }
            viewHolder.tvBigType1.setText(recentBigActivity.getBigTypeA());
            viewHolder.tvBigType2.setText(recentBigActivity.getBigTypeB());
            viewHolder.tvBigType3.setText(recentBigActivity.getBigTypeC());
            viewHolder.tvBigType4.setText(recentBigActivity.getBigTypeD());
            viewHolder.tvBigLove1.setText(recentBigActivity.getBigLoveA());
            viewHolder.tvBigLove2.setText(recentBigActivity.getBigLoveB());
            viewHolder.tvBigLove3.setText(recentBigActivity.getBigLoveC());
            viewHolder.tvBigLove4.setText(recentBigActivity.getBigLoveD());
        }*/
        if(onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱\
                    onItemClickListener.OnItemClickListener(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mValues.size() <= 0) {
            return VIEW_TYPE;
        }else {
//            if (position == 2) {
//                return TYPE_BASEINFO;
//            } else {
                return TYPE_ITEM;
//            }
        }
    }
    @Override
    public int getItemCount() {
        return mValues.size() > 0 ? mValues.size() : 1;
    }
    class empty extends RecyclerView.ViewHolder{
        private final TextView text_empty;
        public empty(View itemView) {
            super(itemView);
            text_empty=(TextView) itemView.findViewById(R.id.text_empty);
        }
    }
      class PersonViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.llitem) LinearLayout llitem;
        @BindView(R.id.ivImage) ImageView ivImage;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvnickname) TextView tvnickname;
        @BindView(R.id.tvaddress) TextView tvaddress;
        @BindView(R.id.tvstartTime) TextView tvstartTime;
        @BindView(R.id.tvcollection) TextView tvcollection;
        @BindView(R.id.tvprice) TextView tvprice;
        public PersonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    public void setFriends(List<NewActivityItemDevas> data) {
        //复制数据
        mCopyInviteMessages = new ArrayList<>();
        this.mCopyInviteMessages.addAll(data);
        this.inviteMessages = data;
        this.notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //初始化过滤结果对象
                FilterResults results = new FilterResults();
                //假如搜索为空的时候，将复制的数据添加到原始数据，用于继续过滤操作
                if (results.values == null) {
                    mValues.clear();
                    mValues.addAll(mCopyInviteMessages);
                }
                //关键字为空的时候，搜索结果为复制的结果
                if (constraint == null || constraint.length() == 0) {
                    results.values = mCopyInviteMessages;
                    results.count = mCopyInviteMessages.size();
                } else {
                    String searchText= HomeSearchResultAcitivity.getSearchText();
                    String prefixString;
                    if (searchText.equals("")){
                        prefixString=searchText.toString();
                    }else {
                        prefixString= constraint.toString();
                    }
                    final int count = inviteMessages.size();
                    //用于存放暂时的过滤结果
                    final ArrayList<NewActivityItemDevas> newValues = new ArrayList<NewActivityItemDevas>();
                    for (int i = 0; i < count; i++) {
                        final NewActivityItemDevas value = inviteMessages.get(i);
                        String username = value.getTitle();
                        // First match against the whole ,non-splitted value，假如含有关键字的时候，添加
                        if (username.contains(prefixString)) {
                            newValues.add(value);
                        } else {
                            //过来空字符开头
                            final String[] words = username.split(" ");
                            final int wordCount = words.length;
                            // Start at index 0, in case valueText starts with space(s)
                            for (int k = 0; k < wordCount; k++) {
                                if (words[k].contains(prefixString)) {
                                    newValues.add(value);
                                    break;
                                }
                            }
                        }
                    }
                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;//过滤结果
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                NewActivityAdapter.this.inviteMessages.clear();//清除原始数据
                NewActivityAdapter.this.inviteMessages.addAll((List<NewActivityItemDevas>) results.values);//将过滤结果添加到这个对象
                if (results.count > 0) {
                    NewActivityAdapter.this.notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        NewActivityAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    NewActivityAdapter.this.setFriends(mCopyInviteMessages);
                }
            }
        };
    }
     class PersonViewHolderBig  extends RecyclerView.ViewHolder{
        ImageView ivDetailBig,ivBig1,ivBig2,ivBig3,ivBig4;
        TextView tvBigTitle,tvBigWholeNum,
                tvBigName1,tvBigName2,tvBigName3,tvBigName4,
                tvBigType1,tvBigType2,tvBigType3,tvBigType4,
                tvBigLove1,tvBigLove2,tvBigLove3,tvBigLove4;
        public PersonViewHolderBig(View itemView) {
            super(itemView);
            ivDetailBig= (ImageView) itemView.findViewById(R.id.ivDetailBig);
            ivBig1= (ImageView) itemView.findViewById(R.id.ivBig1);
            ivBig2= (ImageView) itemView.findViewById(R.id.ivBig2);
            ivBig3= (ImageView) itemView.findViewById(R.id.ivBig3);
            ivBig4= (ImageView) itemView.findViewById(R.id.ivBig4);
            tvBigTitle= (TextView) itemView.findViewById(R.id.tvBigTitle);
            tvBigWholeNum= (TextView) itemView.findViewById(R.id.tvBigWholeNum);
            tvBigName1= (TextView) itemView.findViewById(R.id.tvBigName1);
            tvBigName2= (TextView) itemView.findViewById(R.id.tvBigName2);
            tvBigName3= (TextView) itemView.findViewById(R.id.tvBigName3);
            tvBigName4= (TextView) itemView.findViewById(R.id.tvBigName4);
            tvBigType1= (TextView) itemView.findViewById(R.id.tvBigType1);
            tvBigType2= (TextView) itemView.findViewById(R.id.tvBigType2);
            tvBigType3= (TextView) itemView.findViewById(R.id.tvBigType3);
            tvBigType4= (TextView) itemView.findViewById(R.id.tvBigType4);
            tvBigLove1= (TextView) itemView.findViewById(R.id.tvBigLove1);
            tvBigLove2= (TextView) itemView.findViewById(R.id.tvBigLove2);
            tvBigLove3= (TextView) itemView.findViewById(R.id.tvBigLove3);
            tvBigLove4= (TextView) itemView.findViewById(R.id.tvBigLove4);
            ButterKnife.bind(this, itemView);
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void OnItemClickListener(View view, int position);
    }
}
