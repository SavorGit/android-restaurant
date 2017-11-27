package com.savor.resturant.utils;

import com.savor.resturant.bean.MediaInfo;

import java.util.ArrayList;

/**
 * 投屏管理器
 * Created by hezd on 2017/3/6.
 */

public class ProjectionManager {
    private static volatile ProjectionManager instance = null;
    /**当前幻灯片图片集合*/
    private ArrayList<MediaInfo> mImgList;
    /**当前是否正在搜索ssdp*/
    private boolean isLookingSSDP;

    private ProjectionManager(){}

    public static ProjectionManager getInstance() {
        if(instance==null) {
            synchronized (ProjectionManager.class) {
                if(instance == null)
                    instance = new ProjectionManager();
            }
        }
        return instance;
    }


    public void setImgList(ArrayList<MediaInfo> imagelist) {
        this.mImgList = imagelist;
    }

    public ArrayList<MediaInfo> getImgList() {
        return mImgList;
    }

    public boolean isLookingSSDP() {
        return isLookingSSDP;
    }

    public void setLookingSSDP(boolean lookingSSDP) {
        isLookingSSDP = lookingSSDP;
    }
}
