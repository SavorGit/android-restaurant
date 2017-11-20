package com.savor.resturant.action;

import android.content.Context;

import com.common.api.utils.FileUtils;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.bean.VodBean;
import com.savor.resturant.bean.VodStoreListVo;


import java.util.HashMap;
import java.util.Map;

public class VodStroeAction {
	private static final String TAG = "VodStroeAction";
	private static VodStoreListVo mVodStoreListVo;

	public static void addItem(Context context, VodBean vodItem) {
		mVodStoreListVo = readStoreList(context);
		if (mVodStoreListVo == null)
			mVodStoreListVo = new VodStoreListVo();
		Map<String, VodBean> storeItems = mVodStoreListVo.getMap();
		if (storeItems == null) {
			storeItems = new HashMap<String, VodBean>();
		}
		storeItems.put(vodItem.getTitle(), vodItem);
		mVodStoreListVo.setMap(storeItems);
		FileUtils.saveObject(context, ((SavorApplication) context.getApplicationContext()).VodStorePath, mVodStoreListVo);
	}

	public static void removeItem(Context context, VodBean libItem) {
		VodStoreListVo readStoreList = readStoreList(context);
		readStoreList.getMap().remove(libItem.getTitle());
		FileUtils.saveObject(context, ((SavorApplication) context.getApplicationContext()).VodStorePath, readStoreList);
	}

	public static VodStoreListVo readStoreList(Context context) {
		return FileUtils.readObject(context, ((SavorApplication) context.getApplicationContext()).VodStorePath, VodStoreListVo.class);
	}

	public static boolean contains(Context context, VodBean libItem) {
		VodStoreListVo readStoreList = readStoreList(context);
		if (readStoreList == null) {
			return false;
		}
		Map<String, VodBean> media_lib = readStoreList.getMap();
		if (media_lib == null) {
			return false;
		}
		return media_lib.containsKey(libItem.getTitle());
	}
}
