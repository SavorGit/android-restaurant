package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/12/22.
 */

public class CustomerHistoryBean implements Serializable {
    private String username;
    private String create_time;
    private String type;
    private String usermobile;
    private String face_url;
    private String customer_id;

    @Override
    public String toString() {
        return "CustomerHistoryBean{" +
                "username='" + username + '\'' +
                ", create_time='" + create_time + '\'' +
                ", type='" + type + '\'' +
                ", usermobile='" + usermobile + '\'' +
                ", face_url='" + face_url + '\'' +
                ", customer_id='" + customer_id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerHistoryBean that = (CustomerHistoryBean) o;

        if (username != null ? !username.equals(that.username) : that.username != null)
            return false;
        if (create_time != null ? !create_time.equals(that.create_time) : that.create_time != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (usermobile != null ? !usermobile.equals(that.usermobile) : that.usermobile != null)
            return false;
        if (face_url != null ? !face_url.equals(that.face_url) : that.face_url != null)
            return false;
        return customer_id != null ? customer_id.equals(that.customer_id) : that.customer_id == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (create_time != null ? create_time.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (usermobile != null ? usermobile.hashCode() : 0);
        result = 31 * result + (face_url != null ? face_url.hashCode() : 0);
        result = 31 * result + (customer_id != null ? customer_id.hashCode() : 0);
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

    public String getFace_url() {
        return face_url;
    }

    public void setFace_url(String face_url) {
        this.face_url = face_url;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }
}
