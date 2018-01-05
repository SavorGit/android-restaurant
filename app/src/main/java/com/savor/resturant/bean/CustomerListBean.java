package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户列表实体类
 * Created by hezd on 2018/1/5.
 */

public class CustomerListBean implements Serializable {
    private String mobile;
    private List<ContactFormat> customerList = new ArrayList<>();

    @Override
    public String toString() {
        return "CustomerListBean{" +
                "mobile='" + mobile + '\'' +
                ", customerList=" + customerList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerListBean that = (CustomerListBean) o;

        if (mobile != null ? !mobile.equals(that.mobile) : that.mobile != null) return false;
        return customerList != null ? customerList.equals(that.customerList) : that.customerList == null;
    }

    @Override
    public int hashCode() {
        int result = mobile != null ? mobile.hashCode() : 0;
        result = 31 * result + (customerList != null ? customerList.hashCode() : 0);
        return result;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<ContactFormat> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<ContactFormat> customerList) {
        this.customerList = customerList;
    }
}
