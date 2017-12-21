package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/12/21.
 */

public class OrderListBean implements Serializable {
    private static final long serialVersionUID = -1;
    private String order_id;
    private String person_nums;
    private String order_name;
    private String order_mobile;
    private String is_welcome;
    private String is_recfood;
    private String room_name;
    private String time_str;
    private int is_expense;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPerson_nums() {
        return person_nums;
    }

    public void setPerson_nums(String person_nums) {
        this.person_nums = person_nums;
    }

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }

    public String getOrder_mobile() {
        return order_mobile;
    }

    public void setOrder_mobile(String order_mobile) {
        this.order_mobile = order_mobile;
    }

    public String getIs_welcome() {
        return is_welcome;
    }

    public void setIs_welcome(String is_welcome) {
        this.is_welcome = is_welcome;
    }

    public String getIs_recfood() {
        return is_recfood;
    }

    public void setIs_recfood(String is_recfood) {
        this.is_recfood = is_recfood;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getTime_str() {
        return time_str;
    }

    public void setTime_str(String time_str) {
        this.time_str = time_str;
    }

    public int getIs_expense() {
        return is_expense;
    }

    public void setIs_expense(int is_expense) {
        this.is_expense = is_expense;
    }

    @Override
    public String toString() {
        return "OrderListBean{" +
                "order_id='" + order_id + '\'' +
                ", person_nums='" + person_nums + '\'' +
                ", order_name='" + order_name + '\'' +
                ", order_mobile='" + order_mobile + '\'' +
                ", is_welcome='" + is_welcome + '\'' +
                ", is_recfood='" + is_recfood + '\'' +
                ", room_name='" + room_name + '\'' +
                ", time_str='" + time_str + '\'' +
                ", is_expense=" + is_expense +
                '}';
    }
}
