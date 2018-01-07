package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by buslee on 2017/12/26.
 */

public class RecTopList implements Serializable {
    private static final long serialVersionUID = -1;
    private List<ConRecBean> list ;
    private String min_id;
    private String max_id;

    public List<ConRecBean> getList() {
        return list;
    }

    public void setList(List<ConRecBean> list) {
        this.list = list;
    }

    public String getMin_id() {
        return min_id;
    }

    public void setMin_id(String min_id) {
        this.min_id = min_id;
    }

    public String getMax_id() {
        return max_id;
    }

    public void setMax_id(String max_id) {
        this.max_id = max_id;
    }

    @Override
    public String toString() {
        return "RecTopList{" +
                "list=" + list +
                ", min_id='" + min_id + '\'' +
                ", max_id='" + max_id + '\'' +
                '}';
    }
}
