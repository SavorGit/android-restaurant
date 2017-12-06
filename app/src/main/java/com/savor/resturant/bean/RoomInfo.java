package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 包间信息
 * Created by hezd on 2017/12/5.
 */

public class RoomInfo implements Serializable {

    /**
     * box_name : v0
     * box_ip : 192.168.1.10
     * room_id : 1
     * box_mac : FC2131313123123
     */

    private String box_name;
    private String box_ip;
    private String room_id;
    private String box_mac;
    private boolean isSelected;

    @Override
    public String toString() {
        return "RoomInfo{" +
                "box_name='" + box_name + '\'' +
                ", box_ip='" + box_ip + '\'' +
                ", room_id='" + room_id + '\'' +
                ", box_mac='" + box_mac + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomInfo roomInfo = (RoomInfo) o;

        if (isSelected != roomInfo.isSelected) return false;
        if (box_name != null ? !box_name.equals(roomInfo.box_name) : roomInfo.box_name != null)
            return false;
        if (box_ip != null ? !box_ip.equals(roomInfo.box_ip) : roomInfo.box_ip != null)
            return false;
        if (room_id != null ? !room_id.equals(roomInfo.room_id) : roomInfo.room_id != null)
            return false;
        return box_mac != null ? box_mac.equals(roomInfo.box_mac) : roomInfo.box_mac == null;
    }

    @Override
    public int hashCode() {
        int result = box_name != null ? box_name.hashCode() : 0;
        result = 31 * result + (box_ip != null ? box_ip.hashCode() : 0);
        result = 31 * result + (room_id != null ? room_id.hashCode() : 0);
        result = 31 * result + (box_mac != null ? box_mac.hashCode() : 0);
        result = 31 * result + (isSelected ? 1 : 0);
        return result;
    }

    public String getBox_name() {
        return box_name;
    }

    public void setBox_name(String box_name) {
        this.box_name = box_name;
    }

    public String getBox_ip() {
        return box_ip;
    }

    public void setBox_ip(String box_ip) {
        this.box_ip = box_ip;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getBox_mac() {
        return box_mac;
    }

    public void setBox_mac(String box_mac) {
        this.box_mac = box_mac;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
