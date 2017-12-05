package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 首页功能
 * Created by hezd on 2017/11/30.
 */

public class FunctionItem implements Serializable {
    public enum FunctionType implements Serializable {
        /**推荐菜*/
        TYPE_RECOMMAND_FOODS,
        /**宣传片*/
        TYPE_ADVERT,
        /**欢迎词*/
        TYPE_WELCOME_WORD,
        /**照片*/
        TYPE_PIC,
        /**视频*/
        TYPE_VIDEO,
    }

    /**功能类型*/
    public FunctionType type;
    /**图片资源id*/
    public int resId;

    public String content;

    @Override
    public String toString() {
        return "FunctionItem{" +
                "type=" + type +
                ", resId=" + resId +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionItem that = (FunctionItem) o;

        if (resId != that.resId) return false;
        if (type != that.type) return false;
        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + resId;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    public FunctionType getType() {
        return type;
    }

    public void setType(FunctionType type) {
        this.type = type;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
