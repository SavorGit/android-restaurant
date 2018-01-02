package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 五预定信息消费记录接口返回数据
 * Created by hezd on 2018/1/2.
 */

public class AddSpendTicketNoBookInfo implements Serializable {

    /**
     * list : {"customer_id":"68"}
     */

    private ListBean list;

    public ListBean getList() {
        return list;
    }

    public void setList(ListBean list) {
        this.list = list;
    }

    public class ListBean implements Serializable{
        /**
         * customer_id : 68
         */

        private String customer_id;

        @Override
        public String toString() {
            return "ListBean{" +
                    "customer_id='" + customer_id + '\'' +
                    '}';
        }

        public String getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(String customer_id) {
            this.customer_id = customer_id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ListBean listBean = (ListBean) o;

            return customer_id != null ? customer_id.equals(listBean.customer_id) : listBean.customer_id == null;
        }

        @Override
        public int hashCode() {
            return customer_id != null ? customer_id.hashCode() : 0;
        }
    }
}
