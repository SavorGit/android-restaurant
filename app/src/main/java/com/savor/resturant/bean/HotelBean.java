package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bushlee on 2017/12/1.
 */

public class HotelBean implements Serializable {
    private String hotel_id;
    private String hotel_name;
    private String invite_id;
    private String tel;
    private List<ContactFormat> customer_list = new ArrayList<>();
    private String invitation;

    @Override
    public String toString() {
        return "HotelBean{" +
                "hotel_id='" + hotel_id + '\'' +
                ", hotel_name='" + hotel_name + '\'' +
                ", invite_id='" + invite_id + '\'' +
                ", tel='" + tel + '\'' +
                ", customer_list=" + customer_list +
                ", invitation='" + invitation + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HotelBean hotelBean = (HotelBean) o;

        if (hotel_id != null ? !hotel_id.equals(hotelBean.hotel_id) : hotelBean.hotel_id != null)
            return false;
        if (hotel_name != null ? !hotel_name.equals(hotelBean.hotel_name) : hotelBean.hotel_name != null)
            return false;
        if (invite_id != null ? !invite_id.equals(hotelBean.invite_id) : hotelBean.invite_id != null)
            return false;
        if (tel != null ? !tel.equals(hotelBean.tel) : hotelBean.tel != null) return false;
        if (customer_list != null ? !customer_list.equals(hotelBean.customer_list) : hotelBean.customer_list != null)
            return false;
        return invitation != null ? invitation.equals(hotelBean.invitation) : hotelBean.invitation == null;
    }

    @Override
    public int hashCode() {
        int result = hotel_id != null ? hotel_id.hashCode() : 0;
        result = 31 * result + (hotel_name != null ? hotel_name.hashCode() : 0);
        result = 31 * result + (invite_id != null ? invite_id.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (customer_list != null ? customer_list.hashCode() : 0);
        result = 31 * result + (invitation != null ? invitation.hashCode() : 0);
        return result;
    }

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

    public String getInvite_id() {
        return invite_id;
    }

    public void setInvite_id(String invite_id) {
        this.invite_id = invite_id;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public List<ContactFormat> getCustomer_list() {
        return customer_list;
    }

    public void setCustomer_list(List<ContactFormat> customer_list) {
        this.customer_list = customer_list;
    }

    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }
}
