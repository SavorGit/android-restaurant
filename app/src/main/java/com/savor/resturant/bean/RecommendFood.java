package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 推荐菜
 * Created by hezd on 2017/12/4.
 */

public class RecommendFood implements Serializable {

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
    private boolean isSelected;

    @Override
    public String toString() {
        return "RecommendFood{" +
                "food_id='" + food_id + '\'' +
                ", id='" + id + '\'' +
                ", food_name='" + food_name + '\'' +
                ", oss_path='" + oss_path + '\'' +
                ", chinese_name='" + chinese_name + '\'' +
                ", md5='" + md5 + '\'' +
                ", md5_type='" + md5_type + '\'' +
                ", suffix='" + suffix + '\'' +
                ", name='" + name + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecommendFood food = (RecommendFood) o;

        if (isSelected != food.isSelected) return false;
        if (food_id != null ? !food_id.equals(food.food_id) : food.food_id != null) return false;
        if (id != null ? !id.equals(food.id) : food.id != null) return false;
        if (food_name != null ? !food_name.equals(food.food_name) : food.food_name != null)
            return false;
        if (oss_path != null ? !oss_path.equals(food.oss_path) : food.oss_path != null)
            return false;
        if (chinese_name != null ? !chinese_name.equals(food.chinese_name) : food.chinese_name != null)
            return false;
        if (md5 != null ? !md5.equals(food.md5) : food.md5 != null) return false;
        if (md5_type != null ? !md5_type.equals(food.md5_type) : food.md5_type != null)
            return false;
        if (suffix != null ? !suffix.equals(food.suffix) : food.suffix != null) return false;
        return name != null ? name.equals(food.name) : food.name == null;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

