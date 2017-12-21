package com.savor.resturant.bean;

import com.savor.resturant.core.CustomerBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bushlee on 2017/12/1.
 */

public class HotelBean implements Serializable {
    private static final long serialVersionUID = -1;
    private String hotel_id;
    private String hotel_name;
    private String invitation;
    private String tel;
    private List<CustomerBean> customer_list = new ArrayList<CustomerBean>();

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public void setHotel_name(String hotel_name) {
        this.hotel_name = hotel_name;
    }


    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public List<CustomerBean> getCustomer_list() {
        return customer_list;
    }

    public void setCustomer_list(List<CustomerBean> customer_list) {
        this.customer_list = customer_list;
    }


    @Override
    public String toString() {
        return "HotelBean{" +
                "hotel_id='" + hotel_id + '\'' +
                ", hotel_name='" + hotel_name + '\'' +
                ", invitation='" + invitation + '\'' +
                ", tel='" + tel + '\'' +
                ", customer_list=" + customer_list +
                '}';
    }
}
