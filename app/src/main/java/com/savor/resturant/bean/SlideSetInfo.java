package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 新建幻灯片信息
 */
public class SlideSetInfo implements Serializable {

    /**
     * 是否是新建幻灯片集
     */
    public boolean isNewCreate;
    //文件夹名（组名）
    public String groupName;
    //文件夹创建/更新时间
    public long updateTime;
    //该组所有图片路径集合
    public List<MediaInfo> imageList = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SlideSetInfo that = (SlideSetInfo) o;

        return groupName != null ? groupName.equals(that.groupName) : that.groupName == null;
    }

    @Override
    public int hashCode() {
        return groupName != null ? groupName.hashCode() : 0;
    }

    public boolean isNewCreate() {
        return isNewCreate;
    }

    public void setNewCreate(boolean newCreate) {
        isNewCreate = newCreate;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public List<MediaInfo> getImageList() {
        return imageList;
    }

    public void setImageList(List<MediaInfo> imageList) {
        this.imageList = imageList;
    }
}
