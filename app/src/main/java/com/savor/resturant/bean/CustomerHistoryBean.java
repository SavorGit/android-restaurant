package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/12/22.
 */

public class CustomerHistoryBean implements Serializable {
    private static final long serialVersionUID = -1;
    private String username;
    private String create_time;
    private String type;
    private String usermobile;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsermobile() {
        return usermobile;
    }

    public void setUsermobile(String usermobile) {
        this.usermobile = usermobile;
    }

    @Override
    public String toString() {
        return "CustomerHistoryBean{" +
                "username='" + username + '\'' +
                ", create_time='" + create_time + '\'' +
                ", type='" + type + '\'' +
                ", usermobile='" + usermobile + '\'' +
                '}';
    }
}
