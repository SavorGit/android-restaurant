package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 添加客户返回
 * Created by hezd on 2018/1/5.
 */

public class AddCustomerResponse implements Serializable {

    /**
     * list : {"customer_id":"66"}
     */

    private ListBean list;

    @Override
    public String toString() {
        return "AddCustomerResponse{" +
                "list=" + list +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddCustomerResponse that = (AddCustomerResponse) o;

        return list != null ? list.equals(that.list) : that.list == null;
    }

    @Override
    public int hashCode() {
        return list != null ? list.hashCode() : 0;
    }

    public ListBean getList() {
        return list;
    }

    public void setList(ListBean list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * customer_id : 66
         */

        private String customer_id;

        public String getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(String customer_id) {
            this.customer_id = customer_id;
        }
    }
}
