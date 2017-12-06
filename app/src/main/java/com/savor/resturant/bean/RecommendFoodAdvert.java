package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 推荐菜和宣传片共用实体类
 * Created by hezd on 2017/12/4.
 */

public class RecommendFoodAdvert implements Serializable {

    /**
     * food_id : 1
     * id : 3798
     * food_name : 111111112222
     * oss_path : media/resource/ATcxtWnRwb.jpg
     * chinese_name : 特色
     * md5 : a90600ac734eb705ef1c57fcf42fd39c
     * md5_type : easyMd5
     * suffix : jpg
     * name : ATcxtWnRwb.jpg
     */

    private String food_id;
    private String id;
    private String food_name;
    private String oss_path;
    private String chinese_name;
    private String md5;
    private String md5_type;
    private String suffix;
    private String name;
    private String duration;
    private String img_url;
    private boolean isSelected;

    @Override
    public String toString() {
        return "RecommendFoodAdvert{" +
                "food_id='" + food_id + '\'' +
                ", id='" + id + '\'' +
                ", food_name='" + food_name + '\'' +
                ", oss_path='" + oss_path + '\'' +
                ", chinese_name='" + chinese_name + '\'' +
                ", md5='" + md5 + '\'' +
                ", md5_type='" + md5_type + '\'' +
                ", suffix='" + suffix + '\'' +
                ", name='" + name + '\'' +
                ", duration='" + duration + '\'' +
                ", img_url='" + img_url + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecommendFoodAdvert that = (RecommendFoodAdvert) o;

        if (isSelected != that.isSelected) return false;
        if (food_id != null ? !food_id.equals(that.food_id) : that.food_id != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (food_name != null ? !food_name.equals(that.food_name) : that.food_name != null)
            return false;
        if (oss_path != null ? !oss_path.equals(that.oss_path) : that.oss_path != null)
            return false;
        if (chinese_name != null ? !chinese_name.equals(that.chinese_name) : that.chinese_name != null)
            return false;
        if (md5 != null ? !md5.equals(that.md5) : that.md5 != null) return false;
        if (md5_type != null ? !md5_type.equals(that.md5_type) : that.md5_type != null)
            return false;
        if (suffix != null ? !suffix.equals(that.suffix) : that.suffix != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (duration != null ? !duration.equals(that.duration) : that.duration != null)
            return false;
        return img_url != null ? img_url.equals(that.img_url) : that.img_url == null;
    }

    @Override
    public int hashCode() {
        int result = food_id != null ? food_id.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (food_name != null ? food_name.hashCode() : 0);
        result = 31 * result + (oss_path != null ? oss_path.hashCode() : 0);
        result = 31 * result + (chinese_name != null ? chinese_name.hashCode() : 0);
        result = 31 * result + (md5 != null ? md5.hashCode() : 0);
        result = 31 * result + (md5_type != null ? md5_type.hashCode() : 0);
        result = 31 * result + (suffix != null ? suffix.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (img_url != null ? img_url.hashCode() : 0);
        result = 31 * result + (isSelected ? 1 : 0);
        return result;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getOss_path() {
        return oss_path;
    }

    public void setOss_path(String oss_path) {
        this.oss_path = oss_path;
    }

    public String getChinese_name() {
        return chinese_name;
    }

    public void setChinese_name(String chinese_name) {
        this.chinese_name = chinese_name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMd5_type() {
        return md5_type;
    }

    public void setMd5_type(String md5_type) {
        this.md5_type = md5_type;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

