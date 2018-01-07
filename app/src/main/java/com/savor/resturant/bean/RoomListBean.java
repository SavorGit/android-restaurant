package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/12/20.
 */

public class RoomListBean implements Serializable {
    private static final long serialVersionUID = -1;
    private String room_id;
    private String room_name;
    private String room_type;

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    @Override
    public String toString() {
        return "RoomListBean{" +
                "room_id='" + room_id + '\'' +
                ", room_name='" + room_name + '\'' +
                ", room_type='" + room_type + '\'' +
                '}';
    }
}
