package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by buslee on 2017/12/26.
 */

public class BookListResult implements Serializable {
    private static final long serialVersionUID = -1;
    private List<OrderListBean> order_list;
    private String yesterday_order_nums;
    private String today_order_nums;
    private String tomorrow_order_nums;
    private String after_tomorrow_order_nums;

    public List<OrderListBean> getOrder_list() {
        return order_list;
    }

    public void setOrder_list(List<OrderListBean> order_list) {
        this.order_list = order_list;
    }

    public String getYesterday_order_nums() {
        return yesterday_order_nums;
    }

    public void setYesterday_order_nums(String yesterday_order_nums) {
        this.yesterday_order_nums = yesterday_order_nums;
    }

    public String getToday_order_nums() {
        return today_order_nums;
    }

    public void setToday_order_nums(String today_order_nums) {
        this.today_order_nums = today_order_nums;
    }

    public String getTomorrow_order_nums() {
        return tomorrow_order_nums;
    }

    public void setTomorrow_order_nums(String tomorrow_order_nums) {
        this.tomorrow_order_nums = tomorrow_order_nums;
    }

    public String getAfter_tomorrow_order_nums() {
        return after_tomorrow_order_nums;
    }

    public void setAfter_tomorrow_order_nums(String after_tomorrow_order_nums) {
        this.after_tomorrow_order_nums = after_tomorrow_order_nums;
    }

    @Override
    public String toString() {
        return "BookListResult{" +
                "order_list=" + order_list +
                ", yesterday_order_nums='" + yesterday_order_nums + '\'' +
                ", today_order_nums='" + today_order_nums + '\'' +
                ", tomorrow_order_nums='" + tomorrow_order_nums + '\'' +
                ", after_tomorrow_order_nums='" + after_tomorrow_order_nums + '\'' +
                '}';
    }
}
