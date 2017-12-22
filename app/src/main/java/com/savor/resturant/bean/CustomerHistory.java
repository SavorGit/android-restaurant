package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bushlee on 2017/12/22.
 */

public class CustomerHistory implements Serializable {
    private static final long serialVersionUID = -1;
    private List<CustomerHistoryBean> list = new ArrayList<CustomerHistoryBean>();

    public List<CustomerHistoryBean> getList() {
        return list;
    }

    public void setList(List<CustomerHistoryBean> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "CustomerHistory{" +
                "list=" + list +
                '}';
    }
}
