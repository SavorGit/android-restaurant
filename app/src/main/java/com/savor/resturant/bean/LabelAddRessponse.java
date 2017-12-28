package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 添加标签
 * Created by hezd on 2017/12/28.
 */

public class LabelAddRessponse implements Serializable {
    private CustomerLabel list;

    @Override
    public String toString() {
        return "LabelAddRessponse{" +
                "list=" + list +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LabelAddRessponse that = (LabelAddRessponse) o;

        return list != null ? list.equals(that.list) : that.list == null;
    }

    @Override
    public int hashCode() {
        return list != null ? list.hashCode() : 0;
    }

    public CustomerLabel getList() {
        return list;
    }

    public void setList(CustomerLabel list) {
        this.list = list;
    }
}
