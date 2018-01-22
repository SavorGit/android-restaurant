package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2018/1/22.
 */

public class Hotel implements Serializable {
    private String hotel_id;
    private String hotel_name;

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

    @Override
    public String toString() {
        return "Hotel{" +
                "hotel_id='" + hotel_id + '\'' +
                ", hotel_name='" + hotel_name + '\'' +
                '}';
    }
}
