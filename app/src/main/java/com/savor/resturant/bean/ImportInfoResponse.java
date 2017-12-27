package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 导入通讯录
 * Created by hezd on 2017/12/27.
 */

public class ImportInfoResponse implements Serializable {
    private List<ContactFormat> customer_list;

    @Override
    public String toString() {
        return "ImportInfoResponse{" +
                "customer_list=" + customer_list +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportInfoResponse that = (ImportInfoResponse) o;

        return customer_list != null ? customer_list.equals(that.customer_list) : that.customer_list == null;
    }

    @Override
    public int hashCode() {
        return customer_list != null ? customer_list.hashCode() : 0;
    }

    public List<ContactFormat> getCustomer_list() {
        return customer_list;
    }

    public void setCustomer_list(List<ContactFormat> customer_list) {
        this.customer_list = customer_list;
    }
}
