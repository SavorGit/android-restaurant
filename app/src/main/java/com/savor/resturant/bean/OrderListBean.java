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
    private String room_name;
    private String time_str;
    private int is_expense;
    private String is_welcome;
    private String is_recfood;
    private String customer_id;
    private String room_id;
    private String room_type;
    private String face_url;
    private String moment_str;


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

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getFace_url() {
        return face_url;
    }

    public void setFace_url(String face_url) {
        this.face_url = face_url;
    }

    public String getMoment_str() {
        return moment_str;
    }

    public void setMoment_str(String moment_str) {
        this.moment_str = moment_str;
    }

    @Override
    public String toString() {
        return "OrderListBean{" +
                "order_id='" + order_id + '\'' +
                ", person_nums='" + person_nums + '\'' +
                ", order_name='" + order_name + '\'' +
                ", order_mobile='" + order_mobile + '\'' +
                ", room_name='" + room_name + '\'' +
                ", time_str='" + time_str + '\'' +
                ", is_expense=" + is_expense +
                ", is_welcome='" + is_welcome + '\'' +
                ", is_recfood='" + is_recfood + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", room_id='" + room_id + '\'' +
                ", room_type='" + room_type + '\'' +
                ", face_url='" + face_url + '\'' +
                ", moment_str='" + moment_str + '\'' +
                '}';
    }
}
