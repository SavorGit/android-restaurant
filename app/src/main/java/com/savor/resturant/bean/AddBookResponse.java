package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 添加预定接口返回
 * Created by hezd on 2018/1/19.
 */

public class AddBookResponse implements Serializable {
    private String customer_id;

    @Override
    public String toString() {
        return "AddBookResponse{" +
                "customer_id='" + customer_id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddBookResponse that = (AddBookResponse) o;

        return customer_id != null ? customer_id.equals(that.customer_id) : that.customer_id == null;
    }

    @Override
    public int hashCode() {
        return customer_id != null ? customer_id.hashCode() : 0;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }
}
