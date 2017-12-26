package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/12/26.
 */

public class CustomerBean implements Serializable {
    private static final long serialVersionUID = -1;
    private Customer list;

    public Customer getList() {
        return list;
    }

    public void setList(Customer list) {
        this.list = list;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
