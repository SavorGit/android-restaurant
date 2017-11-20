package com.savor.resturant.bean;

import java.io.Serializable;

public class CategoryItemVo implements Serializable {

	private static final long serialVersionUID = -1;
	/**分类ID**/
	private int id;
	private String imageURL;
	/**分类名称**/
	private String name;
	private String englishName;
	/**0:未开放，1：开放**/
	private int state;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
