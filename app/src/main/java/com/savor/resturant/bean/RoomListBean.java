package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/12/20.
 */

public class RoomListBean implements Serializable {
    private static final long serialVersionUID = -1;
    private String id;
    private String name;
    private String room_type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", room_type='" + room_type + '\'' +
                '}';
    }
}
