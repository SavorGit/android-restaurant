package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 客户标签列表
 * Created by hezd on 2017/12/27.
 */

public class CustomerLabelList implements Serializable {
    private List<CustomerLabel> list;

    @Override
    public String toString() {
        return "CustomerLabelList{" +
                "list=" + list +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerLabelList that = (CustomerLabelList) o;

        return list != null ? list.equals(that.list) : that.list == null;
    }

    @Override
    public int hashCode() {
        return list != null ? list.hashCode() : 0;
    }

    public List<CustomerLabel> getList() {
        return list;
    }

    public void setList(List<CustomerLabel> list) {
        this.list = list;
    }
}
