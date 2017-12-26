package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by buslee on 2017/12/26.
 */

public class RecTopList implements Serializable {
    private static final long serialVersionUID = -1;
    private List<ConRecBean> list ;

    public List<ConRecBean> getList() {
        return list;
    }

    public void setList(List<ConRecBean> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "RecTopList{" +
                "list=" + list +
                '}';
    }
}
