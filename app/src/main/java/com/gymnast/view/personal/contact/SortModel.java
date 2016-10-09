package com.gymnast.view.personal.contact;

import android.graphics.Bitmap;

public class SortModel {
	private String name; // 显示的数据
	private String sortLetters; // 显示数据拼音的首字母
	private Bitmap smallPhoto;
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Bitmap getSmallPhoto() {
		return smallPhoto;
	}

	public void setSmallPhoto(Bitmap smallPhoto) {
		this.smallPhoto = smallPhoto;
	}
}
