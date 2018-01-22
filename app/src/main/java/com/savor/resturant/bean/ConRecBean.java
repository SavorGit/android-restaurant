package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/12/26.
 */

public class ConRecBean implements Serializable {
    private static final long serialVersionUID = -1;
    private String create_time;
    private String recipt;
    private String id;
    private String room_name;
    private String bigrecipt;

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getRecipt() {
        return recipt;
    }

    public void setRecipt(String recipt) {
        this.recipt = recipt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getBigrecipt() {
        return bigrecipt;
    }

    public void setBigrecipt(String bigrecipt) {
        this.bigrecipt = bigrecipt;
    }

    @Override
    public String toString() {
        return "ConRecBean{" +
                "create_time='" + create_time + '\'' +
                ", recipt='" + recipt + '\'' +
                ", id='" + id + '\'' +
                ", room_name='" + room_name + '\'' +
                ", bigrecipt='" + bigrecipt + '\'' +
                '}';
    }
}
