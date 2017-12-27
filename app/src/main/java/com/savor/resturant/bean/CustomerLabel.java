package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 客户标签
 * Created by hezd on 2017/12/27.
 */

public class CustomerLabel implements Serializable {

    /**
     * label_id : 1
     * label_name : 23423423
     * light : 0
     */

    private String label_id;
    private String label_name;
    private int light;

    @Override
    public String toString() {
        return "CustomerLabel{" +
                "label_id='" + label_id + '\'' +
                ", label_name='" + label_name + '\'' +
                ", light=" + light +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerLabel that = (CustomerLabel) o;

        if (light != that.light) return false;
        if (label_id != null ? !label_id.equals(that.label_id) : that.label_id != null)
            return false;
        return label_name != null ? label_name.equals(that.label_name) : that.label_name == null;
    }

    @Override
    public int hashCode() {
        int result = label_id != null ? label_id.hashCode() : 0;
        result = 31 * result + (label_name != null ? label_name.hashCode() : 0);
        result = 31 * result + light;
        return result;
    }

    public String getLabel_id() {
        return label_id;
    }

    public void setLabel_id(String label_id) {
        this.label_id = label_id;
    }

    public String getLabel_name() {
        return label_name;
    }

    public void setLabel_name(String label_name) {
        this.label_name = label_name;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }
}
