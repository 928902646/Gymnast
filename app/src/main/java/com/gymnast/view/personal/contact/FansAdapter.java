package com.gymnast.view.personal.contact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.gymnast.R;

import java.util.List;

public class FansAdapter extends BaseAdapter implements SectionIndexer {
	private List<SortModel> list = null;
	private Context mContext;
	public static final int TYPE_CONCERN=3;
	public FansAdapter(Context mContext, List<SortModel> list, int type) {
		this.mContext = mContext;
		this.list = list;
		/*if (type==TYPE_CONCERN){
			Bitmap bitmap1= BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_big_ka_shot);
			Bitmap bitmap2= BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.icon_build_circle);
			SortModel sortModel1=new SortModel();
			sortModel1.setName("大咖");
			sortModel1.setSortLetters("@");
			sortModel1.setSmallPhoto(bitmap1);
			SortModel sortModel2=new SortModel();
			sortModel2.setName("圈子");
			sortModel2.setSortLetters("@");
			sortModel2.setSmallPhoto(bitmap2);
			list.add(0, sortModel1);
			list.add(1,sortModel2);
		}*/
	}
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<SortModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	public int getCount() {
		return this.list.size();
	}
	public Object getItem(int position) {
		return list.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_contact_listview, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.contact_title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.imhead = (ImageView) view.findViewById(R.id.contact_head);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		viewHolder.tvTitle.setText(this.list.get(position).getName());
		viewHolder.imhead.setImageBitmap(list.get(position).getSmallPhoto());
		return view;
	}
	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageView imhead;
	}
	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}
	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}
	@Override
	public Object[] getSections() {
		return null;
	}
}