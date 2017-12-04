package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 登录返回结果
 * Created by hezd on 2017/12/4.
 */

public class LoginResponse implements Serializable {

    /**
     * hotel_id : 7
     * hotel_name : 永峰写字楼(测试)
     */

    private String hotel_id;
    private String hotel_name;

    @Override
    public String toString() {
        return "LoginResponse{" +
                "hotel_id='" + hotel_id + '\'' +
                ", hotel_name='" + hotel_name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginResponse that = (LoginResponse) o;

        if (hotel_id != null ? !hotel_id.equals(that.hotel_id) : that.hotel_id != null)
            return false;
        return hotel_name != null ? hotel_name.equals(that.hotel_name) : that.hotel_name == null;
    }

    @Override
    public int hashCode() {
        int result = hotel_id != null ? hotel_id.hashCode() : 0;
        result = 31 * result + (hotel_name != null ? hotel_name.hashCode() : 0);
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
}
