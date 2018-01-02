package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 用户操作历史
 * Created by hezd on 2018/1/2.
 */

public class OperationHistory implements Serializable {

    /**
     * username : 小三vvvvv3434死神
     * create_time : 47分钟
     * type : 修改
     * usermobile : 13712759212
     */

    private String username;
    private String create_time;
    private String type;
    private String usermobile;

    @Override
    public String toString() {
        return "OperationHistory{" +
                "username='" + username + '\'' +
                ", create_time='" + create_time + '\'' +
                ", type='" + type + '\'' +
                ", usermobile='" + usermobile + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationHistory that = (OperationHistory) o;

        if (username != null ? !username.equals(that.username) : that.username != null)
            return false;
        if (create_time != null ? !create_time.equals(that.create_time) : that.create_time != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return usermobile != null ? usermobile.equals(that.usermobile) : that.usermobile == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (create_time != null ? create_time.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (usermobile != null ? usermobile.hashCode() : 0);
        return result;
    }

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
}
