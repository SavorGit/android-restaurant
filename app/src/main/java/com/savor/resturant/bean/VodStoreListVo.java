package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * 收藏点播
 * 
 * @author savor
 * 
 */
public class VodStoreListVo implements Serializable {

	private static final long serialVersionUID = -1;
	private Map<String, VodBean> map;

	public Map<String, VodBean> getMap() {
		return map;
	}

	public void setMap(Map<String, VodBean> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "VodStoreListVo [map=" + map + "]";
	}

}
